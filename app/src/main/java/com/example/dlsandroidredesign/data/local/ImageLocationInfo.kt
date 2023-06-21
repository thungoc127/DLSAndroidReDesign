package com.example.dlsandroidredesign.data.local

import android.net.Uri
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.dlsandroidredesign.domain.entity.LocationObject

@Entity
data class ImageLocationInfo (
    @PrimaryKey(autoGenerate = true)
    val number:Int?=null,
    val uriImage: Uri,
    val locationObject: LocationObject?,
)

