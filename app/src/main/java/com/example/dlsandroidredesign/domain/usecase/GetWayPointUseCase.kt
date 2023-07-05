package com.example.dlsandroidredesign.domain.usecase

import com.example.dlsandroidredesign.domain.DLSRepository
import javax.inject.Inject

class GetWayPointUseCase @Inject constructor(
    private val dlsRepository: DLSRepository
)
