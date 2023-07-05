package com.example.dlsandroidredesign.domain.usecase

import com.example.dlsandroidredesign.domain.DLSRepository
import okhttp3.MultipartBody
import javax.inject.Inject

// TODO: File 'UploadPicture.kt' contains a single class and possibly also extension functions for that class and should be named same after that class 'UploadPhoto.kt'
class UploadPicture @Inject constructor(private val dlsRepository: DLSRepository) {
    suspend operator fun invoke(apiKey: String, wayPointId: String, photo: MultipartBody.Part?) {
        dlsRepository.uploadPhoto(apiKey, wayPointId, photo)
    }
}
