package vn.ldbach.bnote

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.RectF
import android.net.Uri
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.isseiaoki.simplecropview.CropImageView
import com.isseiaoki.simplecropview.callback.CropCallback
import com.isseiaoki.simplecropview.callback.LoadCallback
import kotlinx.android.synthetic.main.activity_crop.*
import java.util.*

class CropActivity : AppCompatActivity() {

    private lateinit var photoUri: Uri
    private lateinit var cropRect: RectF
    private var noChange: Boolean = true

    private val loadCallback = object : LoadCallback {
        override fun onSuccess() {
        }

        override fun onError(e: Throwable?) {
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_crop)

        photoUri = Uri.parse(intent.getStringExtra("photoUri"))

        btn_crop.setOnClickListener { _ ->
            // BasicFragmentPermissionsDispatcher

            image_cropper.crop(photoUri).execute(object : CropCallback {
                override fun onError(e: Throwable?) {
                }

                override fun onSuccess(cropped: Bitmap?) {
                    noChange = false
                    image_preview.setImageBitmap(cropped)
                    //cropRect = image_cropper.actualCropRect
                    //inflateCropper()
                }
            })
        }

        btn_finish_crop.setOnClickListener { _ ->
            if (noChange) {
                setResult(Activity.RESULT_CANCELED)
                finish()
            } else
                saveChanges()
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
            setHandleSizeInDp(10)
            setTouchPaddingInDp(14)
        }

        // cropRect = image_cropper.actualCropRect
        inflateCropper()
    }

    private fun saveChanges() {
        val fileName = UUID.randomUUID().toString()
        val dataStorage = NoteDataStorage()
        dataStorage.saveImage(this, image_cropper.croppedBitmap, fileName)
        val data = Intent()
        data.putExtra("croppedImageName", fileName)
        setResult(Activity.RESULT_OK, data)
        finish()
    }


    private fun inflateCropper() {
        image_cropper.load(photoUri)
                .execute(loadCallback)
    }
}
