package com.example.dlsandroidredesign

import android.net.Uri
import androidx.room.ProvidedTypeConverter
import androidx.room.TypeConverter
import com.example.dlsandroidredesign.domain.entity.LocationObject
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

@ProvidedTypeConverter
class Converters {
    @TypeConverter
    fun stringToUri(value: String): Uri {
        return Uri.parse(value)
    }

    @TypeConverter
    fun uritoString(uri: Uri): String {
        return uri.toString()
    }

    @TypeConverter
    fun locationObjecttoString(value: LocationObject?): String? {
        return if (value == null) null else Gson().toJson(value)
    }

    private inline fun <reified T> Gson.fromJson(json: String) =
        fromJson<T>(json, object : TypeToken<T>() {}.type)

    @TypeConverter
    fun stringToLocationObject(value: String?): LocationObject? {
        return if (value == null) null else Gson().fromJson<LocationObject>(value)
    }
}
