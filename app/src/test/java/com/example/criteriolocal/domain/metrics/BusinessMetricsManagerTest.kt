package com.example.criteriolocal.domain.metrics

import com.example.criteriolocal.domain.model.AvailabilityOption
import com.example.criteriolocal.domain.model.Business
import com.example.criteriolocal.domain.model.BusinessStatus
import com.example.criteriolocal.domain.model.BusinessWithCategory
import com.example.criteriolocal.domain.model.CatalogStatus
import com.example.criteriolocal.domain.model.Category
import com.example.criteriolocal.domain.model.Evidence
import com.example.criteriolocal.domain.model.Quality
import com.example.criteriolocal.domain.model.Rating
import com.example.criteriolocal.domain.model.RatingDetails
import com.example.criteriolocal.domain.model.RatingQuality
import com.example.criteriolocal.domain.model.ServiceModeOption
import com.example.criteriolocal.domain.model.UsageFrequencyOption
import com.example.criteriolocal.domain.model.User
import com.example.criteriolocal.domain.model.UserStatus
import com.example.criteriolocal.domain.model.WaitTimeOption
import com.example.criteriolocal.domain.repository.BusinessRepository
import com.example.criteriolocal.domain.repository.RatingRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class BusinessMetricsManagerTest {
    @Test
    fun observeBusinessSummary_calculatesAveragesRecommendationPricesAndTopQualities() = runTest {
        val manager = BusinessMetricsManager(
            businessRepository = FakeMetricsBusinessRepository(defaultBusinesses),
            ratingRepository = FakeMetricsRatingRepository(defaultRatings),
        )

        val summary = manager.observeBusinessSummary(businessId = 1).first()

        assertEquals(2, summary?.totalRatings)
        assertEquals(4.0, summary?.averageServiceScore ?: 0.0, 0.001)
        assertEquals(4.0, summary?.averageAttentionScore ?: 0.0, 0.001)
        assertEquals(4.5, summary?.averageSatisfactionScore ?: 0.0, 0.001)
        assertEquals(4.166, summary?.generalScore ?: 0.0, 0.001)
        assertEquals(50.0, summary?.recommendationPercentage ?: 0.0, 0.001)
        assertEquals(20.0, summary?.minReportedPrice ?: 0.0, 0.001)
        assertEquals(60.0, summary?.maxReportedPrice ?: 0.0, 0.001)
        assertEquals(40.0, summary?.averageReportedPrice ?: 0.0, 0.001)
        assertEquals("Atencion rapida", summary?.topQualities?.first()?.quality?.name)
        assertEquals(2, summary?.topQualities?.first()?.selectionCount)
    }

    @Test
    fun observeBusinessSummary_returnsNullForMissingBusiness() = runTest {
        val manager = BusinessMetricsManager(
            businessRepository = FakeMetricsBusinessRepository(defaultBusinesses),
            ratingRepository = FakeMetricsRatingRepository(defaultRatings),
        )

        val summary = manager.observeBusinessSummary(businessId = 99).first()

        assertNull(summary)
    }

    @Test
    fun observeAllBusinessSummaries_keepsBusinessesWithoutRatingsWithNullMetrics() = runTest {
        val manager = BusinessMetricsManager(
            businessRepository = FakeMetricsBusinessRepository(defaultBusinesses),
            ratingRepository = FakeMetricsRatingRepository(defaultRatings),
        )

        val summaries = manager.observeAllBusinessSummaries().first()
        val unrated = summaries.single { it.businessWithCategory.business.id == 4L }

        assertEquals(4, summaries.size)
        assertEquals(0, unrated.totalRatings)
        assertNull(unrated.generalScore)
        assertNull(unrated.recommendationPercentage)
    }

    @Test
    fun observeCategoryRanking_ordersByGeneralScoreThenRecommendationPercentage() = runTest {
        val manager = BusinessMetricsManager(
            businessRepository = FakeMetricsBusinessRepository(defaultBusinesses),
            ratingRepository = FakeMetricsRatingRepository(defaultRatings),
        )

        val ranking = manager.observeCategoryRanking(categoryId = 2).first()

        assertEquals("Farmacia", ranking?.category?.name)
        assertEquals(
            listOf("Farmacia Excelencia", "Farmacia Recomendable", "Farmacia Base"),
            ranking?.items?.map { it.summary.businessWithCategory.business.name },
        )
        assertEquals(listOf(1, 2, 3), ranking?.items?.map { it.position })
    }

    private companion object {
        val category = Category(2, "Farmacia", "Venta de medicamentos.")
        val user = User(
            id = 1,
            name = "Usuario",
            email = "usuario@criteriolocal.local",
            passwordHash = "hash",
            registeredOn = "2026-05-08",
            status = UserStatus.ACTIVE,
        )
        val quickQuality = Quality(1, "Atencion rapida", "Atencion en poco tiempo.", null, CatalogStatus.ACTIVE)
        val kindQuality = Quality(2, "Trato amable", "Atencion cordial.", null, CatalogStatus.ACTIVE)

        val businessBase = businessWithCategory(1, "Farmacia Base")
        val businessExcellent = businessWithCategory(2, "Farmacia Excelencia")
        val businessRecommended = businessWithCategory(3, "Farmacia Recomendable")
        val businessUnrated = businessWithCategory(4, "Farmacia Sin Valoraciones")

        val defaultBusinesses = listOf(
            businessBase,
            businessExcellent,
            businessRecommended,
            businessUnrated,
        )

        val defaultRatings = listOf(
            ratingDetails(
                rating = rating(id = 1, businessId = 1, price = 20.0, service = 5, attention = 4, satisfaction = 5, recommended = true),
                business = businessBase.business,
                qualities = listOf(quickQuality, kindQuality),
            ),
            ratingDetails(
                rating = rating(id = 2, businessId = 1, price = 60.0, service = 3, attention = 4, satisfaction = 4, recommended = false),
                business = businessBase.business,
                qualities = listOf(quickQuality),
            ),
            ratingDetails(
                rating = rating(id = 3, businessId = 2, price = 80.0, service = 5, attention = 5, satisfaction = 5, recommended = false),
                business = businessExcellent.business,
            ),
            ratingDetails(
                rating = rating(id = 4, businessId = 3, price = 30.0, service = 4, attention = 4, satisfaction = 4, recommended = true),
                business = businessRecommended.business,
            ),
            ratingDetails(
                rating = rating(id = 5, businessId = 3, price = 35.0, service = 4, attention = 4, satisfaction = 5, recommended = true),
                business = businessRecommended.business,
            ),
        )

        fun businessWithCategory(id: Long, name: String): BusinessWithCategory {
            return BusinessWithCategory(
                business = Business(
                    id = id,
                    googlePlaceId = "google-place-$id",
                    name = name,
                    description = "Negocio importado desde Google Places.",
                    address = "Chiquimula",
                    categoryId = category.id,
                    status = BusinessStatus.ACTIVE,
                ),
                category = category,
            )
        }

        fun rating(
            id: Long,
            businessId: Long,
            price: Double,
            service: Int,
            attention: Int,
            satisfaction: Int,
            recommended: Boolean,
        ): Rating {
            return Rating(
                id = id,
                userId = user.id,
                businessId = businessId,
                ratedOn = "2026-05-08",
                reportedPrice = price,
                priceReportedOn = "2026-05-08",
                serviceScore = service,
                attentionScore = attention,
                satisfactionScore = satisfaction,
                waitTime = WaitTimeOption.UP_TO_15_MINUTES,
                wouldRecommend = recommended,
                usageFrequency = UsageFrequencyOption.OCCASIONAL,
                availability = AvailabilityOption.AVAILABLE,
                serviceMode = ServiceModeOption.IN_PERSON,
            )
        }

        fun ratingDetails(
            rating: Rating,
            business: Business,
            qualities: List<Quality> = emptyList(),
        ): RatingDetails {
            return RatingDetails(
                rating = rating,
                user = user,
                business = business,
                qualities = qualities,
                evidences = emptyList(),
            )
        }
    }
}

