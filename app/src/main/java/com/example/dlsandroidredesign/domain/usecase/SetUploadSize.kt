package com.example.dlsandroidredesign.domain.usecase

import com.example.dlsandroidredesign.domain.DLSRepository
import javax.inject.Inject


class SetUploadSize @Inject constructor(
    private val dlsRepository: DLSRepository
) {

    suspend operator fun invoke(uploadSize: String) {
        return dlsRepository.setUploadSize(uploadSize)
    }
}