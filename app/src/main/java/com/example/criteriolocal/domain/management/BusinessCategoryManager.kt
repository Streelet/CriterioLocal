package com.example.criteriolocal.domain.management

import com.example.criteriolocal.domain.model.Business
import com.example.criteriolocal.domain.model.BusinessStatus
import com.example.criteriolocal.domain.model.BusinessWithCategory
import com.example.criteriolocal.domain.model.Category
import com.example.criteriolocal.domain.model.NearbyPlace
import com.example.criteriolocal.domain.repository.BusinessRepository
import com.example.criteriolocal.domain.repository.CategoryRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first

class BusinessCategoryManager(
    private val categoryRepository: CategoryRepository,
    private val businessRepository: BusinessRepository,
) {
    suspend fun linkGooglePlaceAutomatically(
        request: LinkGooglePlaceAutomaticallyRequest,
    ): ManagementResult<Business> {
        val errors = validateGooglePlaceBusiness(request.place).toMutableList()
        val categories = categoryRepository.observeCategories().first()
        errors += validateCategoryCatalog(categories)
        val resolvedCategory = GooglePlaceCategoryResolver.resolveCategory(
            googleTypes = request.place.types,
            availableCategories = categories,
        )

        if (resolvedCategory == null) {
            errors += ManagementError(
                code = ManagementErrorCode.CATEGORY_NOT_FOUND,
                field = "types",
                message = "No existe una categoria local para los tipos de Google recibidos.",
            )
        }

        if (errors.isNotEmpty()) {
            return ManagementResult.failure(errors)
        }

        return saveGooglePlaceWithCategory(
            place = request.place,
            categoryId = checkNotNull(resolvedCategory).id,
        )
    }

    suspend fun linkGooglePlacesAutomatically(
        places: List<NearbyPlace>,
    ): List<ManagementResult<Business>> {
        return places.map { place ->
            linkGooglePlaceAutomatically(LinkGooglePlaceAutomaticallyRequest(place = place))
        }
    }

    private suspend fun saveGooglePlaceWithCategory(
        place: NearbyPlace,
        categoryId: Long,
    ): ManagementResult<Business> {
        val existingBusiness = businessRepository.getBusinessByGooglePlaceId(place.googlePlaceId.trim())
        val business = Business(
            id = existingBusiness?.id ?: 0,
            googlePlaceId = place.googlePlaceId.trim(),
            name = place.name.trim(),
            description = place.asBusinessDescription(),
            address = place.address?.trim()?.takeIf { it.isNotBlank() } ?: ADDRESS_NOT_AVAILABLE,
            phone = place.phone?.trim()?.takeIf { it.isNotBlank() },
            latitude = place.latitude,
            longitude = place.longitude,
            categoryId = categoryId,
            status = place.asBusinessStatus(),
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

    private fun validateGooglePlaceBusiness(
        place: NearbyPlace,
    ): List<ManagementError> {
        val errors = mutableListOf<ManagementError>()
        if (place.googlePlaceId.isBlank()) {
            errors += ManagementError(
                code = ManagementErrorCode.GOOGLE_PLACE_ID_REQUIRED,
                field = "googlePlaceId",
                message = "El negocio de Google debe incluir Google Place ID.",
            )
        }
        if (place.name.isBlank()) {
            errors += ManagementError(
                code = ManagementErrorCode.GOOGLE_PLACE_NAME_REQUIRED,
                field = "name",
                message = "El negocio de Google debe incluir nombre.",
            )
        }
        return errors
    }

    private fun validateCategoryCatalog(categories: List<Category>): List<ManagementError> {
        val errors = mutableListOf<ManagementError>()
        if (categories.any { it.name.isBlank() }) {
            errors += ManagementError(
                code = ManagementErrorCode.CATEGORY_NAME_REQUIRED,
                field = "categories",
                message = "El catalogo oficial contiene una categoria sin nombre.",
            )
        }

        val duplicatedNames = categories
            .groupingBy { it.name.trim().lowercase() }
            .eachCount()
            .filter { (name, count) -> name.isNotBlank() && count > 1 }
            .keys

        if (duplicatedNames.isNotEmpty()) {
            errors += ManagementError(
                code = ManagementErrorCode.CATEGORY_ALREADY_EXISTS,
                field = "categories",
                message = "El catalogo oficial contiene categorias duplicadas: ${duplicatedNames.joinToString()}.",
            )
        }
        return errors
    }

    private fun NearbyPlace.asBusinessDescription(): String {
        val normalizedTypes = types
            .filter { it.isNotBlank() }
            .joinToString(separator = ", ")
            .ifBlank { "sin tipos publicados" }
        return "Negocio importado desde Google Places. Tipos: $normalizedTypes."
    }

    private fun NearbyPlace.asBusinessStatus(): BusinessStatus {
        return if (businessStatus == null || businessStatus == GOOGLE_OPERATIONAL_STATUS) {
            BusinessStatus.ACTIVE
        } else {
            BusinessStatus.INACTIVE
        }
    }

    private companion object {
        const val ADDRESS_NOT_AVAILABLE = "Direccion no disponible en Google Places"
        const val GOOGLE_OPERATIONAL_STATUS = "OPERATIONAL"
    }
}
