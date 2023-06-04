package com.example.dlsandroidredesign

import com.google.gson.annotations.SerializedName

 data class WaypointGroupsDTO (
     @SerializedName("success")
     var success: Int? = null,
     @SerializedName("errmsg")
    var errmsg: String? = null,
    @SerializedName("id")
     var id: String? = null,
    @SerializedName("name")
    val name: String? = null,
    @SerializedName("waypointgroups")
    var waypointgroups: List<Waypointgroup>? = null
)


