package com.example.dlsandroidredesign.domain.entity

import com.example.dlsandroidredesign.Waypointgroup

data class User(
    val id: String,
    var waypointgroups: List<Waypointgroup>
)
