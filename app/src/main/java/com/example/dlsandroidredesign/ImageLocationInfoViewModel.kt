package com.example.dlsandroidredesign

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import android.location.Address
import android.location.Geocoder
import android.os.Looper
import android.util.Log
<<<<<<< HEAD
import android.widget.Toast
=======
>>>>>>> origin/master
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
import com.arcgismaps.mapping.view.MapView
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.IOException
import java.math.RoundingMode
import java.text.DecimalFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class ImageLocationInfoViewModel @Inject constructor(
    private val application: Application,
    private val dao: ImageLocaitonInfoDAO) : ViewModel() {

    private val fusedLocationClient = LocationServices.getFusedLocationProviderClient(application)
    private val packagePath = File(application.getExternalFilesDir(null), "sections.mmpk").path
    private val mobileMapPackage = MobileMapPackage(packagePath)

<<<<<<< HEAD
    private val _location = MutableStateFlow<LocationObject?>(LocationObject())
=======
    private val _location = MutableStateFlow<LocationObject?>(null)
>>>>>>> origin/master
    val location: StateFlow<LocationObject?> = _location

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

    init {
        viewModelScope.launch {
<<<<<<< HEAD
            Log.d("load: ", "init+load")
=======
>>>>>>> origin/master
            mobileMapPackage.load()
             mobileMapPackage.loadStatus.collect {
                 Log.d("status: ", it.toString())
                when(it) {
                    is LoadStatus.FailedToLoad -> {}
                    LoadStatus.Loaded -> {
                        getLocationInfo()
                    }
                    LoadStatus.Loading -> {}
                    LoadStatus.NotLoaded -> {}
                }
            }
        }
    }

    @SuppressLint("MissingPermission")
    private fun getLocationInfo() {
<<<<<<< HEAD
        //ARCGIS

=======
        var locationObject = LocationObject()
        var lat: String = ""
        var lon: String = ""
        var elevation: Double = 0.0
        var gridLocation: String = ""
        var distance: String = ""
        var utmCoordinate: String = ""
        var bearing: String = ""
        var address: String = ""
        var date: String = ""

        //ARCGIS



>>>>>>> origin/master
        var sectionLayer: FeatureLayer? = null
        var mMapView: MapView? = null
        val dec = DecimalFormat("#.000")
        dec.roundingMode = RoundingMode.CEILING

        var utmString: String? = null

        val locationRequest: LocationRequest =
            LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 1000).build()

        val locationCallback = object : LocationCallback() {
            @SuppressLint("MissingPermission")
            override fun onLocationResult(locationResult: LocationResult) {
                super.onLocationResult(locationResult)
                val location = fusedLocationClient.lastLocation
                location.addOnSuccessListener { location ->
                    if (location != null) {
                        // Location retrieved successfully
                        val lonDouble = location.longitude
                        val latDouble = location.latitude
<<<<<<< HEAD
                        val elevationDouble = location.altitude
                        val bearingDouble  = location.bearing
                        _location.value!!.address = getAddressFromLocation(application, latDouble, lonDouble)
                        _location.value!!.lat = String.format("%.6f", latDouble)
                        _location.value!!.lon = String.format("%.6f", lonDouble)
                        _location.value!!.elevation = String.format("%.6f", elevationDouble)
                        _location.value!!.bearing = String.format("%.0f", bearingDouble) + "\u2103 TN"

                        val agsString: String = _location.value!!.lat + "N" + _location.value!!.lat + "W"
=======
                        getAddressFromLocation(application, latDouble, lonDouble, locationObject)
                        elevation = location.altitude
                        bearing = String.format("%.0f", location.bearing) + "\u2103 TN"
                        lat = String.format("%.6f", latDouble)
                        lon = String.format("%.6f", lonDouble)
                        locationObject.lat = lat
                        locationObject.lon = lon
                        val agsString: String = lat + "N" + lon + "W"
>>>>>>> origin/master
                        //Calculate the GridLocation (SW) 6-15-25-2W5
                        val pnt = CoordinateFormatter.fromLatitudeLongitudeOrNull(
                            agsString,
                            SpatialReference.webMercator()
                        )
                        // Formatter
                        if (pnt != null) {
                            utmString = CoordinateFormatter.toUtmOrNull(
                                pnt,
                                UtmConversionMode.NorthSouthIndicators,
                                true
                            )

                            if (utmString != null) {

                            }
                        }
                        pnt?.let {

                        }
                        //String utmString = AGSCoordinateFormatter.utmString(from: pnt!, conversionMode: .northSouthIndicators, addSpaces: true)

                        val queryParams = QueryParameters()
                        queryParams.geometry = pnt
                        queryParams.spatialRelationship = SpatialRelationship.Intersects
                        viewModelScope.launch {
                            withContext(Dispatchers.Default) {
                                val result = sectionLayer?.featureTable?.queryFeatures(queryParams)

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
                                                    "TTTMRRSS" -> {
                                                        secId = attr[key] as String?
                                                    }

                                                    "LABEL1" -> {
                                                        secLbl = attr[key] as String?
                                                    }

                                                    "SEC" -> {
                                                        secTxt = attr[key] as String?
                                                    }
                                                }
                                            }

                                            val xDelta =
                                                (lonDouble - extent.xMin) / (extent.xMax - extent.xMin)
                                            val yDelta =
                                                (latDouble - extent.yMin) / (extent.yMax - extent.yMin)
                                            //Caculate GridLocation
<<<<<<< HEAD
                                            _location.value!!.gridLocation =
                                                getGridLocation(secLbl, xDelta, yDelta)
                                            //Calculate UTM Coordinate
                                            val utmTup = sepUtm(utmString!!)
                                            _location.value!!.utmCoordinate =
                                                utmTup!!.third + " m " + utmTup!!.second + " m " + "Zone: " + utmTup.first
                                            //Calculate Distance
                                            _location.value!!.distance =
=======
                                            gridLocation = getGridLocation(secLbl, xDelta, yDelta)
                                            //Calculate UTM Coordinate
                                            val utmTup = sepUtm(utmString!!)
                                            utmCoordinate =
                                                utmTup!!.third + " m " + utmTup!!.second + " m " + "Zone: " + utmTup.first
                                            //Calculate Distance
                                            distance =
>>>>>>> origin/master
                                                calcDistances(
                                                    utmTup.second!!.toInt(),
                                                    utmTup.third!!.toInt(),
                                                    extent
                                                )
<<<<<<< HEAD
                                        }

                                            onFailure {
                                                Toast.makeText(
                                                    application,
                                                    "Fail Query",
                                                    Toast.LENGTH_SHORT
                                                )
                                            }
=======

                                            _location.value = LocationObject().apply {
                                                this.lat = lat
                                                this.lon = lon
                                            }

//                                            onFailure {
//                                                Toast.makeText(
//                                                    context,
//                                                    "Fail Query",
//                                                    Toast.LENGTH_SHORT
//                                                )
//                                            }
>>>>>>> origin/master
                                        }
                                    }

                                }
                            }
                        }
                    }
                }
