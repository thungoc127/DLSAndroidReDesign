package com.example.dlsandroidredesign.ui.mainScreen

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ModalBottomSheetState
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.dlsandroidredesign.domain.usecase.GetAllImages
import com.example.dlsandroidredesign.domain.usecase.GetLocationInfoUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.async
import javax.inject.Inject


@OptIn(ExperimentalMaterialApi::class)
@HiltViewModel
class MainScreenViewModel @Inject constructor(@ApplicationContext private val context: Context,
                                              private val getLocationInfoUseCase: GetLocationInfoUseCase
                                              ,private val getAllImages: GetAllImages
):ViewModel(){
    val zoomRatio= mutableStateOf(0.0f)
    val galleryModalSheetVisible = mutableStateOf(false)
    val settingSheetState = ModalBottomSheetState(initialValue = ModalBottomSheetValue.Hidden, isSkipHalfExpanded = true)
    val loginAndWaypointgroupSheetState = ModalBottomSheetState(initialValue = ModalBottomSheetValue.Hidden, isSkipHalfExpanded = true)
    var waypointGroupSheetState  = ModalBottomSheetState(initialValue = ModalBottomSheetValue.Hidden, isSkipHalfExpanded = true)
    var bmp =  mutableStateOf<Bitmap?>(null)
    var allImages = mutableStateOf(emptyList<Uri>())
    fun getAllImage():List<Uri> {
        allImages.value= viewModelScope.async {getAllImages.invoke()}.getCompleted()
        return allImages.value
    }











}