private class FakeMetricsBusinessRepository(
    businesses: List<BusinessWithCategory>,
) : BusinessRepository {
    private val state = MutableStateFlow(businesses)

    override fun observeBusinesses(): Flow<List<BusinessWithCategory>> = state

    override fun observeBusinessesByCategory(categoryId: Long): Flow<List<BusinessWithCategory>> {
        return flowOf(state.value.filter { it.category.id == categoryId })
    }

    override fun searchBusinesses(query: String): Flow<List<BusinessWithCategory>> {
        return flowOf(state.value.filter { it.business.name.contains(query, ignoreCase = true) })
    }

    override fun observeBusiness(businessId: Long): Flow<BusinessWithCategory?> {
        return flowOf(state.value.firstOrNull { it.business.id == businessId })
    }

    override suspend fun getBusinessByGooglePlaceId(googlePlaceId: String): Business? {
        return state.value.firstOrNull { it.business.googlePlaceId == googlePlaceId }?.business
    }

    override suspend fun saveBusiness(business: Business): Long = business.id

    override suspend fun saveBusinesses(businesses: List<Business>) = Unit
}

private class FakeMetricsRatingRepository(
    ratings: List<RatingDetails>,
) : RatingRepository {
    private val state = MutableStateFlow(ratings)

    override fun observeAllRatings(): Flow<List<RatingDetails>> = state

    override fun observeRatingsByUser(userId: Long): Flow<List<RatingDetails>> {
        return flowOf(state.value.filter { it.rating.userId == userId })
    }

    override fun observeRatingsByBusiness(businessId: Long): Flow<List<RatingDetails>> {
        return flowOf(state.value.filter { it.rating.businessId == businessId })
    }

    override fun observeRating(ratingId: Long): Flow<RatingDetails?> {
        return flowOf(state.value.firstOrNull { it.rating.id == ratingId })
    }

    override suspend fun saveStructuredRating(
        rating: Rating,
        selectedQualityIds: List<Long>,
        evidences: List<Evidence>,
    ): Long = rating.id

    override suspend fun saveRating(rating: Rating): Long = rating.id

    override suspend fun saveRatingQualities(items: List<RatingQuality>) = Unit

    override suspend fun saveEvidence(items: List<Evidence>) = Unit
}
