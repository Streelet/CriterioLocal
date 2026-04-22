package com.example.criteriolocal.domain.model

data class User(
    val id: Long = 0,
    val name: String,
    val email: String,
    val passwordHash: String,
    val registeredOn: String,
    val status: UserStatus,
)

data class Category(
    val id: Long,
    val name: String,
    val description: String,
)

data class Business(
    val id: Long = 0,
    val googlePlaceId: String? = null,
    val name: String,
    val description: String,
    val address: String,
    val phone: String? = null,
    val latitude: Double? = null,
    val longitude: Double? = null,
    val categoryId: Long,
    val status: BusinessStatus,
)

data class Quality(
    val id: Long,
    val name: String,
    val description: String,
    val applicableCategoryId: Long?,
    val status: CatalogStatus,
)

data class Rating(
    val id: Long = 0,
    val userId: Long,
    val businessId: Long,
    val ratedOn: String,
    val reportedPrice: Double,
    val priceReportedOn: String,
    val serviceScore: Int,
    val attentionScore: Int,
    val satisfactionScore: Int,
    val waitTime: WaitTimeOption,
    val wouldRecommend: Boolean,
    val usageFrequency: UsageFrequencyOption,
    val availability: AvailabilityOption,
    val serviceMode: ServiceModeOption,
)

data class RatingQuality(
    val id: Long = 0,
    val ratingId: Long,
    val qualityId: Long,
)

data class Evidence(
    val id: Long = 0,
    val ratingId: Long,
    val filePath: String,
    val fileType: EvidenceType,
    val uploadedOn: String,
)
