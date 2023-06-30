package com.example.dlsandroidredesign.domain.usecase

import com.example.dlsandroidredesign.domain.DLSRepository
import javax.inject.Inject

class SetCheckBoxUseCase @Inject constructor(private val dlsRepository: DLSRepository) {
    suspend operator fun invoke(checkBoxKey: String, newValue: Boolean) {
        return dlsRepository.setCheckBox(checkBoxKey, newValue)
    }
}
