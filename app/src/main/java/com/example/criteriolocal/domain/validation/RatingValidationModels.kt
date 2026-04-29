package com.example.criteriolocal.domain.validation

import com.example.criteriolocal.domain.catalog.AppCatalogs
import com.example.criteriolocal.domain.model.AvailabilityOption
import com.example.criteriolocal.domain.model.Quality
import com.example.criteriolocal.domain.model.ServiceModeOption
import com.example.criteriolocal.domain.model.UsageFrequencyOption
import com.example.criteriolocal.domain.model.WaitTimeOption

data class RatingValidationRequest(
    val userId: Long?,
    val businessId: Long?,
    val ratedOn: String?,
    val reportedPrice: Double?,
    val priceReportedOn: String?,
    val serviceScore: Int?,
    val attentionScore: Int?,
    val satisfactionScore: Int?,
    val waitTime: WaitTimeOption?,
    val wouldRecommend: Boolean?,
    val usageFrequency: UsageFrequencyOption?,
    val availability: AvailabilityOption?,
    val serviceMode: ServiceModeOption?,
    val selectedQualityIds: List<Long>,
    val freeTextComment: String? = null,
)

data class RatingValidationContext(
    val existingUserIds: Set<Long>,
    val existingBusinessCategoryIds: Map<Long, Long>,
    val officialQualities: List<Quality> = AppCatalogs.officialQualities,
    val ratingScale: Set<Int> = AppCatalogs.ratingScale.toSet(),
    val minReportedPrice: Double = AppCatalogs.minReportedPrice,
    val maxReportedPrice: Double = AppCatalogs.maxReportedPrice,
)

data class ValidationResult(
    val errors: List<ValidationError>,
) {
    val isValid: Boolean = errors.isEmpty()

    fun hasError(code: ValidationErrorCode): Boolean {
        return errors.any { it.code == code }
    }
}

data class ValidationError(
    val code: ValidationErrorCode,
    val field: String,
    val message: String,
)

enum class ValidationErrorCode {
    FREE_TEXT_NOT_ALLOWED,
    USER_REQUIRED,
    USER_NOT_FOUND,
    BUSINESS_REQUIRED,
    BUSINESS_NOT_FOUND,
    RATED_DATE_REQUIRED,
    RATED_DATE_INVALID,
    PRICE_REQUIRED,
    PRICE_OUT_OF_RANGE,
    PRICE_DATE_REQUIRED,
    PRICE_DATE_INVALID,
    SERVICE_SCORE_REQUIRED,
    SERVICE_SCORE_OUT_OF_RANGE,
    ATTENTION_SCORE_REQUIRED,
    ATTENTION_SCORE_OUT_OF_RANGE,
    SATISFACTION_SCORE_REQUIRED,
    SATISFACTION_SCORE_OUT_OF_RANGE,
    WAIT_TIME_REQUIRED,
    RECOMMENDATION_REQUIRED,
    USAGE_FREQUENCY_REQUIRED,
    AVAILABILITY_REQUIRED,
    SERVICE_MODE_REQUIRED,
    QUALITY_REQUIRED,
    QUALITY_DUPLICATED,
    QUALITY_NOT_IN_CATALOG,
    QUALITY_INACTIVE,
    QUALITY_NOT_APPLICABLE_TO_BUSINESS_CATEGORY,
}
