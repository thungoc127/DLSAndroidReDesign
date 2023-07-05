package com.example.dlsandroidredesign.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes
import androidx.compose.ui.unit.dp

val Shapes = Shapes(
    small = RoundedCornerShape(4.dp),
    medium = RoundedCornerShape(4.dp),
    large = RoundedCornerShape(0.dp),
    extraLarge = RoundedCornerShape(3.dp)
)

val RoundedCornerShape = Shapes(
    small = RoundedCornerShape(5.dp),
    medium = RoundedCornerShape(10.dp),
    large = RoundedCornerShape(15.dp)
)

val BottomSheetRoundedCornerShape = Shapes(
    small = RoundedCornerShape(topStart = 5.dp, topEnd = 5.dp),
    medium = RoundedCornerShape(topStart = 10.dp, topEnd = 10.dp),
    large = RoundedCornerShape(topStart = 15.dp, topEnd = 15.dp)
)
