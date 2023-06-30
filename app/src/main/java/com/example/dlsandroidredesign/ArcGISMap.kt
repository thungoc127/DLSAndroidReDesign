// package com.example.dlsandroidredesign
//
// import android.location.Location
// import android.util.Log
// import androidx.compose.runtime.Composable
// import com.esri.arcgisruntime.data.Feature
// import com.esri.arcgisruntime.data.QueryParameters
// import com.esri.arcgisruntime.geometry.CoordinateFormatter
// import com.esri.arcgisruntime.geometry.Envelope
// import com.esri.arcgisruntime.geometry.SpatialReferences
// import com.esri.arcgisruntime.layers.FeatureLayer
// import com.esri.arcgisruntime.loadable.LoadStatus
// import com.esri.arcgisruntime.mapping.MobileMapPackage
// import com.esri.arcgisruntime.mapping.view.MapView
//
// @Composable
// fun MapViewWithCompose(){
//     val packagePath = "sections.mmpk"
//     lateinit var mapView: MapView
//     var sectionLayer: FeatureLayer? = null
//     var mapLoaded = false
//    var lastSeenLocation: Location? = null
//    var sectionText = ""
//    var sectionDist = ""
//    val utmNorthing = ""
//    val utmEasting = ""
//    val utmZone = ""
//
//    var mobileMapPackage: MobileMapPackage = MobileMapPackage(packagePath)
//    mobileMapPackage.loadAsync()
//    mobileMapPackage.addDoneLoadingListener {
//        if (mobileMapPackage.loadStatus == LoadStatus.LOADED) {
//            // The .mmpk has been loaded successfully
//            // You can now work with the map package
//            mapView.map = mobileMapPackage.maps[0]
//            sectionLayer = mobileMapPackage.maps[0].operationalLayers[0] as FeatureLayer
//            mapLoaded = true
//            val lat: Double = lastSeenLocation!!.latitude
//            val latStr = String.format("%.6f", lat)
//            val lon : Double = lastSeenLocation!!.longitude
//            val lonStr = String.format("%.6f", lon)
//            val agsString: String = latStr +"N"+lonStr+"W"
//            val pnt = CoordinateFormatter.fromLatitudeLongitude(
//                agsString,
//                SpatialReferences.getWebMercator()
//            )
//
//            //String utmString = AGSCoordinateFormatter.utmString(from: pnt!, conversionMode: .northSouthIndicators, addSpaces: true)
//            val utmString = CoordinateFormatter.toUtm(
//                pnt,
//                CoordinateFormatter.UtmConversionMode.NORTH_SOUTH_INDICATORS,
//                true
//            )
//
//            val queryParams = QueryParameters()
//            queryParams.geometry = pnt
//            queryParams.spatialRelationship = QueryParameters.SpatialRelationship.INTERSECTS
//
//            val future = sectionLayer!!.featureTable.queryFeaturesAsync(queryParams)
//
//            future.addDoneListener {
//                try {
//                    val result = future.get()
//                    val resultIterator: Iterator<Feature> = result.iterator()
//                    if (resultIterator.hasNext()) {
//                        val feature = resultIterator.next()
//                        val extent: Envelope = feature.geometry.extent
//                        val attr: Map<String, Any> = feature.attributes
//                        var secId: String? = ""
//                        var secLbl: String? = ""
//                        var secTxt: String? = ""
//
//                        val xDelta:Double = lat
//
//                        for (key in attr.keys) {
//                            when (key) {
//                                "TTTMRRSS" -> {
//                                    secId = attr[key] as String?
//                                }
//                                "LABEL1" -> {
//                                    secLbl = attr[key] as String?
//                                }
//                                "SEC" -> {
//                                    secTxt = attr[key] as String?
//                                }
//                            }
//                        }
//
//                    } else {
//                        sectionText = ""
//                        sectionDist = ""
//                    }
//                } catch (e: Exception) {
//                    val error = "Feature search failed. Error: " + e.message
//                    /*Toast.makeText(this, error, Toast.LENGTH_LONG).show();*/Log.e(
//                        null,
//                        error
//                    )
//                }
//            }
//
//        } else {
//            // There was an error loading the .mmpk
//            val error = mobileMapPackage.loadError
//            // Handle the error
//        }
// }}
//
//
// fun calcLsd(sec: String, x: Double, y: Double): String? {
//    var lsd = 0
//    var qtr = ""
//    if (x > 0 && x <= 0.25) {
//        if (y > 0 && y <= 0.25) {
//            lsd = 4
//            qtr = "SW"
//        } else if (y > 0.25 && y <= 0.50) {
//            lsd = 5
//            qtr = "SW"
//        } else if (y > 0.50 && y <= 0.75) {
//            lsd = 12
//            qtr = "NW"
//        } else if (y > 0.75 && y <= 1.00) {
//            lsd = 13
//            qtr = "NW"
//        }
//    } else if (x > 0.25 && x <= 0.50) {
//        if (y > 0 && y <= 0.25) {
//            lsd = 3
//            qtr = "SW"
//        } else if (y > 0.25 && y <= 0.50) {
//            lsd = 6
//            qtr = "SW"
//        } else if (y > 0.50 && y <= 0.75) {
//            lsd = 11
//            qtr = "NW"
//        } else if (y > 0.75 && y <= 1.00) {
//            lsd = 14
//            qtr = "NW"
//        }
//    } else if (x > 0.50 && x <= 0.75) {
//        if (y > 0 && y <= 0.25) {
//            lsd = 2
//            qtr = "SE"
//        } else if (y > 0.25 && y <= 0.50) {
//            lsd = 7
//            qtr = "SE"
//        } else if (y > 0.50 && y <= 0.75) {
//            lsd = 10
//            qtr = "NE"
//        } else if (y > 0.75 && y <= 1.00) {
//            lsd = 15
//            qtr = "NE"
//        }
//    } else if (x > 0.75 && x <= 1.00) {
//        if (y > 0 && y <= 0.25) {
//            lsd = 1
//            qtr = "SE"
//        } else if (y > 0.25 && y <= 0.50) {
//            lsd = 8
//            qtr = "SE"
//        } else if (y > 0.50 && y <= 0.75) {
//            lsd = 9
//            qtr = "NE"
//        } else if (y > 0.75 && y <= 1.00) {
//            lsd = 16
//            qtr = "NE"
//        }
//    }
//    return "($qtr) $lsd-$sec"
// }
