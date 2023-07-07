package com.example.dlsandroidredesign.ui.mainScreen

import android.content.ContentValues
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.dlsandroidredesign.domain.entity.LocationObject
import com.example.dlsandroidredesign.domain.usecase.AddLocationToImageUri
import com.example.dlsandroidredesign.domain.usecase.AddTextOnImageAndSave
import com.example.dlsandroidredesign.domain.usecase.ConvertUriToMultipart
import com.example.dlsandroidredesign.domain.usecase.CreateText
import com.example.dlsandroidredesign.domain.usecase.FetchLocation
import com.example.dlsandroidredesign.domain.usecase.GetAutoUploadStatus
import com.example.dlsandroidredesign.domain.usecase.GetCurrentUser
import com.example.dlsandroidredesign.domain.usecase.GetLocationFromPicture
import com.example.dlsandroidredesign.domain.usecase.GetLocationInfoUseCase
import com.example.dlsandroidredesign.domain.usecase.GetWayPointId
import com.example.dlsandroidredesign.domain.usecase.InsertImageLocationInfo
import com.example.dlsandroidredesign.domain.usecase.LoadMobileMapPackage
import com.example.dlsandroidredesign.domain.usecase.UploadPicture
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import okhttp3.MultipartBody
import java.math.RoundingMode
import java.text.DecimalFormat
import javax.inject.Inject

