package com.example.criteriolocal.ui.business

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import androidx.lifecycle.createSavedStateHandle
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class BusinessDetailViewModel(
    savedStateHandle: SavedStateHandle,
) : ViewModel() {

    private val businessId: Long = savedStateHandle.get<Long>(BusinessIdArg) ?: 0L

    private val _uiState = MutableStateFlow(
        BusinessDetailUiState(businessId = businessId, isLoading = false),
    )
    val uiState: StateFlow<BusinessDetailUiState> = _uiState.asStateFlow()

    companion object {
        const val BusinessIdArg = "businessId"

        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                BusinessDetailViewModel(savedStateHandle = createSavedStateHandle())
            }
        }
    }
}
