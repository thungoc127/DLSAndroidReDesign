package com.example.dlsandroidredesign.domain.usecase

import android.net.Uri
import android.util.Log
import com.example.dlsandroidredesign.domain.DLSRepository
import com.example.dlsandroidredesign.domain.entity.LocationObject
import com.google.gson.JsonObject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import javax.inject.Inject

class GetWayPointId @Inject constructor(private val dlsRepository: DLSRepository, ) {
    suspend operator fun invoke(uriImage: Uri?, locationObject: LocationObject): String {
        var result = ""
        withContext(Dispatchers.IO) {
            Log.d("getWayPointId", "uriImage:$$uriImage")
            Log.d("getWayPointId", "locationObjectawait:$locationObject")
            val c = Calendar.getInstance().time
            val df = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val formattedDate = df.format(c)
            val obj = JSONObject()
            val wayPointObj = JsonObject()
            wayPointObj.addProperty("date", formattedDate)
            wayPointObj.addProperty("lat", locationObject.lat)
            wayPointObj.addProperty("lon", locationObject.lon)
            val currentUser = dlsRepository.getCurrentUser()
            wayPointObj.addProperty("groupid", currentUser.first()!!.groupIdCheck)
            Log.d("getWayPointId", "locationObjectawait:$locationObject")
            Log.d("AutoUpload", "groupId:${currentUser.first()!!.groupIdCheck}")
            obj.put("waypoint", wayPointObj)
            result = dlsRepository.getWayPointId(currentUser.first()!!.id,wayPointObj)!!
            Log.d("AutoUpload", "wayPointObj:$wayPointObj")
        }
        return result
}
}
