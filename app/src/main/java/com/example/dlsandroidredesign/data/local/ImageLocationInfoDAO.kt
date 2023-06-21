package com.example.dlsandroidredesign.data.local

import android.net.Uri
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.dlsandroidredesign.domain.entity.LocationObject


@Dao
interface ImageLocationInfoDAO {
    @Insert
    suspend fun insertImageLocationInfo(imageLocationInfo: ImageLocationInfo)
    @Query("DELETE FROM ImageLocationInfo WHERE uriImage = :uriImage")
    suspend fun deleteImageLocationInfo(uriImage: Uri)

    @Query("SELECT locationObject FROM ImageLocationInfo WHERE uriImage = :uriImage")
    suspend fun getLocationObjectByUri(uriImage: Uri): LocationObject
}