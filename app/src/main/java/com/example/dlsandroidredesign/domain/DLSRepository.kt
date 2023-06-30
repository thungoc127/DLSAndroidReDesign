package com.example.dlsandroidredesign.domain

import android.net.Uri
import androidx.datastore.preferences.core.Preferences
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

    suspend fun getLocationUpdate(): StateFlow<LocationObject>
    suspend fun getWayPointId(apiKey: String?, bean: JsonObject?): String?
    suspend fun uploadPhoto(apiKey: String?, waypointId: String?, photo: MultipartBody.Part?)
    fun getAllGalleryImages(): List<Uri>
    suspend fun uploadPicture(apiKey: String?, bean: JsonObject?)
    fun getLogInStatus(): Flow<Boolean>
    suspend fun refreshWaypointGroup()
    suspend fun setGroupIdAndName(groupId: String, groupName: String)
    suspend fun setCusText(cusText: String)
    fun getCusText(): Flow<String>
    fun getCheckBox(): Flow<Preferences>
    suspend fun setCheckBox(checkBoxKey: String, value: Boolean)
    suspend fun insertImageLocationInfo(imageUri: Uri, locationInfoObject: LocationObject)
    fun getIssAutomaticUpload(): Flow<Boolean>
    suspend fun setIsAutomaticUpload(isAutomaticUploadSInput: Boolean)
    suspend fun getGroupNameCheck(): Flow<String?>
    suspend fun getLocationObjectByUri(): LocationObject
    suspend fun getLocationObjectByUri(uriImage: Uri): LocationObject

    fun getPhotoSize(): Flow<String>
    suspend fun setPhotoSize(resolutionInput: String)
    suspend fun setUploadSize(resolutionInput: String)
    fun getUploadSize(): Flow<String>
}
