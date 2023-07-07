package com.example.dlsandroidredesign.domain.usecase

import com.example.dlsandroidredesign.domain.DLSRepository
import javax.inject.Inject

class CreateTextUseCase @Inject constructor(
    private val getCheckBoxUseCase: GetCheckBoxUseCase,
    private val dlsRepository: DLSRepository
    ) {

}