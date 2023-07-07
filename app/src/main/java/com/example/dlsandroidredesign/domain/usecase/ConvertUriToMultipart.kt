package com.example.dlsandroidredesign.domain.usecase

import android.content.Context
import android.net.Uri
import dagger.hilt.android.qualifiers.ApplicationContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.ByteArrayOutputStream
import javax.inject.Inject

class ConvertUriToMultipart @Inject constructor(@ApplicationContext private val context: Context) {
    operator fun invoke(ImageUri: Uri?): MultipartBody.Part {
        val test = context.contentResolver.openInputStream(ImageUri!!)
        val byteBuff = ByteArrayOutputStream()
        val buffSize = 1024
        val buff = ByteArray(buffSize)
        var len = 0
        while (test!!.read(buff).also { len = it } != -1) {
            byteBuff.write(buff, 0, len)
        }
        val requestFileFront: RequestBody =
            byteBuff.toByteArray().toRequestBody("image/png".toMediaTypeOrNull())
        return MultipartBody.Part.createFormData(
            "photodata",
            "picture.png",
            requestFileFront
        )
    }
}
