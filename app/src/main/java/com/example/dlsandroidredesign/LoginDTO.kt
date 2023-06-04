package com.example.dlsandroidredesign

import com.google.gson.annotations.SerializedName

data class LoginDTO(
    @SerializedName("success")
    var _success: Int? = null,
    @SerializedName("errmsg")
    var errmsg: String? = null,
    @SerializedName("id")
    var id: String? = null,
    @SerializedName("name")
    var name: String? = null,
    @SerializedName("waypointgroups")
    var waypointgroups: List<Waypointgroup>? = null

) {
    val success: Boolean
        get() = _success == 1
}

