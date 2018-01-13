package vn.ldbach.bnote

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
 * Created by Duy-Bach on 1/12/2018.
 */
class ScheduleDialog(private val item: NoteItem, private val mContext: Context) :
        Dialog(mContext), View.OnClickListener, AdapterView.OnItemSelectedListener {
    override fun onNothingSelected(parent: AdapterView<*>?) {
    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, pos: Int, id: Long) {
        if (view?.id == R.id.spinner_notification) {
            // Toast.makeText(mContext, "Notification option changed", Toast.LENGTH_SHORT).show()
        } else {
            // Toast.makeText(mContext, "Notification repeat changed", Toast.LENGTH_SHORT).show()
        }
    }

    private fun showPickDate() {
        val c = Calendar.getInstance()

        val datePickerCallback = DatePickerDialog.OnDateSetListener { _, year, monthOfYear, dayOfMonth ->
            c.set(year, monthOfYear, dayOfMonth)

            // Update UI


            // Save changes to temp vars (expected Item to be passed by reference so
            // update to item can be directly reflected when OK is clicked)

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
        val timePickerCallback = TimePickerDialog.OnTimeSetListener { _, hourOfDay, minute ->
            c.apply {
                set(Calendar.HOUR_OF_DAY, hourOfDay)
                set(Calendar.MINUTE, minute)

                // Now update view


                // Save changes to temp vars (expected Item to be passed by reference so
                // update to item can be directly reflected when OK is clicked)
                item.hasAlarm = true
                item.nextAlarm = c.timeInMillis
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

    }

    private fun setupUi() {
        setTitle(R.string.schedule_dialog_title)
        setupDateTime()
        setupNotifySpinner()
        setupRepeatSpinner()
    }

    private fun setupDateTime() {
        val d = item.nextAlarm
        dialog_tv_date.text = "$d"

        val t = item.nextAlarm
        dialog_tv_time.text = "$t"

        dialog_tv_date.setOnClickListener(this)
        dialog_tv_time.setOnClickListener(this)
    }

    private fun setupNotifySpinner() {
        val adapter = ArrayAdapter.createFromResource(
                mContext, R.array.notify_in_advance_arrays, android.R.layout.simple_spinner_item)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner_notification.adapter = adapter
        spinner_notification.onItemSelectedListener = this
    }

    private fun setupRepeatSpinner() {
        val adapter = ArrayAdapter.createFromResource(
                mContext, R.array.repeat_frequency_arrays, android.R.layout.simple_spinner_item)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner_repeat.adapter = adapter
        spinner_repeat.onItemSelectedListener = this
    }
}