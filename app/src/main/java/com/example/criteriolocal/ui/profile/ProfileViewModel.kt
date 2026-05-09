package com.example.criteriolocal.ui.profile

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class ProfileViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(
        ProfileUiState(
            name = "Erick Pineda",
            email = "erickestuardopineda82004@gmail.com",
        ),
    )
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()
}
