package com.example.criteriolocal.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.criteriolocal.domain.contract.BusinessSearchFiltersDto
import com.example.criteriolocal.domain.contract.FrontendContractManager
import com.example.criteriolocal.domain.model.NearbyPlaceSearchRequest
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class HomeViewModel(
    private val frontendContractManager: FrontendContractManager,
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    private val filters = MutableStateFlow(BusinessSearchFiltersDto())

    init {
        refreshFromGooglePlaces()
        observeFilteredBusinesses()
        observeAllBusinessesForCategories()
    }

    private fun refreshFromGooglePlaces() {
        viewModelScope.launch {
            val errorMessage = coroutineScope {
                DefaultNearbyRequests
                    .map { request -> async { frontendContractManager.refreshNearbyPlaces(request) } }
                    .awaitAll()
                    .firstOrNull { !it.isSuccess }
                    ?.errors
                    ?.firstOrNull()
                    ?.message
            }
            if (errorMessage != null) {
                _uiState.update { it.copy(errorMessage = errorMessage) }
            }
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    private fun observeFilteredBusinesses() {
        viewModelScope.launch {
            filters
                .flatMapLatest { current -> frontendContractManager.searchBusinesses(current) }
                .collect { result ->
                    if (result.isSuccess) {
                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                businesses = result.data.orEmpty(),
                                errorMessage = null,
                            )
                        }
                    } else {
                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                errorMessage = result.errors.firstOrNull()?.message
                                    ?: "No se pudieron cargar los negocios.",
                            )
                        }
                    }
                }
        }
    }

    private fun observeAllBusinessesForCategories() {
        viewModelScope.launch {
            frontendContractManager.searchBusinesses().collect { result ->
                if (!result.isSuccess) return@collect
                val items = result.data.orEmpty()
                val derived = listOf(CategoryFilterUi(id = null, label = "Todos")) +
                    items
                        .distinctBy { it.business.categoryId }
                        .map { CategoryFilterUi(id = it.business.categoryId, label = it.business.categoryName) }
                _uiState.update { it.copy(categories = derived) }
            }
        }
    }

    fun onQueryChange(value: String) {
        _uiState.update { it.copy(query = value) }
        filters.update { it.copy(nameQuery = value.trim().ifBlank { null }) }
    }

    fun onClearQuery() {
        _uiState.update { it.copy(query = "") }
        filters.update { it.copy(nameQuery = null) }
    }

    fun onCategorySelected(categoryId: Long?) {
        _uiState.update { it.copy(selectedCategoryId = categoryId) }
        filters.update { it.copy(categoryId = categoryId) }
    }

    companion object {
        private const val DefaultLatitude = 14.7906
        private const val DefaultLongitude = -89.5447
        private const val DefaultRadiusMeters = 3_000
        private val DefaultGoogleTypes = listOf(
            "pharmacy",
            "restaurant",
            "car_repair",
            "doctor",
            "store",
        )

        private val DefaultNearbyRequests = DefaultGoogleTypes.map { type ->
            NearbyPlaceSearchRequest(
                latitude = DefaultLatitude,
                longitude = DefaultLongitude,
                radiusMeters = DefaultRadiusMeters,
                type = type,
            )
        }

        fun factory(frontendContractManager: FrontendContractManager): ViewModelProvider.Factory =
            viewModelFactory {
                initializer { HomeViewModel(frontendContractManager) }
            }
    }
}
