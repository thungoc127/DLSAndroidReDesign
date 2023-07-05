package com.example.dlsandroidredesign.domain.usecase

import com.example.dlsandroidredesign.data.local.CheckBoxDataStore
import com.example.dlsandroidredesign.domain.DLSRepository
import com.example.dlsandroidredesign.domain.entity.CheckBox
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class CreateTextUseCase @Inject constructor(
    private val getCheckBoxUseCase: GetCheckBoxUseCase,
    private val dlsRepository: DLSRepository
    ) {
    suspend operator fun invoke(): Flow<CheckBox> {
        val checkBox = getCheckBoxUseCase().first()
        val createText = dlsRepository.createText(checkBox)
    }
}