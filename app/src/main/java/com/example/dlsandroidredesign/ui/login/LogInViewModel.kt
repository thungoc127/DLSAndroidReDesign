package com.example.dlsandroidredesign.ui.login

import android.content.Context
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ModalBottomSheetState
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.dlsandroidredesign.domain.entity.User
import com.example.dlsandroidredesign.domain.usecase.GetCurrentUser
import com.example.dlsandroidredesign.domain.usecase.GetLogInStatus
import com.example.dlsandroidredesign.domain.usecase.LoginUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@OptIn(ExperimentalMaterialApi::class)
@HiltViewModel
class LogInViewModel @Inject constructor(
    @ApplicationContext context: Context,
    private val loginUseCase: LoginUseCase,
    private val getCurrentUser: GetCurrentUser,
    private val getLogInStatus: GetLogInStatus

) : ViewModel() {
    var loginSheetState = ModalBottomSheetState(initialValue = ModalBottomSheetValue.Hidden, isSkipHalfExpanded = true)
    var loginVisible = mutableStateOf(false)
    fun setLoginVisible(newValue: Boolean) {
        loginVisible.value = newValue
    }
    var currentUser: Flow<User?> = getCurrentUser.invoke()
    val isLogInSuccess = getLogInStatus.invoke()
    val success = MutableStateFlow(false)

    var errorMessage = MutableStateFlow("")
    fun validate(username: String, password: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val loginStatus = async { loginUseCase.invoke(username, password) }
            if (loginStatus.await()) {
                success.value = true
                delay(1000)
                setLoginVisible(false)
                errorMessage.value = ""
            } else {
                errorMessage.value = "Failed! Please try again!"
                success.value = false
            }
        }
    }
}
