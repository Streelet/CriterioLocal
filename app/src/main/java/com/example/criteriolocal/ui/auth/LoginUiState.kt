package com.example.criteriolocal.ui.auth

data class LoginUiState(
    val email: String = "",
    val password: String = "",
    val isSubmitting: Boolean = false,
    val errorMessage: String? = null,
) {
    val canSubmit: Boolean
        get() = email.isNotBlank() && password.isNotBlank() && !isSubmitting
}

sealed interface LoginEvent {
    data object SignedIn : LoginEvent
}
