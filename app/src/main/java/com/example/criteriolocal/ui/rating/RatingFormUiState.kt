package com.example.criteriolocal.ui.rating

data class CatalogChoice(
    val code: String,
    val label: String,
)

data class QualityChoice(
    val id: Long,
    val label: String,
)

data class EvidenceAttachment(
    val fileName: String,
    val sizeBytes: Long,
    val mimeType: String,
)

data class RatingFormUiState(
    val businessId: Long,
    val serviceScore: Int = 0,
    val attentionScore: Int = 0,
    val satisfactionScore: Int = 0,
    val reportedPrice: String = "",
    val priceDateMillis: Long = System.currentTimeMillis(),
    val waitTimeOptions: List<CatalogChoice> = emptyList(),
    val selectedWaitTime: String? = null,
    val usageFrequencyOptions: List<CatalogChoice> = emptyList(),
    val selectedUsageFrequency: String? = null,
    val availabilityOptions: List<CatalogChoice> = emptyList(),
    val selectedAvailability: String? = null,
    val wouldRecommend: Boolean? = null,
    val qualityOptions: List<QualityChoice> = emptyList(),
    val selectedQualityIds: Set<Long> = emptySet(),
    val evidence: EvidenceAttachment? = null,
    val isSubmitting: Boolean = false,
    val errorMessage: String? = null,
    val ethicalNotice: String =
        "Aviso de uso etico y responsable: esta valoracion debe ser objetiva, basada en tu " +
            "experiencia real y respetando a las personas y al negocio. Evita atacar, descalificar o " +
            "incluir informacion falsa. Tu opinion suma cuando es honesta y constructiva.",
) {
    val priceAsDouble: Double?
        get() = reportedPrice.toDoubleOrNull()

    val canSubmit: Boolean
        get() = !isSubmitting &&
            serviceScore in 1..5 &&
            attentionScore in 1..5 &&
            satisfactionScore in 1..5 &&
            (priceAsDouble ?: 0.0) > 0.0 &&
            selectedWaitTime != null &&
            selectedUsageFrequency != null &&
            selectedAvailability != null &&
            wouldRecommend != null
}

sealed interface RatingFormEvent {
    data object SubmittedSuccessfully : RatingFormEvent
}
