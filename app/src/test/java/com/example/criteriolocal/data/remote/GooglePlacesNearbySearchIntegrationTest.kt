package com.example.criteriolocal.data.remote

import com.example.criteriolocal.BuildConfig
import com.example.criteriolocal.data.remote.api.GooglePlacesApiFactory
import com.example.criteriolocal.data.repository.DefaultRemotePlaceRepository
import com.example.criteriolocal.domain.model.NearbyPlaceSearchRequest
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Assume.assumeTrue
import org.junit.Test

class GooglePlacesNearbySearchIntegrationTest {
    private val repository by lazy {
        DefaultRemotePlaceRepository(
            apiService = GooglePlacesApiFactory.create(baseUrl = BuildConfig.GOOGLE_PLACES_BASE_URL),
            apiKey = BuildConfig.GOOGLE_PLACES_API_KEY,
        )
    }

    @Test
    fun searchNearby_returnsStructuredPlacesFromGoogle() = runBlocking {
        assumeTrue("GOOGLE_PLACES_API_KEY no esta configurada.", BuildConfig.GOOGLE_PLACES_API_KEY.isNotBlank())

        val request = NearbyPlaceSearchRequest(
            latitude = 14.7906,
            longitude = -89.5447,
            radiusMeters = 2000,
            type = "pharmacy",
        )

        val response = repository.searchNearby(request)

        assertEquals("OK", response.status)
        assertTrue("La API debe devolver negocios cercanos.", response.places.isNotEmpty())
        assertTrue(
            "Cada negocio debe tener Google Place ID y nombre.",
            response.places.all { it.googlePlaceId.isNotBlank() && it.name.isNotBlank() },
        )
        assertTrue(
            "La lista debe incluir al menos un lugar de tipo pharmacy.",
            response.places.any { "pharmacy" in it.types },
        )
        assertTrue(
            "Las coordenadas deben caer en el area esperada de Chiquimula.",
            response.places.all { place ->
                place.latitude in 14.75..14.85 && place.longitude in -89.60..-89.50
            },
        )
    }
}
