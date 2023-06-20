package com.example.dlsandroidredesign.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.dlsandroidredesign.domain.entity.LocationObject

@Entity
data class ImageLocationInfo (
    @PrimaryKey(autoGenerate = true)
    val uriImage: Int?,
    val locationObject: LocationObject?,
)

