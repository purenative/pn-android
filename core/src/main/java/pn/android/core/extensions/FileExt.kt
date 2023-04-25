package pn.android.core.extensions

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.media.ExifInterface
import java.io.File
import java.io.FileOutputStream
import java.util.*
import kotlin.math.min

fun File.scaleImage(context: Context, scaleTo: Int = 64): File? {

    val bmOptions = BitmapFactory.Options().apply {
        inJustDecodeBounds = false
    }

    val originalBitmap = BitmapFactory.decodeFile(absolutePath,  bmOptions) ?: return null
    val scaledBitmap = Bitmap.createScaledBitmap(originalBitmap, scaleTo, scaleTo, true)
    val rotatedBitmap = modifyOrientation(scaledBitmap, absolutePath)

    val tempFile = File(context.filesDir, "${UUID.randomUUID()}.jpg")
    tempFile.createNewFile()

    FileOutputStream(tempFile).use {
        rotatedBitmap.compress(Bitmap.CompressFormat.JPEG, 75, it)
        rotatedBitmap.recycle()
    }

    return tempFile

}

fun File.resizeImage(scaleTo: Int = 128): File? {
    val bmOptions = BitmapFactory.Options().apply {
        inJustDecodeBounds = true
    }
    BitmapFactory.decodeFile(absolutePath, bmOptions)
    val photoW = bmOptions.outWidth
    val photoH = bmOptions.outHeight

    // Determine how much to scale down the image
    val scaleFactor = min(photoW / scaleTo, photoH / scaleTo)

    bmOptions.inJustDecodeBounds = false
    bmOptions.inSampleSize = scaleFactor

    val resized = BitmapFactory.decodeFile(absolutePath, bmOptions) ?: return null
    val rotated = modifyOrientation(resized, absolutePath)


    outputStream().use {
        rotated.compress(Bitmap.CompressFormat.JPEG, 75, it)
        rotated.recycle()
    }

    return this
}

fun modifyOrientation(bitmap: Bitmap, absolutePath: String): Bitmap {
    val ei = ExifInterface(absolutePath)
    return when (ei.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL)) {
        ExifInterface.ORIENTATION_ROTATE_90 -> rotateImage(bitmap, 90f)
        ExifInterface.ORIENTATION_ROTATE_180 -> rotateImage(bitmap, 180f)
        ExifInterface.ORIENTATION_ROTATE_270 -> rotateImage(bitmap, 270f)
        ExifInterface.ORIENTATION_FLIP_HORIZONTAL -> flipImage(bitmap, true, false)
        ExifInterface.ORIENTATION_FLIP_VERTICAL -> flipImage(bitmap, false, true)
        else -> bitmap
    }
}

private fun rotateImage(bitmap: Bitmap, degrees: Float): Bitmap {
    val matrix = Matrix()
    matrix.postRotate(degrees)
    return Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
}

private fun flipImage(bitmap: Bitmap, horizontal: Boolean, vertical: Boolean): Bitmap {
    val matrix = Matrix()
    matrix.preScale((if (horizontal) -1 else 1).toFloat(), (if (vertical) -1 else 1).toFloat())
    return Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
}