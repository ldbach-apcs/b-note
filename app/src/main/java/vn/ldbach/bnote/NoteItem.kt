package vn.ldbach.bnote

import android.graphics.drawable.Drawable
import org.json.JSONObject
import java.io.Serializable
import java.util.*

/**
 * Abstract Note Item
 */
abstract class NoteItem(internal var header: String = "",
                        internal var content: String = "",
                        private var picture: Drawable? = null) : Serializable {

    val uuid = UUID.randomUUID().toString()

    open fun toJSON(): JSONObject {
        val jsonObject = JSONObject()
        jsonObject.put("header", header)
        jsonObject.put("content", content)
        return jsonObject
    }

}