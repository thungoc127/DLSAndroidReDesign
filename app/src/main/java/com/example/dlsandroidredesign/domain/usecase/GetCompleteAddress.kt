package com.example.dlsandroidredesign.domain.usecase

import android.location.Location
import com.arcgismaps.mapping.layers.FeatureLayer
import com.example.dlsandroidredesign.domain.DLSRepository
import javax.inject.Inject

class GetCompleteAddress @Inject constructor(private val dlsRepository: DLSRepository) {
    suspend operator fun invoke(location: Location?,sectionLayer:FeatureLayer?) {
        }
    }


