package com.example.criteriolocal.ui.rating

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.criteriolocal.domain.contract.EvidenceRequestDto
import com.example.criteriolocal.domain.contract.FrontendContractManager
import com.example.criteriolocal.domain.contract.RatingRegistrationRequestDto
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class RatingFormViewModel(
    private val businessId: Long,
    private val frontendContractManager: FrontendContractManager,
) : ViewModel() {

    private val _uiState = MutableStateFlow(RatingFormUiState(businessId = businessId))
    val uiState: StateFlow<RatingFormUiState> = _uiState.asStateFlow()

    private val _events = Channel<RatingFormEvent>(capacity = Channel.BUFFERED)
    val events: Flow<RatingFormEvent> = _events.receiveAsFlow()

    init {
        viewModelScope.launch {
            frontendContractManager.observeRatingFormCatalogs().collect { catalogs ->
                _uiState.update { state ->
                    state.copy(
                        waitTimeOptions = catalogs.waitTimeOptions.map {
                            CatalogChoice(code = it.code, label = it.label)
                        },
                        usageFrequencyOptions = catalogs.usageFrequencyOptions.map {
                            CatalogChoice(code = it.code, label = it.label)
                        },
                        availabilityOptions = catalogs.availabilityOptions.map {
                            CatalogChoice(code = it.code, label = it.label)
                        },
                        qualityOptions = catalogs.qualities.map {
                            QualityChoice(id = it.id, label = it.name)
                        },
                        ethicalNotice = catalogs.ethicalNotice,
                    )
                }
            }
        }
    }

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
            val today = isoFormatter().format(Date())
            val priceDate = isoFormatter().format(Date(current.priceDateMillis))
            val request = RatingRegistrationRequestDto(
                userId = TempUserId,
                businessId = current.businessId,
                ratedOn = today,
                reportedPrice = current.priceAsDouble,
                priceReportedOn = priceDate,
                serviceScore = current.serviceScore,
                attentionScore = current.attentionScore,
                satisfactionScore = current.satisfactionScore,
                waitTimeCode = current.selectedWaitTime,
                wouldRecommend = current.wouldRecommend,
                usageFrequencyCode = current.selectedUsageFrequency,
                availabilityCode = current.selectedAvailability,
                serviceModeCode = DefaultServiceModeCode,
                selectedQualityIds = current.selectedQualityIds.toList(),
                evidences = current.evidence?.let {
                    listOf(it.toRequestDto(uploadedOn = today))
                } ?: emptyList(),
            )

            val result = frontendContractManager.registerStructuredRating(request)
            if (result.isSuccess) {
                _uiState.update {
                    it.copy(
                        serviceScore = 0,
                        attentionScore = 0,
                        satisfactionScore = 0,
                        reportedPrice = "",
                        priceDateMillis = System.currentTimeMillis(),
                        selectedWaitTime = null,
                        selectedUsageFrequency = null,
                        selectedAvailability = null,
                        wouldRecommend = null,
                        selectedQualityIds = emptySet(),
                        evidence = null,
                        isSubmitting = false,
                        errorMessage = null,
                    )
                }
                _events.send(RatingFormEvent.SubmittedSuccessfully)
            } else {
                _uiState.update {
                    it.copy(
                        isSubmitting = false,
                        errorMessage = result.errors.firstOrNull()?.message
                            ?: "No se pudo enviar la valoracion.",
                    )
                }
            }
        }
    }

    private fun EvidenceAttachment.toRequestDto(uploadedOn: String): EvidenceRequestDto {
        val typeCode = when {
            mimeType.startsWith("image", ignoreCase = true) -> "IMAGE"
            mimeType.equals("application/pdf", ignoreCase = true) -> "PDF"
            else -> "IMAGE"
        }
        return EvidenceRequestDto(
            filePath = fileName,
            fileTypeCode = typeCode,
            fileSizeBytes = sizeBytes,
            uploadedOn = uploadedOn,
        )
    }

    private fun isoFormatter(): SimpleDateFormat =
        SimpleDateFormat("yyyy-MM-dd", Locale.US)

    companion object {
        private const val MaxPriceLength = 9
        private const val TempUserId = 1L
        private const val DefaultServiceModeCode = "IN_PERSON"

        private val SimulatedEvidence = EvidenceAttachment(
            fileName = "factura_001.jpg",
            sizeBytes = 2_202_010L,
            mimeType = "image/jpeg",
        )

        fun factory(
            businessId: Long,
            frontendContractManager: FrontendContractManager,
        ): ViewModelProvider.Factory = viewModelFactory {
            initializer { RatingFormViewModel(businessId, frontendContractManager) }
        }
    }
}
