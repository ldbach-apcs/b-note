package vn.ldbach.bnote

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.RectF
import android.net.Uri
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import com.isseiaoki.simplecropview.CropImageView
import com.isseiaoki.simplecropview.callback.CropCallback
import com.isseiaoki.simplecropview.callback.LoadCallback
import kotlinx.android.synthetic.main.activity_crop.*
import java.io.BufferedInputStream

class CropActivity : AppCompatActivity() {

    private lateinit var photoUri: Uri
    private lateinit var cropRect: RectF

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
                    image_preview.setImageBitmap(cropped)
                    //cropRect = image_cropper.actualCropRect
                    //inflateCropper()
                }
            })
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

    private fun inflateCropper() {
        image_cropper.load(photoUri)
                .execute(loadCallback)
    }


    private fun getBitmapFromData(data: Intent): Bitmap {
        val photoUri = data.data
        Log.d("b-note", data.data.toString())
        val inputStream = contentResolver.openInputStream(photoUri)
        val bufferedInputStream = BufferedInputStream(inputStream)
        return BitmapFactory.decodeStream(bufferedInputStream)
    }
}
