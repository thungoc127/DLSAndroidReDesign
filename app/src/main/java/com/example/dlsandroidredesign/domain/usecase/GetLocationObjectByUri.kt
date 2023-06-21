package com.example.dlsandroidredesign.domain.usecase

import android.net.Uri
import com.example.dlsandroidredesign.domain.DLSRepository
import com.example.dlsandroidredesign.domain.entity.LocationObject
import javax.inject.Inject

class GetLocationObjectByUri @Inject constructor(private val dlsRepository: DLSRepository) {
suspend operator fun invoke(uriImage:Uri): LocationObject {
    return  dlsRepository.getLocationObjectByUri(uriImage)
}
}