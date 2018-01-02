package vn.ldbach.bnote

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.Button
import android.widget.EditText
import android.widget.TextView

class AddActivity : AppCompatActivity() {

    private val headerEditText by lazy { findViewById<EditText>(R.id.header_edit) }
    private val contentEditText by lazy { findViewById<EditText>(R.id.content_edit) }
    private val btnFinish by lazy { findViewById<Button>(R.id.btn_finish) }

    private lateinit var item: NoteItem

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add)

        loadNote()
        btnFinish.setOnClickListener { _ ->
            saveChanges()
        }
    }

    fun loadNote() {
        item = (intent.getSerializableExtra(MainActivity.SEND_NOTE_ITEM) ?: return) as NoteItem

        headerEditText.setText(item.header, TextView.BufferType.EDITABLE)
        contentEditText.setText(item.content, TextView.BufferType.EDITABLE)
    }

    private fun saveChanges() {
        // Create note
        item.header = headerEditText.text.toString()
        item.content = contentEditText.text.toString()

        // Pass note to intent
        val resultIntent = Intent()
        resultIntent.putExtra(MainActivity.RECEIVE_NOTE_ITEM, item)
        // Finish
        setResult(RESULT_OK, resultIntent)
        finish()
    }

    private fun discardChanges() {
        // finish, discard all changes
        setResult(Activity.RESULT_CANCELED)
        finish()
    }

    override fun onBackPressed() {
        // If there are changes made to the notes
        if (item.header == headerEditText.text.toString() &&
                item.content == contentEditText.text.toString()) {
            discardChanges()
            return
        } else {
            val builder = AlertDialog.Builder(this)
                    .setMessage(R.string.save_changes)
                    .setPositiveButton(R.string.save, { _, _ ->
                        saveChanges()
                    })
                    .setNegativeButton(R.string.discard, { _, _ ->
                        discardChanges()
                    })

            builder.show()
        }
    }
}
