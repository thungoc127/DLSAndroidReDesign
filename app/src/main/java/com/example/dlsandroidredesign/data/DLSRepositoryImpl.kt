package com.example.dlsandroidredesign.data

import android.annotation.SuppressLint
import android.app.Application
import android.content.ContentResolver
import android.content.ContentUris
import android.content.Context
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.net.Uri
import android.os.Looper
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.lifecycle.LifecycleOwner
import com.arcgismaps.LoadStatus
import com.arcgismaps.data.QueryParameters
import com.arcgismaps.data.SpatialRelationship
import com.arcgismaps.geometry.CoordinateFormatter
import com.arcgismaps.geometry.Envelope
import com.arcgismaps.geometry.SpatialReference
import com.arcgismaps.geometry.UtmConversionMode
import com.arcgismaps.mapping.MobileMapPackage
import com.arcgismaps.mapping.layers.FeatureLayer
import com.example.dlsandroidredesign.data.local.PreferencesDataStore
import com.example.dlsandroidredesign.data.local.UserDataStore
import com.example.dlsandroidredesign.data.remote.DLSService
import com.example.dlsandroidredesign.domain.DLSRepository
import com.example.dlsandroidredesign.domain.entity.LocationObject
import com.example.dlsandroidredesign.domain.entity.User
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.gson.JsonObject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.ByteArrayOutputStream
import java.io.File
import java.math.RoundingMode
import java.text.DecimalFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

