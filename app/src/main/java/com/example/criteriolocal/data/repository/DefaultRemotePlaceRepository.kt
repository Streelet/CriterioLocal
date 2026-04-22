package com.example.criteriolocal.data.repository

import com.example.criteriolocal.data.remote.api.GooglePlacesApiService
import com.example.criteriolocal.data.remote.mapper.asDomain
import com.example.criteriolocal.domain.model.NearbyPlaceSearchRequest
import com.example.criteriolocal.domain.model.NearbyPlaceSearchResult
import com.example.criteriolocal.domain.repository.RemotePlaceRepository
import java.net.URLEncoder
import java.util.Locale

class DefaultRemotePlaceRepository(
    private val apiService: GooglePlacesApiService,
    private val apiKey: String,
    private val baseUrl: String = "https://maps.googleapis.com/",
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

        return response.asDomain(::buildPhotoUrl)
    }

    private fun buildPhotoUrl(photoReference: String): String {
        val encodedReference = URLEncoder.encode(photoReference, Charsets.UTF_8.name())
        val encodedApiKey = URLEncoder.encode(apiKey, Charsets.UTF_8.name())
        return "${baseUrl}maps/api/place/photo?maxwidth=600&photo_reference=$encodedReference&key=$encodedApiKey"
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
