package vn.ldbach.bnote

import android.content.Context
import android.util.Log
import org.json.JSONArray
import org.json.JSONTokener
import java.io.BufferedReader
import java.io.FileNotFoundException
import java.io.InputStreamReader
import java.io.OutputStreamWriter

/**
 *
 */
class NoteDataStorage {
    private val fileName = "bnote_notes"

    fun loadNotes(context: Context): ArrayList<NoteItem> {
        val items = ArrayList<NoteItem>()

        try {
            val fileInputStream = context.openFileInput(fileName)
            val builder = StringBuilder()
            var line: String?
            val bufferReader = BufferedReader(InputStreamReader(fileInputStream))

            // Read the whole input file
            while (true) {
                line = bufferReader.readLine()
                if (line == null) break

                builder.append(line)
            }

            Log.d("b-note", builder.toString())

            // Split into many JsonObject
            val jsonArray = JSONTokener(builder.toString()).nextValue() as JSONArray
            (0 until jsonArray.length()).mapTo(items) { TextNoteItem(jsonArray.getJSONObject(it)) }

            fileInputStream.close()
            bufferReader.close()
        } catch (e: FileNotFoundException) {
            // Do nothing because the first time user run app there is no file
        }

        return items
    }

    fun saveNotes(notes: ArrayList<NoteItem>, context: Context) {

        try {
            val jsonArr = JSONArray()
            for (note in notes) {
                Log.d("b-note", "Note: ${note.header}")
                jsonArr.put(note.toJSON())
            }

            val fileOutputStream = context.openFileOutput(fileName, Context.MODE_PRIVATE)
            val outputStreamWriter = OutputStreamWriter(fileOutputStream)
            outputStreamWriter.write(jsonArr.toString())
            outputStreamWriter.close()
            fileOutputStream.close()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }


}