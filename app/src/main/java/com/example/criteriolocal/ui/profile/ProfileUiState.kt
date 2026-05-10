package com.example.criteriolocal.ui.profile

data class HistoryEntry(
    val id: Long,
    val businessName: String,
    val ratedOn: String,
    val averageScore: Double,
    val reportedPrice: Double,
)

data class ProfileUiState(
    val name: String = "Usuario invitado",
    val email: String = "invitado@criteriolocal.app",
    val history: List<HistoryEntry> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
)
