package com.example.criteriolocal.domain.management

import com.example.criteriolocal.domain.model.Evidence
import com.example.criteriolocal.domain.model.Rating
import com.example.criteriolocal.domain.repository.BusinessRepository
import com.example.criteriolocal.domain.repository.QualityRepository
import com.example.criteriolocal.domain.repository.RatingRepository
import com.example.criteriolocal.domain.repository.UserRepository
import com.example.criteriolocal.domain.validation.EvidenceValidationRequest
import com.example.criteriolocal.domain.validation.EvidenceValidator
import com.example.criteriolocal.domain.validation.RatingValidationContext
import com.example.criteriolocal.domain.validation.RatingValidationRequest
import com.example.criteriolocal.domain.validation.RatingValidator
import kotlinx.coroutines.flow.first

class RatingRegistrationManager(
    private val userRepository: UserRepository,
    private val businessRepository: BusinessRepository,
    private val qualityRepository: QualityRepository,
    private val ratingRepository: RatingRepository,
) {
    suspend fun registerStructuredRating(
        request: RegisterStructuredRatingRequest,
    ): RatingRegistrationResult {
        val validationRequest = request.asValidationRequest()
        val validationContext = buildValidationContext(request)

        val ratingErrors = RatingValidator
            .validate(validationRequest, validationContext)
            .errors
        val evidenceErrors = EvidenceValidator
            .validate(request.evidences.map { it.asEvidenceValidationRequest() })
            .errors
        val errors = ratingErrors + evidenceErrors

        if (errors.isNotEmpty()) {
            return RatingRegistrationResult.failure(errors)
        }

        val ratingId = ratingRepository.saveStructuredRating(
            rating = request.asRating(),
            selectedQualityIds = request.selectedQualityIds.distinct(),
            evidences = request.evidences.map { it.asEvidence() },
        )
        return RatingRegistrationResult.success(ratingId)
    }

    private suspend fun buildValidationContext(
        request: RegisterStructuredRatingRequest,
    ): RatingValidationContext {
        val user = request.userId
            ?.takeIf { it > 0 }
            ?.let { userRepository.observeUser(it).first() }
        val business = request.businessId
            ?.takeIf { it > 0 }
            ?.let { businessRepository.observeBusiness(it).first() }
        val qualities = qualityRepository.observeQualities().first()

        return RatingValidationContext(
            existingUserIds = setOfNotNull(user?.id),
            existingBusinessCategoryIds = business?.let {
                mapOf(it.business.id to it.category.id)
            } ?: emptyMap(),
            officialQualities = qualities,
        )
    }

    private fun RegisterStructuredRatingRequest.asValidationRequest(): RatingValidationRequest {
        return RatingValidationRequest(
            userId = userId,
            businessId = businessId,
            ratedOn = ratedOn,
            reportedPrice = reportedPrice,
            priceReportedOn = priceReportedOn,
            serviceScore = serviceScore,
            attentionScore = attentionScore,
            satisfactionScore = satisfactionScore,
            waitTime = waitTime,
            wouldRecommend = wouldRecommend,
            usageFrequency = usageFrequency,
            availability = availability,
            serviceMode = serviceMode,
            selectedQualityIds = selectedQualityIds,
            freeTextComment = freeTextComment,
        )
    }

    private fun RegisterStructuredRatingRequest.asRating(): Rating {
        return Rating(
            userId = requireNotNull(userId),
            businessId = requireNotNull(businessId),
            ratedOn = requireNotNull(ratedOn),
            reportedPrice = requireNotNull(reportedPrice),
            priceReportedOn = requireNotNull(priceReportedOn),
            serviceScore = requireNotNull(serviceScore),
            attentionScore = requireNotNull(attentionScore),
            satisfactionScore = requireNotNull(satisfactionScore),
            waitTime = requireNotNull(waitTime),
            wouldRecommend = requireNotNull(wouldRecommend),
            usageFrequency = requireNotNull(usageFrequency),
            availability = requireNotNull(availability),
            serviceMode = requireNotNull(serviceMode),
        )
    }

    private fun RegisterEvidenceRequest.asEvidenceValidationRequest(): EvidenceValidationRequest {
        return EvidenceValidationRequest(
            filePath = filePath,
            fileType = fileType,
            uploadedOn = uploadedOn,
        )
    }

    private fun RegisterEvidenceRequest.asEvidence(): Evidence {
        return Evidence(
            ratingId = 0,
            filePath = requireNotNull(filePath),
            fileType = requireNotNull(fileType),
            uploadedOn = requireNotNull(uploadedOn),
        )
    }
}
