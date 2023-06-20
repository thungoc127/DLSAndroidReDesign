package com.example.dlsandroidredesign.data.remote

import com.google.gson.JsonObject
import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Query

interface DLSService {

    @GET("dlsphoto-validate")
    suspend fun validate(
        @Query("username") userName: String?,
        @Query("password") pass: String?
    ): Response<LoginDTO>

    @GET("dlsphoto-waypointgroups")
    suspend fun getWayPointGroups(
        @Query("apiKey") apiKey: String?
    ): Response<WaypointGroupsDTO>

    @POST("dlsphoto-upload")
    suspend fun getWayPointID(
        @Query("apiKey") apiKey: String?,
        @Body bean: JsonObject?
    ): Response<CreateWayPointIDDTO>

    @Multipart
    @POST("dlsphoto-uploadfile")
    suspend fun uploadPhoto(
        @Query("apiKey") apiKey: String?,
        @Query("waypointId") waypointId: String?,
        @Part photo: MultipartBody.Part?
    ): Response<CreateWayPointIDDTO>
}