@SuppressLint("MissingPermission")
class DLSRepositoryImpl @Inject constructor(
    private val application: Application,
    private val userDataStore: UserDataStore,
    private val dlsService: DLSService,
    private val viewModelScope:CoroutineScope,
    private val lifecycleOwner: LifecycleOwner

) : DLSRepository {
    // state flow variable
    // private val _location = MutableState<LocationObject?>(null)
    // override fun getLocationUpdate() = _location
    //every variable locations
    // val requestLocation = LocationRequest
    //

    // LocationCallback  {
    //      onUpdate() {
    //          // Do calculate things
    //          _location.value = newValue
    // }

    // fun fetchLocation() {
    //     fusedLocation.request
    // }
    //

    private val _locationObject = MutableStateFlow(LocationObject())
    override suspend fun getLocationUpdate(): StateFlow<LocationObject> {
       return _locationObject
    }
    val preferenceDataStore = PreferencesDataStore(application)
    private val fusedLocationClient = LocationServices.getFusedLocationProviderClient(application)
    private val locationRequest: LocationRequest = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 1000).build()
    private val packagePath = File(application.getExternalFilesDir(null), "sections.mmpk").path
    private val mobileMapPackage = MobileMapPackage(packagePath)
    private var sectionLayer: FeatureLayer? = null
    private val dec = DecimalFormat("#.000").apply {
        roundingMode = RoundingMode.CEILING
    }


    //Camera
    var cameraProviderFuture = ProcessCameraProvider.getInstance(application)
    var cameraSelectorState by mutableStateOf(CameraSelector.DEFAULT_BACK_CAMERA)
    var flashModeState by mutableStateOf(ImageCapture.FLASH_MODE_OFF)
    val imageCapture by mutableStateOf( ImageCapture.Builder().build())
    val preview by mutableStateOf(Preview.Builder().build())

    init {
        viewModelScope.launch {
            mobileMapPackage.load()
            mobileMapPackage.loadStatus.collect {
                Log.d("status: ", it.toString())
                when (it) {
                    LoadStatus.Loaded -> {
                        sectionLayer = mobileMapPackage.maps
                            .getOrNull(0)
                            ?.operationalLayers
                            ?.getOrNull(0) as? FeatureLayer

                        fetchLocationUpdates().collect { location ->
                            // Location retrieved successfully
                            if (location != null) {

                                val newLocationObject = withContext(Dispatchers.Default) {
                                    getCompleteAddress(location)
                                }

                                _locationObject.value = newLocationObject
                            }
                        }
                    }

                    else -> {}
                }
            }
        }
    }

    override fun getCurrentUser(): Flow<User?> = userDataStore.getUser()

    override suspend fun login(
        username: String,
        password: String
    ): User? = withContext(Dispatchers.IO) {
        try {
            val response = dlsService.validate(username, password)
            val body = response.body()
            if(response.isSuccessful && body != null && body.success && body.id != null) {
                userDataStore.setUser(body.id!!, body.waypointgroups?: listOf())
                userDataStore.getUser().first()
            } else {
                null
            }
        } catch (error: Exception) {
            null
        }
    }

    override suspend fun getWayPoint(): String? {
        TODO("Not yet implemented")

    }


    override suspend fun fetchLocationUpdates(): Flow<Location?> = callbackFlow {
        val locationCallback = object : LocationCallback() {
            @SuppressLint("MissingPermission")
            override fun onLocationResult(locationResult: LocationResult) {
                super.onLocationResult(locationResult)
                val lastLocation = fusedLocationClient.lastLocation
                lastLocation.addOnSuccessListener { location ->
                    trySend(location)
                }.addOnFailureListener {
                    trySend(null)
                }
            }
        }

        fusedLocationClient.requestLocationUpdates(
            locationRequest,
            locationCallback,
            Looper.getMainLooper()
        )
        awaitClose { fusedLocationClient.removeLocationUpdates(locationCallback) }
    }

    override fun getGridLocation(sec: String?, x: Double, y: Double): String {
        var qtr: String = ""
        var lsd: Int = 0
        if (x > 0 && x <= 0.25) {
            if (y > 0 && y <= 0.25) {
                lsd = 4
                qtr = "SW"
            } else if (y > 0.25 && y <= 0.50) {
                lsd = 5
                qtr = "SW"
            } else if (y > 0.50 && y <= 0.75) {
                lsd = 12
                qtr = "NW"
            } else if (y > 0.75 && y <= 1.00) {
                lsd = 13
                qtr = "NW"
            }
        } else if (x > 0.25 && x <= 0.50) {
            if (y > 0 && y <= 0.25) {
                lsd = 3
                qtr = "SW"
            } else if (y > 0.25 && y <= 0.50) {
                lsd = 6
                qtr = "SW"
            } else if (y > 0.50 && y <= 0.75) {
                lsd = 11
                qtr = "NW"
            } else if (y > 0.75 && y <= 1.00) {
                lsd = 14
                qtr = "NW"
            }
        } else if (x > 0.50 && x <= 0.75) {
            if (y > 0 && y <= 0.25) {
                lsd = 2
                qtr = "SE"
            } else if (y > 0.25 && y <= 0.50) {
                lsd = 7
                qtr = "SE"
            } else if (y > 0.50 && y <= 0.75) {
                lsd = 10
                qtr = "NE"
            } else if (y > 0.75 && y <= 1.00) {
                lsd = 15
                qtr = "NE"
            }
        } else if (x > 0.75 && x <= 1.00) {
            if (y > 0 && y <= 0.25) {
                lsd = 1
                qtr = "SE"
            } else if (y > 0.25 && y <= 0.50) {
                lsd = 8
                qtr = "SE"
            } else if (y > 0.50 && y <= 0.75) {
                lsd = 9
                qtr = "NE"
            } else if (y > 0.75 && y <= 1.00) {
                lsd = 16
                qtr = "NE"
            }
        }


        return "($qtr) $lsd-$sec"
    }

    fun sepUtm(str: String): Triple<String?, String?, String?>? {
        val utmArr = str.split(" ".toRegex()).dropLastWhile { it.isEmpty() }
            .toTypedArray()
        val zone = utmArr[0].substring(0, utmArr[0].length - 1)
        val east = utmArr[1]
        val nor = utmArr[2]
        return Triple(zone, east, nor)
    }

    override fun getDistances(x: Int, y: Int, ext: Envelope): String {
        val strMin = String.format("%.6f", ext.yMin) + "N " + String.format("%.6f", ext.xMin) + "W"
        val pntMin = CoordinateFormatter.fromLatitudeLongitudeOrNull(strMin, SpatialReference.webMercator())

        val utmMin = CoordinateFormatter.toUtmOrNull(pntMin!!,UtmConversionMode.NorthSouthIndicators,true)

        val minTup = sepUtm(utmMin!!)
        val minX: Int = try {
            minTup!!.component2()!!.toInt()
        } catch (e: Exception) {
            0
        }
        val minY: Int = try {
            minTup!!.component3()!!.toInt()
        } catch (e: Exception) {
            0
        }
        val xd = x - minX
        val yd = y - minY
        var dist = ""
        if (yd in 0..800) {
            dist = yd.toString() + "m North"
        } else if (yd > 800) {
            val temp = 1600 - yd
            dist = temp.toString() + "m South"
        }
        if (xd in 0..800) {
            dist += " " + xd.toString() + "m East"
        } else if (xd > 800) {
            val temp = 1600 - xd
            dist += " " + temp.toString() + "m West"
        }
        val ch = '\u223D'
        return "$ch $dist"

    }

    override fun getAddressFromLocation(
        context: Context,
        latitude: Double?,
        longitude: Double?
    ): String {
        val geocoder = Geocoder(context, Locale.getDefault())
        return try {
            @Suppress("DEPRECATION") val addresses: List<Address> =
                geocoder.getFromLocation(latitude!!, longitude!!, 1) ?: listOf()
            return if (addresses.isNotEmpty()) {
                val address: Address = addresses[0]

                // Retrieve the address information
                val addressLine: String = address.getAddressLine(0)
                val city: String? = address.locality
                val state: String? = address.adminArea
                val country: String? = address.countryName
                val postalCode: String? = address.postalCode
                addressLine
            } else {
                ""
            }
        } catch (e: Exception) {
            e.printStackTrace()
            ""
        }
    }
    override suspend fun getCompleteAddress(location: Location) = suspendCoroutine { cont ->
        val lonDouble = location!!.longitude
        val latDouble = location!!.latitude
        val elevationDouble = location!!.altitude
        val bearingDouble = location!!.bearing
        var dataTime = LocalDateTime.now()

        viewModelScope.launch {
            val queryParams = QueryParameters()
            val agsString = "${latDouble}N${lonDouble}W"
            val pnt = CoordinateFormatter.fromLatitudeLongitudeOrNull(
                agsString,
                SpatialReference.webMercator()
            )
            queryParams.geometry = pnt
            queryParams.spatialRelationship = SpatialRelationship.Intersects
            val result = sectionLayer?.featureTable?.queryFeatures(queryParams)
            val utmString = CoordinateFormatter.toUtmOrNull(
                pnt!!,
                UtmConversionMode.NorthSouthIndicators,
                true
            )

            result?.apply {
                onSuccess { queryResult ->
                    val resultIterator = queryResult.iterator()
                    if (resultIterator.hasNext()) {
                        val feature = resultIterator.next()
                        val extent: Envelope = feature.geometry!!.extent
                        val attr: MutableMap<String, Any?> = feature.attributes
                        var secId: String? = ""
                        var secLbl: String? = ""
                        var secTxt: String? = ""

                        for (key in attr.keys) {
                            when (key) {
                                "TTTMRRSS" -> secId = attr[key] as String?
                                "LABEL1" -> secLbl = attr[key] as String?
                                "SEC" -> secTxt = attr[key] as String?
                            }
                        }

                        val xDelta = (lonDouble - extent.xMin) / (extent.xMax - extent.xMin)
                        val yDelta = (latDouble - extent.yMin) / (extent.yMax - extent.yMin)
                        val utmTup = sepUtm(utmString!!)
//                        val getCustomText = preferenceDataStore.getCustomText.last()



                        cont.resume(LocationObject().apply {
                            this.lat = String.format("%.6f", latDouble)
                            this.lon = String.format("%.6f", lonDouble)
                            this.elevation = "Eleve: ${dec.format(elevationDouble)} m"
                            this.gridLocation = getGridLocation(secLbl, xDelta, yDelta)
                            this.distance = getDistances(utmTup!!.second!!.toInt(), utmTup!!.third!!.toInt(), extent)
                            this.utmCoordinate = utmTup!!.third + " m " + utmTup.second + " m " + "Zone: " + utmTup.first
                            this.bearing = String.format("Bearing: %.0f", bearingDouble) + "\u2103 TN"
                            this.address = getAddressFromLocation(application,latDouble,lonDouble)
                            this.date = dataTime.format(DateTimeFormatter.ofPattern("MMM d, yyyy HH:mm:ss", Locale.ENGLISH))
//                            this.custText = getCustomText
                        })
                    }
                }

                onFailure {
                    cont.resume(LocationObject())
                }
            }
        }
    }



    override suspend fun getWayPoint(apiKey: String?, bean: JsonObject?):String? {
        val response = dlsService.getWayPointID(apiKey = "1f593949-c520-4747-a162-1c37229a9f54", bean = bean)
        val body = response.body()
        var id:String? =null
        withContext(Dispatchers.Main) {
            if (response.isSuccessful) {
                id = response.body()!!.waypointid
                Toast.makeText(application, "Success + $id", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(application, "Fail", Toast.LENGTH_SHORT).show()

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
                Toast.makeText(application, "Success $id ", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(application, "Fail", Toast.LENGTH_SHORT).show()

            }
        }
    }



    override fun getAllGalleryImages(): List<Uri> {
        val folderPath = "/Pictures/DLSPhotoCompose"
        val images = mutableListOf<Uri>()
        val projection = arrayOf(MediaStore.Images.Media._ID)
        val sortOrder = "${MediaStore.Images.Media.DATE_ADDED} DESC LIMIT 25"
        val selection = "${MediaStore.Images.Media.DATA} like ?"
        val selectionArgs = arrayOf("%$folderPath%")
        val query = application.contentResolver.query(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            projection,
            null,    //if would like to choose all gallery leave this null
            null, //if would like to choose all gallery leave this null
            sortOrder
        )
        query?.use { cursor ->
            val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID)
            while (cursor.moveToNext()) {
                val id = cursor.getLong(idColumn)
                val contentUri = ContentUris.withAppendedId(
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                    id
                )
                images.add(contentUri)
            }
        }
        return images
    }

//    override fun zoomCamera(zoomRatio: Float) {
//        val cameraProvider = cameraProviderFuture.get()
//        // Bind the preview use case to the camera with the specified selector
//        val camera= cameraProvider.bindToLifecycle(
//            lifecycleOwner,
//            cameraSelectorState,
//            preview,
//            imageCapture
//        )
//
//        val cameraControl = camera.cameraControl
//        cameraControl.setLinearZoom(viewModelMainScreenViewModel.zoomRatio.value)
//    }
//
//

    override suspend fun uploadPicture(apiKey:String?, bean: JsonObject?){
        val respond  = dlsService.getWayPointID(apiKey,bean)
        val wayPointObj: JsonObject = JsonObject()
        val obj:JsonObject = JsonObject()

        wayPointObj.addProperty("date", "John")
        wayPointObj.addProperty("lat", "John")
        wayPointObj.addProperty("lon", "John")
        wayPointObj.addProperty("groupid", "John")
        obj.add("waypoint",wayPointObj)
        if(respond.isSuccessful){

// Get the InputStream from the selectedUri
            val contentResolver: ContentResolver = application.contentResolver
            val inputStream = contentResolver.openInputStream(null)

// Read the InputStream and convert it to a byte array
            val byteBuff = ByteArrayOutputStream()
            val buffSize = 1024
            val buff = ByteArray(buffSize)

            var len: Int
            while (inputStream?.read(buff).also { len = it ?: -1 } != -1) {
                byteBuff.write(buff, 0, len)
            }

// Create the request body using the byte array
            val requestFileFront = byteBuff.toByteArray().toRequestBody("image/png".toMediaTypeOrNull())

// Create the MultipartBody.Part using the request body
            val picture = MultipartBody.Part.createFormData("photodata", "picture.png", requestFileFront)



            val wayPointId = respond.body()!!.waypointid

            val respond= dlsService.uploadPhoto(apiKey,wayPointId,picture)
            Toast.makeText(application, "Success", Toast.LENGTH_SHORT).show()
            if(respond.isSuccessful) {
                Toast.makeText(application, "Photo Uploaded Successfully!", Toast.LENGTH_SHORT).show()

            }
        }

    }

}