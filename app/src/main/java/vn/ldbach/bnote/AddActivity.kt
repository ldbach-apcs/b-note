package vn.ldbach.bnote

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.widget.TextView
import kotlinx.android.synthetic.main.activity_add.*
import java.io.BufferedInputStream
import java.util.*

class AddActivity : AppCompatActivity() {

    companion object {
        @JvmStatic
        internal val CHOICE_PICK_GALLERY = 404
        @JvmStatic
        internal val PERM_STORAGE = 222
    }


    //private val headerEditText by lazy { findViewById<EditText>(R.id.header_edit) }
    //private val contentEditText by lazy { findViewById<EditText>(R.id.content_edit) }
    //private val btnFinish by lazy { findViewById<Button>(R.id.btn_finish) }
    //private val noteImageView by lazy { findViewById<ImageView>(R.id.iv_image) }

    private lateinit var item: NoteItem
    private var tempImageName = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add)

        loadNote()
        btn_finish.setOnClickListener { _ ->
            saveChanges()
        }

        iv_image.setOnClickListener { _ ->
            chooseImage()
        }
    }

    private fun pickImageAndCrop() {
        val pickImage = Intent(Intent.ACTION_PICK)
        // pickImage.addCategory(Intent.CATEGORY_OPENABLE)
        pickImage.type = "image/*"
        startActivityForResult(getCropIntent(pickImage), CHOICE_PICK_GALLERY)
    }

    private fun chooseImage() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                    PERM_STORAGE)
        } else {
            pickImageAndCrop()
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        if (requestCode == PERM_STORAGE && grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            pickImageAndCrop()
        }
    }

    private fun getCropIntent(intent: Intent): Intent {
        intent.putExtra("crop", "true")
        intent.putExtra("aspectX", 1)
        intent.putExtra("aspectY", 1)
        intent.putExtra("outputX", 320)
        intent.putExtra("outputY", 320)
        intent.putExtra("return-data", "true")
        return intent
    }

    // private fun cropImage() {}


    private fun loadNote() {
        item = (intent.getSerializableExtra(MainActivity.SEND_NOTE_ITEM) ?: return) as NoteItem

        header_edit.setText(item.header, TextView.BufferType.EDITABLE)
        content_edit.setText(item.content, TextView.BufferType.EDITABLE)

        tempImageName = item.imageName
        if (tempImageName != "") {
            val storage = NoteDataStorage()
            val bm = storage.loadImage(this, tempImageName)
            if (bm != null) iv_image.setImageBitmap(bm)
        }
    }

    private fun saveChanges() {
        // Create note
        item.header = header_edit.text.toString()
        item.content = content_edit.text.toString()
        // item.imageName = tempImageName

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
        if (item.header == header_edit.text.toString() &&
                item.content == content_edit.text.toString() &&
                item.imageName == tempImageName) {
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == RESULT_OK && requestCode == CHOICE_PICK_GALLERY) {
            val chosenPic = getBitmapFromData(data!!)
            val storage = NoteDataStorage()
            val imageName = UUID.randomUUID().toString()
            storage.saveImage(this, chosenPic, imageName)
            item.imageName = imageName
            iv_image.setImageBitmap(chosenPic)
        }

        super.onActivityResult(requestCode, resultCode, data)
    }

    private fun getBitmapFromData(data: Intent): Bitmap {
        val photoUri = data.data
        Log.d("b-note", data.data.toString())
        val inputStream = contentResolver.openInputStream(photoUri)
        val bufferedInputStream = BufferedInputStream(inputStream)
        return BitmapFactory.decodeStream(bufferedInputStream)
    }
}
