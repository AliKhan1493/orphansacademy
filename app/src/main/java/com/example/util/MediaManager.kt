package com.example.util

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Log
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream

/**
 * Persistent Media Manager for Orphan's Academy.
 * Ensures student photos are compressed and stored permanently in internal app storage (context.filesDir)
 * rather than temporary camera cache folders, preventing broken images.
 */
object MediaManager {
    private const val TAG = "MediaManager"
    private const val DIRECTORY_STUDENT_PHOTOS = "student_photos"
    private const val JPEG_QUALITY = 85
    private const val MAX_DIMENSION = 800

    fun getPhotosDirectory(context: Context): File {
        val dir = File(context.filesDir, DIRECTORY_STUDENT_PHOTOS)
        if (!dir.exists()) {
            dir.mkdirs()
        }
        return dir
    }

    /**
     * Compresses and permanently saves a captured Bitmap into internal files directory.
     * Returns the permanent file path (e.g. file:///data/user/0/.../student_photos/photo_xyz.jpg).
     */
    fun saveStudentPhotoPermanently(
        context: Context,
        bitmap: Bitmap,
        admissionNo: String,
        existingPath: String? = null
    ): String? {
        return try {
            // Remove previous photo if it exists to prevent disk bloat
            existingPath?.let { deletePhotoIfExists(it) }

            val dir = getPhotosDirectory(context)
            val cleanAdmission = admissionNo.replace("[^a-zA-Z0-9]".toRegex(), "_")
            val fileName = "photo_${cleanAdmission}_${System.currentTimeMillis()}.jpg"
            val targetFile = File(dir, fileName)

            // Scale down if image is too large
            val scaledBitmap = scaleBitmapIfNeeded(bitmap, MAX_DIMENSION)

            FileOutputStream(targetFile).use { out ->
                scaledBitmap.compress(Bitmap.CompressFormat.JPEG, JPEG_QUALITY, out)
                out.flush()
            }

            Log.d(TAG, "Student photo saved permanently at: ${targetFile.absolutePath}")
            targetFile.absolutePath
        } catch (e: Exception) {
            Log.e(TAG, "Failed to save student photo permanently: ${e.message}", e)
            null
        }
    }

    /**
     * Copies and compresses a photo from an external Uri to internal filesDir permanently.
     */
    fun savePhotoFromUriPermanently(
        context: Context,
        sourceUri: Uri,
        admissionNo: String,
        existingPath: String? = null
    ): String? {
        return try {
            val inputStream: InputStream? = context.contentResolver.openInputStream(sourceUri)
            val bitmap = BitmapFactory.decodeStream(inputStream)
            inputStream?.close()
            if (bitmap != null) {
                saveStudentPhotoPermanently(context, bitmap, admissionNo, existingPath)
            } else {
                null
            }
        } catch (e: Exception) {
            Log.e(TAG, "Failed to decode and save photo from Uri: ${e.message}", e)
            null
        }
    }

    fun deletePhotoIfExists(path: String) {
        try {
            val file = File(path)
            if (file.exists()) {
                file.delete()
            }
        } catch (e: Exception) {
            Log.w(TAG, "Could not delete old photo at $path: ${e.message}")
        }
    }

    private fun scaleBitmapIfNeeded(bitmap: Bitmap, maxDim: Int): Bitmap {
        val width = bitmap.width
        val height = bitmap.height
        if (width <= maxDim && height <= maxDim) return bitmap

        val ratio = width.toFloat() / height.toFloat()
        val targetWidth: Int
        val targetHeight: Int
        if (width > height) {
            targetWidth = maxDim
            targetHeight = (maxDim / ratio).toInt()
        } else {
            targetHeight = maxDim
            targetWidth = (maxDim * ratio).toInt()
        }
        return Bitmap.createScaledBitmap(bitmap, targetWidth, targetHeight, true)
    }
}
