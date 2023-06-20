package com.example.dlsandroidredesign.domain.entity

import com.google.gson.annotations.SerializedName

 class Waypointgroup (
    @SerializedName("groupid")
    var groupid: String? = null,
    @SerializedName("groupname")
    var groupname: String? = null,
)