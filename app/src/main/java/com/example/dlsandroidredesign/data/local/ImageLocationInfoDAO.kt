package com.example.dlsandroidredesign.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert


@Dao
interface ImageLocationInfoDAO {
    @Insert
    suspend fun insertImageLocationInfo(imageLocationInfo: ImageLocationInfo)
    @Delete
    suspend fun deleteImageLocationInfo(imageLocationInfo: ImageLocationInfo)
/*    @Query("SELECT locationObject from ImageLocationInfo where uriImage =:UriImageInput ")
    suspend fun getImageLocationInfoByUri(UriImageInput: Uri): List<LocationObject>*/
}