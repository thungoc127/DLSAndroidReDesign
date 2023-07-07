package com.example.dlsandroidredesign.domain.usecase

import android.content.ContentResolver
import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import com.example.dlsandroidredesign.domain.entity.PhotoSize
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.first
import java.io.File
import java.io.IOException
import java.io.OutputStream
import javax.inject.Inject

class AddTextOnImageAndSave @Inject constructor(
    @ApplicationContext private val context: Context,
    private val uploadSize: GetUploadSize,
    private val photoSize: GetPhotoSize,
    private val getCusText: GetCusText,
    private val autoUploadStatus: GetAutoUploadStatus,

    ) {
    private val contentResolver: ContentResolver = context.contentResolver
    suspend operator fun invoke(savedUriCapture: Uri,fileNameCapture:String,locationInfoLeft:String,locationInfoRight:String): Uri {
        val inputStream = context.contentResolver.openInputStream(savedUriCapture)
        var bitmap = BitmapFactory.decodeStream(inputStream)
        var bitmapConfig = bitmap.config
        if (bitmapConfig == null) {
            bitmapConfig = Bitmap.Config.ARGB_8888
        }
        bitmap = bitmap.copy(bitmapConfig, true)
//         bitmap = scaleDown(bitmap,300f,true)
        val canvas = Canvas(bitmap)
        val paint = Paint().apply {
            color = android.graphics.Color.WHITE
            textSize = 80f
            textAlign = Paint.Align.LEFT
        }
        val paintRight = Paint().apply {
            color = android.graphics.Color.WHITE
            textSize = 80f
            textAlign = Paint.Align.RIGHT
        }
        val xleft = 100f
        var yleft = 120f
        val locationInfoLeftnew = locationInfoLeft.split("\n")
        locationInfoLeftnew.forEach { line ->
            // Process each line
            // Example: Print each line
            canvas.drawText(line, xleft, yleft, paint)
            yleft += 140f
        }

        val xright = (bitmap.width - 20).toFloat()
        var yright = 120f
        val locationInfoRightnew = locationInfoRight.split("\n")
        locationInfoRightnew.forEach { line ->
            // Process each line
            // Example: Print each line
            canvas.drawText(line, xright, yright, paintRight)
            yright += 140f
        }

        val cusText = getCusText.invoke().first()

        val textBounds = Rect()
        paint.getTextBounds(cusText, 0, cusText.length, textBounds)

        val paintMid = Paint().apply {
            color = android.graphics.Color.WHITE
            textSize = 80f
            textAlign = Paint.Align.LEFT
        }
        val xMid = (bitmap.width - paint.measureText(cusText)) / 2
        val yMid = bitmap.height - 60f

        canvas.drawText(cusText, xMid, yMid, paintMid)

        val existingImageFile = File(context.filesDir, fileNameCapture)

        if (existingImageFile.exists()) {
            existingImageFile.delete()
        }

        val fileName = "IMG_${System.currentTimeMillis()}.JPEG"
        val mimeType = "image/jpeg"

// Insert the image to the MediaStore
        val values = ContentValues().apply {
            put(MediaStore.Images.Media.TITLE, fileName)
            put(MediaStore.Images.Media.MIME_TYPE, mimeType)
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.P) {
                put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/DLSPhotoCompose")
            }
        }

// Get the content resolver
        val resolver: ContentResolver = context.contentResolver

// Insert the image and get its content URI
        val imageUri: Uri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)!!

// Open an output stream to write the bitmap data
        try {
            imageUri.let { uri ->
                val outputStream: OutputStream? = resolver.openOutputStream(uri)

                // Compress the bitmap to JPEG format and write it to the output stream
                val autoUploadStatus = autoUploadStatus.invoke().first()

                if (autoUploadStatus) {
                    val size = PhotoSize()
                    when (uploadSize.invoke().first()) {
                        "Tiny" -> bitmap.compress(Bitmap.CompressFormat.JPEG, size.tiny, outputStream)
                        "Small" -> bitmap.compress(Bitmap.CompressFormat.JPEG, size.small, outputStream)
                        "Medium" -> bitmap.compress(Bitmap.CompressFormat.JPEG, size.medium, outputStream)
                        "Large" -> bitmap.compress(Bitmap.CompressFormat.JPEG, size.large, outputStream)
                        "Original" -> bitmap.compress(Bitmap.CompressFormat.JPEG, size.original, outputStream)
                    }
                } else {
                    val size = PhotoSize()
                    when (photoSize.invoke().first()) {
                        "Tiny" -> bitmap.compress(Bitmap.CompressFormat.JPEG, size.tiny, outputStream)
                        "Small" -> bitmap.compress(Bitmap.CompressFormat.JPEG, size.small, outputStream)
                        "Medium" -> bitmap.compress(Bitmap.CompressFormat.JPEG, size.medium, outputStream)
                        "Large" -> bitmap.compress(Bitmap.CompressFormat.JPEG, size.large, outputStream)
                        "Original" -> bitmap.compress(Bitmap.CompressFormat.JPEG, size.original, outputStream)
                    }
                }

                // Flush and close the output stream
                outputStream?.flush()
                outputStream?.close()

                // Optionally, you can display a toast message to indicate the image has been saved
                Toast.makeText(context, "Image saved to gallery", Toast.LENGTH_SHORT).show()
                deleteImageByName(fileNameCapture,contentResolver)
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return imageUri
    }
}

private fun deleteImageByName (imageName: String,contentResolver:ContentResolver) {
    val selection = "${MediaStore.Images.Media.DISPLAY_NAME} = ?"
    val selectionArgs = arrayOf(imageName)
    val deletedRows = contentResolver.delete(
        MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
        selection,
        selectionArgs
    )
    if (deletedRows > 0) {
        Log.d("deleteRows", "Morethan1picneedtodelel")
    } else {
    }
}