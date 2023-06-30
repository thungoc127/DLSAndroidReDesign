package com.example.dlsandroidredesign.domain.usecase

import android.net.Uri
import com.example.dlsandroidredesign.domain.DLSRepository
import javax.inject.Inject

class GetAllImages @Inject constructor(private val dlsRepository: DLSRepository) {
    suspend operator fun invoke(): List<Uri> {
        val allImages = dlsRepository.getAllGalleryImages()
        return allImages
    }
}
