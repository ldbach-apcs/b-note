package vn.ldbach.bnote

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView

class MainActivity : AppCompatActivity() {

    companion object {
        @JvmStatic
        internal val RECEIVE_NOTE_ITEM = "new_note_item"
        @JvmStatic
        internal val SEND_NOTE_ITEM = "old_note_item"
        @JvmStatic
        internal val REQUEST_ADD_NOTE = 113
    }

    private val listNote by lazy { findViewById<RecyclerView>(R.id.list_notes) }
    private val addButton by lazy { findViewById<FloatingActionButton>(R.id.btn_add_note) }
    private val dataStorage = NoteDataStorage()

    private lateinit var adapter: NoteArrayAdapter
    private lateinit var notes: ArrayList<NoteItem>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        notes = dataStorage.loadNotes(this)
        adapter = NoteArrayAdapter(notes)
        listNote.adapter = adapter
        listNote.layoutManager = LinearLayoutManager(applicationContext)

        addButton.setOnClickListener { _ ->
            startAddNoteActivity()
        }
    }

    private fun startAddNoteActivity() {
        val intent = Intent(this, AddActivity::class.java)
        intent.putExtra(SEND_NOTE_ITEM, TextNoteItem())
        startActivityForResult(intent, REQUEST_ADD_NOTE)
    }


    override fun onPause() {
        dataStorage.saveNotes(notes, this)
        super.onPause()
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode != Activity.RESULT_OK) return

        if (requestCode == REQUEST_ADD_NOTE) {
            var exist = false

            val item = data!!.getSerializableExtra(RECEIVE_NOTE_ITEM) as NoteItem

            for (idx in 0 until notes.size) {
                if (notes[idx].uuid == item.uuid) {
                    exist = true
                    notes[idx] = item
                    adapter.notifyItemChanged(idx)
                }
            }

            // Get data
            if (!exist) {
                notes.add(item)
                adapter.notifyItemInserted(notes.size - 1)
            }
        }
    }
}
