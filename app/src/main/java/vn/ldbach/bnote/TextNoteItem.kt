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
        // Log.d("b-note", "Object loaded: $header")
    }

    override fun toJSON(): JSONObject {
        val jsonObject = JSONObject()
        jsonObject.put("header", header)
        jsonObject.put("content", content)
        jsonObject.put("imageName", imageName)
        jsonObject.put("uuid", uuid)
        return jsonObject
    }
}