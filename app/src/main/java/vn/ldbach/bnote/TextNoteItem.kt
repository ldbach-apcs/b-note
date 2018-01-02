package vn.ldbach.bnote

import android.graphics.Bitmap
import org.json.JSONObject

/**
 * Simple note with text only
 */
class TextNoteItem(header: String = "",
                   content: String = "",
                   picture: Bitmap? = null) : NoteItem(header, content, picture) {

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
        return jsonObject
    }
}