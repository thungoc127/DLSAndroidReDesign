package com.example.dlsandroidredesign.domain.usecase

import com.example.dlsandroidredesign.domain.DLSRepository
import com.example.dlsandroidredesign.domain.entity.User
import com.google.gson.JsonObject
import javax.inject.Inject

class LoginUseCase @Inject constructor(
    private val dlsRepository: DLSRepository
) {

    suspend operator fun invoke(username: String, password: String): Boolean {
        val user = dlsRepository.login(username, password)
        return user != null
    }
}
