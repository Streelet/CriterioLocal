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
            history = DummyHistory,
        ),
    )
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    private companion object {
        val DummyHistory = listOf(
            HistoryEntry(
                id = 1L,
                businessName = "Cafe La Antigua",
                ratedOn = "2026-04-22",
                averageScore = 4.7,
                reportedPrice = 65.0,
            ),
            HistoryEntry(
                id = 2L,
                businessName = "Clinica Medica San Lucas",
                ratedOn = "2026-03-15",
                averageScore = 4.5,
                reportedPrice = 250.0,
            ),
            HistoryEntry(
                id = 3L,
                businessName = "Taller Mecanico El Motor",
                ratedOn = "2026-02-08",
                averageScore = 4.0,
                reportedPrice = 480.0,
            ),
            HistoryEntry(
                id = 4L,
                businessName = "Farmacia Vida",
                ratedOn = "2026-01-19",
                averageScore = 4.2,
                reportedPrice = 90.0,
            ),
        )
    }
}
