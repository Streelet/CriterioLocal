package com.example.criteriolocal.domain.query

import com.example.criteriolocal.domain.model.AvailabilityOption
import com.example.criteriolocal.domain.model.Business
import com.example.criteriolocal.domain.model.BusinessStatus
import com.example.criteriolocal.domain.model.BusinessWithCategory
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

class BusinessQueryManagerTest {
    @Test
    fun searchBusinessesByName_returnsMatchingBusinessesWithBasicStats() = runTest {
        val manager = BusinessQueryManager(
            businessRepository = FakeQueryBusinessRepository(defaultBusinesses),
            ratingRepository = FakeQueryRatingRepository(defaultRatings),
        )

        val result = manager.searchBusinessesByName("farma").first()

        assertEquals(1, result.size)
        assertEquals("Farmacia Bienestar", result.single().businessWithCategory.business.name)
        assertEquals(2, result.single().totalRatings)
        assertEquals(4.0, result.single().averageScore ?: 0.0, 0.001)
        assertEquals(25.0, result.single().minReportedPrice ?: 0.0, 0.001)
        assertEquals(40.0, result.single().maxReportedPrice ?: 0.0, 0.001)
    }

    @Test
    fun searchBusinessesByCategory_returnsOnlyCategoryMatches() = runTest {
        val manager = BusinessQueryManager(
            businessRepository = FakeQueryBusinessRepository(defaultBusinesses),
            ratingRepository = FakeQueryRatingRepository(defaultRatings),
        )

        val result = manager.searchBusinessesByCategory(categoryId = 3).first()

        assertEquals(1, result.size)
        assertEquals("Restaurante Central", result.single().businessWithCategory.business.name)
    }

    @Test
    fun searchBusinesses_appliesScoreAndPriceFiltersWithoutRanking() = runTest {
        val manager = BusinessQueryManager(
            businessRepository = FakeQueryBusinessRepository(defaultBusinesses),
            ratingRepository = FakeQueryRatingRepository(defaultRatings),
        )

        val result = manager.searchBusinesses(
            BusinessSearchFilters(
                minAverageScore = 4.0,
                minReportedPrice = 20.0,
                maxReportedPrice = 45.0,
            ),
        ).first()

        assertEquals(1, result.size)
        assertEquals("Farmacia Bienestar", result.single().businessWithCategory.business.name)
    }

    @Test
    fun observeBusinessDetail_returnsBusinessAndRatings() = runTest {
        val manager = BusinessQueryManager(
            businessRepository = FakeQueryBusinessRepository(defaultBusinesses),
            ratingRepository = FakeQueryRatingRepository(defaultRatings),
        )

        val detail = manager.observeBusinessDetail(businessId = 1).first()

        assertEquals("Farmacia Bienestar", detail?.businessWithCategory?.business?.name)
        assertEquals(2, detail?.ratings?.size)
    }

    @Test
    fun observeBusinessDetail_returnsNullForMissingBusiness() = runTest {
        val manager = BusinessQueryManager(
            businessRepository = FakeQueryBusinessRepository(defaultBusinesses),
            ratingRepository = FakeQueryRatingRepository(defaultRatings),
        )

        val detail = manager.observeBusinessDetail(businessId = 99).first()

        assertNull(detail)
    }

    @Test
    fun observeUserRatingHistory_returnsOnlyUserRatings() = runTest {
        val manager = BusinessQueryManager(
            businessRepository = FakeQueryBusinessRepository(defaultBusinesses),
            ratingRepository = FakeQueryRatingRepository(defaultRatings),
        )

        val history = manager.observeUserRatingHistory(userId = 1).first()

        assertEquals(1L, history.userId)
        assertEquals(2, history.ratings.size)
        assertEquals(setOf(1L), history.ratings.map { it.rating.userId }.toSet())
    }

    private companion object {
        val pharmacyCategory = Category(2, "Farmacia", "Venta de medicamentos.")
        val restaurantCategory = Category(3, "Restaurante", "Alimentos preparados.")

        val userOne = User(
            id = 1,
            name = "Usuario Uno",
            email = "uno@criteriolocal.local",
            passwordHash = "hash",
            registeredOn = "2026-04-30",
            status = UserStatus.ACTIVE,
        )
        val userTwo = User(
            id = 2,
            name = "Usuario Dos",
            email = "dos@criteriolocal.local",
            passwordHash = "hash",
            registeredOn = "2026-04-30",
            status = UserStatus.ACTIVE,
        )

        val pharmacy = BusinessWithCategory(
            business = Business(
                id = 1,
                googlePlaceId = "pharmacy-google-id",
                name = "Farmacia Bienestar",
                description = "Negocio importado desde Google Places.",
                address = "9a Avenida, Chiquimula",
                categoryId = 2,
                status = BusinessStatus.ACTIVE,
            ),
            category = pharmacyCategory,
        )
        val restaurant = BusinessWithCategory(
            business = Business(
                id = 2,
                googlePlaceId = "restaurant-google-id",
                name = "Restaurante Central",
                description = "Negocio importado desde Google Places.",
                address = "Centro, Chiquimula",
                categoryId = 3,
                status = BusinessStatus.ACTIVE,
            ),
            category = restaurantCategory,
        )

        val defaultBusinesses = listOf(pharmacy, restaurant)
        val defaultRatings = listOf(
            ratingDetails(rating = rating(id = 1, userId = 1, businessId = 1, price = 25.0, service = 4, attention = 4, satisfaction = 4), user = userOne, business = pharmacy.business),
            ratingDetails(rating = rating(id = 2, userId = 1, businessId = 1, price = 40.0, service = 5, attention = 3, satisfaction = 4), user = userOne, business = pharmacy.business),
            ratingDetails(rating = rating(id = 3, userId = 2, businessId = 2, price = 90.0, service = 5, attention = 5, satisfaction = 5), user = userTwo, business = restaurant.business),
        )

        fun rating(
            id: Long,
            userId: Long,
            businessId: Long,
            price: Double,
            service: Int,
            attention: Int,
            satisfaction: Int,
        ): Rating {
            return Rating(
                id = id,
                userId = userId,
                businessId = businessId,
                ratedOn = "2026-04-30",
                reportedPrice = price,
                priceReportedOn = "2026-04-30",
                serviceScore = service,
                attentionScore = attention,
                satisfactionScore = satisfaction,
                waitTime = WaitTimeOption.UP_TO_15_MINUTES,
                wouldRecommend = true,
                usageFrequency = UsageFrequencyOption.OCCASIONAL,
                availability = AvailabilityOption.AVAILABLE,
                serviceMode = ServiceModeOption.IN_PERSON,
            )
        }

        fun ratingDetails(
            rating: Rating,
            user: User,
            business: Business,
        ): RatingDetails {
            return RatingDetails(
                rating = rating,
                user = user,
                business = business,
                qualities = emptyList(),
                evidences = emptyList(),
            )
        }
    }
}

private class FakeQueryBusinessRepository(
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

private class FakeQueryRatingRepository(
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
