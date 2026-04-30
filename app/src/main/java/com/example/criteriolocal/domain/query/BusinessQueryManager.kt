package com.example.criteriolocal.domain.query

import com.example.criteriolocal.domain.model.BusinessWithCategory
import com.example.criteriolocal.domain.model.Rating
import com.example.criteriolocal.domain.model.RatingDetails
import com.example.criteriolocal.domain.repository.BusinessRepository
import com.example.criteriolocal.domain.repository.RatingRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map

class BusinessQueryManager(
    private val businessRepository: BusinessRepository,
    private val ratingRepository: RatingRepository,
) {
    fun searchBusinesses(filters: BusinessSearchFilters = BusinessSearchFilters()): Flow<List<BusinessSearchItem>> {
        return combine(
            businessRepository.observeBusinesses(),
            ratingRepository.observeAllRatings(),
        ) { businesses, ratings ->
            val normalizedNameQuery = filters.nameQuery?.trim()?.takeIf { it.isNotBlank() }
            val ratingsByBusiness = ratings.groupBy { it.rating.businessId }

            businesses
                .asSequence()
                .filter { item -> normalizedNameQuery == null || item.business.name.contains(normalizedNameQuery, ignoreCase = true) }
                .filter { item -> filters.categoryId == null || item.category.id == filters.categoryId }
                .map { item -> item.toSearchItem(ratingsByBusiness[item.business.id].orEmpty()) }
                .filter { item -> filters.minAverageScore == null || (item.averageScore != null && item.averageScore >= filters.minAverageScore) }
                .filter { item -> filters.minReportedPrice == null || item.maxReportedPrice != null && item.maxReportedPrice >= filters.minReportedPrice }
                .filter { item -> filters.maxReportedPrice == null || item.minReportedPrice != null && item.minReportedPrice <= filters.maxReportedPrice }
                .sortedWith(compareBy<BusinessSearchItem> { it.businessWithCategory.business.name.lowercase() })
                .toList()
        }
    }

    fun searchBusinessesByName(query: String): Flow<List<BusinessSearchItem>> {
        return searchBusinesses(BusinessSearchFilters(nameQuery = query))
    }

    fun searchBusinessesByCategory(categoryId: Long): Flow<List<BusinessSearchItem>> {
        return searchBusinesses(BusinessSearchFilters(categoryId = categoryId))
    }

    fun observeBusinessDetail(businessId: Long): Flow<BusinessDetail?> {
        return combine(
            businessRepository.observeBusiness(businessId),
            ratingRepository.observeRatingsByBusiness(businessId),
        ) { business, ratings ->
            business?.let {
                BusinessDetail(
                    businessWithCategory = it,
                    ratings = ratings,
                )
            }
        }
    }

    fun observeUserRatingHistory(userId: Long): Flow<UserRatingHistory> {
        return ratingRepository.observeRatingsByUser(userId).map { ratings ->
            UserRatingHistory(
                userId = userId,
                ratings = ratings,
            )
        }
    }

    private fun BusinessWithCategory.toSearchItem(
        ratings: List<RatingDetails>,
    ): BusinessSearchItem {
        val ratingValues = ratings.map { it.rating }
        return BusinessSearchItem(
            businessWithCategory = this,
            totalRatings = ratingValues.size,
            averageScore = ratingValues.averageStructuredScoreOrNull(),
            minReportedPrice = ratingValues.minOfOrNull { it.reportedPrice },
            maxReportedPrice = ratingValues.maxOfOrNull { it.reportedPrice },
        )
    }

    private fun List<Rating>.averageStructuredScoreOrNull(): Double? {
        if (isEmpty()) return null
        return flatMap { rating ->
            listOf(
                rating.serviceScore,
                rating.attentionScore,
                rating.satisfactionScore,
            )
        }.average()
    }
}
