package jp.co.ta.loaders

import android.content.Context

object FileLoader {
    private const val FILE_NAME = "current_link"
    fun loadFromFile(context: Context): String? = try {
        context.openFileInput(FILE_NAME).bufferedReader().useLines { lines ->
            lines.first()
        }
    } catch (e: Exception) {
        null
    }

    fun uploadToFile(data: String, context: Context, initialData: String) {
        if (loadFromFile(context) == null && initialData !in data) {
            context.openFileOutput(FILE_NAME, Context.MODE_PRIVATE).use { fileOutputStream ->
                fileOutputStream.write(data.toByteArray())
            }
        }
    }
}