@HiltViewModel
class ImageLocationInfoViewModel @Inject constructor(
    getCurrentUser: GetCurrentUser,
    private val uploadPicture: UploadPicture,
    private val autoUploadStatus: GetAutoUploadStatus,
    private val insertImagelocationinfo: InsertImageLocationInfo,
    private val getLocationFromPicture: GetLocationFromPicture,
    private val addLocationToImageUri: AddLocationToImageUri,
    private val addTextOnImageAndSave: AddTextOnImageAndSave,
    private val createText: CreateText,
    private val getWayPointId: GetWayPointId,
    private val getConvertUriToMultipart: ConvertUriToMultipart,
    private val loadMobileMapPackage: LoadMobileMapPackage,
    private val fetchLocation: FetchLocation,
    private val getLocationInfoUseCase: GetLocationInfoUseCase
) : ViewModel() {
    private val fileNameCapture = "temp.jpeg"
    val contentValues = ContentValues().apply {
        put(MediaStore.Images.Media.DISPLAY_NAME, fileNameCapture)
        put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.P) {
            put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/DLSPhotoCompose")
        }
    }
    var uriImage = mutableStateOf<Uri?>(null)
    private val dec = DecimalFormat("#.000").apply { roundingMode = RoundingMode.CEILING }
    var locationInfoLeft = MutableStateFlow("")
    var locationInfoRight = MutableStateFlow("")
    private val _locationObject = MutableStateFlow(LocationObject())
    val locationObject = _locationObject

    private var _wayPointId = MutableStateFlow<String?>(null)
    private val wayPointId = _wayPointId
    val currentUser = getCurrentUser.invoke()
    val uriSet: MutableStateFlow<MutableSet<Uri>> = MutableStateFlow(emptySet<Uri>().toMutableSet())

    init {
        viewModelScope.launch {
            // TODO: Could remove this use case since the map package has initialized in repository init {}
            loadMobileMapPackage.invoke()
            launch {
                getLocationInfoUseCase().collect {
                    _locationObject.value = it
                }
            }
        }
    }

    suspend fun startFetchingLocation() {
        fetchLocation.invoke()
    }

    fun setCusTextLocationObject(newValue: String) {
        // _locationObject.value.custText = newValue
    }

    fun checkUriContain(imageUri: Uri): Boolean {
        return uriSet.value.contains(imageUri)
    }

    fun setUriSet(imageUri: Uri) {
        if (!uriSet.value.contains(imageUri)) {
            uriSet.value.add(imageUri)
        } else {
            uriSet.value.remove(imageUri)
        }
    }

    fun getLocationFromPicture(imageUri: Uri): LocationObject {
        return getLocationFromPicture.invoke(imageUri)
    }

    private fun addLocationToImageUri(imageUri: Uri, locationObject: LocationObject) {
        addLocationToImageUri.invoke(imageUri, locationObject)
    }

    fun insertImagelocationinfo(imageUri: Uri, locationObject: LocationObject?) {
        viewModelScope.launch { insertImagelocationinfo.invoke(imageUri, locationObject) }
    }

    private suspend fun createText(locationObject: LocationObject?): List<String> {
        return createText.invoke(locationObject)
    }

    private suspend fun addTextOnImageAndSave(
        savedUriCapture: Uri,
        fileNameCapture: String,
        locationInfoLeft: String,
        locationInfoRight: String
    ): Uri {
        return addTextOnImageAndSave.invoke(
            savedUriCapture,
            fileNameCapture,
            locationInfoLeft,
            locationInfoRight
        )
    }

    private suspend fun processImage(savedUriCapture: Uri?, locationObject: LocationObject?): Uri {
        runBlocking {
            Log.d("Upload", "uriResultfromProcess:$locationObject")
            val locationInfo = createText(locationObject)
            locationInfoLeft.value = locationInfo[0]
            locationInfoRight.value = locationInfo[1]
        }

        val result = viewModelScope.async {
            addTextOnImageAndSave(
                savedUriCapture!!,
                fileNameCapture,
                locationInfoLeft.value,
                locationInfoRight.value
            )
        }
        uriImage.value = result.await()
        insertImagelocationinfo(result.await(), locationObject)
        if (locationObject!!.lat != "") {
            addLocationToImageUri(result.await(), locationObject)
        }
        Log.d("Upload", "uriResultFromProcess:$result")

        return result.await()
    }

    private suspend fun getWayPointId(uriImage: Uri?, locationObject: LocationObject): String {
        return getWayPointId.invoke(uriImage, locationObject)
    }

    private fun convertUriToMultipart(imageUri: Uri?): MultipartBody.Part {
        return getConvertUriToMultipart(imageUri)
    }

    private val _processedImage = MutableStateFlow<Uri?>(null)
    val processedImage: StateFlow<Uri?> = _processedImage

    private suspend fun uploadPhoto(
        apiKey: String,
        wayPointId: String,
        photo: MultipartBody.Part?
    ) {
        uploadPicture.invoke(apiKey, wayPointId, photo)
    }

    fun processAutoUpload(savedUriCapture: Uri?) {
        viewModelScope.launch {
            val locationObject = locationObject.value
            Log.d("getLocationProcess: ", "processAutoUpload$locationObject")
            val image = async { processImage(savedUriCapture, locationObject) }
            if (!locationObject.lat.isNullOrBlank()) {
                Log.d("processAutoUpload", "locationObject1:$$locationObject")
                if (!currentUser.first()?.id.isNullOrEmpty()) {
                    if (autoUploadStatus.invoke().first()) {
                        val apiKey = async { currentUser.first()?.id }
                        val wayPoint = async { getWayPointId(image.await(), locationObject) }
                        Log.d("AutoUpload", "apiKey:${apiKey.await()}")
                        Log.d("AutoUpload", "wayPointId:${wayPoint.await()}")
                        val pictureMultipartBody = async { convertUriToMultipart(image.await()) }
                        uploadPhoto(
                            apiKey.await()!!,
                            wayPoint.await(),
                            pictureMultipartBody.await()
                        )
                        Log.d("AutoUpload", "uri:$$pictureMultipartBody.await()")
                    }
                }
                val getLocationFromPicture = getLocationFromPicture(image.await())
                Log.d("AutoUpload", "finish upload:locationObject:$getLocationFromPicture")
            }
        }
    }

    fun processUpload() {
        viewModelScope.launch {
            if (!currentUser.first()?.id.isNullOrEmpty()) {
                for (uri in uriSet.value) {
                    val locationObjectbyUri = getLocationFromPicture(uri)
                    Log.d("getLocation", "locationObjectbyUri:$$locationObjectbyUri")
                    Log.d("getLocation", "uri:$${currentUser.first()}")
                    if (!locationObjectbyUri.lat.isNullOrBlank()) {
                        val apiKey = async { currentUser.first()?.id }
                        val wayPoint = async { getWayPointId(uri, locationObjectbyUri) }
                        val pictureMultipartBody = async { convertUriToMultipart(uri) }
                        uploadPhoto(
                            apiKey.await()!!,
                            wayPoint.await(),
                            pictureMultipartBody.await()
                        )
                    } else {
                        val currentLocationObject = locationObject.value
                        Log.d(
                            "getLocationProcess: ",
                            "locationObjectStateinprocessUpload$currentLocationObject"
                        )
                        val apiKey = async { currentUser.first()?.id }
                        val wayPoint = async { getWayPointId(uri, currentLocationObject) }
                        val pictureMultipartBody = async { convertUriToMultipart(uri) }
                        uploadPhoto(
                            apiKey.await()!!,
                            wayPoint.await(),
                            pictureMultipartBody.await()
                        )
                    }
                }
            } else {
                Log.d("getLocation", "currentUser:$${currentUser.first()}")
                // TODO: sent event to view to make the toast
//                Toast.makeText(context, "Please login before uploading.", LENGTH_SHORT).show()
            }
        }
    }
}
