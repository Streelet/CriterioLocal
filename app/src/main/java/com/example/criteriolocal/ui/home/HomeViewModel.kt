package com.example.criteriolocal.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import com.example.criteriolocal.core.di.AppContainer
import com.example.criteriolocal.domain.repository.BootstrapRepository
import com.example.criteriolocal.domain.repository.BusinessRepository
import com.example.criteriolocal.domain.repository.CategoryRepository
import com.example.criteriolocal.domain.repository.QualityRepository
import com.example.criteriolocal.domain.repository.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class HomeViewModel(
    private val bootstrapRepository: BootstrapRepository,
    categoryRepository: CategoryRepository,
    businessRepository: BusinessRepository,
    qualityRepository: QualityRepository,
    userRepository: UserRepository,
) : ViewModel() {

    private val isLoading = MutableStateFlow(true)
    private val errorMessage = MutableStateFlow<String?>(null)

    private val baseState = combine(
        categoryRepository.observeCategories(),
        businessRepository.observeBusinesses(),
        qualityRepository.observeQualities(),
        userRepository.observeUsers(),
    ) { categories, businesses, qualities, users ->
        HomeUiState(
            isLoading = false,
            categories = categories,
            businesses = businesses,
            qualities = qualities,
            users = users,
        )
    }

    val uiState: StateFlow<HomeUiState> = combine(
        isLoading,
        errorMessage,
        baseState,
    ) { loading, error, state ->
        state.copy(
            isLoading = loading,
            errorMessage = error,
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = HomeUiState(),
    )

    init {
        viewModelScope.launch {
            runCatching { bootstrapRepository.seedBaseData() }
                .onFailure { throwable ->
                    errorMessage.value = throwable.message ?: "No se pudo inicializar la base local."
                }
            isLoading.value = false
        }
    }

    companion object {
        fun factory(appContainer: AppContainer): ViewModelProvider.Factory {
            return object : ViewModelProvider.Factory {
                override fun <T : ViewModel> create(
                    modelClass: Class<T>,
                    extras: CreationExtras,
                ): T {
                    if (modelClass.isAssignableFrom(HomeViewModel::class.java)) {
                        @Suppress("UNCHECKED_CAST")
                        return HomeViewModel(
                            bootstrapRepository = appContainer.bootstrapRepository,
                            categoryRepository = appContainer.categoryRepository,
                            businessRepository = appContainer.businessRepository,
                            qualityRepository = appContainer.qualityRepository,
                            userRepository = appContainer.userRepository,
                        ) as T
                    }
                    throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
                }
            }
        }
    }
}
