package com.example.dlsandroidredesign.domain.usecase

import com.example.dlsandroidredesign.domain.DLSRepository
import com.google.gson.JsonObject
import javax.inject.Inject

class GetWayPointId @Inject constructor(private val dlsRepository: DLSRepository) {
    suspend operator fun invoke(apiKey: String?, bean: JsonObject?):String?{
            return dlsRepository.getWayPointId(apiKey,bean)
    }
}