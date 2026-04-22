package com.example.criteriolocal.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import com.example.criteriolocal.core.di.AppContainer
import com.example.criteriolocal.domain.model.NearbyPlace
import com.example.criteriolocal.domain.model.NearbyPlaceSearchRequest
import com.example.criteriolocal.domain.repository.BootstrapRepository
import com.example.criteriolocal.domain.repository.BusinessRepository
import com.example.criteriolocal.domain.repository.CategoryRepository
import com.example.criteriolocal.domain.repository.QualityRepository
import com.example.criteriolocal.domain.repository.RemotePlaceRepository
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
    private val remotePlaceRepository: RemotePlaceRepository,
) : ViewModel() {

    private val isLoading = MutableStateFlow(true)
    private val errorMessage = MutableStateFlow<String?>(null)
    private val remotePlaces = MutableStateFlow(emptyList<NearbyPlace>())
    private val remotePlacesStatus = MutableStateFlow<String?>(null)
    private val remotePlacesError = MutableStateFlow<String?>(null)

    private val localState = combine(
        categoryRepository.observeCategories(),
        businessRepository.observeBusinesses(),
        qualityRepository.observeQualities(),
        userRepository.observeUsers(),
    ) { categories, businesses, qualities, users ->
        LocalHomeState(
            categories = categories,
            businesses = businesses,
            qualities = qualities,
            users = users,
        )
    }

    private val remoteState = combine(
        remotePlaces,
        remotePlacesStatus,
        remotePlacesError,
    ) { apiPlaces, apiStatus, apiError ->
        RemoteHomeState(
            places = apiPlaces,
            status = apiStatus,
            error = apiError,
        )
    }

    private val baseState = combine(
        localState,
        remoteState,
    ) { local, remote ->
        HomeUiState(
            isLoading = false,
            categories = local.categories,
            businesses = local.businesses,
            remotePlaces = remote.places,
            remotePlacesStatus = remote.status,
            remotePlacesError = remote.error,
            qualities = local.qualities,
            users = local.users,
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
            loadRemotePlaces()
            isLoading.value = false
        }
    }

    private suspend fun loadRemotePlaces() {
        runCatching {
            remotePlaceRepository.searchNearby(DEFAULT_NEARBY_REQUEST)
        }.onSuccess { result ->
            remotePlaces.value = result.places
            remotePlacesStatus.value = result.status
            remotePlacesError.value = null
        }.onFailure { throwable ->
            remotePlaces.value = emptyList()
            remotePlacesStatus.value = null
            remotePlacesError.value = throwable.message ?: "No se pudieron cargar los negocios desde la API."
        }
    }

    companion object {
        private val DEFAULT_NEARBY_REQUEST = NearbyPlaceSearchRequest(
            latitude = 14.7906,
            longitude = -89.5447,
            radiusMeters = 2_000,
            type = "pharmacy",
        )

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
                            remotePlaceRepository = appContainer.remotePlaceRepository,
                        ) as T
                    }
                    throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
                }
            }
        }
    }
}

private data class LocalHomeState(
    val categories: List<com.example.criteriolocal.domain.model.Category>,
    val businesses: List<com.example.criteriolocal.domain.model.BusinessWithCategory>,
    val qualities: List<com.example.criteriolocal.domain.model.Quality>,
    val users: List<com.example.criteriolocal.domain.model.User>,
)

private data class RemoteHomeState(
    val places: List<NearbyPlace>,
    val status: String?,
    val error: String?,
)
