package com.example.criteriolocal.ui.business

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.criteriolocal.domain.contract.BusinessDetailDto
import com.example.criteriolocal.domain.contract.BusinessDto
import com.example.criteriolocal.domain.contract.BusinessMetricsSummaryDto
import com.example.criteriolocal.domain.contract.QualityOptionDto
import com.example.criteriolocal.domain.contract.QualitySelectionMetricDto
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class BusinessDetailViewModel(
    private val businessId: Long,
) : ViewModel() {

    private val sample: DummySample = DummyBusinesses[businessId] ?: DummyBusinesses.values.first()

    private val _uiState = MutableStateFlow(
        BusinessDetailUiState(
            businessId = businessId,
            isLoading = false,
            detail = sample.detail,
            metrics = sample.metrics,
        ),
    )
    val uiState: StateFlow<BusinessDetailUiState> = _uiState.asStateFlow()

    private data class DummySample(
        val detail: BusinessDetailDto,
        val metrics: BusinessMetricsSummaryDto,
    )

    companion object {
        fun factory(businessId: Long): ViewModelProvider.Factory = viewModelFactory {
            initializer { BusinessDetailViewModel(businessId) }
        }

        private fun sampleOf(
            id: Long,
            name: String,
            categoryId: Long,
            categoryName: String,
            address: String,
            phone: String,
            generalScore: Double,
            attentionScore: Double,
            recommendationPercentage: Double,
            minPrice: Double,
            avgPrice: Double,
            maxPrice: Double,
            qualities: List<Pair<String, Int>>,
        ): DummySample {
            val business = BusinessDto(
                id = id,
                googlePlaceId = null,
                name = name,
                description = "Negocio de prueba para validar la pantalla de detalle.",
                address = address,
                phone = phone,
                latitude = null,
                longitude = null,
                categoryId = categoryId,
                categoryName = categoryName,
                status = "ACTIVE",
            )
            val topQualities = qualities.mapIndexed { index, (label, count) ->
                QualitySelectionMetricDto(
                    quality = QualityOptionDto(
                        id = (id * 100) + index,
                        name = label,
                        description = label,
                        applicableCategoryId = categoryId,
                    ),
                    selectionCount = count,
                )
            }
            return DummySample(
                detail = BusinessDetailDto(business = business, ratings = emptyList()),
                metrics = BusinessMetricsSummaryDto(
                    business = business,
                    totalRatings = topQualities.sumOf { it.selectionCount },
                    averageServiceScore = generalScore,
                    averageAttentionScore = attentionScore,
                    averageSatisfactionScore = generalScore,
                    generalScore = generalScore,
                    recommendationPercentage = recommendationPercentage,
                    minReportedPrice = minPrice,
                    maxReportedPrice = maxPrice,
                    averageReportedPrice = avgPrice,
                    topQualities = topQualities,
                ),
            )
        }

        private val DummyBusinesses: Map<Long, DummySample> = mapOf(
            101L to sampleOf(
                id = 101L,
                name = "Cafe La Antigua",
                categoryId = 1L,
                categoryName = "Restaurantes",
                address = "Calle del Arco 5-12, Antigua Guatemala",
                phone = "+502 5555-1010",
                generalScore = 4.7,
                attentionScore = 4.6,
                recommendationPercentage = 92.0,
                minPrice = 25.0,
                avgPrice = 48.0,
                maxPrice = 80.0,
                qualities = listOf(
                    "Atencion amable" to 84,
                    "Ambiente acogedor" to 71,
                    "Producto fresco" to 63,
                    "Buena relacion precio" to 41,
                ),
            ),
            102L to sampleOf(
                id = 102L,
                name = "Clinica Medica San Lucas",
                categoryId = 2L,
                categoryName = "Medicos",
                address = "8a Avenida 4-30, Chiquimula",
                phone = "+502 5555-2020",
                generalScore = 4.3,
                attentionScore = 4.5,
                recommendationPercentage = 78.0,
                minPrice = 100.0,
                avgPrice = 220.0,
                maxPrice = 350.0,
                qualities = listOf(
                    "Diagnostico claro" to 41,
                    "Atencion puntual" to 33,
                    "Instalaciones limpias" to 28,
                ),
            ),
            103L to sampleOf(
                id = 103L,
                name = "Taller Mecanico El Motor",
                categoryId = 3L,
                categoryName = "Talleres",
                address = "Ruta al Atlantico km 169",
                phone = "+502 5555-3030",
                generalScore = 4.0,
                attentionScore = 3.9,
                recommendationPercentage = 71.0,
                minPrice = 150.0,
                avgPrice = 480.0,
                maxPrice = 1200.0,
                qualities = listOf(
                    "Diagnostico preciso" to 22,
                    "Cumple tiempos" to 18,
                    "Precio justo" to 12,
                ),
            ),
        )
    }
}
