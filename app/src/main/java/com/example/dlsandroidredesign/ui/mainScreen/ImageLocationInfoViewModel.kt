package com.example.dlsandroidredesign.ui.mainScreen

import android.annotation.SuppressLint
import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.arcgismaps.mapping.MobileMapPackage
import com.arcgismaps.mapping.layers.FeatureLayer
import com.example.dlsandroidredesign.data.local.ImageLocationInfo
import com.example.dlsandroidredesign.data.local.ImageLocationInfoDAO
import com.example.dlsandroidredesign.data.local.PreferencesDataStore
import com.example.dlsandroidredesign.domain.entity.LocationObject
import com.example.dlsandroidredesign.domain.usecase.GetLocationInfoUseCase
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.io.File
import java.math.RoundingMode
import java.text.DecimalFormat
import javax.inject.Inject

@SuppressLint("MissingPermission")
@HiltViewModel
class ImageLocationInfoViewModel @Inject constructor(
    private val application: Application,
    private val dao: ImageLocationInfoDAO,
    private val imageLocationInfoUseCase: GetLocationInfoUseCase
) : ViewModel() {
    val preferenceDataStore = PreferencesDataStore(application)
    private val fusedLocationClient = LocationServices.getFusedLocationProviderClient(application)
    private val locationRequest: LocationRequest = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 1000).build()
    private val packagePath = File(application.getExternalFilesDir(null), "sections.mmpk").path
    private val mobileMapPackage = MobileMapPackage(packagePath)
    private var sectionLayer: FeatureLayer? = null
    private val dec = DecimalFormat("#.000").apply {
        roundingMode = RoundingMode.CEILING
    }
    fun insertImageLocationInfo(imageUri: Int?, locationInfoObject: LocationObject) {
        viewModelScope.launch {
            dao.insertImageLocationInfo(
                ImageLocationInfo(
                    imageUri,
                    locationInfoObject
                )
            )
        }}


    val getLocationObject: StateFlow<LocationObject> =  viewModelScope.async {imageLocationInfoUseCase.invoke()}.getCompleted()










}




