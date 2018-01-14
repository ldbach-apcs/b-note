package vn.ldbach.bnote

import android.graphics.drawable.Drawable
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.view.View
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import kotlinx.android.synthetic.main.activity_notify.*
import kotlinx.android.synthetic.main.content_notify.*

class NotifyActivity : AppCompatActivity() {

    private var item: TextNoteItem? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_notify)


        val b = intent.getBundleExtra("bundle")
        item = b.getSerializable("note_item") as? TextNoteItem

        val path = (this.filesDir.absolutePath + "/${item?.imageName}")

        Glide.with(this).load(path)
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

        tv_note_content.text = item?.content

        if (item?.content!!.isEmpty()) tv_note_content.text = resources.getString(R.string.note_no_content)

        tv_note_header.text = item?.content

        if (item?.content!!.isEmpty()) tv_note_header.text = resources.getString(R.string.note_untitled)
        //supportActionBar?.title = item?.header

        //toolbar.title = item?.header
        //setSupportActionBar(toolbar)

        setSupportActionBar(notify_toolbar)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.menu_notify_layout, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        finish()
        return true
    }

}
