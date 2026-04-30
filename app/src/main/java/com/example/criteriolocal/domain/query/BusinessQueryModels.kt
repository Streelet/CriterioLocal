package com.example.criteriolocal.domain.query

import com.example.criteriolocal.domain.model.BusinessWithCategory
import com.example.criteriolocal.domain.model.RatingDetails

data class BusinessSearchFilters(
    val nameQuery: String? = null,
    val categoryId: Long? = null,
    val minAverageScore: Double? = null,
    val minReportedPrice: Double? = null,
    val maxReportedPrice: Double? = null,
)

data class BusinessSearchItem(
    val businessWithCategory: BusinessWithCategory,
    val totalRatings: Int,
    val averageScore: Double?,
    val minReportedPrice: Double?,
    val maxReportedPrice: Double?,
)

data class BusinessDetail(
    val businessWithCategory: BusinessWithCategory,
    val ratings: List<RatingDetails>,
)

data class UserRatingHistory(
    val userId: Long,
    val ratings: List<RatingDetails>,
)
