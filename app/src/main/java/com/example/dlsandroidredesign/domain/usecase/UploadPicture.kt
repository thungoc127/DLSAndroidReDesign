package com.example.dlsandroidredesign.domain.usecase

import com.example.dlsandroidredesign.domain.DLSRepository
import okhttp3.MultipartBody
import javax.inject.Inject

class UploadPhoto @Inject constructor(private val dlsRepository: DLSRepository) {
    suspend operator fun invoke(apiKey: String, wayPointId: String, photo: MultipartBody.Part?) {
        dlsRepository.uploadPhoto(apiKey, wayPointId, photo)
    }
}
