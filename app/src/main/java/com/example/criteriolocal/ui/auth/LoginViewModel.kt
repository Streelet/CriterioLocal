package com.example.criteriolocal.ui.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.criteriolocal.core.session.SessionManager
import com.example.criteriolocal.domain.management.LoginRequest
import com.example.criteriolocal.domain.management.UserManager
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class LoginViewModel(
    private val userManager: UserManager,
    private val sessionManager: SessionManager,
) : ViewModel() {

    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    private val _events = Channel<LoginEvent>(capacity = Channel.BUFFERED)
    val events: Flow<LoginEvent> = _events.receiveAsFlow()

    fun onEmailChange(value: String) {
        _uiState.update { it.copy(email = value, errorMessage = null) }
    }

    fun onPasswordChange(value: String) {
        _uiState.update { it.copy(password = value, errorMessage = null) }
    }

    fun onSubmit() {
        val current = _uiState.value
        if (!current.canSubmit) return
        _uiState.update { it.copy(isSubmitting = true, errorMessage = null) }
        viewModelScope.launch {
            val result = userManager.login(
                LoginRequest(email = current.email, password = current.password),
            )
            val user = result.value
            if (result.isSuccess && user != null) {
                sessionManager.setUserId(user.id)
                _uiState.value = LoginUiState()
                _events.send(LoginEvent.SignedIn)
            } else {
                _uiState.update {
                    it.copy(
                        isSubmitting = false,
                        errorMessage = result.errors.firstOrNull()?.message
                            ?: "No se pudo iniciar sesion.",
                    )
                }
            }
        }
    }

    companion object {
        fun factory(
            userManager: UserManager,
            sessionManager: SessionManager,
        ): ViewModelProvider.Factory = viewModelFactory {
            initializer { LoginViewModel(userManager, sessionManager) }
        }
    }
}
