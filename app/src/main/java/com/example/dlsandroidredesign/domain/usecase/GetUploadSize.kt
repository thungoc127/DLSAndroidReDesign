package com.example.dlsandroidredesign.domain.usecase

import com.example.dlsandroidredesign.domain.DLSRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetUploadSize @Inject constructor(
    private val dlsRepository: DLSRepository
) {

    operator fun invoke(): Flow<String> {
        return dlsRepository.getUploadSize()
    }
}