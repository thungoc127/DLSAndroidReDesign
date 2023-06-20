package com.example.dlsandroidredesign.domain

import com.example.dlsandroidredesign.domain.entity.User
import kotlinx.coroutines.flow.Flow

interface DLSRepository {
    fun getCurrentUser(): Flow<User?>
    suspend fun login(username: String, password: String): User?
    suspend fun getWayPoint(): String?
}