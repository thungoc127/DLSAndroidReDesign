package com.example.dlsandroidredesign.domain.usecase

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.exifinterface.media.ExifInterface
import com.example.dlsandroidredesign.domain.entity.LocationObject
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import kotlin.math.abs

class AddLocationToImageUri @Inject constructor(
    @ApplicationContext private val context: Context,
    private val getLocationFromPicture: GetLocationFromPicture
) {
    operator fun invoke(imageUri: Uri, locationObject: LocationObject) {
        val latitude = locationObject.lat.toDouble()
        val longitude = locationObject.lon.toDouble()

        val exif = ExifInterface(context.contentResolver.openFileDescriptor(imageUri, "rw")?.fileDescriptor!!)
        exif.setAttribute(
            ExifInterface.TAG_GPS_LATITUDE,
            convert(latitude)
        )
        exif.setAttribute(
            ExifInterface.TAG_GPS_LATITUDE_REF,
            if (latitude > 0) "N" else "S"
        )
        exif.setAttribute(
            ExifInterface.TAG_GPS_LONGITUDE,
            convert(longitude)
        )
        exif.setAttribute(
            ExifInterface.TAG_GPS_LONGITUDE_REF,
            if (longitude > 0) "E" else "W"
        )
        exif.saveAttributes()

        val getLocationObjectfromPic = getLocationFromPicture(imageUri)
        Log.d("getLocation", "urifromAddexif: $imageUri")
        Log.d("getLocation", "finishSetExif ${exif.getAttribute(ExifInterface.TAG_GPS_LONGITUDE)}")
        Log.d("getLocation", "finishSetExif getLocationObjectfromPic $getLocationObjectfromPic")
    }
}

private fun convert(coordinate: Double): String {
    var coord = coordinate
    coord = abs(coord)
    val degrees = coord.toInt()
    coord = (coord - degrees) * 60
    val minutes = coord.toInt()
    coord = (coord - minutes) * 60
    val seconds = (coord * 1000).toInt()
    return "$degrees/1,$minutes/1,$seconds/1000"
}
