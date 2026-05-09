package com.example.criteriolocal.ui.business

data class BusinessDetailUiState(
    val businessId: Long,
    val isLoading: Boolean = true,
    val errorMessage: String? = null,
)
