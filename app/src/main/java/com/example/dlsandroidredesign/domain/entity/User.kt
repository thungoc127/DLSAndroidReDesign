package com.example.dlsandroidredesign.domain.entity

data class User(
    val id: String,
    val userName: String,
    var waypointgroups: List<Waypointgroup>,
    var groupIdCheck: String,
    var groupNameCheck: String
)
