package vn.ldbach.bnote

import org.json.JSONObject

/**
 * Simple note with text only
 */
class TextNoteItem(header: String = "",
                   content: String = "") : NoteItem(header, content) {

    constructor(jsonObject: JSONObject) : this() {
        header = jsonObject["header"] as String
        content = jsonObject["content"] as String
        imageName = jsonObject["imageName"] as String


        eventTime = jsonObject["event_time"] as Long
        alarmInterval = AlarmInterval.valueOf(jsonObject["interval"] as String)
        earlyNotifyTime = EarlyNotifyTime.valueOf(jsonObject["early"] as String)
    }

    override fun toJSON(): JSONObject {
        val jsonObject = JSONObject()
        jsonObject.put("header", header)
        jsonObject.put("content", content)
        jsonObject.put("imageName", imageName)
        jsonObject.put("uuid", uuid)

        jsonObject.put("event_time", eventTime)
        jsonObject.put("interval", alarmInterval.name)
        jsonObject.put("early", earlyNotifyTime.name)
        return jsonObject
    }
}