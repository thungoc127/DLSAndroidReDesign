package com.example.dlsandroidredesign.ui.setting

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.dlsandroidredesign.domain.entity.CheckBox
import com.example.dlsandroidredesign.domain.usecase.GetAutoUploadStatus
import com.example.dlsandroidredesign.domain.usecase.GetCheckBoxUseCase
import com.example.dlsandroidredesign.domain.usecase.GetCusText
import com.example.dlsandroidredesign.domain.usecase.GetPhotoSize
import com.example.dlsandroidredesign.domain.usecase.GetUploadSize
import com.example.dlsandroidredesign.domain.usecase.SetAutoUploadStatus
import com.example.dlsandroidredesign.domain.usecase.SetCheckBoxUseCase
import com.example.dlsandroidredesign.domain.usecase.SetCusText
import com.example.dlsandroidredesign.domain.usecase.SetPhotoSize
import com.example.dlsandroidredesign.domain.usecase.SetUploadSize
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

// TODO: alt + enter on the val
@HiltViewModel
class SettingFragmentViewModel @Inject constructor(
    getCusText: GetCusText,
    private val setCusText: SetCusText,
    getCheckBoxUseCase: GetCheckBoxUseCase,
    private val setCheckBoxUseCase: SetCheckBoxUseCase,
    private val setAutoUploadStatus: SetAutoUploadStatus,
    getAutoUploadStatus: GetAutoUploadStatus,
    getPhotoSize: GetPhotoSize,
    private val setPhotoSize: SetPhotoSize,
    private val setUploadSize: SetUploadSize,
    getUploadSize: GetUploadSize

) : ViewModel() {

    val uploadSize = getUploadSize.invoke()
    fun setUploadSize(photoSize: String) {
        viewModelScope.launch(Dispatchers.IO) { setUploadSize.invoke(photoSize) }
    }

    val photoSize = getPhotoSize.invoke()
    fun setPhotoSize(photoSize: String) {
        viewModelScope.launch(Dispatchers.IO) { setPhotoSize.invoke(photoSize) }
    }
    private val _waypointGroupName = MutableStateFlow("")
    val waypointGroupName: StateFlow<String> = _waypointGroupName

    val autoUploadStatus = getAutoUploadStatus.invoke()

    // TODO: Recommend to use IO Dispatcher in repository. In viewModel, left just only viewModelScope.launch (let the data layer handle switching scope)
    fun setAutoUpload(isAutoUpload: Boolean) = viewModelScope.launch { withContext(Dispatchers.IO) { setAutoUploadStatus.invoke(isAutoUpload) } }

    var isLoginSuccessful by mutableStateOf(false)
    val cusText = getCusText.invoke()

    val checkBox = getCheckBoxUseCase().stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = CheckBox()
    )

    fun setCheckBox(checkBoxKey: String, newValue: Boolean) {
        viewModelScope.launch(Dispatchers.IO) { setCheckBoxUseCase(checkBoxKey, newValue) }
    }

    fun setCusText(cusText: String) {
        viewModelScope.launch(Dispatchers.IO) { setCusText.invoke(cusText) }
    }

//    fun setCheckBoxList(checkBoxList:List<Boolean>){
//        viewModelScope.launch { setCheckBoxList.invoke(checkBoxList) }
//    }
}
