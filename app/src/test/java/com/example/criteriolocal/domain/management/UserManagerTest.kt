package com.example.criteriolocal.domain.management

import com.example.criteriolocal.domain.model.Rating
import com.example.criteriolocal.domain.model.User
import com.example.criteriolocal.domain.model.UserStatus
import com.example.criteriolocal.domain.model.UserWithRatings
import com.example.criteriolocal.domain.repository.UserRepository
import com.example.criteriolocal.domain.security.PasswordHasher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class UserManagerTest {
    @Test
    fun registerUser_savesNormalizedEmailAndPasswordHash() = runTest {
        val repository = FakeUserRepository()
        val manager = UserManager(repository)

        val result = manager.registerUser(
            RegisterUserRequest(
                name = "  Maria Lopez  ",
                email = " MARIA@Correo.COM ",
                password = "claveSegura123",
                registeredOn = "2026-04-29",
            ),
        )

        assertTrue(result.isSuccess)
        assertEquals(1L, result.value?.id)
        assertEquals("Maria Lopez", result.value?.name)
        assertEquals("maria@correo.com", result.value?.email)
        assertNotEquals("claveSegura123", result.value?.passwordHash)
        assertEquals(PasswordHasher.sha256("claveSegura123"), repository.users.value.first().passwordHash)
    }

    @Test
    fun registerUser_rejectsDuplicatedEmail() = runTest {
        val repository = FakeUserRepository(
            initialUsers = listOf(
                User(
                    id = 1,
                    name = "Usuario existente",
                    email = "persona@correo.com",
                    passwordHash = PasswordHasher.sha256("claveSegura123"),
                    registeredOn = "2026-04-29",
                    status = UserStatus.ACTIVE,
                ),
            ),
        )
        val manager = UserManager(repository)

        val result = manager.registerUser(
            RegisterUserRequest(
                name = "Persona",
                email = "PERSONA@correo.com",
                password = "claveSegura123",
                registeredOn = "2026-04-29",
            ),
        )

        assertFalse(result.isSuccess)
        assertEquals(ManagementErrorCode.EMAIL_ALREADY_REGISTERED, result.errors.single().code)
    }

    @Test
    fun login_acceptsValidCredentialsAndRejectsInvalidPassword() = runTest {
        val repository = FakeUserRepository(
            initialUsers = listOf(
                User(
                    id = 1,
                    name = "Usuario activo",
                    email = "activo@correo.com",
                    passwordHash = PasswordHasher.sha256("claveSegura123"),
                    registeredOn = "2026-04-29",
                    status = UserStatus.ACTIVE,
                ),
            ),
        )
        val manager = UserManager(repository)

        val validLogin = manager.login(LoginRequest(email = "ACTIVO@correo.com", password = "claveSegura123"))
        val invalidLogin = manager.login(LoginRequest(email = "activo@correo.com", password = "otraClave123"))

        assertTrue(validLogin.isSuccess)
        assertEquals(1L, validLogin.value?.id)
        assertFalse(invalidLogin.isSuccess)
        assertEquals(ManagementErrorCode.INVALID_CREDENTIALS, invalidLogin.errors.single().code)
    }

    @Test
    fun getBasicProfile_returnsUserAndRatingCount() = runTest {
        val repository = FakeUserRepository(
            initialUsers = listOf(
                User(
                    id = 7,
                    name = "Usuario historial",
                    email = "historial@correo.com",
                    passwordHash = "hash",
                    registeredOn = "2026-04-29",
                    status = UserStatus.ACTIVE,
                ),
            ),
            ratingsByUser = mapOf(7L to listOf(FakeRatings.rating(userId = 7), FakeRatings.rating(userId = 7))),
        )
        val manager = UserManager(repository)

        val result = manager.getBasicProfile(userId = 7)

        assertTrue(result.isSuccess)
        assertEquals("Usuario historial", result.value?.user?.name)
        assertEquals(2, result.value?.totalRatings)
    }
}

private class FakeUserRepository(
    initialUsers: List<User> = emptyList(),
    private val ratingsByUser: Map<Long, List<Rating>> = emptyMap(),
) : UserRepository {
    val users = MutableStateFlow(initialUsers)

    override fun observeUsers(): Flow<List<User>> = users

    override fun observeUser(userId: Long): Flow<User?> {
        return flowOf(users.value.firstOrNull { it.id == userId })
    }

    override fun observeUserWithRatings(userId: Long): Flow<UserWithRatings?> {
        val user = users.value.firstOrNull { it.id == userId } ?: return flowOf(null)
        return flowOf(UserWithRatings(user = user, ratings = ratingsByUser[userId].orEmpty()))
    }

    override suspend fun getUserByEmail(email: String): User? {
        return users.value.firstOrNull { it.email.equals(email, ignoreCase = true) }
    }

    override suspend fun saveUser(user: User): Long {
        val savedId = user.id.takeIf { it > 0 } ?: ((users.value.maxOfOrNull { it.id } ?: 0) + 1)
        val saved = user.copy(id = savedId)
        users.value = users.value.filterNot { it.id == savedId } + saved
        return savedId
    }

    override suspend fun saveUsers(users: List<User>) {
        this.users.value = users
    }
}
