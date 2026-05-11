package com.example.criteriolocal.ui.auth

data class RegisterUiState(
    val name: String = "",
    val email: String = "",
    val password: String = "",
    val isSubmitting: Boolean = false,
    val errorMessage: String? = null,
) {
    val canSubmit: Boolean
        get() = name.isNotBlank() &&
            email.isNotBlank() &&
            password.length >= MinPasswordLength &&
            !isSubmitting

    companion object {
        const val MinPasswordLength = 8
    }
}

sealed interface RegisterEvent {
    data object Registered : RegisterEvent
}
