package com.example.criteriolocal.domain.management

import com.example.criteriolocal.domain.model.AvailabilityOption
import com.example.criteriolocal.domain.model.Business
import com.example.criteriolocal.domain.model.BusinessStatus
import com.example.criteriolocal.domain.model.BusinessWithCategory
import com.example.criteriolocal.domain.model.CatalogStatus
import com.example.criteriolocal.domain.model.Category
import com.example.criteriolocal.domain.model.Evidence
import com.example.criteriolocal.domain.model.EvidenceType
import com.example.criteriolocal.domain.model.Quality
import com.example.criteriolocal.domain.model.Rating
import com.example.criteriolocal.domain.model.RatingDetails
import com.example.criteriolocal.domain.model.RatingQuality
import com.example.criteriolocal.domain.model.ServiceModeOption
import com.example.criteriolocal.domain.model.UsageFrequencyOption
import com.example.criteriolocal.domain.model.User
import com.example.criteriolocal.domain.model.UserStatus
import com.example.criteriolocal.domain.model.UserWithRatings
import com.example.criteriolocal.domain.model.WaitTimeOption
import com.example.criteriolocal.domain.repository.BusinessRepository
import com.example.criteriolocal.domain.repository.QualityRepository
import com.example.criteriolocal.domain.repository.RatingRepository
import com.example.criteriolocal.domain.repository.UserRepository
import com.example.criteriolocal.domain.validation.ValidationErrorCode
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class RatingRegistrationManagerTest {
    @Test
    fun registerStructuredRating_savesRatingQualitiesAndEvidence() = runTest {
        val ratingRepository = FakeRatingRepository()
        val manager = ratingManager(ratingRepository = ratingRepository)

        val result = manager.registerStructuredRating(
            validRequest(
                evidences = listOf(
                    RegisterEvidenceRequest(
                        filePath = "evidencias/factura-001.jpg",
                        fileType = EvidenceType.IMAGE,
                        fileSizeBytes = 250_000L,
                        uploadedOn = "2026-04-30",
                    ),
                ),
            ),
        )

        assertTrue(result.isSuccess)
        assertEquals(1L, result.ratingId)
        assertEquals(1, ratingRepository.savedRatings.size)
        assertEquals(listOf(1L, 2L), ratingRepository.savedQualityIds)
        assertEquals(1, ratingRepository.savedEvidences.size)
        assertEquals(1L, ratingRepository.savedEvidences.single().ratingId)
    }

    @Test
    fun registerStructuredRating_acceptsEmptyEvidenceBecauseItIsOptional() = runTest {
        val ratingRepository = FakeRatingRepository()
        val manager = ratingManager(ratingRepository = ratingRepository)

        val result = manager.registerStructuredRating(validRequest(evidences = emptyList()))

        assertTrue(result.isSuccess)
        assertEquals(1L, result.ratingId)
        assertTrue(ratingRepository.savedEvidences.isEmpty())
    }

    @Test
    fun registerStructuredRating_rejectsFreeTextAndDoesNotPersist() = runTest {
        val ratingRepository = FakeRatingRepository()
        val manager = ratingManager(ratingRepository = ratingRepository)

        val result = manager.registerStructuredRating(
            validRequest(freeTextComment = "Muy malo, no vuelvo."),
        )

        assertFalse(result.isSuccess)
        assertTrue(result.errors.any { it.code == ValidationErrorCode.FREE_TEXT_NOT_ALLOWED })
        assertTrue(ratingRepository.savedRatings.isEmpty())
    }

    @Test
    fun registerStructuredRating_rejectsQualityOutsideOfficialCatalog() = runTest {
        val ratingRepository = FakeRatingRepository()
        val manager = ratingManager(ratingRepository = ratingRepository)

        val result = manager.registerStructuredRating(
            validRequest(selectedQualityIds = listOf(99L)),
        )

        assertFalse(result.isSuccess)
        assertTrue(result.errors.any { it.code == ValidationErrorCode.QUALITY_NOT_IN_CATALOG })
        assertTrue(ratingRepository.savedRatings.isEmpty())
    }

    @Test
    fun registerStructuredRating_rejectsMissingUserAndBusiness() = runTest {
        val ratingRepository = FakeRatingRepository()
        val manager = ratingManager(
            userRepository = FakeRatingUserRepository(emptyList()),
            businessRepository = FakeRatingBusinessRepository(emptyList()),
            ratingRepository = ratingRepository,
        )

        val result = manager.registerStructuredRating(validRequest(userId = 99, businessId = 88))

        assertFalse(result.isSuccess)
        assertTrue(result.errors.any { it.code == ValidationErrorCode.USER_NOT_FOUND })
        assertTrue(result.errors.any { it.code == ValidationErrorCode.BUSINESS_NOT_FOUND })
        assertTrue(ratingRepository.savedRatings.isEmpty())
    }

    @Test
    fun registerStructuredRating_rejectsInvalidEvidenceButKeepsEvidenceOptional() = runTest {
        val ratingRepository = FakeRatingRepository()
        val manager = ratingManager(ratingRepository = ratingRepository)

        val result = manager.registerStructuredRating(
            validRequest(
                evidences = listOf(
                    RegisterEvidenceRequest(
                        filePath = "",
                        fileType = null,
                        fileSizeBytes = 6L * 1024L * 1024L,
                        uploadedOn = "2026-14-99",
                    ),
                ),
            ),
        )

        assertFalse(result.isSuccess)
        assertTrue(result.errors.any { it.code == ValidationErrorCode.EVIDENCE_FILE_PATH_REQUIRED })
        assertTrue(result.errors.any { it.code == ValidationErrorCode.EVIDENCE_FILE_TYPE_REQUIRED })
        assertTrue(result.errors.any { it.code == ValidationErrorCode.EVIDENCE_FILE_TOO_LARGE })
        assertTrue(result.errors.any { it.code == ValidationErrorCode.EVIDENCE_UPLOAD_DATE_INVALID })
        assertTrue(ratingRepository.savedRatings.isEmpty())
    }

    private fun ratingManager(
        userRepository: UserRepository = FakeRatingUserRepository(listOf(defaultUser)),
        businessRepository: BusinessRepository = FakeRatingBusinessRepository(listOf(defaultBusinessWithCategory)),
        qualityRepository: QualityRepository = FakeRatingQualityRepository(defaultQualities),
        ratingRepository: RatingRepository = FakeRatingRepository(),
    ): RatingRegistrationManager {
        return RatingRegistrationManager(
            userRepository = userRepository,
            businessRepository = businessRepository,
            qualityRepository = qualityRepository,
            ratingRepository = ratingRepository,
        )
    }

    private fun validRequest(
        userId: Long? = 1,
        businessId: Long? = 1,
        selectedQualityIds: List<Long> = listOf(1L, 2L),
        evidences: List<RegisterEvidenceRequest> = emptyList(),
        freeTextComment: String? = null,
    ): RegisterStructuredRatingRequest {
        return RegisterStructuredRatingRequest(
            userId = userId,
            businessId = businessId,
            ratedOn = "2026-04-30",
            reportedPrice = 45.50,
            priceReportedOn = "2026-04-30",
            serviceScore = 5,
            attentionScore = 4,
            satisfactionScore = 5,
            waitTime = WaitTimeOption.UP_TO_15_MINUTES,
            wouldRecommend = true,
            usageFrequency = UsageFrequencyOption.OCCASIONAL,
            availability = AvailabilityOption.AVAILABLE,
            serviceMode = ServiceModeOption.IN_PERSON,
            selectedQualityIds = selectedQualityIds,
            evidences = evidences,
            freeTextComment = freeTextComment,
        )
    }

    private companion object {
        val defaultUser = User(
            id = 1,
            name = "Usuario Piloto",
            email = "piloto@criteriolocal.local",
            passwordHash = "hash",
            registeredOn = "2026-04-30",
            status = UserStatus.ACTIVE,
        )

        val defaultCategory = Category(
            id = 2,
            name = "Farmacia",
            description = "Venta de medicamentos.",
        )

        val defaultBusinessWithCategory = BusinessWithCategory(
            business = Business(
                id = 1,
                googlePlaceId = "ChIJ7fWfyIYxYo8R9Gy1r1pI_eI",
                name = "Farmavital L&N",
                description = "Negocio importado desde Google Places.",
                address = "4a Calle 1-70, Chiquimula",
                categoryId = 2,
                status = BusinessStatus.ACTIVE,
            ),
            category = defaultCategory,
        )

        val defaultQualities = listOf(
            Quality(1, "Atencion rapida", "Atencion en poco tiempo.", null, CatalogStatus.ACTIVE),
            Quality(2, "Variedad de productos", "Surtido suficiente.", 2, CatalogStatus.ACTIVE),
        )
    }
}