<<<<<<< HEAD
=======
            }

>>>>>>> origin/master

            //getAddress
            fun getAddressFromLocation(
                context: Context,
                latitude: Double,
                longitude: Double,
<<<<<<< HEAD
            ):String {
=======
                locationInfoObject: LocationObject
            ) {
>>>>>>> origin/master
                val geocoder = Geocoder(context, Locale.getDefault())
                try {
                    @Suppress("DEPRECATION") val addresses: List<Address> =
                        geocoder.getFromLocation(latitude, longitude, 1) ?: listOf()
<<<<<<< HEAD
                      if (addresses.isNotEmpty()) {
=======
                    return if (addresses.isNotEmpty()) {
>>>>>>> origin/master
                        val address: Address = addresses[0]

                        // Retrieve the address information
                        val addressLine: String = address.getAddressLine(0)
                        val city: String = address.locality
                        val state: String = address.adminArea
                        val country: String = address.countryName
                        val postalCode: String = address.postalCode
<<<<<<< HEAD
                         return addressLine
                     }
                     else {
=======
                        locationInfoObject.address = addressLine
                    } else {
>>>>>>> origin/master

                    }
                } catch (e: IOException) {
                    e.printStackTrace()
                }
<<<<<<< HEAD
                return ""
=======
>>>>>>> origin/master
            }

            //GetDateTime
            fun getDateTime(): String {
                var dataTime = LocalDateTime.now()
                val dateTimeFormatted =
                    dataTime.format(DateTimeFormatter.ofPattern("MMM d, yyyy HH:mm:ss", Locale.ENGLISH))

                return dateTimeFormatted
            }
        }

        sectionLayer = mobileMapPackage.maps
            .getOrNull(0)
            ?.operationalLayers
            ?.getOrNull(0) as? FeatureLayer
        fusedLocationClient.requestLocationUpdates(
            locationRequest,
            locationCallback,
            Looper.getMainLooper()
        )
    }
}
