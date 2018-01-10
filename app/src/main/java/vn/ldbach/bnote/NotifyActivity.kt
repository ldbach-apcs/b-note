package vn.ldbach.bnote

import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity

import kotlinx.android.synthetic.main.activity_notify.*

class NotifyActivity : AppCompatActivity() {

    private var item: TextNoteItem? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_notify)
        setSupportActionBar(toolbar)

        val b = intent.getBundleExtra("bundle")
        item = b.getSerializable("note_item") as? TextNoteItem

        fab.setOnClickListener { view ->
            if (item == null)
                return@setOnClickListener
            else Snackbar.make(view, item!!.header, Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show()
        }
    }

}
