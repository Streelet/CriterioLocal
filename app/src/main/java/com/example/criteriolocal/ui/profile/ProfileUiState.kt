package com.example.criteriolocal.ui.profile

data class HistoryEntry(
    val id: Long,
    val businessName: String,
    val ratedOn: String,
    val averageScore: Double,
    val reportedPrice: Double,
)

data class ProfileUiState(
    val name: String = "",
    val email: String = "",
    val history: List<HistoryEntry> = emptyList(),
    val isLoading: Boolean = true,
    val errorMessage: String? = null,
)
