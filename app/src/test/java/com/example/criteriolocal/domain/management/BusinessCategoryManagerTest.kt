package com.example.criteriolocal.domain.management

import com.example.criteriolocal.domain.model.Business
import com.example.criteriolocal.domain.model.BusinessStatus
import com.example.criteriolocal.domain.model.BusinessWithCategory
import com.example.criteriolocal.domain.model.Category
import com.example.criteriolocal.domain.model.NearbyPlace
import com.example.criteriolocal.domain.repository.BusinessRepository
import com.example.criteriolocal.domain.repository.CategoryRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class BusinessCategoryManagerTest {
    @Test
    fun linkGooglePlaceAutomatically_rejectsDuplicatedCategoryCatalog() = runTest {
        val categoryRepository = FakeCategoryRepository(
            initialCategories = listOf(
                Category(2, "Farmacia", "Servicios farmaceuticos"),
                Category(9, "farmacia", "Categoria duplicada"),
            ),
        )
        val manager = BusinessCategoryManager(
            categoryRepository = categoryRepository,
            businessRepository = FakeBusinessRepository(categoryRepository),
        )

        val result = manager.linkGooglePlaceAutomatically(
            LinkGooglePlaceAutomaticallyRequest(
                place = googlePlace(
                    googlePlaceId = "ChIJZ0VERwAxYo8RBdXP_OtR3vY",
                    name = "Farmacia Bienestar",
                    types = listOf("pharmacy", "store"),
                ),
            ),
        )

        assertFalse(result.isSuccess)
        assertEquals(ManagementErrorCode.CATEGORY_ALREADY_EXISTS, result.errors.single().code)
    }

    @Test
    fun linkGooglePlaceAutomatically_requiresLocalCategoryCatalog() = runTest {
        val categoryRepository = FakeCategoryRepository()
        val manager = BusinessCategoryManager(
            categoryRepository = categoryRepository,
            businessRepository = FakeBusinessRepository(categoryRepository),
        )

        val result = manager.linkGooglePlaceAutomatically(
            LinkGooglePlaceAutomaticallyRequest(
                place = googlePlace(
                    googlePlaceId = "ChIJ7fWfyIYxYo8R9Gy1r1pI_eI",
                    name = "Farmacia Central",
                ),
            ),
        )

        assertFalse(result.isSuccess)
        assertEquals(ManagementErrorCode.CATEGORY_NOT_FOUND, result.errors.single().code)
    }

    @Test
    fun linkGooglePlaceAutomatically_resolvesPharmacyCategoryAndSavesBusiness() = runTest {
        val categoryRepository = FakeCategoryRepository(
            initialCategories = listOf(Category(5, "Farmacia", "Servicios farmaceuticos")),
        )
        val businessRepository = FakeBusinessRepository(categoryRepository)
        val manager = BusinessCategoryManager(
            categoryRepository = categoryRepository,
            businessRepository = businessRepository,
        )

        val result = manager.linkGooglePlaceAutomatically(
            LinkGooglePlaceAutomaticallyRequest(
                place = googlePlace(
                    googlePlaceId = "ChIJZ0VERwAxYo8RBdXP_OtR3vY",
                    name = "Farmacia Bienestar",
                    address = "9a Avenida 5-20, Chiquimula",
                    phone = "+502 5555 0000",
                    latitude = 14.7976531,
                    longitude = -89.543457,
                    types = listOf("pharmacy", "store", "health"),
                ),
            ),
        )

        val savedBusiness = businessRepository.observeBusiness(result.value?.id ?: 0).first()

        assertTrue(result.isSuccess)
        assertEquals(1L, result.value?.id)
        assertEquals(5L, result.value?.categoryId)
        assertEquals("ChIJZ0VERwAxYo8RBdXP_OtR3vY", result.value?.googlePlaceId)
        assertTrue(result.value?.description?.contains("Google Places") == true)
        assertEquals(BusinessStatus.ACTIVE, result.value?.status)
        assertEquals("Farmacia", savedBusiness?.category?.name)
    }

    @Test
    fun linkGooglePlaceAutomatically_updatesExistingGoogleBusinessInsteadOfDuplicating() = runTest {
        val categoryRepository = FakeCategoryRepository(
            initialCategories = listOf(
                Category(2, "Farmacia", "Servicios farmaceuticos"),
            ),
        )
        val businessRepository = FakeBusinessRepository(categoryRepository)
        val manager = BusinessCategoryManager(categoryRepository, businessRepository)
        val googlePlaceId = "ChIJ0XRw33kxYo8RIPRdpHlvbFQ"

        val firstLink = manager.linkGooglePlaceAutomatically(
            LinkGooglePlaceAutomaticallyRequest(
                place = googlePlace(googlePlaceId = googlePlaceId, name = "Farmacia Doctor Farma"),
            ),
        )
        val secondLink = manager.linkGooglePlaceAutomatically(
            LinkGooglePlaceAutomaticallyRequest(
                place = googlePlace(googlePlaceId = googlePlaceId, name = "Farmacia Doctor Farma"),
            ),
        )

        assertTrue(firstLink.isSuccess)
        assertTrue(secondLink.isSuccess)
        assertEquals(firstLink.value?.id, secondLink.value?.id)
        assertEquals(2L, secondLink.value?.categoryId)
        assertEquals(1, businessRepository.observeBusinesses().first().size)
    }

    @Test
    fun linkGooglePlaceAutomatically_prioritizesMedicalCategoryOverPharmacyForHospitalPlace() = runTest {
        val categoryRepository = FakeCategoryRepository(
            initialCategories = listOf(
                Category(2, "Farmacia", "Servicios farmaceuticos"),
                Category(5, "Clinica medica", "Atencion medica"),
            ),
        )
        val manager = BusinessCategoryManager(
            categoryRepository = categoryRepository,
            businessRepository = FakeBusinessRepository(categoryRepository),
        )

        val result = manager.linkGooglePlaceAutomatically(
            LinkGooglePlaceAutomaticallyRequest(
                place = googlePlace(
                    googlePlaceId = "ChIJEZquc28xYo8RQT7eAI8K1XU",
                    name = "Portal America",
                    types = listOf("hospital", "doctor", "pharmacy", "store", "health"),
                ),
            ),
        )

        assertTrue(result.isSuccess)
        assertEquals(5L, result.value?.categoryId)
    }

    @Test
    fun linkGooglePlaceAutomatically_usesLocalStoreFallbackForUnknownTypes() = runTest {
        val categoryRepository = FakeCategoryRepository(
            initialCategories = listOf(Category(6, "Tienda local", "Comercio local")),
        )
        val manager = BusinessCategoryManager(
            categoryRepository = categoryRepository,
            businessRepository = FakeBusinessRepository(categoryRepository),
        )

        val result = manager.linkGooglePlaceAutomatically(
            LinkGooglePlaceAutomaticallyRequest(
                place = googlePlace(
                    googlePlaceId = "unknown-place",
                    name = "Negocio sin tipo especifico",
                    types = listOf("point_of_interest", "establishment"),
                ),
            ),
        )

        assertTrue(result.isSuccess)
        assertEquals(6L, result.value?.categoryId)
    }

    @Test
    fun searchBusinesses_usesAllBusinessesWhenQueryIsBlank() = runTest {
        val category = Category(2, "Farmacia", "Servicios farmaceuticos")
        val categoryRepository = FakeCategoryRepository(listOf(category))
        val businessRepository = FakeBusinessRepository(
            categoryRepository = categoryRepository,
            initialBusinesses = listOf(
                BusinessWithCategory(
                    business = Business(
                        id = 1,
                        name = "Farmacia Local",
                        description = "Negocio local",
                        address = "Centro",
                        categoryId = 2,
                        status = BusinessStatus.ACTIVE,
                    ),
                    category = category,
                ),
            ),
        )
        val manager = BusinessCategoryManager(categoryRepository, businessRepository)

        val result = manager.searchBusinesses(" ").first()

        assertEquals(1, result.size)
        assertEquals("Farmacia Local", result.single().business.name)
    }
}

