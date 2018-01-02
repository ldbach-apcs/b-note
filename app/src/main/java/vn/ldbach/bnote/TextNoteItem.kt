package vn.ldbach.bnote

import android.graphics.Bitmap
import android.util.Log
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
        Log.d("b-note", "Object loaded: $header")
    }
}