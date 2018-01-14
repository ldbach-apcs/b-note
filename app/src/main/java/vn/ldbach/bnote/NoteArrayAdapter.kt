package vn.ldbach.bnote

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.drawable.Drawable
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import java.util.*

/**
 * Adapter for notes
 */
class NoteArrayAdapter(private val notes: ArrayList<NoteItem>) : RecyclerView.Adapter<NoteHolder>(), ItemTouchHelperAdapter {
    override fun onItemMove(from: Int, to: Int) {
        if (from < to) {
            for (i in from until to) {
                Collections.swap(notes, i, i + 1)
            }
        } else {
            for (i in from downTo to + 1) {
                Collections.swap(notes, i, i - 1)
            }
        }
        notifyItemMoved(from, to)
    }

    override fun onItemDismiss(where: Int) {
        notes.removeAt(where)
        notifyItemRemoved(where)
    }


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
    private val image: ImageView = v!!.findViewById(R.id.note_image)

    fun bindView(item: NoteItem) {
        // Display here
        this.item = item

        header.text = item.header
        content.text = item.content

        if (item.header.isEmpty()) {
            header.text = context.getString(R.string.note_untitled)
        }
        if (item.content.isEmpty()) {
            content.text = context.getString(R.string.note_no_content)
        }

        val tempImageName = item.imageName
        Log.d("b-note", tempImageName)

        //if (tempImageName != "") {
        //    val storage = NoteDataStorage()
        //    val bm = storage.loadImage(context, tempImageName)
        //    if (bm != null) image.setImageBitmap(bm)
        //}

        val path = (context.filesDir.absolutePath + "/$tempImageName")

        Glide.with(context).load(path)
                .thumbnail(0.2f)
                .listener(object : RequestListener<Drawable> {
                    override fun onResourceReady(resource: Drawable?, model: Any?, target: Target<Drawable>?, dataSource: DataSource?, isFirstResource: Boolean): Boolean {
                        return false
                    }

                    override fun onLoadFailed(e: GlideException?, model: Any?, target: Target<Drawable>?, isFirstResource: Boolean): Boolean {
                        image.visibility = View.INVISIBLE
                        return true
                    }
                })

                .into(image)

        setOnClick()
    }

    private fun setOnClick() {
        v!!.setOnClickListener(this)
    }
}
