package com.example.dlsandroidredesign.domain.usecase

import com.example.dlsandroidredesign.domain.DLSRepository
import com.example.dlsandroidredesign.domain.entity.User
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetCurrentUser @Inject constructor(
    private val dlsRepository: DLSRepository
) {

    operator fun invoke(): Flow<User?> {
        return dlsRepository.getCurrentUser()
    }
}