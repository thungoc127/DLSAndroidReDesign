package com.example.dlsandroidredesign.domain.usecase

import com.arcgismaps.geometry.CoordinateFormatter
import com.arcgismaps.geometry.Envelope
import com.arcgismaps.geometry.SpatialReference
import com.arcgismaps.geometry.UtmConversionMode
import javax.inject.Inject

class GetDistance @Inject constructor(private val sepUtm: SepUtm) {
    operator fun invoke(x: Int, y: Int, ext: Envelope): String {
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
}