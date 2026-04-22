package com.example.criteriolocal.domain.model

enum class UserStatus {
    ACTIVE,
    INACTIVE,
    SUSPENDED,
}

enum class BusinessStatus {
    ACTIVE,
    INACTIVE,
}

enum class CatalogStatus {
    ACTIVE,
    INACTIVE,
}

enum class WaitTimeOption {
    UP_TO_15_MINUTES,
    FROM_15_TO_30_MINUTES,
    FROM_31_TO_60_MINUTES,
    MORE_THAN_60_MINUTES,
}

enum class UsageFrequencyOption {
    FIRST_TIME,
    OCCASIONAL,
    FREQUENT,
    REGULAR,
}

enum class AvailabilityOption {
    NOT_AVAILABLE,
    LIMITED,
    AVAILABLE,
}

enum class ServiceModeOption {
    IN_PERSON,
    HOME_VISIT,
    DELIVERY,
    ONLINE,
}

enum class EvidenceType {
    IMAGE,
    PDF,
}

data class CatalogOption<T>(
    val value: T,
    val label: String,
)
