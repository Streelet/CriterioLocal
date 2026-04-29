package com.example.criteriolocal.domain.management

import com.example.criteriolocal.domain.model.Business
import com.example.criteriolocal.domain.model.BusinessStatus
import com.example.criteriolocal.domain.model.BusinessWithCategory
import com.example.criteriolocal.domain.model.Category
import com.example.criteriolocal.domain.repository.BusinessRepository
import com.example.criteriolocal.domain.repository.CategoryRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first

class BusinessCategoryManager(
    private val categoryRepository: CategoryRepository,
    private val businessRepository: BusinessRepository,
) {
    suspend fun registerCategory(request: CreateCategoryRequest): ManagementResult<Category> {
        val categoryName = request.name.trim()
        if (categoryName.isBlank()) {
            return ManagementResult.failure(
                ManagementError(
                    code = ManagementErrorCode.CATEGORY_NAME_REQUIRED,
                    field = "name",
                    message = "El nombre de la categoria es obligatorio.",
                ),
            )
        }

        val alreadyExists = categoryRepository.observeCategories()
            .first()
            .any { it.name.equals(categoryName, ignoreCase = true) }
        if (alreadyExists) {
            return ManagementResult.failure(
                ManagementError(
                    code = ManagementErrorCode.CATEGORY_ALREADY_EXISTS,
                    field = "name",
                    message = "La categoria ya existe.",
                ),
            )
        }

        val category = Category(
            id = 0,
            name = categoryName,
            description = request.description.trim(),
        )
        val savedId = categoryRepository.saveCategory(category)
        return ManagementResult.success(category.copy(id = savedId))
    }

    suspend fun registerBusiness(request: CreateBusinessRequest): ManagementResult<Business> {
        val errors = validateBusiness(request).toMutableList()
        val category = if (request.categoryId > 0) {
            categoryRepository.getCategory(request.categoryId)
        } else {
            null
        }

        if (request.categoryId > 0 && category == null) {
            errors += ManagementError(
                code = ManagementErrorCode.CATEGORY_NOT_FOUND,
                field = "categoryId",
                message = "La categoria indicada no existe.",
            )
        }

        if (errors.isNotEmpty()) {
            return ManagementResult.failure(errors)
        }

        val business = Business(
            id = 0,
            googlePlaceId = request.googlePlaceId?.trim()?.takeIf { it.isNotBlank() },
            name = request.name.trim(),
            description = request.description.trim(),
            address = request.address.trim(),
            phone = request.phone?.trim()?.takeIf { it.isNotBlank() },
            latitude = request.latitude,
            longitude = request.longitude,
            categoryId = request.categoryId,
            status = BusinessStatus.ACTIVE,
        )
        val savedId = businessRepository.saveBusiness(business)
        return ManagementResult.success(business.copy(id = savedId))
    }

    fun observeCategories(): Flow<List<Category>> {
        return categoryRepository.observeCategories()
    }

    fun observeBusinesses(): Flow<List<BusinessWithCategory>> {
        return businessRepository.observeBusinesses()
    }

    fun observeBusinessesByCategory(categoryId: Long): Flow<List<BusinessWithCategory>> {
        return businessRepository.observeBusinessesByCategory(categoryId)
    }

    fun searchBusinesses(query: String): Flow<List<BusinessWithCategory>> {
        return if (query.isBlank()) {
            businessRepository.observeBusinesses()
        } else {
            businessRepository.searchBusinesses(query.trim())
        }
    }

    fun observeBusinessDetail(businessId: Long): Flow<BusinessWithCategory?> {
        return businessRepository.observeBusiness(businessId)
    }

    private fun validateBusiness(request: CreateBusinessRequest): List<ManagementError> {
        val errors = mutableListOf<ManagementError>()
        if (request.name.isBlank()) {
            errors += ManagementError(
                code = ManagementErrorCode.BUSINESS_NAME_REQUIRED,
                field = "name",
                message = "El nombre del negocio es obligatorio.",
            )
        }
        if (request.address.isBlank()) {
            errors += ManagementError(
                code = ManagementErrorCode.BUSINESS_ADDRESS_REQUIRED,
                field = "address",
                message = "La direccion del negocio es obligatoria.",
            )
        }
        if (request.categoryId <= 0) {
            errors += ManagementError(
                code = ManagementErrorCode.BUSINESS_CATEGORY_REQUIRED,
                field = "categoryId",
                message = "El negocio debe asociarse a una categoria.",
            )
        }
        return errors
    }
}
