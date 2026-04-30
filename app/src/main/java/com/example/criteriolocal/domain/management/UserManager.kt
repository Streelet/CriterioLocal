package com.example.criteriolocal.domain.management

import com.example.criteriolocal.domain.model.User
import com.example.criteriolocal.domain.model.UserStatus
import com.example.criteriolocal.domain.model.UserWithRatings
import com.example.criteriolocal.domain.repository.UserRepository
import com.example.criteriolocal.domain.security.PasswordHasher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first

class UserManager(
    private val userRepository: UserRepository,
) {
    suspend fun registerUser(request: RegisterUserRequest): ManagementResult<User> {
        val normalizedEmail = request.email.trim().lowercase()
        val errors = validateRegistration(request, normalizedEmail).toMutableList()

        if (errors.isEmpty() && userRepository.getUserByEmail(normalizedEmail) != null) {
            errors += ManagementError(
                code = ManagementErrorCode.EMAIL_ALREADY_REGISTERED,
                field = "email",
                message = "El correo ya esta registrado.",
            )
        }

        if (errors.isNotEmpty()) {
            return ManagementResult.failure(errors)
        }

        val user = User(
            id = 0,
            name = request.name.trim(),
            email = normalizedEmail,
            passwordHash = PasswordHasher.sha256(request.password),
            registeredOn = request.registeredOn,
            status = UserStatus.ACTIVE,
        )
        val savedId = userRepository.saveUser(user)
        return ManagementResult.success(user.copy(id = savedId))
    }

    suspend fun login(request: LoginRequest): ManagementResult<User> {
        val normalizedEmail = request.email.trim().lowercase()
        val errors = validateLogin(request, normalizedEmail)
        if (errors.isNotEmpty()) {
            return ManagementResult.failure(errors)
        }

        val user = userRepository.getUserByEmail(normalizedEmail)
            ?: return ManagementResult.failure(
                ManagementError(
                    code = ManagementErrorCode.INVALID_CREDENTIALS,
                    field = "credentials",
                    message = "Correo o contrasena incorrectos.",
                ),
            )

        if (user.status != UserStatus.ACTIVE) {
            return ManagementResult.failure(
                ManagementError(
                    code = ManagementErrorCode.USER_INACTIVE,
                    field = "status",
                    message = "El usuario no esta activo.",
                ),
            )
        }

        val passwordMatches = user.passwordHash == PasswordHasher.sha256(request.password)
        if (!passwordMatches) {
            return ManagementResult.failure(
                ManagementError(
                    code = ManagementErrorCode.INVALID_CREDENTIALS,
                    field = "credentials",
                    message = "Correo o contrasena incorrectos.",
                ),
            )
        }

        return ManagementResult.success(user)
    }

    suspend fun getBasicProfile(userId: Long): ManagementResult<BasicUserProfile> {
        val userWithRatings = userRepository.observeUserWithRatings(userId).first()
            ?: return ManagementResult.failure(
                ManagementError(
                    code = ManagementErrorCode.USER_NOT_FOUND,
                    field = "userId",
                    message = "El usuario no existe.",
                ),
            )

        return ManagementResult.success(
            BasicUserProfile(
                user = userWithRatings.user,
                totalRatings = userWithRatings.ratings.size,
            ),
        )
    }

    fun observeUserHistory(userId: Long): Flow<UserWithRatings?> {
        return userRepository.observeUserWithRatings(userId)
    }

    private fun validateRegistration(
        request: RegisterUserRequest,
        normalizedEmail: String,
    ): List<ManagementError> {
        val errors = mutableListOf<ManagementError>()
        if (request.name.isBlank()) {
            errors += ManagementError(
                code = ManagementErrorCode.NAME_REQUIRED,
                field = "name",
                message = "El nombre es obligatorio.",
            )
        }
        errors += validateEmail(normalizedEmail)
        errors += validatePassword(request.password)
        return errors
    }

    private fun validateLogin(
        request: LoginRequest,
        normalizedEmail: String,
    ): List<ManagementError> {
        val errors = mutableListOf<ManagementError>()
        errors += validateEmail(normalizedEmail)
        if (request.password.isBlank()) {
            errors += ManagementError(
                code = ManagementErrorCode.PASSWORD_REQUIRED,
                field = "password",
                message = "La contrasena es obligatoria.",
            )
        }
        return errors
    }

    private fun validateEmail(email: String): List<ManagementError> {
        if (email.isBlank()) {
            return listOf(
                ManagementError(
                    code = ManagementErrorCode.EMAIL_REQUIRED,
                    field = "email",
                    message = "El correo es obligatorio.",
                ),
            )
        }
        val isValid = EMAIL_PATTERN.matches(email)
        return if (isValid) {
            emptyList()
        } else {
            listOf(
                ManagementError(
                    code = ManagementErrorCode.EMAIL_INVALID,
                    field = "email",
                    message = "El correo no tiene un formato valido.",
                ),
            )
        }
    }

    private fun validatePassword(password: String): List<ManagementError> {
        if (password.isBlank()) {
            return listOf(
                ManagementError(
                    code = ManagementErrorCode.PASSWORD_REQUIRED,
                    field = "password",
                    message = "La contrasena es obligatoria.",
                ),
            )
        }
        return if (password.length >= MIN_PASSWORD_LENGTH) {
            emptyList()
        } else {
            listOf(
                ManagementError(
                    code = ManagementErrorCode.PASSWORD_TOO_SHORT,
                    field = "password",
                    message = "La contrasena debe tener al menos $MIN_PASSWORD_LENGTH caracteres.",
                ),
            )
        }
    }

    private companion object {
        const val MIN_PASSWORD_LENGTH = 8
        val EMAIL_PATTERN = Regex("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,}$", RegexOption.IGNORE_CASE)
    }
}
