package ninja.luois.notinote

import android.app.IntentService
import android.app.RemoteInput
import android.content.Intent
import android.util.Log

class NoteService: IntentService("note_service") {
    override fun onHandleIntent(intent: Intent?) {
        Log.d("NoteService", intent.toString())
        val action = intent?.getStringExtra("action") ?: return
        val manager = NotesManager(baseContext)
        if (action == "add") {
            val text = RemoteInput.getResultsFromIntent(intent)?.
                    getCharSequence("add note")
                    ?: return
            manager.addNote(text.toString())
        } else if (action == "delete") {
            val noteId = intent.getStringExtra("noteId")
            manager.deleteNote(noteId)
        }
    }
}