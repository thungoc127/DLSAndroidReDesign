package com.example.dlsandroidredesign.data

import com.example.dlsandroidredesign.data.local.UserDataStore
import com.example.dlsandroidredesign.data.remote.DLSService
import com.example.dlsandroidredesign.domain.DLSRepository
import com.example.dlsandroidredesign.domain.entity.User
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import javax.inject.Inject

class DLSRepositoryImpl @Inject constructor(
    private val userDataStore: UserDataStore,
    private val dlsService: DLSService,
) : DLSRepository {

    override fun getCurrentUser(): Flow<User?> = userDataStore.getUser()

    override suspend fun login(
        username: String,
        password: String
    ): User? = withContext(Dispatchers.IO) {
        try {
            val response = dlsService.validate(username, password)
            val body = response.body()
            if(response.isSuccessful && body != null && body.success && body.id != null) {
                userDataStore.setUser(body.id!!, body.waypointgroups?: listOf())
                userDataStore.getUser().first()
            } else {
                null
            }
        } catch (error: Exception) {
            null
        }
    }

    override suspend fun getWayPoint(): String? {
        TODO("Not yet implemented")
    }
}