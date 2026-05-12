package com.example.criteriolocal.ui.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.criteriolocal.domain.management.RegisterUserRequest
import com.example.criteriolocal.domain.management.UserManager
import kotlinx.coroutines.delay
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class RegisterViewModel(
    private val userManager: UserManager,
) : ViewModel() {

    private val _uiState = MutableStateFlow(RegisterUiState())
    val uiState: StateFlow<RegisterUiState> = _uiState.asStateFlow()

    private val _events = Channel<RegisterEvent>(capacity = Channel.BUFFERED)
    val events: Flow<RegisterEvent> = _events.receiveAsFlow()

    fun onNameChange(value: String) {
        _uiState.update { it.copy(name = value, errorMessage = null, successMessage = null) }
    }

    fun onEmailChange(value: String) {
        _uiState.update { it.copy(email = value, errorMessage = null, successMessage = null) }
    }

    fun onPasswordChange(value: String) {
        _uiState.update { it.copy(password = value, errorMessage = null, successMessage = null) }
    }

    fun onSubmit() {
        val current = _uiState.value
        if (!current.canSubmit) return
        _uiState.update { it.copy(isSubmitting = true, errorMessage = null) }
        viewModelScope.launch {
            val today = SimpleDateFormat("yyyy-MM-dd", Locale.US).format(Date())
            val result = userManager.registerUser(
                RegisterUserRequest(
                    name = current.name,
                    email = current.email,
                    password = current.password,
                    registeredOn = today,
                ),
            )
            if (result.isSuccess && result.value != null) {
                _uiState.value = RegisterUiState(
                    successMessage = "Cuenta creada con exito. Ya puedes iniciar sesion.",
                )
                delay(SuccessNavigationDelayMillis)
                _events.send(RegisterEvent.Registered)
            } else {
                _uiState.update {
                    it.copy(
                        isSubmitting = false,
                        errorMessage = result.errors.firstOrNull()?.message
                            ?: "No se pudo crear la cuenta.",
                    )
                }
            }
        }
    }

    companion object {
        private const val SuccessNavigationDelayMillis = 1_500L

        fun factory(userManager: UserManager): ViewModelProvider.Factory = viewModelFactory {
            initializer { RegisterViewModel(userManager) }
        }
    }
}
