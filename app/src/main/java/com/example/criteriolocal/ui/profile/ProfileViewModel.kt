package com.example.criteriolocal.ui.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.criteriolocal.domain.contract.FrontendContractManager
import com.example.criteriolocal.domain.management.UserManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ProfileViewModel(
    private val userManager: UserManager,
    private val frontendContractManager: FrontendContractManager,
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    init {
        loadProfile()
        observeHistory()
    }

    private fun loadProfile() {
        viewModelScope.launch {
            val result = userManager.getBasicProfile(TempUserId)
            val profile = result.value
            if (result.isSuccess && profile != null) {
                _uiState.update {
                    it.copy(
                        name = profile.user.name,
                        email = profile.user.email,
                        isLoading = false,
                        errorMessage = null,
                    )
                }
            } else {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = result.errors.firstOrNull()?.message
                            ?: "No se pudo cargar el perfil.",
                    )
                }
            }
        }
    }

    private fun observeHistory() {
        viewModelScope.launch {
            frontendContractManager.observeUserRatingHistory(TempUserId).collect { result ->
                if (!result.isSuccess) return@collect
                val ratings = result.data?.ratings.orEmpty()
                val mapped = ratings.map { rating ->
                    HistoryEntry(
                        id = rating.id,
                        businessName = rating.businessName,
                        ratedOn = rating.ratedOn,
                        averageScore = listOf(
                            rating.serviceScore,
                            rating.attentionScore,
                            rating.satisfactionScore,
                        ).average(),
                        reportedPrice = rating.reportedPrice,
                    )
                }
                _uiState.update { it.copy(history = mapped) }
            }
        }
    }

    companion object {
        private const val TempUserId = 1L

        fun factory(
            userManager: UserManager,
            frontendContractManager: FrontendContractManager,
        ): ViewModelProvider.Factory = viewModelFactory {
            initializer { ProfileViewModel(userManager, frontendContractManager) }
        }
    }
}
