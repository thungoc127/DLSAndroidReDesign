package com.example.dlsandroidredesign.domain

import android.location.Location
import android.net.Uri
import androidx.datastore.preferences.core.Preferences
import com.arcgismaps.geometry.Envelope
import com.example.dlsandroidredesign.domain.entity.LocationObject
import com.example.dlsandroidredesign.domain.entity.User
import com.google.gson.JsonObject
import kotlinx.coroutines.flow.Flow
import okhttp3.MultipartBody

interface DLSRepository {
    fun getCurrentUser(): Flow<User?>
    suspend fun login(username: String, password: String): User?
    suspend fun getLocationUpdate()
    suspend fun getWayPointId(apiKey: String?, bean: JsonObject?): String?
    suspend fun uploadPhoto(apiKey: String?, waypointId: String?, photo: MultipartBody.Part?)
    fun getAllGalleryImages(): List<Uri>
    fun getLogInStatus(): Flow<Boolean>
    suspend fun refreshWaypointGroup()
    suspend fun setGroupIdAndName(groupId: String, groupName: String)
    suspend fun setCusText(cusText: String)
    fun getCusText(): Flow<String>
    fun getCheckBox(): Flow<Preferences>
    suspend fun setCheckBox(checkBoxKey: String, value: Boolean)
    suspend fun insertImageLocationInfo(imageUri: Uri, locationInfoObject: LocationObject?)
    fun getIssAutomaticUpload(): Flow<Boolean>
    suspend fun setIsAutomaticUpload(isAutomaticUploadSInput: Boolean)
    suspend fun getLocationObjectByUri(uriImage: Uri): LocationObject

    fun getPhotoSize(): Flow<String>
    suspend fun setPhotoSize(resolutionInput: String)
    suspend fun setUploadSize(resolutionInput: String)
    fun getUploadSize(): Flow<String>

    suspend fun loadMobileMapPackage()
    fun sepUtm(str: String): Triple<String?, String?, String?>
    fun getGridLocation(sec: String?, x: Double, y: Double): String
    fun getDistance(x: Int, y: Int, ext: Envelope): String
    fun getAddressFromLocation(latitude: Double?, longitude: Double?): String
    suspend fun getCompleteAddress(location: Location?): LocationObject

//    fun getLocationObjectState(): Flow<LocationObject>
    fun getLocation(): Flow<Location?>
}
