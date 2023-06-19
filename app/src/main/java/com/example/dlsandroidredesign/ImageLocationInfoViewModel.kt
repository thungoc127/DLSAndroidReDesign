package com.example.dlsandroidredesign

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.os.Looper
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.arcgismaps.LoadStatus
import com.arcgismaps.data.QueryParameters
import com.arcgismaps.data.SpatialRelationship
import com.arcgismaps.geometry.CoordinateFormatter
import com.arcgismaps.geometry.Envelope
import com.arcgismaps.geometry.SpatialReference
import com.arcgismaps.geometry.UtmConversionMode
import com.arcgismaps.mapping.MobileMapPackage
import com.arcgismaps.mapping.layers.FeatureLayer
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.math.RoundingMode
import java.text.DecimalFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine
import kotlin.random.Random

@SuppressLint("MissingPermission")
@HiltViewModel
class ImageLocationInfoViewModel @Inject constructor(
    private val application: Application,
    private val dao: ImageLocaitonInfoDAO
) : ViewModel() {

    private val fusedLocationClient = LocationServices.getFusedLocationProviderClient(application)
    private val locationRequest: LocationRequest =
        LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 1000).build()
    private val packagePath = File(application.getExternalFilesDir(null), "sections.mmpk").path
    private val mobileMapPackage = MobileMapPackage(packagePath)
    private var sectionLayer: FeatureLayer? = null
    private val dec = DecimalFormat("#.000").apply {
        roundingMode = RoundingMode.CEILING
    }

    private val _locationObject = MutableStateFlow(LocationObject())
    val locationObject: StateFlow<LocationObject> = _locationObject

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
                                val lonDouble = location.longitude
                                val latDouble = location.latitude
                                val address =
                                    getAddressFromLocation(application, latDouble, lonDouble)
                                val elevation = location.altitude
                                val bearing = String.format("%.0f", location.bearing) + "\u2103 TN"
                                val newLocationObject = withContext(Dispatchers.IO) {
                                    getCompleteAddress(latDouble, lonDouble)
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

    private fun fetchLocationUpdates(): Flow<Location?> = callbackFlow {
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

    private fun getAddressFromLocation(
        context: Context,
        latitude: Double,
        longitude: Double,
    ): String? {
        val geocoder = Geocoder(context, Locale.getDefault())
        return try {
            @Suppress("DEPRECATION") val addresses: List<Address> =
                geocoder.getFromLocation(latitude, longitude, 1) ?: listOf()
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
                null
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    private suspend fun getCompleteAddress(
        latitude: Double,
        longitude: Double
    ) = suspendCoroutine { cont ->
        viewModelScope.launch {
            val queryParams = QueryParameters()
            val agsString = "${latitude}N${longitude}W"
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

                        val xDelta =
                            (latitude - extent.xMin) / (extent.xMax - extent.xMin)
                        val yDelta =
                            (longitude - extent.yMin) / (extent.yMax - extent.yMin)
                        // Calculate GridLocation
                        val gridLocation = getGridLocation(secLbl, xDelta, yDelta)
                        //Calculate UTM Coordinate
                        val utmTup = sepUtm(utmString!!)
                        val utmCoordinate =
                            utmTup!!.third + " m " + utmTup.second + " m " + "Zone: " + utmTup.first
                        //Calculate Distance
                        val distance =
                            calcDistances(
                                utmTup.second!!.toInt(),
                                utmTup.third!!.toInt(),
                                extent
                            )

                        cont.resume(LocationObject().apply {
                            this.lat = String.format("%.6f", latitude)
                            this.lon = String.format("%.6f", longitude)
                            this.address = "Some Address"
                            // TODO: just for making the stateflow trigger the new value
                            this.distance = Random.nextInt().toString()
                        })
                    }
                }

                onFailure {
                    cont.resume(LocationObject())
                }
            }
        }
    }

//    private fun fetchLocationObject() {
//        //getAddress
//        //GetDateTime
//        fun getDateTime(): String {
//            return LocalDateTime.now().format(
//                DateTimeFormatter.ofPattern(
//                    "MMM d, yyyy HH:mm:ss",
//                    Locale.ENGLISH
//                )
//            )
//        }
//    }

    fun insertImageLocationInfo(imageUri: Int?, locationInfoObject: LocationObject) {
        viewModelScope.launch {
            dao.insertImageLocationInfo(
                ImageLocationInfo(
                    imageUri,
                    locationInfoObject
                )
            )
        }
    }
}
