package com.example.dlsandroidredesign.domain.usecase

import javax.inject.Inject

class GetGridLocation @Inject constructor() {
    operator fun invoke(sec: String?, x: Double, y: Double): String {
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
}
