package vn.ldbach.bnote

import android.app.Activity
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Intent
import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.helper.ItemTouchHelper
import android.view.Menu
import android.view.MenuItem
import kotlinx.android.synthetic.main.activity_main.*

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
        adapter = NoteArrayAdapter(notes, this)
        listNote.adapter = adapter
        listNote.layoutManager = LinearLayoutManager(this)

        addButton.setOnClickListener { _ ->
            startAddNoteActivity()
        }

        val callback = ItemTouchCallback(adapter)
        val helper = ItemTouchHelper(callback)
        helper.attachToRecyclerView(listNote)

        setSupportActionBar(main_toolbar)
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
                    if (item.isEmpty()) {
                        notes.removeAt(idx)
                        adapter.notifyItemRemoved(idx)
                        break
                    } else {
                        notes[idx] = item
                        adapter.notifyItemChanged(idx)
                        deleteAlarm(item)
                    }
                }
            }

            // Get data
            if (!exist) {
                if (item.isEmpty()) return
                notes.add(item)
                adapter.notifyItemInserted(notes.size - 1)
            }

            setAlarms()
            val storage = NoteDataStorage()
            storage.saveNotes(notes, this)
        }
    }

    override fun onResume() {
        super.onResume()
        setAlarms()
    }

    private fun deleteAlarm(item: NoteItem) {
        val alarmManager = getSystemService(ALARM_SERVICE) as AlarmManager
        val alarmIntent = Intent(this, AlarmReceiver::class.java)

        alarmIntent.action = "vn.ldbach.bnote"

        val bundle = Bundle()
        bundle.putSerializable("note_item", item)
        alarmIntent.putExtra("bundle", bundle)

        val pendingIntent = PendingIntent.getBroadcast(
                this,
                item.uuid.hashCode(),
                alarmIntent,
                PendingIntent.FLAG_UPDATE_CURRENT)

        alarmManager.cancel(pendingIntent)
    }

    private fun setAlarms() {
        notes
                .filter { it.earlyNotifyTime != EarlyNotifyTime.NO_NOTIFY }
                .forEach { setAlarm(it) }
    }

    private fun setAlarm(item: NoteItem) {
        val alarmManager = getSystemService(ALARM_SERVICE) as AlarmManager
        val alarmIntent = Intent(this, AlarmReceiver::class.java)

        alarmIntent.action = "vn.ldbach.bnote"

        val bundle = Bundle()
        bundle.putSerializable("note_item", item)
        alarmIntent.putExtra("bundle", bundle)

        val pendingIntent = PendingIntent.getBroadcast(
                this,
                item.uuid.hashCode(),
                alarmIntent,
                PendingIntent.FLAG_UPDATE_CURRENT)

        var alarmTime = item.eventTime + item.earlyNotifyTime.getValue()
        val interval = item.alarmInterval.getValue()

        while (interval != 0L && alarmTime < System.currentTimeMillis()) {
            item.eventTime += interval
            alarmTime += interval
        }

        // If none repetitive alarm fired already
        if (alarmTime <= System.currentTimeMillis()) {
            // item.earlyNotifyTime = EarlyNotifyTime.NO_NOTIFY
        }
        else
            alarmManager.set(
                    AlarmManager.RTC_WAKEUP,
                    alarmTime,
                    pendingIntent)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.menu_main_layout, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        startAddNoteActivity()
        return true
    }

    fun handleItemRemove(where: Int, removedItem: NoteItem) {
        deleteAlarm(removedItem)
        val snackbarMsg = R.string.note_deleted_msg
        Snackbar.make(main_wrapper, snackbarMsg, Snackbar.LENGTH_LONG)
                .setAction(R.string.snackbar_action, { _ ->
                    undoDeleteNote(where, removedItem)
                })
                .show()
    }

    private fun undoDeleteNote(where: Int, removedItem: NoteItem) {
        notes.add(where, removedItem)
        adapter.notifyItemInserted(where)
        setAlarms()
    }
}
private fun NoteItem.isEmpty(): Boolean {
    return header.isEmpty() and content.isEmpty() and imageName.isEmpty()
}
