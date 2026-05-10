package com.example.criteriolocal.ui.rating

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class RatingFormViewModel(
    businessId: Long,
) : ViewModel() {

    private val _uiState = MutableStateFlow(RatingFormUiState(businessId = businessId))
    val uiState: StateFlow<RatingFormUiState> = _uiState.asStateFlow()

    companion object {
        fun factory(businessId: Long): ViewModelProvider.Factory = viewModelFactory {
            initializer { RatingFormViewModel(businessId) }
        }
    }
}
