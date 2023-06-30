package com.example.dlsandroidredesign.domain.usecase

import android.net.Uri
import com.example.dlsandroidredesign.domain.DLSRepository
import com.example.dlsandroidredesign.domain.entity.LocationObject
import javax.inject.Inject

class InsertImageLocationInfo @Inject constructor(private val dlsRepository: DLSRepository) {
    suspend operator fun invoke(imageUri: Uri, locationInfoObject: LocationObject) {
        dlsRepository.insertImageLocationInfo(imageUri, locationInfoObject)
    }
}
