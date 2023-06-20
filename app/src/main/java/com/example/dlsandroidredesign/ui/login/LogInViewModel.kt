package com.example.dlsandroidredesign.ui.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.dlsandroidredesign.domain.usecase.LoginUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

data class UiState(
    val success: Boolean = false,
    val errorMessage: String? = null
) {
    companion object {
        val Empty = UiState()
    }
}

@HiltViewModel
class LogInViewModel @Inject constructor(
    private val loginUseCase: LoginUseCase
) : ViewModel() {
    fun validate(username: String, password: String) {
        viewModelScope.launch {
            val success = loginUseCase.invoke(username, password)
            if (success) {
                _success.value = true

            } else {
                _errorMessage.value = "Login fail"
            }
        }
    }

    private val _success = MutableStateFlow(false)
    private val _errorMessage = MutableStateFlow<String?>(null)

    val uiState = combine(
        _success,
        _errorMessage
    ) { success, errorMessage ->
        UiState(success, errorMessage)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = UiState.Empty
    )
}