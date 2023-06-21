package com.example.dlsandroidredesign.domain.entity

data class CheckBox(
    var latLon: Boolean = true,
    val elevation: Boolean = true,
    val gridLocation: Boolean = true,
    val distance: Boolean = true,
    val utmCoordinate: Boolean = true,
    val bearing: Boolean = true,
    val address: Boolean = true,
    val date: Boolean = true,
    val cusText: Boolean = true
)
