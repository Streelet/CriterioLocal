package com.example.criteriolocal.ui.home

import androidx.lifecycle.ViewModel
import com.example.criteriolocal.domain.contract.BusinessDto
import com.example.criteriolocal.domain.contract.BusinessListItemDto
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class HomeViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(
        HomeUiState(
            categories = DummyCategories,
            businesses = DummyBusinesses,
        ),
    )
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    fun onQueryChange(value: String) {
        _uiState.update { it.copy(query = value) }
    }

    fun onClearQuery() {
        _uiState.update { it.copy(query = "") }
    }

    fun onCategorySelected(categoryId: Long?) {
        _uiState.update { it.copy(selectedCategoryId = categoryId) }
    }

    private companion object {
        val DummyCategories = listOf(
            CategoryFilterUi(id = null, label = "Todos"),
            CategoryFilterUi(id = 1L, label = "Restaurantes"),
            CategoryFilterUi(id = 2L, label = "Medicos"),
            CategoryFilterUi(id = 3L, label = "Talleres"),
        )

        val DummyBusinesses = listOf(
            BusinessListItemDto(
                business = BusinessDto(
                    id = 101L,
                    googlePlaceId = null,
                    name = "Cafe La Antigua",
                    description = "Cafeteria de especialidad con repostera artesanal.",
                    address = "Calle del Arco 5-12, Antigua Guatemala",
                    phone = "+502 5555-1010",
                    latitude = 14.5586,
                    longitude = -90.7339,
                    categoryId = 1L,
                    categoryName = "Restaurantes",
                    status = "ACTIVE",
                ),
                totalRatings = 124,
                averageScore = 4.7,
                minReportedPrice = 25.0,
                maxReportedPrice = 80.0,
            ),
            BusinessListItemDto(
                business = BusinessDto(
                    id = 102L,
                    googlePlaceId = null,
                    name = "Clinica Medica San Lucas",
                    description = "Atencion medica general y especialidades.",
                    address = "8a Avenida 4-30, Chiquimula",
                    phone = "+502 5555-2020",
                    latitude = 14.7958,
                    longitude = -89.5460,
                    categoryId = 2L,
                    categoryName = "Medicos",
                    status = "ACTIVE",
                ),
                totalRatings = 58,
                averageScore = 4.3,
                minReportedPrice = 100.0,
                maxReportedPrice = 350.0,
            ),
            BusinessListItemDto(
                business = BusinessDto(
                    id = 103L,
                    googlePlaceId = null,
                    name = "Taller Mecanico El Motor",
                    description = "Mantenimiento automotriz y diagnostico computarizado.",
                    address = "Ruta al Atlantico km 169",
                    phone = "+502 5555-3030",
                    latitude = 14.8025,
                    longitude = -89.5444,
                    categoryId = 3L,
                    categoryName = "Talleres",
                    status = "ACTIVE",
                ),
                totalRatings = 32,
                averageScore = 4.0,
                minReportedPrice = 150.0,
                maxReportedPrice = 1200.0,
            ),
        )
    }
}
