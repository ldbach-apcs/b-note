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
    // notification of current note
    internal var nextAlarm: Long = 0

    // This variable is used when alarm is fired, resetting
    // alarm for next occasion, i.e: next year for once
    // a year event
    internal var alarmInterval: Long = Long.MAX_VALUE

    abstract fun toJSON(): JSONObject
}