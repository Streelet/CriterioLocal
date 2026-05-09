package com.example.criteriolocal.domain.contract

data class ContractResultDto<T>(
    val data: T? = null,
    val errors: List<ContractErrorDto> = emptyList(),
) {
    val isSuccess: Boolean
        get() = errors.isEmpty()

    companion object {
        fun <T> success(data: T): ContractResultDto<T> {
            return ContractResultDto(data = data)
        }

        fun <T> failure(errors: List<ContractErrorDto>): ContractResultDto<T> {
            return ContractResultDto(errors = errors)
        }

        fun <T> failure(error: ContractErrorDto): ContractResultDto<T> {
            return ContractResultDto(errors = listOf(error))
        }
    }
}

data class ContractErrorDto(
    val code: String,
    val field: String,
    val message: String,
)

data class CatalogOptionDto(
    val code: String,
    val label: String,
)

data class QualityOptionDto(
    val id: Long,
    val name: String,
    val description: String,
    val applicableCategoryId: Long?,
)

data class RatingFormCatalogsDto(
    val ratingScale: List<Int>,
    val waitTimeOptions: List<CatalogOptionDto>,
    val usageFrequencyOptions: List<CatalogOptionDto>,
    val availabilityOptions: List<CatalogOptionDto>,
    val serviceModeOptions: List<CatalogOptionDto>,
    val qualities: List<QualityOptionDto>,
    val minReportedPrice: Double,
    val maxReportedPrice: Double,
    val evidenceFilePolicy: EvidenceFilePolicyDto,
    val ethicalNotice: String,
)

data class EvidenceFilePolicyDto(
    val maxFileSizeBytes: Long,
    val allowedExtensionsByType: Map<String, List<String>>,
)

data class BusinessSearchFiltersDto(
    val nameQuery: String? = null,
    val categoryId: Long? = null,
    val minAverageScore: Double? = null,
    val minReportedPrice: Double? = null,
    val maxReportedPrice: Double? = null,
)

data class BusinessDto(
    val id: Long,
    val googlePlaceId: String?,
    val name: String,
    val description: String,
    val address: String,
    val phone: String?,
    val latitude: Double?,
    val longitude: Double?,
    val categoryId: Long,
    val categoryName: String,
    val status: String,
)

data class BusinessListItemDto(
    val business: BusinessDto,
    val totalRatings: Int,
    val averageScore: Double?,
    val minReportedPrice: Double?,
    val maxReportedPrice: Double?,
)

data class BusinessDetailDto(
    val business: BusinessDto,
    val ratings: List<RatingDetailDto>,
)

data class UserRatingHistoryDto(
    val userId: Long,
    val ratings: List<RatingDetailDto>,
)

data class RatingDetailDto(
    val id: Long,
    val userId: Long,
    val userName: String,
    val businessId: Long,
    val businessName: String,
    val ratedOn: String,
    val reportedPrice: Double,
    val priceReportedOn: String,
    val serviceScore: Int,
    val attentionScore: Int,
    val satisfactionScore: Int,
    val waitTimeCode: String,
    val wouldRecommend: Boolean,
    val usageFrequencyCode: String,
    val availabilityCode: String,
    val serviceModeCode: String,
    val qualities: List<QualityOptionDto>,
    val evidences: List<EvidenceDto>,
)

data class EvidenceDto(
    val id: Long,
    val filePath: String,
    val fileTypeCode: String,
    val uploadedOn: String,
)

data class RatingRegistrationRequestDto(
    val userId: Long?,
    val businessId: Long?,
    val ratedOn: String?,
    val reportedPrice: Double?,
    val priceReportedOn: String?,
    val serviceScore: Int?,
    val attentionScore: Int?,
    val satisfactionScore: Int?,
    val waitTimeCode: String?,
    val wouldRecommend: Boolean?,
    val usageFrequencyCode: String?,
    val availabilityCode: String?,
    val serviceModeCode: String?,
    val selectedQualityIds: List<Long>,
    val evidences: List<EvidenceRequestDto> = emptyList(),
)

data class EvidenceRequestDto(
    val filePath: String?,
    val fileTypeCode: String?,
    val fileSizeBytes: Long?,
    val uploadedOn: String?,
)

data class RatingRegistrationResponseDto(
    val ratingId: Long,
)

data class BusinessMetricsSummaryDto(
    val business: BusinessDto,
    val totalRatings: Int,
    val averageServiceScore: Double?,
    val averageAttentionScore: Double?,
    val averageSatisfactionScore: Double?,
    val generalScore: Double?,
    val recommendationPercentage: Double?,
    val minReportedPrice: Double?,
    val maxReportedPrice: Double?,
    val averageReportedPrice: Double?,
    val topQualities: List<QualitySelectionMetricDto>,
)

data class QualitySelectionMetricDto(
    val quality: QualityOptionDto,
    val selectionCount: Int,
)

data class CategoryRankingDto(
    val categoryId: Long,
    val categoryName: String,
    val items: List<CategoryRankingItemDto>,
)

data class CategoryRankingItemDto(
    val position: Int,
    val summary: BusinessMetricsSummaryDto,
)
