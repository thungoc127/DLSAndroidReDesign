package com.example.dlsandroidredesign.ui.gallery

import android.annotation.SuppressLint
import android.app.Application
import android.net.Uri
import androidx.lifecycle.ViewModel
import com.example.dlsandroidredesign.data.local.ImageLocationInfoDAO
import com.example.dlsandroidredesign.domain.usecase.GetAllImages
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import javax.inject.Inject

@SuppressLint("MissingPermission")
@HiltViewModel
// TODO: not recommend holding application in viewModel.
class GalleryViewModel @Inject constructor(
    private val application: Application,
    private val dao: ImageLocationInfoDAO,
    private val getAllImages: GetAllImages
) : ViewModel() {
    var selectedImageUris = MutableStateFlow<List<Uri>>(emptyList<Uri>())
    fun getMergeList(addList: List<Uri>) {
        selectedImageUris.value = selectedImageUris.value + addList
        selectedImageUris.value = selectedImageUris.value.distinct()
    }
}
