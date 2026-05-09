package com.example.criteriolocal.domain.contract

import com.example.criteriolocal.domain.catalog.AppCatalogs
import com.example.criteriolocal.domain.management.RatingRegistrationResult
import com.example.criteriolocal.domain.management.RegisterEvidenceRequest
import com.example.criteriolocal.domain.management.RegisterStructuredRatingRequest
import com.example.criteriolocal.domain.metrics.BusinessMetricsSummary
import com.example.criteriolocal.domain.metrics.CategoryRanking
import com.example.criteriolocal.domain.metrics.CategoryRankingItem
import com.example.criteriolocal.domain.metrics.QualitySelectionMetric
import com.example.criteriolocal.domain.model.AvailabilityOption
import com.example.criteriolocal.domain.model.BusinessWithCategory
import com.example.criteriolocal.domain.model.CatalogOption
import com.example.criteriolocal.domain.model.CatalogStatus
import com.example.criteriolocal.domain.model.Evidence
import com.example.criteriolocal.domain.model.EvidenceType
import com.example.criteriolocal.domain.model.Quality
import com.example.criteriolocal.domain.model.RatingDetails
import com.example.criteriolocal.domain.model.ServiceModeOption
import com.example.criteriolocal.domain.model.UsageFrequencyOption
import com.example.criteriolocal.domain.model.WaitTimeOption
import com.example.criteriolocal.domain.query.BusinessDetail
import com.example.criteriolocal.domain.query.BusinessSearchFilters
import com.example.criteriolocal.domain.query.BusinessSearchItem
import com.example.criteriolocal.domain.query.UserRatingHistory
import com.example.criteriolocal.domain.validation.EvidenceFilePolicy
import com.example.criteriolocal.domain.validation.ValidationError

data class ContractMappingResult<T>(
    val value: T? = null,
    val errors: List<ContractErrorDto> = emptyList(),
) {
    val isSuccess: Boolean
        get() = errors.isEmpty()
}

fun BusinessSearchFiltersDto.toDomain(): BusinessSearchFilters {
    return BusinessSearchFilters(
        nameQuery = nameQuery,
        categoryId = categoryId,
        minAverageScore = minAverageScore,
        minReportedPrice = minReportedPrice,
        maxReportedPrice = maxReportedPrice,
    )
}

fun List<Quality>.toRatingFormCatalogsDto(): RatingFormCatalogsDto {
    return RatingFormCatalogsDto(
        ratingScale = AppCatalogs.ratingScale,
        waitTimeOptions = AppCatalogs.waitTimeOptions.map { it.toDto() },
        usageFrequencyOptions = AppCatalogs.usageFrequencyOptions.map { it.toDto() },
        availabilityOptions = AppCatalogs.availabilityOptions.map { it.toDto() },
        serviceModeOptions = AppCatalogs.serviceModeOptions.map { it.toDto() },
        qualities = filter { it.status == CatalogStatus.ACTIVE }.map { it.toDto() },
        minReportedPrice = AppCatalogs.minReportedPrice,
        maxReportedPrice = AppCatalogs.maxReportedPrice,
        evidenceFilePolicy = EvidenceFilePolicy.toDto(),
        ethicalNotice = AppCatalogs.ethicalNotice,
    )
}

fun BusinessSearchItem.toDto(): BusinessListItemDto {
    return BusinessListItemDto(
        business = businessWithCategory.toBusinessDto(),
        totalRatings = totalRatings,
        averageScore = averageScore,
        minReportedPrice = minReportedPrice,
        maxReportedPrice = maxReportedPrice,
    )
}

fun BusinessDetail.toDto(): BusinessDetailDto {
    return BusinessDetailDto(
        business = businessWithCategory.toBusinessDto(),
        ratings = ratings.map { it.toDto() },
    )
}

fun UserRatingHistory.toDto(): UserRatingHistoryDto {
    return UserRatingHistoryDto(
        userId = userId,
        ratings = ratings.map { it.toDto() },
    )
}

fun BusinessMetricsSummary.toDto(): BusinessMetricsSummaryDto {
    return BusinessMetricsSummaryDto(
        business = businessWithCategory.toBusinessDto(),
        totalRatings = totalRatings,
        averageServiceScore = averageServiceScore,
        averageAttentionScore = averageAttentionScore,
        averageSatisfactionScore = averageSatisfactionScore,
        generalScore = generalScore,
        recommendationPercentage = recommendationPercentage,
        minReportedPrice = minReportedPrice,
        maxReportedPrice = maxReportedPrice,
        averageReportedPrice = averageReportedPrice,
        topQualities = topQualities.map { it.toDto() },
    )
}

fun CategoryRanking.toDto(): CategoryRankingDto {
    return CategoryRankingDto(
        categoryId = category.id,
        categoryName = category.name,
        items = items.map { it.toDto() },
    )
}

