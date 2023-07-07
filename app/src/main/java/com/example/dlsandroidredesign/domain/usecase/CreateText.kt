package com.example.dlsandroidredesign.domain.usecase

import android.util.Log
import com.example.dlsandroidredesign.data.CheckBoxKey
import com.example.dlsandroidredesign.domain.entity.LocationObject
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class CreateText @Inject constructor(
    private val getCheckBoxUsecase: GetCheckBoxUseCase
) {
    suspend operator fun invoke(locationObject: LocationObject?): List<String> {
        val locationObjectNullable = locationObject ?: LocationObject()
        val locationInfoList = mutableListOf<String>()
        val checkBoxList = getCheckBoxUsecase.invoke().first()
        Log.d("createText", "locationObject: $locationObjectNullable")
        val latlon: String = if (checkBoxList.latLon) { "Lat/lon\n${locationObjectNullable.lat}\n${locationObjectNullable.lon}\n" } else { "" }
        val elevationNew: String = if (checkBoxList.elevation) { "Eleve: ${CheckBoxKey.Elevation} m" } else { "" }
        val gridLocationNew: String = if (checkBoxList.gridLocation) { "${locationObjectNullable.gridLocation}\n" } else { "" }
        val distanceNew: String = if (checkBoxList.distance) { "${locationObjectNullable.distance}\n" } else { "" }
        val utmCoordinateNew: String = if (checkBoxList.utmCoordinate) { "${locationObjectNullable.utmCoordinate}\n" } else { "" }
        Log.d("createText", "info: ${"$latlon$elevationNew$gridLocationNew$distanceNew$utmCoordinateNew"}")
        val a = "$latlon$elevationNew$gridLocationNew$distanceNew$utmCoordinateNew"
        locationInfoList.add(a)

        val bearingNew: String = if (checkBoxList.bearing) { locationObjectNullable.bearing + "\n" } else { "" }
        val addressNew: String = if (checkBoxList.address) { locationObjectNullable.address + "\n" } else { "" }
        val dateTimeFormattedNew: String = if (checkBoxList.date) { locationObjectNullable.date } else { "" }
        val b = "$bearingNew$addressNew$dateTimeFormattedNew"
        locationInfoList.add(b)
        return locationInfoList
    }
}
