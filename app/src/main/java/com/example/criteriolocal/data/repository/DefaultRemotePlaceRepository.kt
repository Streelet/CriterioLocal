package com.example.criteriolocal.data.repository

import com.example.criteriolocal.data.remote.api.GooglePlacesApiService
import com.example.criteriolocal.data.remote.mapper.asDomain
import com.example.criteriolocal.domain.model.NearbyPlaceSearchRequest
import com.example.criteriolocal.domain.model.NearbyPlaceSearchResult
import com.example.criteriolocal.domain.repository.RemotePlaceRepository
import java.util.Locale

class DefaultRemotePlaceRepository(
    private val apiService: GooglePlacesApiService,
    private val apiKey: String,
) : RemotePlaceRepository {
    override suspend fun searchNearby(request: NearbyPlaceSearchRequest): NearbyPlaceSearchResult {
        require(apiKey.isNotBlank()) {
            "La variable de entorno GOOGLE_PLACES_API_KEY no esta configurada."
        }

        val response = apiService.searchNearby(
            location = request.toLocationParam(),
            radius = request.radiusMeters,
            type = request.type,
            apiKey = apiKey,
        )

        return response.asDomain()
    }
}

private fun NearbyPlaceSearchRequest.toLocationParam(): String {
    return String.format(
        Locale.US,
        "%.6f,%.6f",
        latitude,
        longitude,
    )
}
