package com.example.criteriolocal.ui.home

import com.example.criteriolocal.domain.model.Business
import com.example.criteriolocal.domain.model.BusinessStatus
import com.example.criteriolocal.domain.model.BusinessWithCategory
import com.example.criteriolocal.domain.model.CatalogStatus
import com.example.criteriolocal.domain.model.Category
import com.example.criteriolocal.domain.model.NearbyPlace
import com.example.criteriolocal.domain.model.NearbyPlaceSearchRequest
import com.example.criteriolocal.domain.model.NearbyPlaceSearchResult
import com.example.criteriolocal.domain.model.Quality
import com.example.criteriolocal.domain.model.User
import com.example.criteriolocal.domain.model.UserStatus
import com.example.criteriolocal.domain.model.UserWithRatings
import com.example.criteriolocal.domain.repository.BootstrapRepository
import com.example.criteriolocal.domain.repository.BusinessRepository
import com.example.criteriolocal.domain.repository.CategoryRepository
import com.example.criteriolocal.domain.repository.QualityRepository
import com.example.criteriolocal.domain.repository.RemotePlaceRepository
import com.example.criteriolocal.domain.repository.UserRepository
import com.example.criteriolocal.utils.MainDispatcherRule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class HomeViewModelTest {
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Test
    fun init_loadsLocalState_andRemotePlaces() = runTest {
        val bootstrapRepository = FakeBootstrapRepository()
        val category = Category(2, "Farmacia", "Servicios farmaceuticos")
        val viewModel = HomeViewModel(
            bootstrapRepository = bootstrapRepository,
            categoryRepository = FakeCategoryRepository(listOf(category)),
            businessRepository = FakeBusinessRepository(
                listOf(
                    BusinessWithCategory(
                        business = Business(
                            id = 1,
                            name = "Farmacia Local",
                            description = "Negocio local base",
                            address = "8a Calle 3-18, Chiquimula",
                            phone = "5550-0102",
                            latitude = 14.7967,
                            longitude = -89.5460,
                            categoryId = 2,
                            status = BusinessStatus.ACTIVE,
                        ),
                        category = category,
                    ),
                ),
            ),
            qualityRepository = FakeQualityRepository(
                listOf(
                    Quality(1, "Atencion rapida", "Atencion en poco tiempo", null, CatalogStatus.ACTIVE),
                ),
            ),
            userRepository = FakeUserRepository(
                listOf(
                    User(
                        id = 1,
                        name = "Usuario Piloto",
                        email = "piloto@criteriolocal.local",
                        passwordHash = "hash",
                        registeredOn = "2026-04-22",
                        status = UserStatus.ACTIVE,
                    ),
                ),
            ),
            remotePlaceRepository = FakeRemotePlaceRepository(
                result = NearbyPlaceSearchResult(
                    status = "OK",
                    nextPageToken = null,
                    places = listOf(
                        NearbyPlace(
                            googlePlaceId = "ChIJ7fWfyIYxYo8R9Gy1r1pI_eI",
                            name = "Farmavital L&N",
                            businessStatus = "OPERATIONAL",
                            latitude = 14.7924897,
                            longitude = -89.5450458,
                            address = "4a Calle 1-70, Chiquimula",
                            phone = "+502 3220 3463",
                            types = listOf("pharmacy", "store"),
                            rating = 5.0,
                            userRatingsTotal = 10,
                            isOpenNow = true,
                            iconUrl = null,
                        ),
                    ),
                ),
            ),
        )

        val collector = backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.uiState.collect { }
        }

        advanceUntilIdle()

        val state = viewModel.uiState.value

        assertTrue(bootstrapRepository.wasCalled)
        assertFalse(state.isLoading)
        assertEquals(1, state.categories.size)
        assertEquals(1, state.businesses.size)
        assertEquals(1, state.remotePlaces.size)
        assertEquals("OK", state.remotePlacesStatus)
        assertNull(state.remotePlacesError)
        assertEquals("Farmavital L&N", state.remotePlaces.first().name)

        collector.cancel()
    }

    @Test
    fun init_keepsLocalBusinesses_whenRemoteApiFails() = runTest {
        val category = Category(2, "Farmacia", "Servicios farmaceuticos")
        val viewModel = HomeViewModel(
            bootstrapRepository = FakeBootstrapRepository(),
            categoryRepository = FakeCategoryRepository(listOf(category)),
            businessRepository = FakeBusinessRepository(
                listOf(
                    BusinessWithCategory(
                        business = Business(
                            id = 1,
                            name = "Farmacia Local",
                            description = "Negocio local base",
                            address = "8a Calle 3-18, Chiquimula",
                            phone = "5550-0102",
                            latitude = 14.7967,
                            longitude = -89.5460,
                            categoryId = 2,
                            status = BusinessStatus.ACTIVE,
                        ),
                        category = category,
                    ),
                ),
            ),
            qualityRepository = FakeQualityRepository(emptyList()),
            userRepository = FakeUserRepository(emptyList()),
            remotePlaceRepository = FakeRemotePlaceRepository(
                error = IllegalStateException("Fallo al consultar Google Places"),
            ),
        )

        val collector = backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.uiState.collect { }
        }

        advanceUntilIdle()

        val state = viewModel.uiState.value

        assertFalse(state.isLoading)
        assertEquals(1, state.businesses.size)
        assertTrue(state.remotePlaces.isEmpty())
        assertEquals("Fallo al consultar Google Places", state.remotePlacesError)

        collector.cancel()
    }
}

