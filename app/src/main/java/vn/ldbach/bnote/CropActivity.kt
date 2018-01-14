@file:Suppress("DEPRECATION")

package vn.ldbach.bnote

import android.app.Activity
import android.app.ProgressDialog
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import com.isseiaoki.simplecropview.CropImageView
import com.isseiaoki.simplecropview.callback.CropCallback
import com.isseiaoki.simplecropview.callback.LoadCallback
import kotlinx.android.synthetic.main.activity_crop.*
import java.util.*

class CropActivity : AppCompatActivity() {

    private lateinit var photoUri: Uri

    private lateinit var cropDialog: ProgressDialog

    private lateinit var saveDialog: ProgressDialog

    private val loadCallback = object : LoadCallback {
        override fun onSuccess() {}

        override fun onError(e: Throwable?) {}
    }

    private val cropCallback = object : CropCallback {
        override fun onSuccess(cropped: Bitmap?) {
            saveDialog.show()
            cropDialog.dismiss()
            saveChanges()

            //saveDialog.show()
            // val fileName = UUID.randomUUID().toString()
            // image_cropper.save(cropped).execute(Uri.parse(fileName), saveCallback)
        }

        override fun onError(e: Throwable?) {
            cropDialog.dismiss()
            Toast.makeText(this@CropActivity, resources.getString(R.string.crop_error), Toast.LENGTH_LONG).show()
        }
    }

    /*private val saveCallback = object : SaveCallback {
        override fun onSuccess(uri: Uri?) {
            saveChanges(uri!!)
            // saveDialog.dismiss()
        }

        override fun onError(e: Throwable?) { }

    }*/

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_crop)

        saveDialog = ProgressDialog(this)
        saveDialog.isIndeterminate = true
        saveDialog.setMessage(resources.getString(R.string.saving_dialog))

        cropDialog = ProgressDialog(this)
        cropDialog.isIndeterminate = true
        cropDialog.setMessage(resources.getString(R.string.cropping_dialog))

        setSupportActionBar(crop_toolbar)
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            setDisplayShowHomeEnabled(true)
        }


        photoUri = Uri.parse(intent.getStringExtra("photoUri"))

        btn_save.setOnClickListener { _ ->
            cropAndSave()
        }

        btn_cancel.setOnClickListener { _ ->
            onBackPressed()
        }

        image_cropper.apply {
            setCropEnabled(true)
            setCompressFormat(Bitmap.CompressFormat.PNG)
            setCompressQuality(90)
            setOutputMaxSize(320, 320)
            setMinFrameSizeInDp(48)
            setInitialFrameScale(0.5f)
            setHandleShowMode(CropImageView.ShowMode.SHOW_ON_TOUCH)
            setGuideShowMode(CropImageView.ShowMode.SHOW_ON_TOUCH)

            setHandleColor(resources.getColor(R.color.handle_color, theme))
            setGuideColor(resources.getColor(R.color.guide_color, theme))
            setFrameColor(resources.getColor(R.color.frame_color, theme))

            setBackgroundColor(0xFFFFFFFB.toInt())
            setOverlayColor(0xAA1C1C1C.toInt())
            setGuideStrokeWeightInDp(1)
            setFrameStrokeWeightInDp(2)
            setHandleSizeInDp(11)
            setTouchPaddingInDp(14)
        }

        // cropRect = image_cropper.actualCropRect
        inflateCropper()
    }

    private fun cropAndSave() {
        cropDialog.show()
        image_cropper.crop(photoUri).execute(cropCallback)
    }

    private fun saveChanges() {

        /*val fileName = UUID.randomUUID().toString()
        val dataStorage = NoteDataStorage()
        dataStorage.saveImage(this, image_cropper.croppedBitmap, fileName)
        val data = Intent()
        data.putExtra("croppedImageName", fileName)*/

        Thread {
            try {
                this.runOnUiThread {

                    val fileName = UUID.randomUUID().toString()
                    val dataStorage = NoteDataStorage()
                    dataStorage.saveImage(this, image_cropper.croppedBitmap, fileName)
                    val data = Intent()
                    data.putExtra("croppedImageName", fileName)
                    setResult(Activity.RESULT_OK, data)
                    finish()
                }
            } catch (e: Exception) {
            } // Do nothing
            finally {
                //saveDialog.dismiss()
            }
        }.start()

        /*setResult(Activity.RESULT_OK, data)
        finish()*/
    }


    private fun inflateCropper() {
        image_cropper.load(photoUri)
                .execute(loadCallback)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.menu_crop_layout, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            R.id.action_change_picture -> {
                val pickImage = Intent(Intent.ACTION_PICK)
                pickImage.type = "image/*"
                startActivityForResult(pickImage, AddActivity.CHOICE_PICK_GALLERY)
            }
            else -> return false
        }
        return true
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == RESULT_OK && requestCode == AddActivity.CHOICE_PICK_GALLERY) {
            photoUri = data!!.data
            inflateCropper()
        }
    }

    override fun onBackPressed() {
        setResult(Activity.RESULT_CANCELED)
        finish()
    }
}
