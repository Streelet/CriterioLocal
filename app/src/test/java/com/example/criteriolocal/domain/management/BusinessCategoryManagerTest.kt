package com.example.criteriolocal.domain.management

import com.example.criteriolocal.domain.model.Business
import com.example.criteriolocal.domain.model.BusinessStatus
import com.example.criteriolocal.domain.model.BusinessWithCategory
import com.example.criteriolocal.domain.model.Category
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
    fun registerCategory_savesCategoryAndRejectsDuplicateName() = runTest {
        val categoryRepository = FakeCategoryRepository()
        val manager = BusinessCategoryManager(
            categoryRepository = categoryRepository,
            businessRepository = FakeBusinessRepository(categoryRepository),
        )

        val created = manager.registerCategory(
            CreateCategoryRequest(
                name = " Farmacia ",
                description = "Servicios farmaceuticos",
            ),
        )
        val duplicated = manager.registerCategory(
            CreateCategoryRequest(
                name = "farmacia",
                description = "Duplicada",
            ),
        )

        assertTrue(created.isSuccess)
        assertEquals(1L, created.value?.id)
        assertEquals("Farmacia", created.value?.name)
        assertFalse(duplicated.isSuccess)
        assertEquals(ManagementErrorCode.CATEGORY_ALREADY_EXISTS, duplicated.errors.single().code)
    }

    @Test
    fun registerBusiness_requiresExistingCategory() = runTest {
        val categoryRepository = FakeCategoryRepository()
        val manager = BusinessCategoryManager(
            categoryRepository = categoryRepository,
            businessRepository = FakeBusinessRepository(categoryRepository),
        )

        val result = manager.registerBusiness(
            CreateBusinessRequest(
                name = "Farmacia Central",
                description = "Venta de medicamentos",
                address = "4a Calle, Chiquimula",
                categoryId = 99,
            ),
        )

        assertFalse(result.isSuccess)
        assertEquals(ManagementErrorCode.CATEGORY_NOT_FOUND, result.errors.single().code)
    }

    @Test
    fun registerBusiness_savesBusinessWithCategoryAssociation() = runTest {
        val categoryRepository = FakeCategoryRepository(
            initialCategories = listOf(Category(5, "Farmacia", "Servicios farmaceuticos")),
        )
        val businessRepository = FakeBusinessRepository(categoryRepository)
        val manager = BusinessCategoryManager(
            categoryRepository = categoryRepository,
            businessRepository = businessRepository,
        )

        val result = manager.registerBusiness(
            CreateBusinessRequest(
                name = "Farmacia Bienestar",
                description = "Farmacia local",
                address = "9a Avenida 5-20, Chiquimula",
                phone = "+502 5555 0000",
                categoryId = 5,
                googlePlaceId = "ChIJZ0VERwAxYo8RBdXP_OtR3vY",
                latitude = 14.7976531,
                longitude = -89.543457,
            ),
        )

        val savedBusiness = businessRepository.observeBusiness(result.value?.id ?: 0).first()

        assertTrue(result.isSuccess)
        assertEquals(1L, result.value?.id)
        assertEquals(5L, result.value?.categoryId)
        assertEquals(BusinessStatus.ACTIVE, result.value?.status)
        assertEquals("Farmacia", savedBusiness?.category?.name)
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
