package com.example.criteriolocal.domain.model

data class BusinessWithCategory(
    val business: Business,
    val category: Category,
)

data class UserWithRatings(
    val user: User,
    val ratings: List<Rating>,
)

data class RatingDetails(
    val rating: Rating,
    val user: User,
    val business: Business,
    val qualities: List<Quality>,
    val evidences: List<Evidence>,
)
