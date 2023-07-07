package com.example.dlsandroidredesign.domain.usecase

import com.example.dlsandroidredesign.data.DLSRepositoryImpl
import javax.inject.Inject

class LoadMobileMapPackage @Inject constructor(
    private val dlsRepository: DLSRepositoryImpl
) {
    suspend operator fun invoke() = dlsRepository.loadMobileMapPackage()
}
