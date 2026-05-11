package com.example.criteriolocal.ui.business

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.criteriolocal.domain.contract.FrontendContractManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

class BusinessDetailViewModel(
    private val businessId: Long,
    private val frontendContractManager: FrontendContractManager,
) : ViewModel() {

    private val _uiState = MutableStateFlow(
        BusinessDetailUiState(businessId = businessId, isLoading = true),
    )
    val uiState: StateFlow<BusinessDetailUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            combine(
                frontendContractManager.observeBusinessDetail(businessId),
                frontendContractManager.observeBusinessSummary(businessId),
            ) { detailResult, summaryResult ->
                val detail = detailResult.data
                val metrics = summaryResult.data
                val firstError = (detailResult.errors + summaryResult.errors)
                    .firstOrNull()?.message
                BusinessDetailUiState(
                    businessId = businessId,
                    isLoading = false,
                    detail = detail,
                    metrics = metrics,
                    errorMessage = if (detail == null && metrics == null) firstError else null,
                )
            }.collect { newState -> _uiState.value = newState }
        }
    }

    companion object {
        fun factory(
            businessId: Long,
            frontendContractManager: FrontendContractManager,
        ): ViewModelProvider.Factory = viewModelFactory {
            initializer { BusinessDetailViewModel(businessId, frontendContractManager) }
        }
    }
}
