package pn.android.core.extensions

import android.content.Context
import android.net.Uri
import android.webkit.MimeTypeMap
import timber.log.Timber
import java.io.File
import java.io.FileOutputStream
import java.util.*

fun Uri.getFileExtension(context: Context): String? {
    val fileType: String? = context.contentResolver.getType(this)
    return MimeTypeMap.getSingleton().getExtensionFromMimeType(fileType)
}

fun Uri.toFile(context: Context, name: String? = null, ext: String? = null): File? {
    try {
        val fileExtension = ext ?: getFileExtension(context)
        val fileName = (name ?: UUID.randomUUID().toString()) + (if (fileExtension != null) ".$fileExtension" else "")

        val tempFile = File(if (ext == null) context.filesDir else context.cacheDir, fileName)
        tempFile.createNewFile()

        context.contentResolver.openInputStream(this).use { input ->
            val outputStream = FileOutputStream(tempFile)
            outputStream.use { output ->
                input?.let {
                    val buffer = ByteArray(4 * 1024)
                    while (true) {
                        val byteCount = it.read(buffer)
                        if (byteCount < 0) break
                        output.write(buffer, 0, byteCount)
                    }
                    output.flush()
                }
            }
        }

        return tempFile
    } catch (e: Exception) {
        Timber.e(e)
        return null
    }
}