package com.example.criteriolocal.ui.rating

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class RatingFormViewModel(
    private val businessId: Long,
) : ViewModel() {

    private val _uiState = MutableStateFlow(initialState(businessId))
    val uiState: StateFlow<RatingFormUiState> = _uiState.asStateFlow()

    private val _events = Channel<RatingFormEvent>(capacity = Channel.BUFFERED)
    val events: Flow<RatingFormEvent> = _events.receiveAsFlow()

    fun onServiceScoreChange(value: Int) {
        _uiState.update { it.copy(serviceScore = value, errorMessage = null) }
    }

    fun onAttentionScoreChange(value: Int) {
        _uiState.update { it.copy(attentionScore = value, errorMessage = null) }
    }

    fun onSatisfactionScoreChange(value: Int) {
        _uiState.update { it.copy(satisfactionScore = value, errorMessage = null) }
    }

    fun onPriceChange(value: String) {
        val sanitized = value.filter { it.isDigit() || it == '.' }.take(MaxPriceLength)
        _uiState.update { it.copy(reportedPrice = sanitized, errorMessage = null) }
    }

    fun onPriceDateChange(millis: Long) {
        _uiState.update { it.copy(priceDateMillis = millis, errorMessage = null) }
    }

    fun onWaitTimeSelected(code: String) {
        _uiState.update { it.copy(selectedWaitTime = code, errorMessage = null) }
    }

    fun onUsageFrequencySelected(code: String) {
        _uiState.update { it.copy(selectedUsageFrequency = code, errorMessage = null) }
    }

    fun onAvailabilitySelected(code: String) {
        _uiState.update { it.copy(selectedAvailability = code, errorMessage = null) }
    }

    fun onRecommendationChange(value: Boolean) {
        _uiState.update { it.copy(wouldRecommend = value, errorMessage = null) }
    }

    fun onQualityToggled(id: Long) {
        _uiState.update { state ->
            val updated = if (id in state.selectedQualityIds) {
                state.selectedQualityIds - id
            } else {
                state.selectedQualityIds + id
            }
            state.copy(selectedQualityIds = updated, errorMessage = null)
        }
    }

    fun onAttachEvidence() {
        _uiState.update {
            it.copy(evidence = SimulatedEvidence, errorMessage = null)
        }
    }

    fun onRemoveEvidence() {
        _uiState.update { it.copy(evidence = null, errorMessage = null) }
    }

    fun onSubmit() {
        val current = _uiState.value
        if (!current.canSubmit) {
            _uiState.update {
                it.copy(errorMessage = "Completa los campos requeridos antes de enviar.")
            }
            return
        }
        _uiState.update { it.copy(isSubmitting = true, errorMessage = null) }
        viewModelScope.launch {
            delay(SubmitDelayMillis)
            _uiState.value = initialState(businessId)
            _events.send(RatingFormEvent.SubmittedSuccessfully)
        }
    }

    companion object {
        private const val MaxPriceLength = 9
        private const val SubmitDelayMillis = 350L

        private val SimulatedEvidence = EvidenceAttachment(
            fileName = "factura_001.jpg",
            sizeBytes = 2_202_010L,
            mimeType = "image/jpeg",
        )

        fun factory(businessId: Long): ViewModelProvider.Factory = viewModelFactory {
            initializer { RatingFormViewModel(businessId) }
        }

        private fun initialState(businessId: Long) = RatingFormUiState(
            businessId = businessId,
            waitTimeOptions = WaitTimeOptions,
            usageFrequencyOptions = UsageFrequencyOptions,
            availabilityOptions = AvailabilityOptions,
            qualityOptions = QualityOptions,
        )

        private val WaitTimeOptions = listOf(
            CatalogChoice("UNDER_5", "Menos de 5 min"),
            CatalogChoice("BETWEEN_5_15", "5 a 15 min"),
            CatalogChoice("BETWEEN_15_30", "15 a 30 min"),
            CatalogChoice("OVER_30", "Mas de 30 min"),
        )

        private val UsageFrequencyOptions = listOf(
            CatalogChoice("FIRST_TIME", "Primera vez"),
            CatalogChoice("OCCASIONAL", "Ocasional"),
            CatalogChoice("MONTHLY", "Mensual"),
            CatalogChoice("WEEKLY", "Semanal"),
            CatalogChoice("DAILY", "Diaria"),
        )

        private val AvailabilityOptions = listOf(
            CatalogChoice("IMMEDIATE", "Inmediata"),
            CatalogChoice("SAME_DAY", "Mismo dia"),
            CatalogChoice("APPOINTMENT", "Con cita"),
            CatalogChoice("LIMITED", "Limitada"),
        )

        private val QualityOptions = listOf(
            QualityChoice(1L, "Trato amable"),
            QualityChoice(2L, "Precio accesible"),
            QualityChoice(3L, "Puntualidad"),
            QualityChoice(4L, "Limpieza"),
            QualityChoice(5L, "Atencion rapida"),
            QualityChoice(6L, "Profesionalismo"),
            QualityChoice(7L, "Producto fresco"),
            QualityChoice(8L, "Buena ubicacion"),
        )
    }
}
