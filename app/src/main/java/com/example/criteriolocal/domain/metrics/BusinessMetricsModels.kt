package com.example.criteriolocal.domain.metrics

import com.example.criteriolocal.domain.model.BusinessWithCategory
import com.example.criteriolocal.domain.model.Category
import com.example.criteriolocal.domain.model.Quality

data class BusinessMetricsSummary(
    val businessWithCategory: BusinessWithCategory,
    val totalRatings: Int,
    val averageServiceScore: Double?,
    val averageAttentionScore: Double?,
    val averageSatisfactionScore: Double?,
    val generalScore: Double?,
    val recommendationPercentage: Double?,
    val minReportedPrice: Double?,
    val maxReportedPrice: Double?,
    val averageReportedPrice: Double?,
    val topQualities: List<QualitySelectionMetric>,
)

data class QualitySelectionMetric(
    val quality: Quality,
    val selectionCount: Int,
)

data class CategoryRanking(
    val category: Category,
    val items: List<CategoryRankingItem>,
)

data class CategoryRankingItem(
    val position: Int,
    val summary: BusinessMetricsSummary,
)
