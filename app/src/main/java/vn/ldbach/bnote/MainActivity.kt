package vn.ldbach.bnote

import android.app.Activity
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Intent
import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log

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

    override fun onResume() {
        super.onResume()
        setAlarms()
    }

    private fun setAlarms() {
        notes
                .filter { it.earlyNotifyTime != EarlyNotifyTime.NO_NOTIFY }
                .forEach { setAlarm(it) }
    }

    private fun setAlarm(item: NoteItem) {
        Log.d("b-note", "Test alarm clicked")
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
        if (alarmTime <= System.currentTimeMillis())
            item.earlyNotifyTime = EarlyNotifyTime.NO_NOTIFY
        else
            alarmManager.set(
                    AlarmManager.RTC_WAKEUP,
                    alarmTime,
                    pendingIntent)
    }
}
