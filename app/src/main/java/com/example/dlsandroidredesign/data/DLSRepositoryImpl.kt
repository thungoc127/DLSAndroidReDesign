package com.example.dlsandroidredesign.data

import android.annotation.SuppressLint
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
import androidx.datastore.preferences.core.Preferences
import com.arcgismaps.LoadStatus
import com.arcgismaps.data.QueryParameters
import com.arcgismaps.data.SpatialRelationship
import com.arcgismaps.geometry.CoordinateFormatter
import com.arcgismaps.geometry.Envelope
import com.arcgismaps.geometry.SpatialReference
import com.arcgismaps.geometry.UtmConversionMode
import com.arcgismaps.mapping.MobileMapPackage
import com.arcgismaps.mapping.layers.FeatureLayer
import com.example.dlsandroidredesign.data.di.ApplicationScope
import com.example.dlsandroidredesign.data.di.IoDispatcher
import com.example.dlsandroidredesign.data.local.CheckBoxDataStore
import com.example.dlsandroidredesign.data.local.ImageLocationInfo
import com.example.dlsandroidredesign.data.local.ImageLocationInfoDAO
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
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MultipartBody
import java.io.File
import java.math.RoundingMode
import java.text.DecimalFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class DLSRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context,
    private val userDataStore: UserDataStore,
    private val preferencesDataStore: PreferencesDataStore,
    private val checkBoxDataStore: CheckBoxDataStore,
    private val dlsService: DLSService,
    private val dlsDAO: ImageLocationInfoDAO,
    @IoDispatcher private val dispatcher: CoroutineDispatcher,
    @ApplicationScope private val externalScope: CoroutineScope
) : DLSRepository {

    private var sectionLayer: FeatureLayer? = null
    private val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
    private val packagePath = File(context.getExternalFilesDir(null), "sections.mmpk").path
    private val mobileMapPackage = MobileMapPackage(packagePath)
    private val locationRequest: LocationRequest = LocationRequest.Builder(
        Priority.PRIORITY_HIGH_ACCURACY,
        1000
    ).build()
    private val dec = DecimalFormat("#.000").apply { roundingMode = RoundingMode.CEILING }

    @SuppressLint("MissingPermission")
    private val _locationCallbackFlow = callbackFlow {
        val locationCallback = object : LocationCallback() {
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

    init {
        externalScope.launch {
            mobileMapPackage.load()
            if (mobileMapPackage.loadStatus.value == LoadStatus.Loaded) {
                sectionLayer = mobileMapPackage.maps
                    .getOrNull(0)
                    ?.operationalLayers
                    ?.getOrNull(0) as? FeatureLayer
            }
        }
    }

    override fun getLocation(): Flow<Location?> = _locationCallbackFlow

    // TODO: Can remove this since we initiate map on init {}
    override suspend fun loadMobileMapPackage() {}

    // SepUtm
    override fun sepUtm(str: String): Triple<String?, String?, String?> {
        val utmArr = str.split(" ".toRegex()).dropLastWhile { it.isEmpty() }
            .toTypedArray()
        val zone = utmArr[0].substring(0, utmArr[0].length - 1)
        val east = utmArr[1]
        val nor = utmArr[2]
        return Triple(zone, east, nor)
    }

    // GetGridLocation
    override fun getGridLocation(sec: String?, x: Double, y: Double): String {
        var qtr = ""
        var lsd = 0
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

    // Get Distance
    override fun getDistance(x: Int, y: Int, ext: Envelope): String {
        val strMin = String.format("%.6f", ext.yMin) + "N " + String.format("%.6f", ext.xMin) + "W"
        val pntMin =
            CoordinateFormatter.fromLatitudeLongitudeOrNull(strMin, SpatialReference.webMercator())

        val utmMin =
            CoordinateFormatter.toUtmOrNull(pntMin!!, UtmConversionMode.NorthSouthIndicators, true)

        val minTup = sepUtm(utmMin!!)
        val minX: Int = try {
            minTup.component2()!!.toInt()
        } catch (e: Exception) {
            0
        }
        val minY: Int = try {
            minTup.component3()!!.toInt()
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

    // GetAddressFromLocation
    override fun getAddressFromLocation(latitude: Double?, longitude: Double?): String {
        val geocoder = Geocoder(context, Locale.getDefault())
        return try {
            val addresses: List<Address> =
                geocoder.getFromLocation(latitude!!, longitude!!, 1) ?: listOf()
            return if (addresses.isNotEmpty()) {
                val address: Address = addresses[0]
                // Retrieve the address information
                val addressLine: String = address.getAddressLine(0)
//                val city: String? = address.locality
//                val state: String? = address.adminArea
//                val country: String? = address.countryName
//                val postalCode: String? = address.postalCode
                addressLine
            } else {
                Log.d("getLocationProcess", "address:fail")
                ""
            }
        } catch (e: Exception) {
            Log.d("getLocationProcess", "address exception")
            e.printStackTrace()
            ""
        }
    }

    // GetCompleteAddress
    override suspend fun getCompleteAddress(
        location: Location?
    ): LocationObject = suspendCoroutine { cont ->
        externalScope.launch {
            val lonDouble = location!!.longitude
            val latDouble = location.latitude
            val elevationDouble = location.altitude
            val bearingDouble = location.bearing
            val dataTime = LocalDateTime.now()

            val queryParams = QueryParameters()
            val agsString = "${latDouble}N${lonDouble}W"
            val pnt = CoordinateFormatter.fromLatitudeLongitudeOrNull(
                agsString,
                SpatialReference.webMercator()
            )
            queryParams.geometry = pnt
            queryParams.spatialRelationship = SpatialRelationship.Intersects
            val result =
                sectionLayer?.featureTable?.queryFeatures(queryParams) ?: return@launch cont.resume(
                    LocationObject()
                )
            val utmString = CoordinateFormatter.toUtmOrNull(
                pnt!!,
                UtmConversionMode.NorthSouthIndicators,
                true
            )

            result.apply {
                onSuccess {
                    val resultIterator = it.iterator()
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
                        //                    val getCustomText = preferenceDataStore.getCustomText.last()

                        val locationObject = LocationObject().apply {
                            this.lat = String.format("%.6f", latDouble)
                            this.lon = String.format("%.6f", lonDouble)
                            this.elevation = "Eleve: ${dec.format(elevationDouble)} m"
                            this.gridLocation = getGridLocation(secLbl, xDelta, yDelta)
                            this.distance = getDistance(
                                utmTup.second!!.toInt(),
                                utmTup.third!!.toInt(),
                                extent
                            )
                            this.utmCoordinate =
                                utmTup.third + " m " + utmTup.second + " m " + "Zone: " + utmTup.first
                            this.bearing =
                                String.format("Bearing: %.0f", bearingDouble) + "\u2103 TN"
                            this.address = getAddressFromLocation(latDouble, lonDouble)
                            this.date = dataTime.format(
                                DateTimeFormatter.ofPattern(
                                    "MMM d, yyyy HH:mm:ss",
                                    Locale.ENGLISH
                                )
                            )
                            //                    this.custText = getCustomText
                        }

                        cont.resume(locationObject)
                    } else {
                        cont.resume(LocationObject())
                    }
                }

                onFailure {
                    cont.resume(LocationObject())
                }
            }
        }
    }

    // AddLocationToImageUri
    override fun getCheckBox(): Flow<Preferences> = checkBoxDataStore.getCheckBox()

    override suspend fun setCheckBox(checkBoxKey: String, value: Boolean) {
        checkBoxDataStore.setCheckBox(checkBoxKey, value)
    }

    override suspend fun getLocationUpdate() {}

    override fun getCurrentUser(): Flow<User?> = userDataStore.getUser()

    override fun getLogInStatus(): Flow<Boolean> = userDataStore.getIsLoginSuccess

    override suspend fun refreshWaypointGroup() {
        // TODO: If we see this green underline, it means we might spell wrong.
        val respone = dlsService.getWayPointGroups(userDataStore.getUser().first()!!.id)
        val body = respone.body()
        val currentUser = userDataStore.getUser().first()
        userDataStore.setUser(
            currentUser!!.id,
            currentUser.userName,
            body!!.waypointgroups!!,
            currentUser.groupIdCheck,
            currentUser.groupNameCheck
        )
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
                userDataStore.setUser(
                    body.id!!,
                    body.name!!,
                    body.waypointgroups ?: listOf(),
                    body.waypointgroups!![0].groupid!!,
                    body.waypointgroups!![0].groupname!!
                )
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

    override suspend fun insertImageLocationInfo(
        imageUri: Uri,
        locationInfoObject: LocationObject?
    ) {
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
                // TODO: Toast is an android thing. It should be used in view side only
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
                // TODO: Toast is an android thing. It should be used in view side only
                val id = body!!.waypointid
                Toast.makeText(context, "Photo Uploaded Successfully ", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(context, "Failed to upload. Please try again", Toast.LENGTH_SHORT)
                    .show()
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
            while (cursor.moveToNext() && count <= 25) {
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
