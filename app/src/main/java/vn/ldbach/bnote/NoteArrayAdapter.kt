package vn.ldbach.bnote

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView

/**
 * Adapter for notes
 */
class NoteArrayAdapter(private val notes: ArrayList<NoteItem>) : RecyclerView.Adapter<NoteHolder>() {


    override fun getItemCount(): Int {
        return notes.count()
    }

    override fun onBindViewHolder(holder: NoteHolder?, position: Int) {
        val note = notes[position]
        holder?.bindView(note)
    }


    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): NoteHolder {

        val itemView = LayoutInflater.from(parent!!.context)
                .inflate(R.layout.note_item, parent, false)

        val context = parent.context
        return NoteHolder(itemView, context)
    }
}

class NoteHolder(private val v: View?, private val context: Context) : RecyclerView.ViewHolder(v), View.OnClickListener {
    private lateinit var item: NoteItem

    override fun onClick(p0: View?) {
        val intent = Intent(context, AddActivity::class.java)
        intent.putExtra(MainActivity.SEND_NOTE_ITEM, item)
        (context as Activity).startActivityForResult(intent, MainActivity.REQUEST_ADD_NOTE)
    }

    private val header: TextView = v!!.findViewById(R.id.note_header)
    private val content: TextView = v!!.findViewById(R.id.note_content)

    fun bindView(item: NoteItem) {
        // Display here
        header.text = item.header
        content.text = item.content

        this.item = item
        setOnClick()
    }

    private fun setOnClick() {
        v!!.setOnClickListener(this)
    }
}
