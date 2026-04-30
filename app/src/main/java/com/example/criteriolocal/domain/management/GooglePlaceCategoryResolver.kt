package com.example.criteriolocal.domain.management

import com.example.criteriolocal.domain.model.Category

object GooglePlaceCategoryResolver {
    private val rules = listOf(
        CategoryRule(
            categoryName = "Laboratorio clinico",
            googleTypes = setOf("medical_lab"),
        ),
        CategoryRule(
            categoryName = "Clinica medica",
            googleTypes = setOf("hospital", "doctor", "dentist", "physiotherapist"),
        ),
        CategoryRule(
            categoryName = "Farmacia",
            googleTypes = setOf("pharmacy", "drugstore"),
        ),
        CategoryRule(
            categoryName = "Clinica medica",
            googleTypes = setOf("health"),
        ),
        CategoryRule(
            categoryName = "Restaurante",
            googleTypes = setOf("restaurant", "cafe", "food", "meal_delivery", "meal_takeaway", "bakery"),
        ),
        CategoryRule(
            categoryName = "Taller mecanico",
            googleTypes = setOf("car_repair", "car_wash"),
        ),
        CategoryRule(
            categoryName = "Tienda local",
            googleTypes = setOf("store", "convenience_store", "supermarket", "grocery_or_supermarket"),
        ),
    )

    fun resolveCategory(
        googleTypes: List<String>,
        availableCategories: List<Category>,
    ): Category? {
        val categoryByName = availableCategories.associateBy { it.name.normalized() }
        val normalizedTypes = googleTypes.map { it.normalized() }.toSet()

        val matchedRule = rules.firstOrNull { rule ->
            rule.googleTypes.any { it in normalizedTypes }
        }

        return matchedRule?.let { categoryByName[it.categoryName.normalized()] }
            ?: categoryByName[DEFAULT_FALLBACK_CATEGORY.normalized()]
    }

    private fun String.normalized(): String = trim().lowercase()

    private data class CategoryRule(
        val categoryName: String,
        val googleTypes: Set<String>,
    )

    private const val DEFAULT_FALLBACK_CATEGORY = "Tienda local"
}
