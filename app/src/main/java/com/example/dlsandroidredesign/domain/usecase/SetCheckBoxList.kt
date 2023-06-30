package com.example.dlsandroidredesign.domain.usecase

import com.example.dlsandroidredesign.domain.DLSRepository
import javax.inject.Inject

class SetCheckBoxList @Inject constructor(private val dlsRepository: DLSRepository) {
    suspend operator fun invoke(checkBoxKey: String, value: Boolean) {
        return dlsRepository.setCheckBox(checkBoxKey, value)
    }
}
