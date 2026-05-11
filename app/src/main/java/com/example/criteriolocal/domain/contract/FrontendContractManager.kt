package com.example.criteriolocal.domain.contract

import com.example.criteriolocal.domain.management.BusinessCategoryManager
import com.example.criteriolocal.domain.management.RatingRegistrationManager
import com.example.criteriolocal.domain.metrics.BusinessMetricsManager
import com.example.criteriolocal.domain.model.NearbyPlaceSearchRequest
import com.example.criteriolocal.domain.query.BusinessQueryManager
import com.example.criteriolocal.domain.repository.BootstrapRepository
import com.example.criteriolocal.domain.repository.QualityRepository
import com.example.criteriolocal.domain.repository.RemotePlaceRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class FrontendContractManager(
    private val businessQueryManager: BusinessQueryManager,
    private val businessMetricsManager: BusinessMetricsManager,
    private val ratingRegistrationManager: RatingRegistrationManager,
    private val qualityRepository: QualityRepository,
    private val bootstrapRepository: BootstrapRepository,
    private val remotePlaceRepository: RemotePlaceRepository,
    private val businessCategoryManager: BusinessCategoryManager,
) {
    fun observeRatingFormCatalogs(): Flow<RatingFormCatalogsDto> {
        return qualityRepository.observeQualities().map { qualities ->
            qualities.toRatingFormCatalogsDto()
        }
    }

    fun searchBusinesses(
        filters: BusinessSearchFiltersDto = BusinessSearchFiltersDto(),
    ): Flow<ContractResultDto<List<BusinessListItemDto>>> {
        return businessQueryManager.searchBusinesses(filters.toDomain()).map { items ->
            ContractResultDto.success(items.map { it.toDto() })
        }
    }

    fun observeBusinessDetail(businessId: Long): Flow<ContractResultDto<BusinessDetailDto>> {
        return businessQueryManager.observeBusinessDetail(businessId).map { detail ->
            if (detail == null) {
                ContractResultDto.failure(
                    notFoundError(
                        field = "businessId",
                        message = "El negocio solicitado no existe.",
                    ),
                )
            } else {
                ContractResultDto.success(detail.toDto())
            }
        }
    }

    fun observeUserRatingHistory(userId: Long): Flow<ContractResultDto<UserRatingHistoryDto>> {
        return businessQueryManager.observeUserRatingHistory(userId).map { history ->
            ContractResultDto.success(history.toDto())
        }
    }

    fun observeBusinessSummary(businessId: Long): Flow<ContractResultDto<BusinessMetricsSummaryDto>> {
        return businessMetricsManager.observeBusinessSummary(businessId).map { summary ->
            if (summary == null) {
                ContractResultDto.failure(
                    notFoundError(
                        field = "businessId",
                        message = "El negocio solicitado no existe.",
                    ),
                )
            } else {
                ContractResultDto.success(summary.toDto())
            }
        }
    }

    fun observeCategoryRanking(categoryId: Long): Flow<ContractResultDto<CategoryRankingDto>> {
        return businessMetricsManager.observeCategoryRanking(categoryId).map { ranking ->
            if (ranking == null) {
                ContractResultDto.failure(
                    notFoundError(
                        field = "categoryId",
                        message = "La categoria solicitada no tiene negocios para ranking.",
                    ),
                )
            } else {
                ContractResultDto.success(ranking.toDto())
            }
        }
    }

    suspend fun refreshNearbyPlaces(
        request: NearbyPlaceSearchRequest,
    ): ContractResultDto<Int> {
        return runCatching {
            bootstrapRepository.seedCatalogsOnly()
            val result = remotePlaceRepository.searchNearby(request)
            val linkResults = businessCategoryManager.linkGooglePlacesAutomatically(result.places)
            linkResults.count { it.isSuccess }
        }.fold(
            onSuccess = { count -> ContractResultDto.success(count) },
            onFailure = { throwable ->
                ContractResultDto.failure(
                    ContractErrorDto(
                        code = "REMOTE_SYNC_FAILED",
                        field = "remote",
                        message = throwable.message ?: "Fallo al sincronizar con Google Places.",
                    ),
                )
            },
        )
    }

    suspend fun registerStructuredRating(
        request: RatingRegistrationRequestDto,
    ): ContractResultDto<RatingRegistrationResponseDto> {
        val mappedRequest = request.toDomainRequest()
        if (!mappedRequest.isSuccess || mappedRequest.value == null) {
            return ContractResultDto.failure(mappedRequest.errors)
        }

        return ratingRegistrationManager
            .registerStructuredRating(mappedRequest.value)
            .toDto()
    }
}
