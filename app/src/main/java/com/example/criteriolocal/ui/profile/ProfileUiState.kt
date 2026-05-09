package com.example.criteriolocal.ui.profile

data class ProfileUiState(
    val name: String = "Usuario invitado",
    val email: String = "invitado@criteriolocal.app",
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
)
