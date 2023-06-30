package com.example.dlsandroidredesign.domain.usecase

import com.example.dlsandroidredesign.domain.DLSRepository
import com.example.dlsandroidredesign.domain.entity.LocationObject
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

class GetLocationInfoUseCase @Inject constructor(private val dlsRepository: DLSRepository) {
    suspend operator fun invoke(): StateFlow<LocationObject> {
        val locationObject = dlsRepository.getLocationUpdate()
        return locationObject
    }
}
