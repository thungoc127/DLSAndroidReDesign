package com.example.dlsandroidredesign.ui.mainScreen

import android.graphics.Bitmap
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject


@HiltViewModel
class MainScreenViewModel @Inject constructor(
    ):ViewModel(){
    val zoomRatio= mutableStateOf(0.0f)
    val galleryModalSheetVisible = mutableStateOf(false)
    var bmp =  mutableStateOf<Bitmap?>(null)
}
