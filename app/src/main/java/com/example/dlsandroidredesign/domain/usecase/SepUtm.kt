package com.example.dlsandroidredesign.domain.usecase

import javax.inject.Inject

class SepUtm @Inject constructor() {
    operator fun invoke(str: String): Triple<String?, String?, String?> {
        val utmArr = str.split(" ".toRegex()).dropLastWhile { it.isEmpty() }
            .toTypedArray()
        val zone = utmArr[0].substring(0, utmArr[0].length - 1)
        val east = utmArr[1]
        val nor = utmArr[2]
        return Triple(zone, east, nor)
    }
}
