package com.example.criteriolocal.domain.management

import com.example.criteriolocal.domain.model.AvailabilityOption
import com.example.criteriolocal.domain.model.EvidenceType
import com.example.criteriolocal.domain.model.ServiceModeOption
import com.example.criteriolocal.domain.model.UsageFrequencyOption
import com.example.criteriolocal.domain.model.WaitTimeOption
import com.example.criteriolocal.domain.validation.ValidationError

data class RegisterStructuredRatingRequest(
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
    val evidences: List<RegisterEvidenceRequest> = emptyList(),
    val freeTextComment: String? = null,
)

data class RegisterEvidenceRequest(
    val filePath: String?,
    val fileType: EvidenceType?,
    val uploadedOn: String?,
)

data class RatingRegistrationResult(
    val ratingId: Long? = null,
    val errors: List<ValidationError> = emptyList(),
) {
    val isSuccess: Boolean
        get() = errors.isEmpty()

    companion object {
        fun success(ratingId: Long): RatingRegistrationResult {
            return RatingRegistrationResult(ratingId = ratingId)
        }

        fun failure(errors: List<ValidationError>): RatingRegistrationResult {
            return RatingRegistrationResult(errors = errors)
        }
    }
}
