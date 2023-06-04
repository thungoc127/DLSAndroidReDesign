package com.example.dlsandroidredesign

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class ImageLocationInfo (
    @PrimaryKey(autoGenerate = true)
    val uriImage: Int?,
    val locationObject: LocationObject?,
)

data class LocationObject(
    var lat: String="",
    var lon: String="",
    var elevation: String="",
    var gridLocation: String="",
    var distance: String="",
    var utmCoordinate: String="",
    var bearing: String="",
    var address: String="",
    var date: String="",
    var custText: String=""
)