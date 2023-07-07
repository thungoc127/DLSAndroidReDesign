package com.example.dlsandroidredesign.domain.usecase

import android.content.ContentResolver
import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.exifinterface.media.ExifInterface
import com.example.dlsandroidredesign.domain.entity.LocationObject
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class GetLocationFromPicture @Inject constructor(@ApplicationContext private val context: Context){
    operator fun invoke(imageUri: Uri): LocationObject {
        val contentResolver: ContentResolver = context.contentResolver
        val inputStream = contentResolver.openInputStream(imageUri)
        val exif = ExifInterface(inputStream!!)
        val latitudes = exif.getAttribute(ExifInterface.TAG_GPS_LATITUDE)?.split(',') ?: return LocationObject()
        val latitudesRef = exif.getAttribute(ExifInterface.TAG_GPS_LATITUDE_REF)
        val longitudes = exif.getAttribute(ExifInterface.TAG_GPS_LONGITUDE)?.split(',') ?: return LocationObject()
        val longitudesRef = exif.getAttribute(ExifInterface.TAG_GPS_LONGITUDE_REF)
        Log.d("getLocation", "latitudes$latitudes")
        Log.d("getLocation", "longitudes$longitudes")

        val latitude = convertToDegree(latitudes)
        val longitude = convertToDegree(longitudes)
        val locationObject = LocationObject()
        if (latitudesRef == "N") { locationObject.lat = String.format("%.6f", latitude) } else { locationObject.lat = String.format("%.6f", -latitude) }
        if (longitudesRef == "E") { locationObject.lon = String.format("%.6f", longitude) } else { locationObject.lon = String.format("%.6f", -longitude) }
        Log.d("getLocation", "$locationObject")
        return locationObject
    }
}

private fun convertToDegree(coordinate: List<String>): Double {
    val degrees = coordinate[0].split("/")
    val minutes = coordinate[1].split("/")
    val seconds = coordinate[2].split("/")

    val degreesValue = degrees[0].toDouble() / degrees[1].toDouble()
    val minutesValue = minutes[0].toDouble() / minutes[1].toDouble() / 60
    val secondsValue = seconds[0].toDouble() / seconds[1].toDouble() / 3600

    val decimalDegrees = degreesValue + minutesValue + secondsValue
    return decimalDegrees
}