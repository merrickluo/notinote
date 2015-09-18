package me.himessiah.notinote

//import com.jakewharton.rxbinding.view.clicks
//import com.jakewharton.rxbinding.widget.textChanges
import android.app.Activity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.Button
import android.widget.EditText
import butterknife.bindView
import me.himessiah.notinote.model.NotesManager

public class AddNoteActivity : Activity() {

    val noteEditText: EditText by bindView(R.id.note_content_edit_text)
    val addNoteButton: Button by bindView(R.id.add_note_confirm_button)

    var newNoteContent: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_note)

        setupWindow()

        noteEditText.addTextChangedListener(object : TextWatcher {

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                newNoteContent = s.toString()
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun afterTextChanged(s: Editable?) {

            }

        })

        addNoteButton.setOnClickListener {
            if (newNoteContent.isNotEmpty()) {
                NotesManager.addNote(newNoteContent)
                        .subscribe {
                            finish()
                        }
            }
        }
    }

    private fun setupWindow() {
        val window = getWindow()

        window.setDimAmount(0F)
    }
}
