package com.example.dlsandroidredesign

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.location.Address
import android.location.Geocoder
import android.net.Uri
import android.os.Looper
import android.util.Log
import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.lifecycle.lifecycleScope
import com.arcgismaps.LoadStatus
import com.arcgismaps.data.QueryParameters
import com.arcgismaps.data.SpatialRelationship
import com.arcgismaps.geometry.CoordinateFormatter
import com.arcgismaps.geometry.Envelope
import com.arcgismaps.geometry.SpatialReference
import com.arcgismaps.geometry.UtmConversionMode
import com.arcgismaps.mapping.MobileMapPackage
import com.arcgismaps.mapping.layers.FeatureLayer
import com.google.android.gms.location.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.IOException
import java.math.RoundingMode
import java.text.DecimalFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale


@SuppressLint("MissingPermission", "CoroutineCreationDuringComposition")
@Composable
fun getCurrentLocation1() {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val fusedLocationClient by remember { mutableStateOf(LocationServices.getFusedLocationProviderClient(context)) }
    val locationRequest: LocationRequest by remember { mutableStateOf(LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 1000).build()) }
    val packagePath = File(context.getExternalFilesDir(null), "sections.mmpk").path
    val mobileMapPackage = MobileMapPackage(packagePath)
    var sectionLayer: FeatureLayer? = null
    val lifecycleOwner = LocalLifecycleOwner.current
    val dec = DecimalFormat("#.000")
    dec.roundingMode = RoundingMode.CEILING
    val locationObject: LocationObject = LocationObject()

    coroutineScope.launch(lifecycleOwner.lifecycleScope.coroutineContext) {
        mobileMapPackage.load().getOrElse {
        }
        mobileMapPackage.maps.first()
    }


    var gridLocation by remember { mutableStateOf("") }
    var utmCoordinate by remember { mutableStateOf("") }
    var distance by remember { mutableStateOf("") }
    var latitude by remember { mutableStateOf(0.0) }
    var longitude by remember { mutableStateOf(0.0) }
    var elevation by remember { mutableStateOf(0.0) }
    var formattedBearing by remember { mutableStateOf("") }
    var address by remember { mutableStateOf("") }
    //dateTim

    var utmString: String? = null
    var locationInfoLeft:String = ""
    var locationInfoRight:String = ""



    //getAddress
    fun getAddressFromLocation(context: Context, latitude: Double, longitude: Double): String {
        val geocoder = Geocoder(context, Locale.getDefault())
        try {
            @Suppress("DEPRECATION") val addresses: List<Address> =
                geocoder.getFromLocation(latitude, longitude, 1) ?: listOf()
            return if (addresses.isNotEmpty()) {
                val address: Address = addresses[0]

                // Retrieve the address information
                val addressLine: String = address.getAddressLine(0)
                val city: String = address.locality
                val state: String = address.adminArea
                val country: String = address.countryName
                val postalCode: String = address.postalCode
                addressLine
            } else {
                " "
            }
        } catch (e: IOException) {
            e.printStackTrace()
            return " "
        }
    }



    Log.d("locationCallback", "called")
    val locationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            super.onLocationResult(locationResult)
            val location = fusedLocationClient.lastLocation
            location.addOnSuccessListener { location ->
                if (location != null) {
                    // Location retrieved successfully
                    /////date time
                    var dataTime = LocalDateTime.now()
                    val dateTimeFormatted = dataTime.format(DateTimeFormatter.ofPattern("MMM d, yyyy HH:mm:ss", Locale.ENGLISH))
                    locationObject.date = dateTimeFormatted
                    ///address
                    address = getAddressFromLocation(context, latitude, longitude)
                    longitude= location.longitude
                    latitude= location.latitude
                    elevation = location.altitude
                    var bearing: Float = location.bearing

                    val formattedBearing = String.format("%.0f", bearing) + "\u2103 TN"
                    val latStr = String.format("%.6f", latitude)
                    val lonStr = String.format("%.6f", longitude)

                    locationObject.lat =latStr
                    locationObject.lon= lonStr
                    locationObject.bearing = formattedBearing


                    //Calculate the GridLocation (SW) 6-15-25-2W5
                    val agsString: String = latStr + "N" + lonStr + "W"
                    val pnt = CoordinateFormatter.fromLatitudeLongitudeOrNull(
                        agsString,
                        SpatialReference.webMercator()
                    )
                    // Formatter point to Utm
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
                    coroutineScope.launch {
                        withContext(Dispatchers.Default ) {
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

                                        val xDelta = (longitude - extent.xMin) / (extent.xMax - extent.xMin)
                                        val yDelta = (latitude - extent.yMin) / (extent.yMax - extent.yMin)
                                        //Caculate GridLocation
                                        gridLocation = getGridLocation1(secLbl, xDelta, yDelta)
                                        //Calculate UTM Coordinate
                                        val utmTup = sepUtm1(utmString!!)
                                        utmCoordinate =
                                            utmTup!!.third + " m " + utmTup!!.second + " m " + "Zone: " + utmTup.first
                                        //Calculate Distance
                                        distance =
                                            calcDistances1(utmTup.second!!.toInt(), utmTup.third!!.toInt(), extent)
                                    }

                                }

                                onFailure {
                                    Toast.makeText(context,"Fail Query", Toast.LENGTH_SHORT)
                                }
                            }
                        }

                    }
                }
            }

        }
    }

    val mapStatus by mobileMapPackage.loadStatus.collectAsState()
    when(mapStatus) {
        is LoadStatus.FailedToLoad -> {}
        LoadStatus.Loaded -> {
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
        LoadStatus.Loading -> {}
        LoadStatus.NotLoaded -> {}
    }


}





fun getGridLocation1(sec: String?, x: Double, y: Double): String {
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

fun sepUtm1(str: String): Triple<String?, String?, String?>? {
    val utmArr = str.split(" ".toRegex()).dropLastWhile { it.isEmpty() }
        .toTypedArray()
    val zone = utmArr[0].substring(0, utmArr[0].length - 1)
    val east = utmArr[1]
    val nor = utmArr[2]
    return Triple(zone, east, nor)
}

fun calcDistances1(x: Int, y: Int, ext: Envelope): String {
    val strMin = String.format("%.6f", ext.yMin) + "N " + String.format("%.6f", ext.xMin) + "W"
    val pntMin = CoordinateFormatter.fromLatitudeLongitudeOrNull(strMin, SpatialReference.webMercator())

    val utmMin = CoordinateFormatter.toUtmOrNull(pntMin!!,UtmConversionMode.NorthSouthIndicators,true)

    val minTup = sepUtm1(utmMin!!)
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

fun Uri.loadBitmap11(context: Context): Bitmap? {
    return context.contentResolver.openInputStream(this)?.use { inputStream ->
        BitmapFactory.decodeStream(inputStream)
    }
}