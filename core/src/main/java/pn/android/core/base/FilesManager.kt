package pn.android.core.base

import android.content.Context
import okhttp3.ResponseBody
import timber.log.Timber
import java.io.*
import pn.android.core.base.FilesManager.TypeEnum.*


/**
 * Functionality for saving files in the phone memory to the application package
 * and uploading files from the phone memory from the application package.
 */
object FilesManager {
    private const val VIDEOS_PATH_DIR = "videos/"
    private const val IMAGES_PATH_DIR = "images/"
    private const val MIME_TYPE = "image/*"

    /**
     * Enum class with possible file types.
     */
    enum class TypeEnum(val type: String) {
        MP_FOUR("mp4"),
        PNG("png"),
        JPEG("jpeg"),
        JPG("jpg")
    }

    /**
     * The function of writing a file to the phone's memory.
     */
    fun writeToDisk(
        context: Context,
        filename: String,
        body: ResponseBody
    ) {
        var resultFile: File? = null
        var mimeType: String? = MIME_TYPE
        val splitName: Array<String> = splitFileName(filename)
        when (splitName[1].substringAfterLast(".")) {
            MP_FOUR.type -> {
                mimeType = MP_FOUR.type
            }
            PNG.type -> {
                mimeType = PNG.type
            }
            JPEG.type -> {
                mimeType = JPEG.type
            }
            JPG.type -> {
                mimeType = JPG.type
            }
        }
        try {
            val dir = if (mimeType == MP_FOUR.type) {
                File(context.filesDir, VIDEOS_PATH_DIR)
            } else {
                File(context.filesDir, IMAGES_PATH_DIR)
            }
            if (!dir.exists()) {
                dir.mkdirs()
            }
            resultFile = File(dir, filename.substringAfterLast("/"))
            var inputStream: InputStream? = null
            var outputStream: OutputStream? = null
            try {
                val fileReader = ByteArray(4096)
                inputStream = body.byteStream()
                outputStream = FileOutputStream(resultFile)
                while (true) {
                    val read = inputStream.read(fileReader)
                    if (read == -1) {
                        break
                    }
                    outputStream.write(fileReader, 0, read)
                }
                outputStream.flush()
            } catch (e: IOException) {
                Timber.d("Error ${e.message}")
            } finally {
                inputStream?.close()
                outputStream?.close()
            }
        } catch (_: IOException) {

        }
    }

    /**
     * Function to determine the file name.
     */
    private fun splitFileName(fileName: String): Array<String> {
        var name = fileName
        var extension = ""
        val i = fileName.lastIndexOf(".")
        if (i != -1) {
            name = fileName.substring(0, i)
            extension = fileName.substring(i)
        }
        return arrayOf(name, extension)
    }

    /**
     * Function to retrieve saved file from phone memory.
     */
    fun getFileFromDisk(context: Context, url: String): File? {
        var path = ""
        when (url.substringAfterLast(".")) {
            MP_FOUR.type -> {
                path = context.filesDir.path + "/$VIDEOS_PATH_DIR"
            }
            PNG.type -> {
                path = context.filesDir.path + "/$IMAGES_PATH_DIR"
            }
            JPEG.type -> {
                path = context.filesDir.path + "/$IMAGES_PATH_DIR"
            }
            JPG.type -> {
                path = context.filesDir.path + "/$IMAGES_PATH_DIR"
            }
        }
        if (path.isEmpty()) {
            return null
        }
        val directory = File(path)
        if (!directory.listFiles().isNullOrEmpty()) {
            for (f in directory.listFiles()!!) {
                if(f.path.contains(url.substringAfterLast("/"))) {
                    return f
                }
            }
        } else {
            return null
        }
        return null
    }
}