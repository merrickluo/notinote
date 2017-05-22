package ninja.luois.notinote

import android.content.Context
import android.content.Intent
import android.os.Binder
import android.os.IBinder
import android.widget.RemoteViews
import io.reactivex.android.schedulers.AndroidSchedulers
import kotlin.properties.Delegates
import android.app.*
import android.graphics.drawable.Icon
import java.util.*


class NotificationService : Service() {

    val context: Context = this
    val id: Int = 1000
    val binder = LocalBinder()

    var notificationManager: NotificationManager by Delegates.notNull()

    inner class LocalBinder : Binder() {
        fun getService(): NotificationService {
            return this@NotificationService
        }
    }

    override fun onCreate() {
        super.onCreate()
        notificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
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

    fun refreshNotification() {
        notificationManager.cancel(id)

        NotesManager(context).allNotes().observeOn(AndroidSchedulers.mainThread()).subscribe {
            val allNotesView = RemoteViews(packageName, R.layout.note_list_layout)

            if (it.isEmpty()) {
                val emptyNoteView = RemoteViews(packageName, R.layout.note_item)
                emptyNoteView.setTextViewText(R.id.note_content, getString(R.string.add_new_note))
                allNotesView.addView(R.id.note_list, emptyNoteView)
            } else {
                it.forEach { note ->
                    allNotesView.addView(R.id.note_list, createNoteItemView(note))
                }
            }
            showNotification(allNotesView)
        }
    }

    fun createNoteItemView(note: Note): RemoteViews {
        val view = RemoteViews(packageName, R.layout.note_item)
        view.setTextViewText(R.id.note_content, note.content)
        view.setOnClickPendingIntent(R.id.note_done, newDeleteIntent(note))
        return view
    }

    private fun newAddIntent(): PendingIntent {
        val i = Intent(context, NoteService::class.java)
        i.putExtra("action", "add")
        val requestCode = Random(System.currentTimeMillis()).nextInt()
        return PendingIntent.getService(context, requestCode, i, 0)
    }

    private fun newDeleteIntent(note: Note): PendingIntent {
        val i = Intent(context, NoteService::class.java)
        i.putExtra("action", "delete")
        i.putExtra("noteId", note.id)
        val requestCode = Random(System.currentTimeMillis()).nextInt()
        return PendingIntent.getService(context, requestCode, i, 0)
    }

    fun showNotification(bigView: RemoteViews) {
        val remoteInput = RemoteInput.Builder("add note").setLabel("Add Note").build()
        val icon = Icon.createWithResource(context, android.R.drawable.ic_input_add)
        val action = Notification.Action.Builder(icon, "Add Note", newAddIntent())
                .addRemoteInput(remoteInput)
                .build()
        val defaultNotification: Notification =
                Notification.Builder(context)
                        .setOngoing(true)
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setCustomContentView(bigView)
                        //.setCustomBigContentView(bigView)
                        .setStyle(Notification.DecoratedCustomViewStyle())
                        .setGroup("233")
                        .setPriority(Notification.PRIORITY_HIGH)
                        .addAction(action)
                        .build()

        notificationManager.notify(id, defaultNotification)

    }
}
