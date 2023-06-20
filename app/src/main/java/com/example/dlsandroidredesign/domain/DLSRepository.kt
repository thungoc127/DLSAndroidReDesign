package com.example.dlsandroidredesign.domain

import android.content.Context
import android.location.Location
import android.net.Uri
import androidx.lifecycle.LifecycleOwner
import com.arcgismaps.geometry.Envelope
import com.example.dlsandroidredesign.domain.entity.LocationObject
import com.example.dlsandroidredesign.domain.entity.User
import com.google.gson.JsonObject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import okhttp3.MultipartBody

interface DLSRepository {
    fun getCurrentUser(): Flow<User?>
    suspend fun login(username: String, password: String): User?
    suspend fun getWayPoint(): String?
    suspend fun getCompleteAddress(location: Location): LocationObject
    suspend fun fetchLocationUpdates(): Flow<Location?>
    fun getGridLocation(sec: String?, x: Double, y: Double): String
    fun getDistances(x: Int, y: Int, ext: Envelope): String
    fun getAddressFromLocation(context: Context, latitude: Double?, longitude: Double?, ): String

   suspend fun getLocationUpdate(): StateFlow<LocationObject>
    suspend fun getWayPoint(apiKey: String?, bean: JsonObject?):String?
    suspend fun uploadPhoto(apiKey: String?, waypointId: String?, photo: MultipartBody.Part?)
    fun getAllGalleryImages():List<Uri>
    suspend fun uploadPicture(apiKey:String?,bean: JsonObject?)

    fun zoomCamera(zoomRatio: Float)
}