package com.example.criteriolocal.domain.management

data class CreateCategoryRequest(
    val name: String,
    val description: String,
)

data class CreateBusinessRequest(
    val name: String,
    val description: String,
    val address: String,
    val phone: String? = null,
    val categoryId: Long,
    val googlePlaceId: String? = null,
    val latitude: Double? = null,
    val longitude: Double? = null,
)
