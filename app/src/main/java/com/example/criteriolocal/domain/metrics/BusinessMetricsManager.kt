package com.example.criteriolocal.domain.metrics

import com.example.criteriolocal.domain.model.BusinessWithCategory
import com.example.criteriolocal.domain.model.Quality
import com.example.criteriolocal.domain.model.Rating
import com.example.criteriolocal.domain.model.RatingDetails
import com.example.criteriolocal.domain.repository.BusinessRepository
import com.example.criteriolocal.domain.repository.RatingRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine

class BusinessMetricsManager(
    private val businessRepository: BusinessRepository,
    private val ratingRepository: RatingRepository,
) {
    fun observeBusinessSummary(businessId: Long): Flow<BusinessMetricsSummary?> {
        return combine(
            businessRepository.observeBusiness(businessId),
            ratingRepository.observeRatingsByBusiness(businessId),
        ) { business, ratings ->
            business?.toMetricsSummary(ratings)
        }
    }

    fun observeAllBusinessSummaries(): Flow<List<BusinessMetricsSummary>> {
        return combine(
            businessRepository.observeBusinesses(),
            ratingRepository.observeAllRatings(),
        ) { businesses, ratings ->
            val ratingsByBusiness = ratings.groupBy { it.rating.businessId }
            businesses.map { business ->
                business.toMetricsSummary(ratingsByBusiness[business.business.id].orEmpty())
            }
        }
    }

    fun observeCategoryRanking(categoryId: Long): Flow<CategoryRanking?> {
        return combine(
            businessRepository.observeBusinessesByCategory(categoryId),
            ratingRepository.observeAllRatings(),
        ) { businesses, ratings ->
            val category = businesses.firstOrNull()?.category ?: return@combine null
            val ratingsByBusiness = ratings.groupBy { it.rating.businessId }
            val ranked = businesses
                .map { business -> business.toMetricsSummary(ratingsByBusiness[business.business.id].orEmpty()) }
                .filter { it.totalRatings > 0 && it.generalScore != null }
                .sortedWith(
                    compareByDescending<BusinessMetricsSummary> { it.generalScore }
                        .thenByDescending { it.recommendationPercentage ?: 0.0 }
                        .thenByDescending { it.totalRatings }
                        .thenBy { it.businessWithCategory.business.name.lowercase() },
                )
                .mapIndexed { index, summary ->
                    CategoryRankingItem(
                        position = index + 1,
                        summary = summary,
                    )
                }

            CategoryRanking(
                category = category,
                items = ranked,
            )
        }
    }

    private fun BusinessWithCategory.toMetricsSummary(
        ratings: List<RatingDetails>,
    ): BusinessMetricsSummary {
        val ratingValues = ratings.map { it.rating }
        val averageService = ratingValues.averageOfOrNull { it.serviceScore.toDouble() }
        val averageAttention = ratingValues.averageOfOrNull { it.attentionScore.toDouble() }
        val averageSatisfaction = ratingValues.averageOfOrNull { it.satisfactionScore.toDouble() }

        return BusinessMetricsSummary(
            businessWithCategory = this,
            totalRatings = ratingValues.size,
            averageServiceScore = averageService,
            averageAttentionScore = averageAttention,
            averageSatisfactionScore = averageSatisfaction,
            generalScore = calculateGeneralScore(
                averageService = averageService,
                averageAttention = averageAttention,
                averageSatisfaction = averageSatisfaction,
            ),
            recommendationPercentage = ratingValues.recommendationPercentageOrNull(),
            minReportedPrice = ratingValues.minOfOrNull { it.reportedPrice },
            maxReportedPrice = ratingValues.maxOfOrNull { it.reportedPrice },
            averageReportedPrice = ratingValues.averageOfOrNull { it.reportedPrice },
            topQualities = ratings.topQualities(),
        )
    }

    private fun calculateGeneralScore(
        averageService: Double?,
        averageAttention: Double?,
        averageSatisfaction: Double?,
    ): Double? {
        if (averageService == null || averageAttention == null || averageSatisfaction == null) {
            return null
        }
        return (averageService + averageAttention + averageSatisfaction) / 3.0
    }

    private fun List<Rating>.recommendationPercentageOrNull(): Double? {
        if (isEmpty()) return null
        val recommended = count { it.wouldRecommend }
        return recommended.toDouble() / size.toDouble() * 100.0
    }

    private fun <T> List<T>.averageOfOrNull(selector: (T) -> Double): Double? {
        if (isEmpty()) return null
        return map(selector).average()
    }

    private fun List<RatingDetails>.topQualities(): List<QualitySelectionMetric> {
        return flatMap { it.qualities }
            .groupingBy { it }
            .eachCount()
            .map { (quality, count) ->
                QualitySelectionMetric(
                    quality = quality,
                    selectionCount = count,
                )
            }
            .sortedWith(
                compareByDescending<QualitySelectionMetric> { it.selectionCount }
                    .thenBy { it.quality.name.lowercase() },
            )
    }
}
