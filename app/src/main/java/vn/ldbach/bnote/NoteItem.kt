package vn.ldbach.bnote

import org.json.JSONObject
import java.io.Serializable
import java.util.*

/**
 * Abstract Note Item
 */
abstract class NoteItem(internal var header: String = "",
                        internal var content: String = "") : Serializable {

    val uuid = UUID.randomUUID().toString()
    internal var imageName = ""


    // This variable is used to indicate whether the note
    // has an alarm, and helps re-set the alarm if alarm
    // was scheduled during off time
    internal var hasAlarm = false

    // This variable gives information about the upcoming
    // event of current note
    internal var eventTime: Long = Calendar.getInstance().timeInMillis

    // This variable gives information about how early
    // before the event should the notification fire
    internal var earlyNotifyTime: EarlyNotifyTime = EarlyNotifyTime.NO_NOTIFY

    // This variable is used when alarm is fired, resetting
    // alarm for next occasion, i.e: next year for once
    // a year event
    internal var alarmInterval: AlarmInterval = AlarmInterval.NO_REPEAT

    abstract fun toJSON(): JSONObject
}