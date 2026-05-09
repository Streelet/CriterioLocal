package com.example.criteriolocal.domain.validation

import com.example.criteriolocal.domain.catalog.AppCatalogs
import com.example.criteriolocal.domain.model.AvailabilityOption
import com.example.criteriolocal.domain.model.CatalogStatus
import com.example.criteriolocal.domain.model.Quality
import com.example.criteriolocal.domain.model.ServiceModeOption
import com.example.criteriolocal.domain.model.UsageFrequencyOption
import com.example.criteriolocal.domain.model.WaitTimeOption
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class RatingValidatorTest {
    @Test
    fun validate_acceptsCompleteStructuredRating() {
        val result = RatingValidator.validate(
            request = validRequest(),
            context = validContext(),
        )

        assertTrue(result.isValid)
        assertTrue(result.errors.isEmpty())
    }

    @Test
    fun validate_rejectsFreeTextComments() {
        val result = RatingValidator.validate(
            request = validRequest(freeTextComment = "Muy buen servicio, recomendado."),
            context = validContext(),
        )

        assertFalse(result.isValid)
        assertTrue(result.hasError(ValidationErrorCode.FREE_TEXT_NOT_ALLOWED))
    }

    @Test
    fun validate_rejectsScoresOutsideClosedScale() {
        val result = RatingValidator.validate(
            request = validRequest(
                serviceScore = 0,
                attentionScore = 6,
                satisfactionScore = 99,
            ),
            context = validContext(),
        )

        assertTrue(result.hasError(ValidationErrorCode.SERVICE_SCORE_OUT_OF_RANGE))
        assertTrue(result.hasError(ValidationErrorCode.ATTENTION_SCORE_OUT_OF_RANGE))
        assertTrue(result.hasError(ValidationErrorCode.SATISFACTION_SCORE_OUT_OF_RANGE))
    }

    @Test
    fun validate_requiresValidPriceWithDate() {
        val result = RatingValidator.validate(
            request = validRequest(
                reportedPrice = -1.0,
                priceReportedOn = "2026-99-99",
            ),
            context = validContext(),
        )

        assertTrue(result.hasError(ValidationErrorCode.PRICE_OUT_OF_RANGE))
        assertTrue(result.hasError(ValidationErrorCode.PRICE_DATE_INVALID))
    }

    @Test
    fun validate_rejectsImpossibleCalendarDates() {
        val result = RatingValidator.validate(
            request = validRequest(
                ratedOn = "2026-02-31",
                priceReportedOn = "2026-04-31",
            ),
            context = validContext(),
        )

        assertTrue(result.hasError(ValidationErrorCode.RATED_DATE_INVALID))
        assertTrue(result.hasError(ValidationErrorCode.PRICE_DATE_INVALID))
    }

    @Test
    fun validate_rejectsMissingUserAndBusinessRelations() {
        val result = RatingValidator.validate(
            request = validRequest(
                userId = 99,
                businessId = 88,
            ),
            context = validContext(),
        )

        assertTrue(result.hasError(ValidationErrorCode.USER_NOT_FOUND))
        assertTrue(result.hasError(ValidationErrorCode.BUSINESS_NOT_FOUND))
    }

    @Test
    fun validate_rejectsMissingRequiredStructuredFields() {
        val result = RatingValidator.validate(
            request = validRequest(
                ratedOn = null,
                reportedPrice = null,
                priceReportedOn = null,
                waitTime = null,
                wouldRecommend = null,
                usageFrequency = null,
                availability = null,
                serviceMode = null,
                selectedQualityIds = emptyList(),
            ),
            context = validContext(),
        )

        assertTrue(result.hasError(ValidationErrorCode.RATED_DATE_REQUIRED))
        assertTrue(result.hasError(ValidationErrorCode.PRICE_REQUIRED))
        assertTrue(result.hasError(ValidationErrorCode.PRICE_DATE_REQUIRED))
        assertTrue(result.hasError(ValidationErrorCode.WAIT_TIME_REQUIRED))
        assertTrue(result.hasError(ValidationErrorCode.RECOMMENDATION_REQUIRED))
        assertTrue(result.hasError(ValidationErrorCode.USAGE_FREQUENCY_REQUIRED))
        assertTrue(result.hasError(ValidationErrorCode.AVAILABILITY_REQUIRED))
        assertTrue(result.hasError(ValidationErrorCode.SERVICE_MODE_REQUIRED))
        assertTrue(result.hasError(ValidationErrorCode.QUALITY_REQUIRED))
    }

    @Test
    fun validate_rejectsInvalidDuplicatedInactiveAndNonApplicableQualities() {
        val context = validContext(
            qualities = AppCatalogs.officialQualities + Quality(
                id = 20,
                name = "Cualidad inactiva",
                description = "No debe estar disponible.",
                applicableCategoryId = null,
                status = CatalogStatus.INACTIVE,
            ),
        )

        val result = RatingValidator.validate(
            request = validRequest(selectedQualityIds = listOf(1, 1, 9, 20, 999)),
            context = context,
        )

        assertTrue(result.hasError(ValidationErrorCode.QUALITY_DUPLICATED))
        assertTrue(result.hasError(ValidationErrorCode.QUALITY_NOT_APPLICABLE_TO_BUSINESS_CATEGORY))
        assertTrue(result.hasError(ValidationErrorCode.QUALITY_INACTIVE))
        assertTrue(result.hasError(ValidationErrorCode.QUALITY_NOT_IN_CATALOG))
    }

    private fun validContext(
        qualities: List<Quality> = AppCatalogs.officialQualities,
    ): RatingValidationContext {
        return RatingValidationContext(
            existingUserIds = setOf(1),
            existingBusinessCategoryIds = mapOf(2L to 2L),
            officialQualities = qualities,
        )
    }

    private fun validRequest(
        userId: Long? = 1,
        businessId: Long? = 2,
        ratedOn: String? = "2026-04-29",
        reportedPrice: Double? = 35.50,
        priceReportedOn: String? = "2026-04-29",
        serviceScore: Int? = 5,
        attentionScore: Int? = 4,
        satisfactionScore: Int? = 5,
        waitTime: WaitTimeOption? = WaitTimeOption.UP_TO_15_MINUTES,
        wouldRecommend: Boolean? = true,
        usageFrequency: UsageFrequencyOption? = UsageFrequencyOption.OCCASIONAL,
        availability: AvailabilityOption? = AvailabilityOption.AVAILABLE,
        serviceMode: ServiceModeOption? = ServiceModeOption.IN_PERSON,
        selectedQualityIds: List<Long> = listOf(1, 2, 7),
        freeTextComment: String? = null,
    ): RatingValidationRequest {
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
}
