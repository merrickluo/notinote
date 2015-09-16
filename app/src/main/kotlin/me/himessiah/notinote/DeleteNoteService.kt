package me.himessiah.notinote

import android.app.IntentService
import android.content.Intent
import android.util.Log
import de.greenrobot.event.EventBus
import me.himessiah.notinote.model.Events
import me.himessiah.notinote.model.NotesManager

public class DeleteNoteService(name: String) : IntentService(name) {

    constructor() : this("name")

    override fun onHandleIntent(intent: Intent) {
        val id = intent.getLongExtra("id", -1)
        Log.w("llllll", "id from intent is " + id)
        if (id != -1L) {
            NotesManager.removeNote(id)
                    .subscribe { EventBus.getDefault().post(Events.UpdateNotification) }
        }
    }
}
