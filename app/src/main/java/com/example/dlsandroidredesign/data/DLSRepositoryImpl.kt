package com.example.dlsandroidredesign.data

import android.annotation.SuppressLint
import android.content.ContentUris
import android.content.Context
import android.net.Uri
import android.provider.MediaStore
import android.widget.Toast
import androidx.datastore.preferences.core.Preferences
import com.example.dlsandroidredesign.data.local.CheckBoxDataStore
import com.example.dlsandroidredesign.data.local.ImageLocationInfo
import com.example.dlsandroidredesign.data.local.ImageLocationInfoDAO
import com.example.dlsandroidredesign.data.local.PreferencesDataStore
import com.example.dlsandroidredesign.data.local.UserDataStore
import com.example.dlsandroidredesign.data.remote.DLSService
import com.example.dlsandroidredesign.domain.DLSRepository
import com.example.dlsandroidredesign.domain.entity.LocationObject
import com.example.dlsandroidredesign.domain.entity.User
import com.google.gson.JsonObject
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import okhttp3.MultipartBody
import javax.inject.Inject

@SuppressLint("MissingPermission")
class DLSRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context,
    private val userDataStore: UserDataStore,
    private val preferencesDataStore: PreferencesDataStore,
    private val checkBoxDataStore: CheckBoxDataStore,
    private val dlsService: DLSService,
    private val dlsDAO: ImageLocationInfoDAO
) : DLSRepository {

    override fun getCheckBox(): Flow<Preferences> = checkBoxDataStore.getCheckBox()

    override suspend fun setCheckBox(checkBoxKey: String, value: Boolean) {
        checkBoxDataStore.setCheckBox(checkBoxKey, value)
    }

    private val _locationObject = MutableStateFlow(LocationObject())
    override suspend fun getLocationUpdate(): StateFlow<LocationObject> {
        return _locationObject
    }

    override fun getCurrentUser(): Flow<User?> = userDataStore.getUser()

    override fun getLogInStatus(): Flow<Boolean> = userDataStore.getIsLoginSuccess

    override suspend fun refreshWaypointGroup() {
        val respone = dlsService.getWayPointGroups(userDataStore.getUser().first()!!.id)
        val body = respone.body()
        val currentUser = userDataStore.getUser().first()
        userDataStore.setUser(currentUser!!.id, currentUser.userName, body!!.waypointgroups!!, currentUser.groupIdCheck, currentUser.groupNameCheck)
    }

    override suspend fun setGroupIdAndName(groupId: String, groupName: String) {
        userDataStore.setGroupIdAndName(groupId, groupName)
    }

    override suspend fun setCusText(cusText: String) {
        userDataStore.setCusText(cusText)
    }

    override fun getCusText(): Flow<String> {
        return userDataStore.getCusText
    }

    override suspend fun setIsAutomaticUpload(isAutomaticUploadSInput: Boolean) {
        userDataStore.setIsAutomaticUpload(isAutomaticUploadSInput)
    }


    override suspend fun getLocationObjectByUri(uriImage: Uri): LocationObject {
        return dlsDAO.getLocationObjectByUri(uriImage)
    }

    override fun getIssAutomaticUpload(): Flow<Boolean> {
        return userDataStore.getIssAutomaticUpload
    }

    override suspend fun login(
        username: String,
        password: String
    ): User? = withContext(Dispatchers.IO) {
        try {
            val response = dlsService.validate(username, password)
            val body = response.body()
            if (response.isSuccessful && body != null && body.success && body.id != null) {
                userDataStore.setIsLoginSuccessful(true)
                userDataStore.setUser(body.id!!, body.name!!, body.waypointgroups ?: listOf(), body.waypointgroups!![0].groupid!!, body.waypointgroups!![0].groupname!!)
                userDataStore.getUser().first()
            } else {
                null
            }
        } catch (error: Exception) {
            null
        }
    }

    override suspend fun setPhotoSize(resolutionInput: String) {
        preferencesDataStore.setPhotoSize(resolutionInput)
    }
    override fun getPhotoSize(): Flow<String> {
        return preferencesDataStore.getPhotoSize
    }

    override suspend fun setUploadSize(resolutionInput: String) {
        preferencesDataStore.setUploadSize(resolutionInput)
    }
    override fun getUploadSize(): Flow<String> {
        return preferencesDataStore.getUploadSize
    }

    override suspend fun insertImageLocationInfo(imageUri: Uri, locationInfoObject: LocationObject) {
        dlsDAO.insertImageLocationInfo(
            ImageLocationInfo(
                null,
                imageUri,
                locationInfoObject
            )
        )
    }
    override suspend fun getWayPointId(apiKey: String?, bean: JsonObject?): String? {
        val response = dlsService.getWayPointID(apiKey = apiKey, bean = bean)
        val body = response.body()
        var id: String? = null
        withContext(Dispatchers.Main) {
            if (response.isSuccessful) {
                id = response.body()!!.waypointid
            } else {
                Toast.makeText(context, "Fail to create Waypoint", Toast.LENGTH_SHORT).show()
            }
        }
        return id
    }

    override suspend fun uploadPhoto(
        apiKey: String?,
        waypointId: String?,
        photo: MultipartBody.Part?
    ) {
        val response = dlsService.uploadPhoto(
            apiKey = apiKey,
            waypointId = waypointId,
            photo = photo
        )
        val body = response.body()
        withContext(Dispatchers.Main) {
            if (response.isSuccessful) {
                val id = body!!.waypointid
                Toast.makeText(context, "Photo Uploaded Successfully ", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(context, "Failed to upload. Please try again", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun getAllGalleryImages(): List<Uri> {
        val folderPath = "/Pictures/DLSPhotoCompose"
        val images = mutableListOf<Uri>()
        val projection = arrayOf(MediaStore.Images.Media._ID)
        val sortOrder = "${MediaStore.Images.Media.DATE_ADDED} DESC "
        val selection = "${MediaStore.Images.Media.DATA} like ?"
        val selectionArgs = arrayOf("%$folderPath%")
        val query = context.contentResolver.query(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            projection,
            selection, // if would like to choose all gallery leave this null
            selectionArgs, // if would like to choose all gallery leave this null
            sortOrder
        )
        query?.use { cursor ->
            val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID)
            var count = 0
            while (cursor.moveToNext()&& count <= 25) {
                val id = cursor.getLong(idColumn)
                val contentUri = ContentUris.withAppendedId(
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                    id
                )
                images.add(contentUri)
                count++
            }
        }
        return images
    }


}
