package com.example.dlsandroidredesign.domain.usecase

import android.annotation.SuppressLint
import android.content.Context
import com.example.dlsandroidredesign.data.DLSRepositoryImpl
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject


@SuppressLint("MissingPermission")
class FetchLocation @Inject constructor(@ApplicationContext private val context: Context,
                                        private val dlsRepository: DLSRepositoryImpl
) {
    suspend operator fun invoke() {
    dlsRepository.startFetchingLocation()
    }
}