fun RatingRegistrationRequestDto.toDomainRequest(): ContractMappingResult<RegisterStructuredRatingRequest> {
    val errors = mutableListOf<ContractErrorDto>()
    val waitTime = waitTimeCode.toEnumOrNull<WaitTimeOption>("waitTimeCode", errors)
    val usageFrequency = usageFrequencyCode.toEnumOrNull<UsageFrequencyOption>("usageFrequencyCode", errors)
    val availability = availabilityCode.toEnumOrNull<AvailabilityOption>("availabilityCode", errors)
    val serviceMode = serviceModeCode.toEnumOrNull<ServiceModeOption>("serviceModeCode", errors)
    val evidenceRequests = evidences.mapIndexed { index, evidence ->
        evidence.toDomainRequest(index, errors)
    }

    if (errors.isNotEmpty()) {
        return ContractMappingResult(errors = errors)
    }

    return ContractMappingResult(
        value = RegisterStructuredRatingRequest(
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
            evidences = evidenceRequests,
            freeTextComment = null,
        ),
    )
}

fun RatingRegistrationResult.toDto(): ContractResultDto<RatingRegistrationResponseDto> {
    return if (isSuccess && ratingId != null) {
        ContractResultDto.success(RatingRegistrationResponseDto(ratingId = ratingId))
    } else {
        ContractResultDto.failure(errors.map { it.toDto() })
    }
}

fun notFoundError(field: String, message: String): ContractErrorDto {
    return ContractErrorDto(
        code = "NOT_FOUND",
        field = field,
        message = message,
    )
}

private fun BusinessWithCategory.toBusinessDto(): BusinessDto {
    return BusinessDto(
        id = business.id,
        googlePlaceId = business.googlePlaceId,
        name = business.name,
        description = business.description,
        address = business.address,
        phone = business.phone,
        latitude = business.latitude,
        longitude = business.longitude,
        categoryId = category.id,
        categoryName = category.name,
        status = business.status.name,
    )
}

private fun RatingDetails.toDto(): RatingDetailDto {
    return RatingDetailDto(
        id = rating.id,
        userId = user.id,
        userName = user.name,
        businessId = business.id,
        businessName = business.name,
        ratedOn = rating.ratedOn,
        reportedPrice = rating.reportedPrice,
        priceReportedOn = rating.priceReportedOn,
        serviceScore = rating.serviceScore,
        attentionScore = rating.attentionScore,
        satisfactionScore = rating.satisfactionScore,
        waitTimeCode = rating.waitTime.name,
        wouldRecommend = rating.wouldRecommend,
        usageFrequencyCode = rating.usageFrequency.name,
        availabilityCode = rating.availability.name,
        serviceModeCode = rating.serviceMode.name,
        qualities = qualities.map { it.toDto() },
        evidences = evidences.map { it.toDto() },
    )
}

private fun QualitySelectionMetric.toDto(): QualitySelectionMetricDto {
    return QualitySelectionMetricDto(
        quality = quality.toDto(),
        selectionCount = selectionCount,
    )
}

private fun CategoryRankingItem.toDto(): CategoryRankingItemDto {
    return CategoryRankingItemDto(
        position = position,
        summary = summary.toDto(),
    )
}

private fun Quality.toDto(): QualityOptionDto {
    return QualityOptionDto(
        id = id,
        name = name,
        description = description,
        applicableCategoryId = applicableCategoryId,
    )
}

private fun Evidence.toDto(): EvidenceDto {
    return EvidenceDto(
        id = id,
        filePath = filePath,
        fileTypeCode = fileType.name,
        uploadedOn = uploadedOn,
    )
}

private fun <T> CatalogOption<T>.toDto(): CatalogOptionDto where T : Enum<T> {
    return CatalogOptionDto(
        code = value.name,
        label = label,
    )
}

private fun EvidenceRequestDto.toDomainRequest(
    index: Int,
    errors: MutableList<ContractErrorDto>,
): RegisterEvidenceRequest {
    return RegisterEvidenceRequest(
        filePath = filePath,
        fileType = fileTypeCode.toEnumOrNull<EvidenceType>("evidences[$index].fileTypeCode", errors),
        fileSizeBytes = fileSizeBytes,
        uploadedOn = uploadedOn,
    )
}

private fun EvidenceFilePolicy.toDto(): EvidenceFilePolicyDto {
    return EvidenceFilePolicyDto(
        maxFileSizeBytes = maxFileSizeBytes,
        allowedExtensionsByType = allowedExtensionsByType.mapKeys { it.key.name }
            .mapValues { it.value.sorted() },
    )
}

private inline fun <reified T> String?.toEnumOrNull(
    field: String,
    errors: MutableList<ContractErrorDto>,
): T? where T : Enum<T> {
    if (this == null) return null
    return enumValues<T>().firstOrNull { it.name == this } ?: run {
        errors += ContractErrorDto(
            code = "INVALID_CATALOG_OPTION",
            field = field,
            message = "La opcion '$this' no existe en el catalogo esperado.",
        )
        null
    }
}

private fun ValidationError.toDto(): ContractErrorDto {
    return ContractErrorDto(
        code = code.name,
        field = field,
        message = message,
    )
}