private class FakeRatingUserRepository(
    users: List<User>,
) : UserRepository {
    private val state = MutableStateFlow(users)

    override fun observeUsers(): Flow<List<User>> = state

    override fun observeUser(userId: Long): Flow<User?> {
        return flowOf(state.value.firstOrNull { it.id == userId })
    }

    override fun observeUserWithRatings(userId: Long): Flow<UserWithRatings?> {
        val user = state.value.firstOrNull { it.id == userId } ?: return flowOf(null)
        return flowOf(UserWithRatings(user = user, ratings = emptyList()))
    }

    override suspend fun getUserByEmail(email: String): User? {
        return state.value.firstOrNull { it.email == email }
    }

    override suspend fun saveUser(user: User): Long {
        val id = user.id.takeIf { it > 0 } ?: ((state.value.maxOfOrNull { it.id } ?: 0) + 1)
        state.value = state.value + user.copy(id = id)
        return id
    }

    override suspend fun saveUsers(users: List<User>) {
        state.value = users
    }
}

private class FakeRatingBusinessRepository(
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

private class FakeRatingQualityRepository(
    qualities: List<Quality>,
) : QualityRepository {
    private val state = MutableStateFlow(qualities)

    override fun observeQualities(): Flow<List<Quality>> = state

    override fun observeQualitiesByCategory(categoryId: Long): Flow<List<Quality>> {
        return flowOf(state.value.filter { it.applicableCategoryId == null || it.applicableCategoryId == categoryId })
    }

    override suspend fun saveQualities(qualities: List<Quality>) {
        state.value = qualities
    }
}

private class FakeRatingRepository : RatingRepository {
    val savedRatings = mutableListOf<Rating>()
    val savedQualityIds = mutableListOf<Long>()
    val savedEvidences = mutableListOf<Evidence>()

    override fun observeAllRatings(): Flow<List<RatingDetails>> = flowOf(emptyList())

    override fun observeRatingsByUser(userId: Long): Flow<List<RatingDetails>> = flowOf(emptyList())

    override fun observeRatingsByBusiness(businessId: Long): Flow<List<RatingDetails>> = flowOf(emptyList())

    override fun observeRating(ratingId: Long): Flow<RatingDetails?> = flowOf(null)

    override suspend fun saveStructuredRating(
        rating: Rating,
        selectedQualityIds: List<Long>,
        evidences: List<Evidence>,
    ): Long {
        val ratingId = (savedRatings.maxOfOrNull { it.id } ?: 0) + 1
        savedRatings += rating.copy(id = ratingId)
        savedQualityIds += selectedQualityIds
        savedEvidences += evidences.map { it.copy(ratingId = ratingId) }
        return ratingId
    }

    override suspend fun saveRating(rating: Rating): Long {
        val ratingId = (savedRatings.maxOfOrNull { it.id } ?: 0) + 1
        savedRatings += rating.copy(id = ratingId)
        return ratingId
    }

    override suspend fun saveRatingQualities(items: List<RatingQuality>) {
        savedQualityIds += items.map { it.qualityId }
    }

    override suspend fun saveEvidence(items: List<Evidence>) {
        savedEvidences += items
    }
}
