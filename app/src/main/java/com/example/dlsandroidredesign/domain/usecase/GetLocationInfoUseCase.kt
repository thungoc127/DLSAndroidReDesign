package com.example.dlsandroidredesign.domain.usecase

import com.example.dlsandroidredesign.domain.DLSRepository
import com.example.dlsandroidredesign.domain.entity.LocationObject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class GetLocationInfoUseCase @Inject constructor(
    private val dlsRepository: DLSRepository
) {

    operator fun invoke(): Flow<LocationObject> {
        return dlsRepository.getLocation().map { location ->
            dlsRepository.getCompleteAddress(location)
        }
    }
}
