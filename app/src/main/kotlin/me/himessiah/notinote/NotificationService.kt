package me.himessiah.notinote

import android.app.Notification
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Binder
import android.os.Build
import android.os.IBinder
import android.preference.PreferenceManager
import android.view.WindowManager
import android.widget.RemoteViews
import de.greenrobot.event.EventBus
import me.himessiah.notinote.model.Constant
import me.himessiah.notinote.model.Events
import me.himessiah.notinote.model.Note
import me.himessiah.notinote.model.NotesManager
import kotlin.properties.Delegates

public class NotificationService : Service() {

    val context: Context = this
    val id: Int = 1000
    val binder = LocalBinder()

    var notificationManager: NotificationManager by Delegates.notNull()
    var windowManager: WindowManager by Delegates.notNull()
    var preference: SharedPreferences by Delegates.notNull()

    var notification: Notification by Delegates.notNull()
    var contentView: RemoteViews by Delegates.notNull()
    var contentText: String = ""


    public inner class LocalBinder : Binder() {
        fun getService(): NotificationService {
            return this@NotificationService
        }
    }

    override fun onCreate() {
        super.onCreate()
        notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        windowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        preference = PreferenceManager.getDefaultSharedPreferences(this)
        notification = buildNotification()
        contentView = buildContentView()

        EventBus.getDefault().register(this)

        NotesManager.notesObservable().subscribe {
            contentView.removeAllViews(R.id.note_list)
            var firstNote: String? = null

            if (it.isNotEmpty()) {
                var count = 0
                for (note in it) {
                    contentView.addView(R.id.note_list, createNoteItemView(note))
                    firstNote = firstNote ?: note.content
                    count++
                }

                if (!firstNote.isNullOrBlank() && firstNote != contentText) {
                    contentText = firstNote as String
                    notification = buildNotification()
                }

                if (count >= 4) {
                    contentView.setTextViewText(R.id.add_note_button, getString(R.string.max_note_hint))
                    contentView.setOnClickPendingIntent(R.id.add_note_button, null)
                } else {
                    contentView.setTextViewText(R.id.add_note_button, getString(R.string.add_note))
                    contentView.setOnClickPendingIntent(R.id.add_note_button, newAddNoteIntent())
                }

                notification.bigContentView = contentView
                notification.contentIntent = null
            } else {
                if (contentText != getString(R.string.add_new_note)) {
                    contentText = getString(R.string.add_new_note)
                    notification = buildNotification()
                }

                notification.bigContentView = null
                notification.contentIntent = newAddNoteIntent()
            }

            notificationManager.notify(id, notification)
        }
    }

    private fun buildNotification(): Notification {
        if (contentText.isEmpty()) {
            contentText = getString(R.string.add_new_note)
        }

        val priority = when (preference.getString(Constant.NotificationPriorityKey, "0")) {
            "0" -> Notification.PRIORITY_HIGH
            "1" -> Notification.PRIORITY_DEFAULT
            else -> Notification.PRIORITY_MIN
        }

        val builder = Notification.Builder(context)
                .setOngoing(true)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(getString(R.string.do_not_forget))
                .setContentText(contentText)
                .setPriority(priority)

        if (Build.VERSION.SDK_INT >= 21) {
            builder.setVisibility(Notification.VISIBILITY_SECRET)
        }

        return builder.build()
    }

    private fun buildContentView(): RemoteViews {
        val allNotesView = RemoteViews(getPackageName(), R.layout.note_list_layout)
        allNotesView.setOnClickPendingIntent(R.id.add_note_button, newAddNoteIntent())

        return allNotesView
    }

    fun onEvent(event: Events.UpdatePriority) {
        notification.priority = when (event.priority) {
            "0" -> Notification.PRIORITY_MAX
            "1" -> Notification.PRIORITY_DEFAULT
            else -> Notification.PRIORITY_MIN
        }

        notificationManager.notify(id, notification)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return Service.START_STICKY
    }

    override fun onBind(intent: Intent): IBinder? {
        return binder
    }

    override fun onDestroy() {
        notificationManager.cancel(id)
        EventBus.getDefault().unregister(this)
        super.onDestroy()
    }

    private fun createNoteItemView(note: Note): RemoteViews {
        val view = RemoteViews(getPackageName(), R.layout.note_item)
        view.setTextViewText(R.id.note_content, note.content)
        view.setTextViewText(R.id.note_added, TextUtils.humanReadableDateText(note.added.getTime()))

        view.setOnClickPendingIntent(R.id.note_remove_button, newRemoveIntent(note))
        return view
    }

    private fun newRemoveIntent(note: Note): PendingIntent {
        val removeIntent = Intent(this, DeleteNoteService::class.java)
        removeIntent.putExtra("id", note.id)
        return PendingIntent.getService(this, note.id.toInt(), removeIntent, 0)
    }

    private fun newAddNoteIntent(): PendingIntent {
        val addNoteIntent = Intent(this, AddNoteActivity::class.
                java)
        return PendingIntent.getActivity(this, 0, addNoteIntent, PendingIntent.FLAG_UPDATE_CURRENT)
    }
}
