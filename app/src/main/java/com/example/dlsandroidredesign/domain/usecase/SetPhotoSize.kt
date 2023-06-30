package com.example.dlsandroidredesign.domain.usecase

import com.example.dlsandroidredesign.domain.DLSRepository
import javax.inject.Inject

class SetPhotoSize @Inject constructor(
    private val dlsRepository: DLSRepository
) {

    suspend operator fun invoke(photoSize: String) {
        return dlsRepository.setPhotoSize(photoSize)
    }
}