private class FakeCategoryRepository(
    initialCategories: List<Category> = emptyList(),
) : CategoryRepository {
    val categories = MutableStateFlow(initialCategories)

    override fun observeCategories(): Flow<List<Category>> = categories

    override suspend fun getCategory(categoryId: Long): Category? {
        return categories.value.firstOrNull { it.id == categoryId }
    }

    override suspend fun saveCategory(category: Category): Long {
        val savedId = category.id.takeIf { it > 0 } ?: ((categories.value.maxOfOrNull { it.id } ?: 0) + 1)
        val saved = category.copy(id = savedId)
        categories.value = categories.value.filterNot { it.id == savedId } + saved
        return savedId
    }

    override suspend fun saveCategories(categories: List<Category>) {
        this.categories.value = categories
    }
}

private class FakeBusinessRepository(
    private val categoryRepository: FakeCategoryRepository,
    initialBusinesses: List<BusinessWithCategory> = emptyList(),
) : BusinessRepository {
    private val businesses = MutableStateFlow(initialBusinesses)

    override fun observeBusinesses(): Flow<List<BusinessWithCategory>> = businesses

    override fun observeBusinessesByCategory(categoryId: Long): Flow<List<BusinessWithCategory>> {
        return flowOf(businesses.value.filter { it.category.id == categoryId })
    }

    override fun searchBusinesses(query: String): Flow<List<BusinessWithCategory>> {
        return flowOf(businesses.value.filter { it.business.name.contains(query, ignoreCase = true) })
    }

    override fun observeBusiness(businessId: Long): Flow<BusinessWithCategory?> {
        return flowOf(businesses.value.firstOrNull { it.business.id == businessId })
    }

    override suspend fun getBusinessByGooglePlaceId(googlePlaceId: String): Business? {
        return businesses.value.firstOrNull { it.business.googlePlaceId == googlePlaceId }?.business
    }

    override suspend fun saveBusiness(business: Business): Long {
        val savedId = business.id.takeIf { it > 0 } ?: ((businesses.value.maxOfOrNull { it.business.id } ?: 0) + 1)
        val savedBusiness = business.copy(id = savedId)
        val category = checkNotNull(categoryRepository.getCategory(savedBusiness.categoryId))
        businesses.value = businesses.value.filterNot { it.business.id == savedId } + BusinessWithCategory(
            business = savedBusiness,
            category = category,
        )
        return savedId
    }

    override suspend fun saveBusinesses(businesses: List<Business>) {
        this.businesses.value = businesses.mapNotNull { business ->
            categoryRepository.getCategory(business.categoryId)?.let { category ->
                BusinessWithCategory(business = business, category = category)
            }
        }
    }
}

private fun googlePlace(
    googlePlaceId: String,
    name: String,
    address: String? = "4a Calle, Chiquimula",
    phone: String? = null,
    latitude: Double = 14.7906,
    longitude: Double = -89.5447,
    types: List<String> = listOf("pharmacy"),
    businessStatus: String? = "OPERATIONAL",
): NearbyPlace {
    return NearbyPlace(
        googlePlaceId = googlePlaceId,
        name = name,
        businessStatus = businessStatus,
        latitude = latitude,
        longitude = longitude,
        address = address,
        phone = phone,
        types = types,
        rating = null,
        userRatingsTotal = null,
        isOpenNow = null,
        iconUrl = null,
        photoUrl = null,
    )
}
