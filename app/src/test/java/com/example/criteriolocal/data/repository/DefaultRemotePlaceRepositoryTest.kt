package com.example.criteriolocal.data.repository

import com.example.criteriolocal.data.remote.api.GooglePlacesApiService
import com.example.criteriolocal.data.remote.dto.GeometryDto
import com.example.criteriolocal.data.remote.dto.LocationDto
import com.example.criteriolocal.data.remote.dto.NearbySearchResponseDto
import com.example.criteriolocal.data.remote.dto.PlaceResultDto
import com.example.criteriolocal.domain.model.NearbyPlaceSearchRequest
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class DefaultRemotePlaceRepositoryTest {
    @Test
    fun searchNearby_formatsLocation_andMapsResponse() = runTest {
        val fakeService = FakeGooglePlacesApiService(
            response = NearbySearchResponseDto(
                status = "OK",
                results = listOf(
                    PlaceResultDto(
                        name = "Farmacia Doctor Farma",
                        placeId = "ChIJ0XRw33kxYo8RIPRdpHlvbFQ",
                        geometry = GeometryDto(
                            location = LocationDto(
                                lat = 14.7967479,
                                lng = -89.5459980,
                            ),
                        ),
                        types = listOf("pharmacy", "store"),
                    ),
                ),
            ),
        )
        val repository = DefaultRemotePlaceRepository(
            apiService = fakeService,
            apiKey = "fake-key",
        )

        val result = repository.searchNearby(
            NearbyPlaceSearchRequest(
                latitude = 14.7906,
                longitude = -89.5447,
                radiusMeters = 2000,
                type = "pharmacy",
            ),
        )

        assertEquals("14.790600,-89.544700", fakeService.location)
        assertEquals(2000, fakeService.radius)
        assertEquals("pharmacy", fakeService.type)
        assertEquals("fake-key", fakeService.apiKey)
        assertEquals("OK", result.status)
        assertEquals("Farmacia Doctor Farma", result.places.first().name)
    }

    @Test(expected = IllegalArgumentException::class)
    fun searchNearby_requiresConfiguredApiKey() = runTest {
        val repository = DefaultRemotePlaceRepository(
            apiService = FakeGooglePlacesApiService(
                response = NearbySearchResponseDto(status = "OK"),
            ),
            apiKey = "",
        )

        repository.searchNearby(
            NearbyPlaceSearchRequest(
                latitude = 14.7906,
                longitude = -89.5447,
                radiusMeters = 2000,
                type = "pharmacy",
            ),
        )
    }
}

private class FakeGooglePlacesApiService(
    private val response: NearbySearchResponseDto,
) : GooglePlacesApiService {
    var location: String? = null
    var radius: Int? = null
    var type: String? = null
    var apiKey: String? = null

    override suspend fun searchNearby(
        location: String,
        radius: Int,
        type: String,
        apiKey: String,
    ): NearbySearchResponseDto {
        this.location = location
        this.radius = radius
        this.type = type
        this.apiKey = apiKey
        return response
    }
}