private class FakeBootstrapRepository : BootstrapRepository {
    var wasCalled: Boolean = false

    override suspend fun seedBaseData() {
        wasCalled = true
    }
}

private class FakeCategoryRepository(
    items: List<Category>,
) : CategoryRepository {
    private val state = MutableStateFlow(items)

    override fun observeCategories(): Flow<List<Category>> = state

    override suspend fun getCategory(categoryId: Long): Category? = state.value.firstOrNull { it.id == categoryId }

    override suspend fun saveCategories(categories: List<Category>) {
        state.value = categories
    }
}

private class FakeBusinessRepository(
    items: List<BusinessWithCategory>,
) : BusinessRepository {
    private val state = MutableStateFlow(items)

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

    override suspend fun saveBusinesses(businesses: List<Business>) {
        state.value = businesses.map {
            BusinessWithCategory(
                business = it,
                category = Category(it.categoryId, "Categoria ${it.categoryId}", "Categoria generada"),
            )
        }
    }
}

private class FakeQualityRepository(
    items: List<Quality>,
) : QualityRepository {
    private val state = MutableStateFlow(items)

    override fun observeQualities(): Flow<List<Quality>> = state

    override fun observeQualitiesByCategory(categoryId: Long): Flow<List<Quality>> {
        return flowOf(state.value.filter { it.applicableCategoryId == null || it.applicableCategoryId == categoryId })
    }

    override suspend fun saveQualities(qualities: List<Quality>) {
        state.value = qualities
    }
}

private class FakeUserRepository(
    items: List<User>,
) : UserRepository {
    private val state = MutableStateFlow(items)

    override fun observeUsers(): Flow<List<User>> = state

    override fun observeUser(userId: Long): Flow<User?> = flowOf(state.value.firstOrNull { it.id == userId })

    override fun observeUserWithRatings(userId: Long): Flow<UserWithRatings?> {
        val user = state.value.firstOrNull { it.id == userId } ?: return flowOf(null)
        return flowOf(UserWithRatings(user = user, ratings = emptyList()))
    }

    override suspend fun getUserByEmail(email: String): User? = state.value.firstOrNull { it.email == email }

    override suspend fun saveUsers(users: List<User>) {
        state.value = users
    }
}

private class FakeRemotePlaceRepository(
    private val result: NearbyPlaceSearchResult? = null,
    private val error: Throwable? = null,
) : RemotePlaceRepository {
    override suspend fun searchNearby(request: NearbyPlaceSearchRequest): NearbyPlaceSearchResult {
        error?.let { throw it }
        return checkNotNull(result)
    }
}
