package com.example.criteriolocal.domain.management

data class ManagementResult<T>(
    val value: T? = null,
    val errors: List<ManagementError> = emptyList(),
) {
    val isSuccess: Boolean
        get() = errors.isEmpty()

    companion object {
        fun <T> success(value: T): ManagementResult<T> = ManagementResult(value = value)

        fun <T> failure(errors: List<ManagementError>): ManagementResult<T> {
            return ManagementResult(errors = errors)
        }

        fun <T> failure(vararg errors: ManagementError): ManagementResult<T> {
            return ManagementResult(errors = errors.toList())
        }
    }
}

data class ManagementError(
    val code: ManagementErrorCode,
    val field: String,
    val message: String,
)

enum class ManagementErrorCode {
    NAME_REQUIRED,
    EMAIL_REQUIRED,
    EMAIL_INVALID,
    PASSWORD_REQUIRED,
    PASSWORD_TOO_SHORT,
    EMAIL_ALREADY_REGISTERED,
    INVALID_CREDENTIALS,
    USER_INACTIVE,
    USER_NOT_FOUND,
    CATEGORY_NAME_REQUIRED,
    CATEGORY_ALREADY_EXISTS,
    CATEGORY_NOT_FOUND,
    BUSINESS_NAME_REQUIRED,
    BUSINESS_ADDRESS_REQUIRED,
    BUSINESS_CATEGORY_REQUIRED,
    BUSINESS_NOT_FOUND,
}
