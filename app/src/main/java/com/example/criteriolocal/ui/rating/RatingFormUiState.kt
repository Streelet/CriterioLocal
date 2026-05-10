package com.example.criteriolocal.ui.rating

data class RatingFormUiState(
    val businessId: Long,
    val isSubmitting: Boolean = false,
    val errorMessage: String? = null,
)
