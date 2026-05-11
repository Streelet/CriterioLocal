package com.example.criteriolocal.ui.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.criteriolocal.core.session.SessionManager
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
    private val sessionManager: SessionManager,
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    init {
        val userId = sessionManager.currentUserId
        if (userId == null) {
            _uiState.update {
                it.copy(
                    isLoading = false,
                    errorMessage = "No hay una sesion activa.",
                )
            }
        } else {
            loadProfile(userId)
            observeHistory(userId)
        }
    }

    fun onSignOut() {
        sessionManager.clear()
    }

    private fun loadProfile(userId: Long) {
        viewModelScope.launch {
            val result = userManager.getBasicProfile(userId)
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

    private fun observeHistory(userId: Long) {
        viewModelScope.launch {
            frontendContractManager.observeUserRatingHistory(userId).collect { result ->
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
        fun factory(
            userManager: UserManager,
            frontendContractManager: FrontendContractManager,
            sessionManager: SessionManager,
        ): ViewModelProvider.Factory = viewModelFactory {
            initializer { ProfileViewModel(userManager, frontendContractManager, sessionManager) }
        }
    }
}
