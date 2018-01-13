package vn.ldbach.bnote

import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.widget.Toast
import org.json.JSONArray
import org.json.JSONTokener
import java.io.*


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

            // Log.d("b-note", builder.toString())

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
                // Log.d("b-note", "Note: ${note.header}")
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

    fun saveImage(context: Context, image: Bitmap, imageName: String) {
        val fileOutputStream = context.openFileOutput(imageName, MODE_PRIVATE)
        image.compress(Bitmap.CompressFormat.PNG, 100, fileOutputStream)
        fileOutputStream.close()
    }

    fun loadImage(context: Context, imageName: String): Bitmap? {
        var bitmap: Bitmap? = null
        val fiStream: FileInputStream
        try {
            fiStream = context.openFileInput(imageName)
            bitmap = BitmapFactory.decodeStream(fiStream)
            fiStream.close()
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return bitmap
    }

    fun deleteImage(context: Context, imageName: String) {
        Toast.makeText(context, "Delete old image in background", Toast.LENGTH_SHORT).show()
        context.deleteFile(imageName)
    }
}