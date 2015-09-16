package me.himessiah.notinote

import android.app.Activity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import butterknife.bindView
import com.jakewharton.rxbinding.view.clicks
import com.jakewharton.rxbinding.widget.textChanges
import de.greenrobot.event.EventBus
import me.himessiah.notinote.model.Events
import me.himessiah.notinote.model.NotesManager

public class AddNoteActivity : Activity() {

    val noteEditText: EditText by bindView(R.id.note_content_edit_text)
    val addNoteButton: Button by bindView(R.id.add_note_confirm_button)

    var newNoteContent: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_note)

        setupWindow()

        noteEditText.requestFocus()

        noteEditText.textChanges().subscribe { text ->
            newNoteContent = text.toString()
        }

        addNoteButton.clicks()
                .filter { newNoteContent.isNotEmpty() }
                .flatMap { NotesManager.addNote(newNoteContent) }
                .subscribe {
                    EventBus.getDefault().post(Events.UpdateNotification)
                    finish()
                }
    }

    private fun setupWindow() {
        val window = getWindow()

        window.setDimAmount(0F)
    }
}
