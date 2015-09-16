package me.himessiah.notinote

import android.app.*
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Binder
import android.os.IBinder
import android.view.WindowManager
import android.widget.RemoteViews
import de.greenrobot.event.EventBus
import me.himessiah.notinote.model.Events
import me.himessiah.notinote.model.Note
import me.himessiah.notinote.model.NotesManager
import rx.android.schedulers.AndroidSchedulers
import kotlin.properties.Delegates

public class NotificationService : Service() {

    val context: Context = this
    val id: Int = 1000
    val binder = LocalBinder()

    var notificationManager: NotificationManager by Delegates.notNull()
    var windowManager: WindowManager by Delegates.notNull()

    public inner class LocalBinder : Binder() {
        fun getService(): NotificationService {
            return this@NotificationService
        }
    }

    override fun onCreate() {
        super.onCreate()
        notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        windowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager

        EventBus.getDefault().register(this)

        registerReceiver(ScreenOnOffReceiver(), IntentFilter("android.intent.action.USER_PRESENT"))
        registerReceiver(ScreenOnOffReceiver(), IntentFilter("android.intent.action.SCREEN_OFF"))
        refreshNotification()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return Service.START_STICKY
    }

    override fun onBind(intent: Intent): IBinder? {
        return binder
    }

    override fun onDestroy() {
        notificationManager.cancel(id)
        super.onDestroy()
    }

    private fun refreshNotification() {
        refreshNotification(Notification.PRIORITY_MAX)
    }

    private fun refreshNotification(priority: Int) {
        notificationManager.cancel(id)

        NotesManager.allNotes().observeOn(AndroidSchedulers.mainThread()).subscribe {
            if (it.isNotEmpty()) {
                val allNotesView = RemoteViews(getPackageName(), R.layout.note_list_layout)

                var firstNote: String? = null
                for (note in it) {
                    allNotesView.addView(R.id.note_list, createNoteItemView(note))
                    firstNote = firstNote ?: note.content
                }

                allNotesView.setOnClickPendingIntent(R.id.add_note_button, newAddNoteIntent())

                showNotification(allNotesView, firstNote, priority)
            } else {
                showNotification(null, null, priority)
            }
        }
    }

    private fun createNoteItemView(note: Note): RemoteViews {
        val view = RemoteViews(getPackageName(), R.layout.note_item)
        view.setTextViewText(R.id.note_content, note.content)

        view.setOnClickPendingIntent(R.id.note_remove_button, newRemoveIntent(note))
        return view
    }

    private fun newRemoveIntent(note: Note): PendingIntent {
        val removeIntent = Intent(this, javaClass<DeleteNoteService>())
        removeIntent.putExtra("id", note.id)
        return PendingIntent.getService(this, note.id.toInt(), removeIntent, 0)
    }

    private fun newAddNoteIntent(): PendingIntent {
        val addNoteIntent = Intent(this, javaClass<AddNoteActivity>())
        return PendingIntent.getActivity(this, 0, addNoteIntent, PendingIntent.FLAG_UPDATE_CURRENT)
    }

    private fun showNotification(bigView: RemoteViews?, firstNote: String?, priority: Int) {
        val first = firstNote ?: getString(R.string.add_new_note)

        val notification = Notification.Builder(context)
                .setOngoing(true)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(getString(R.string.do_not_forget))
                .setContentText(first)
                .setPriority(priority)
                .build()

        if (bigView == null) {
            notification.contentIntent = newAddNoteIntent()
        } else {
            notification.bigContentView = bigView
        }

        notificationManager.notify(id, notification)

    }

    public fun onEvent(event: Events.UpdateNotification) {
        refreshNotification(Notification.PRIORITY_HIGH)
    }

    public fun onEvent(event: Events.HideNotification) {
        refreshNotification(Notification.PRIORITY_MIN)
    }

    public fun onEvent(event: Events.PriorNotification) {
        refreshNotification(Notification.PRIORITY_HIGH)
    }

    private class ScreenOnOffReceiver : BroadcastReceiver() {

        override fun onReceive(context: Context, intent: Intent) {
            val keyguardManager = context.getSystemService(Context.KEYGUARD_SERVICE) as KeyguardManager
            if (keyguardManager.isKeyguardLocked()) {
                EventBus.getDefault().post(Events.HideNotification)
            } else {
                EventBus.getDefault().post(Events.UpdateNotification)
            }
        }

    }
}
