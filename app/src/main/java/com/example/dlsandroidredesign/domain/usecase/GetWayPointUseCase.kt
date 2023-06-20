package com.example.dlsandroidredesign.domain.usecase

import com.example.dlsandroidredesign.domain.DLSRepository
import com.google.gson.JsonObject
import javax.inject.Inject

class GetWayPointUseCase @Inject constructor(
    private val dlsRepository: DLSRepository
) {

    suspend operator fun invoke(bean: JsonObject?): String? {
        return dlsRepository.getWayPoint()
    }
}