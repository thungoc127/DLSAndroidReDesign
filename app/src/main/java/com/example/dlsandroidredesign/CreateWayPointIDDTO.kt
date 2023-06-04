package com.example.dlsandroidredesign

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class CreateWayPointIDDTO {
    @SerializedName("success")
    @Expose
    var success: Int? = null

    @SerializedName("errmsg")
    @Expose
    var errmsg: String? = null

    @SerializedName("waypointid")
    @Expose
    var waypointid: String? = null
}