package vn.ldbach.bnote

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.app.Dialog
import android.app.TimePickerDialog
import android.content.Context
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import kotlinx.android.synthetic.main.dialog_schedule.*
import java.util.*

/**
 * Create a dialog for recording the schedule and help in setting alarm
 */
class ScheduleDialog(private val item: NoteItem, private val mContext: Context, private val observer: ScheduleObserver) :
        Dialog(mContext), View.OnClickListener, AdapterView.OnItemSelectedListener {
    override fun onNothingSelected(parent: AdapterView<*>?) {
    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, pos: Int, id: Long) {
        if (parent?.id == R.id.spinner_notification) {
            // Toast.makeText(mContext, "Notification option changed", Toast.LENGTH_SHORT).show()
            item.earlyNotifyTime = EarlyNotifyTime.values()[pos]
        } else if (parent?.id == R.id.spinner_repeat) {
            // Toast.makeText(mContext, "Notification repeat changed", Toast.LENGTH_SHORT).show()
            item.alarmInterval = AlarmInterval.values()[pos]
        }
    }

    private fun showPickDate() {
        val c = Calendar.getInstance()
        c.timeInMillis = item.eventTime

        val datePickerCallback = DatePickerDialog.OnDateSetListener { _, year, monthOfYear, dayOfMonth ->
            c.apply {
                // timeInMillis = item.eventTime
                set(year, monthOfYear, dayOfMonth)

                // Update UI
                dialog_tv_date.text = mContext.resources.getString(R.string.date_dialog, dayOfMonth, monthOfYear + 1, year)

                // Save changes to temp vars (expected Item to be passed by reference so
                // update to item can be directly reflected when OK is clicked)
                item.eventTime = c.timeInMillis
            }
        }

        DatePickerDialog(
                mContext,
                datePickerCallback,
                c.get(Calendar.YEAR),
                c.get(Calendar.MONTH),
                c.get(Calendar.DAY_OF_MONTH)
        ).show()
    }

    private fun showPickTime() {
        val c = Calendar.getInstance()
        c.timeInMillis = item.eventTime

        val timePickerCallback = TimePickerDialog.OnTimeSetListener { _, hourOfDay, minute ->
            c.apply {
                // timeInMillis = item.eventTime
                set(Calendar.HOUR_OF_DAY, hourOfDay)
                set(Calendar.MINUTE, minute)

                // Now update view
                dialog_tv_time.text = mContext.resources.getString(R.string.time_dialog, hourOfDay, minute)


                // Save changes to temp vars (expected Item to be passed by reference so
                // update to item can be directly reflected when OK is clicked)
                item.eventTime = c.timeInMillis
            }
        }

        TimePickerDialog(
                mContext,
                timePickerCallback,
                c.get(Calendar.HOUR_OF_DAY),
                c.get(Calendar.MINUTE),
                false
        ).show()
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.dialog_tv_date -> {
                showPickDate()
            }
            R.id.dialog_tv_time -> {
                showPickTime()
            }
            R.id.btn_done_schedule -> {
                observer.onFinish(item)
                // dismiss()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        requestWindowFeature(Window.FEATURE_CUSTOM_TITLE)
        setContentView(R.layout.dialog_schedule)
        //       window.setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.string.schedule_dialog_title)
        setupUi()
        setupButton()
    }

    private fun setupButton() {
        btn_done_schedule.setOnClickListener(this)
    }

    private fun setupUi() {
        setupDateTime()
        setupNotifySpinner()
        setupRepeatSpinner()
    }

    @SuppressLint("SimpleDateFormat")
    private fun setupDateTime() {
        val totalMs = item.eventTime
        val c = Calendar.getInstance()
        c.timeInMillis = totalMs

        val y = c.get(Calendar.YEAR)
        val m = c.get(Calendar.MONTH) + 1
        val d = c.get(Calendar.DAY_OF_MONTH)

        dialog_tv_date.text =
                mContext.resources.getString(R.string.date_dialog, d, m, y)


        val h = c.get(Calendar.HOUR_OF_DAY)
        val min = c.get(Calendar.MINUTE)
        dialog_tv_time.text = mContext.resources.getString(R.string.time_dialog, h, min)


        dialog_tv_date.setOnClickListener(this)
        dialog_tv_time.setOnClickListener(this)
    }

    private fun setupNotifySpinner() {
        val adapter = ArrayAdapter.createFromResource(
                mContext, R.array.notify_in_advance_arrays, android.R.layout.simple_spinner_item)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner_notification.adapter = adapter
        spinner_notification.onItemSelectedListener = this

        spinner_notification.setSelection(EarlyNotifyTime.values().indexOf(item.earlyNotifyTime))
    }

    private fun setupRepeatSpinner() {
        val adapter = ArrayAdapter.createFromResource(
                mContext, R.array.repeat_frequency_arrays, android.R.layout.simple_spinner_item)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner_repeat.adapter = adapter
        spinner_repeat.onItemSelectedListener = this

        spinner_repeat.setSelection(AlarmInterval.values().indexOf(item.alarmInterval))
    }
}