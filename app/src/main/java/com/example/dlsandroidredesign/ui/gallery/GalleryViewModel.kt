package com.example.dlsandroidredesign.ui.gallery

import android.annotation.SuppressLint
import android.app.Application
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.dlsandroidredesign.data.local.ImageLocationInfoDAO
import com.example.dlsandroidredesign.domain.usecase.GetAllImages
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import javax.inject.Inject

@SuppressLint("MissingPermission")
@HiltViewModel
class GalleryViewModel @Inject constructor(
    private val application: Application,
    private val dao: ImageLocationInfoDAO,
    private val getAllImages: GetAllImages
) : ViewModel() {
    val allImages: List<Uri> =  viewModelScope.async {getAllImages.invoke()}.getCompleted()
}