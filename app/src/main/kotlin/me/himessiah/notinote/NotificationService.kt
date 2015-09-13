package me.himessiah.notinote

import android.app.Notification
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Binder
import android.os.IBinder
import android.widget.RemoteViews
import me.himessiah.notinote.model.Note
import me.himessiah.notinote.model.NotesManager
import rx.android.schedulers.AndroidSchedulers
import kotlin.properties.Delegates

public class NotificationService : Service() {

    val context: Context = this
    val id: Int = 1000
    val binder = LocalBinder()

    var notificationManager: NotificationManager by Delegates.notNull()

    public inner class LocalBinder : Binder() {
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

        NotesManager(this).allNotes().observeOn(AndroidSchedulers.mainThread()).subscribe {
            val allNotesView = RemoteViews(getPackageName(), R.layout.note_list_layout)
            var firstNote: String? = null
            for (note in it) {
                allNotesView.addView(R.id.note_list, createNoteItemView(note))
                firstNote = firstNote ?: note.content
            }
            showNotification(allNotesView, firstNote)
        }
    }

    fun createNoteItemView(note: Note): RemoteViews {
        val view = RemoteViews(getPackageName(), R.layout.note_item)
        view.setTextViewText(R.id.note_content, note.content)

        return view
    }

    fun showNotification(bigView: RemoteViews, firstNote: String?) {
        val first = firstNote ?: getString(R.string.add_new_note)

        val defaultNotification: Notification =
                Notification.Builder(context)
                        .setOngoing(true)
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setContentTitle(getString(R.string.do_not_forget))
                        .setContentText(firstNote)
                        .setPriority(Notification.PRIORITY_HIGH)
                        .build()

        defaultNotification.bigContentView = bigView

        notificationManager.notify(id, defaultNotification)

    }
}
