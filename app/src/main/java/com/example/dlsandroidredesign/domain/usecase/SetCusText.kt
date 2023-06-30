package com.example.dlsandroidredesign.domain.usecase

import com.example.dlsandroidredesign.domain.DLSRepository
import javax.inject.Inject

class SetCusText @Inject constructor(private val dlsRepository: DLSRepository) {
    suspend operator fun invoke(cusText: String) {
        return dlsRepository.setCusText(cusText)
    }
}
