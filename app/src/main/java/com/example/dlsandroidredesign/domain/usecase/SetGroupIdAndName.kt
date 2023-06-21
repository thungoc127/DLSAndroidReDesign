package com.example.dlsandroidredesign.domain.usecase

import com.example.dlsandroidredesign.domain.DLSRepository
import javax.inject.Inject

class SetGroupIdAndName @Inject constructor(private val dlsRepository: DLSRepository){
    suspend operator fun invoke(groupId:String,groupName:String) {
        dlsRepository.setGroupIdAndName(groupId,groupName)
    }
}