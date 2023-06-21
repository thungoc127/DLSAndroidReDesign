package com.example.dlsandroidredesign.domain.usecase

import com.example.dlsandroidredesign.data.local.CheckBoxDataStore.Companion.address
import com.example.dlsandroidredesign.data.local.CheckBoxDataStore.Companion.bearing
import com.example.dlsandroidredesign.data.local.CheckBoxDataStore.Companion.cusText
import com.example.dlsandroidredesign.data.local.CheckBoxDataStore.Companion.date
import com.example.dlsandroidredesign.data.local.CheckBoxDataStore.Companion.distance
import com.example.dlsandroidredesign.data.local.CheckBoxDataStore.Companion.elevation
import com.example.dlsandroidredesign.data.local.CheckBoxDataStore.Companion.gridLocation
import com.example.dlsandroidredesign.data.local.CheckBoxDataStore.Companion.latLon
import com.example.dlsandroidredesign.data.local.CheckBoxDataStore.Companion.utmCoordinate
import com.example.dlsandroidredesign.domain.DLSRepository
import com.example.dlsandroidredesign.domain.entity.CheckBox
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class GetCheckBoxUseCase @Inject constructor(private val dlsRepository: DLSRepository) {
    operator fun invoke(): Flow<CheckBox> {
        return dlsRepository.getCheckBox().map { preference ->
            CheckBox(
                latLon = preference[latLon]?:true,
                elevation =preference[elevation]?:true,
                gridLocation = preference[gridLocation]?:true,
                distance = preference[distance]?:true,
                utmCoordinate = preference[utmCoordinate]?:true,
                bearing = preference[bearing]?:true,
                address = preference[address]?:true,
                date = preference[date]?:true,
                cusText = preference[cusText]?:true,

            )
        }
//        return dlsRepository.getCheckboxList().map {
//            CheckBox(
//                latLon = it[0],
//                elevationNew = it[1],
//                gridLocationNew = it[2],
//                bearingNew =it[3] ,
//                distanceNew = it[4],
//                utmCoordinateNew = it[5],
//                dateTimeFormatterNew = it[6],
//                addressNew = it[7],
//                cusText = it[8]
//
//
//            )
//        }
    }
}