package vn.ldbach.bnote

import android.Manifest
import android.app.Activity
import android.app.AlarmManager
import android.app.AlertDialog
import android.app.PendingIntent
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Build
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.util.DisplayMetrics
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import kotlinx.android.synthetic.main.activity_add.*
import kotlinx.android.synthetic.main.content_add.*
import java.io.BufferedInputStream
import java.util.*

class AddActivity : AppCompatActivity(), ScheduleObserver {
    override fun onFinish(newItem: NoteItem) {
        item = newItem
        dialog.dismiss()
    }

    companion object {
        @JvmStatic
        internal val CHOICE_PICK_GALLERY = 404
        @JvmStatic
        internal val PERM_STORAGE = 222
        @JvmStatic
        internal val FINISH_IMAGE_CROP = 235
        @JvmStatic
        internal val ALARM_REQUEST_CODE = 111
    }


    //private val headerEditText by lazy { findViewById<EditText>(R.id.header_edit) }
    //private val contentEditText by lazy { findViewById<EditText>(R.id.content_edit) }
    //private val btnFinish by lazy { findViewById<Button>(R.id.btn_finish) }
    //private val noteImageView by lazy { findViewById<ImageView>(R.id.iv_image) }

    private lateinit var item: NoteItem
    private var tempImageName = ""
    private var c = Calendar.getInstance()
    // private var firstUse = true
    private var hasImage = false
    private lateinit var dialog: ScheduleDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add)

        loadNote()
        loadUi()

        dialog = ScheduleDialog(item, this, this)

        btn_finish.requestFocus()
        btn_finish.setOnClickListener { _ ->
            saveChanges()
        }

        iv_image.setOnClickListener { _ ->
            handleImageItem()
        }

        btn_sendNotification.setOnClickListener { _ ->
            // sendNotification()
            setAlarm()
        }

        /*tv_pick_time_date.setOnClickListener { _ ->
            pickDateTime()
        }*/
    }

    private fun loadUi() {
        /*add_toolbar.apply {
            setSupportActionBar(this)
        }*/

        header_edit.apply {
            setSelection(this.length())
        }

        setSupportActionBar(add_toolbar)
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            setDisplayShowHomeEnabled(true)
        }

        val displayMetrics = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(displayMetrics)

        /*iv_image.apply {
            layoutParams.height = displayMetrics.widthPixels
            requestLayout()
        }*/

        if (hasImage) iv_image.visibility = View.VISIBLE

        // loadBottomSheet()
    }

    /*private fun pickDateTime() {
        if (firstUse) {
            c = Calendar.getInstance()
            firstUse = false
        }

        val datePickerCallback = DatePickerDialog.OnDateSetListener { _, year, monthOfYear, dayOfMonth ->
            c.set(year, monthOfYear, dayOfMonth)
            showPickTime()
        }

        DatePickerDialog(
                this,
                datePickerCallback,
                c.get(Calendar.YEAR),
                c.get(Calendar.MONTH),
                c.get(Calendar.DAY_OF_MONTH)
        ).show()
    }*/

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.menu_add_layout, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            R.id.action_add_picture -> handleImageItem()
            R.id.action_schedule -> handleScheduleItemClicked()
            else -> return false
        }
        return true
    }

    private fun handleImageItem() {
        if (hasImage) {
            // Prompt
            val dialogBuilder = AlertDialog.Builder(this)
            dialogBuilder.setItems(R.array.handle_image_array) { _, which ->
                if (which == 0) {
                    // Replace picture
                    chooseImage()
                } else {
                    // delete picture
                    val storage = NoteDataStorage()
                    if (item.imageName.isNotEmpty())
                        storage.deleteImage(this, item.imageName)
                    item.imageName = ""
                    iv_image.visibility = View.GONE
                    hasImage = false
                }
            }.show()


        } else {
            // Go straight to pick image
            chooseImage()
        }
    }

    private fun handleScheduleItemClicked() {
        /*if (hasSchedule) {
            // Prompt
            val dialogBuilder = AlertDialog.Builder(this)
            dialogBuilder.setItems(arrayOf("Replace picture", "Delete picture")) { _, which ->
                if (which == 0) {
                    // Replace picture
                    chooseImage()
                } else {
                    // delete picture
                    val storage = NoteDataStorage()
                    if (item.imageName.isNotEmpty())
                        storage.deleteImage(this, item.imageName)
                    item.imageName = ""
                }
            }.show()


        }
        else {
            // Go straight to pick image
            chooseImage()
        }*/

        // Show a dialog
        dialog = ScheduleDialog(item, this, this)
        //dialog.setTitle(R.string.schedule_dialog_title)
        //dialog.window.setTitle(resources.getString(R.string.schedule_dialog_title))
        dialog.show()
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    /*private fun showPickTime() {
        val timePickerCallback = TimePickerDialog.OnTimeSetListener { _, hourOfDay, minute ->
            c.apply {
                set(Calendar.HOUR_OF_DAY, hourOfDay)
                set(Calendar.MINUTE, minute)

                // Now update view
                tv_pick_time_date.text =
                        "${c.timeInMillis}"

                // And save alarm
                item.hasAlarm = true
                item.eventTime = c.timeInMillis
            }
        }

        TimePickerDialog(
                this,
                timePickerCallback,
                c.get(Calendar.HOUR_OF_DAY),
                c.get(Calendar.MINUTE),
                false
        ).show()
    }*/

    private fun pickImageAndCrop() {
        val pickImage = Intent(Intent.ACTION_PICK)
        // pickImage.addCategory(Intent.CATEGORY_OPENABLE)
        pickImage.type = "image/*"
        // startActivityForResult(getCropIntent(pickImage), CHOICE_PICK_GALLERY)
        startActivityForResult(pickImage, CHOICE_PICK_GALLERY)
    }

    private fun chooseImage() {

        if (Build.VERSION.SDK_INT >= 23) {
            chooseImageDynamicPermission()
        } else {
            chooseImageStaticPermission()
        }
    }

    private fun chooseImageStaticPermission() {
        pickImageAndCrop()
    }

    private fun chooseImageDynamicPermission() {
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

    @Suppress("unused")
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
            if (bm != null) {
                hasImage = true
                iv_image.setImageBitmap(bm)
            }
        }
    }

    private fun saveChanges() {
        // Create note
        item.header = header_edit.text.toString()
        item.content = content_edit.text.toString()

        // Pass note to intent
        val resultIntent = Intent()
        resultIntent.putExtra(MainActivity.RECEIVE_NOTE_ITEM, item)
        // Finish
        setResult(RESULT_OK, resultIntent)
        finish()
    }


    @Suppress("unused")
    private fun discardChanges() {
        // finish, discard all changes
        setResult(Activity.RESULT_CANCELED)
        finish()
    }

    override fun onBackPressed() {
        // If there are changes made to the notes
        /*if (itemChanged()) {
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
        }*/

        saveChanges()
    }

    @Suppress("unused")
    private fun itemChanged(): Boolean {
        return item.header == header_edit.text.toString() &&
                item.content == content_edit.text.toString() &&
                item.imageName == tempImageName &&
                item.eventTime == c.timeInMillis
    }

    private fun setAlarm() {
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

        alarmManager.set(
                AlarmManager.RTC_WAKEUP,
                item.eventTime,
                pendingIntent)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {

        if (resultCode != Activity.RESULT_OK) return

        when (requestCode) {
            CHOICE_PICK_GALLERY -> {
                // val chosenPic = getBitmapFromData(data!!)
                val photoUri = data!!.data
                val cropIntent = Intent(this, CropActivity::class.java)
                cropIntent.putExtra("photoUri", photoUri.toString())
                startActivityForResult(cropIntent, FINISH_IMAGE_CROP)
            }
            FINISH_IMAGE_CROP -> {
                if (data == null) return

                // val imageName = UUID.randomUUID().toString()
                val storage = NoteDataStorage()
                val croppedImageName = data.getStringExtra("croppedImageName")
                val croppedImage = storage.loadImage(this, croppedImageName) ?: return
                // storage.saveImage(this, croppedImage, imageName)

                // if previously hasImage, delete old image
                if (hasImage) storage.deleteImage(this, item.imageName)

                hasImage = true
                item.imageName = croppedImageName
                iv_image.visibility = View.VISIBLE
                iv_image.setImageBitmap(croppedImage)
            }
        }

        /*
        if (resultCode == RESULT_OK && requestCode == CHOICE_PICK_GALLERY) {
            val chosenPic = getBitmapFromData(data!!)
            val storage = NoteDataStorage()
            val imageName = UUID.randomUUID().toString()
            storage.saveImage(this, chosenPic, imageName)
            item.imageName = imageName
            iv_image.setImageBitmap(chosenPic)
        }
        */

        super.onActivityResult(requestCode, resultCode, data)
    }

    @Suppress("unused")
    private fun getBitmapFromData(data: Intent): Bitmap {
        val photoUri = data.data
        Log.d("b-note", data.data.toString())
        val inputStream = contentResolver.openInputStream(photoUri)
        val bufferedInputStream = BufferedInputStream(inputStream)
        return BitmapFactory.decodeStream(bufferedInputStream)
    }
}
