package com.example.criteriolocal.data.remote

import com.example.criteriolocal.data.remote.dto.GeometryDto
import com.example.criteriolocal.data.remote.dto.LocationDto
import com.example.criteriolocal.data.remote.dto.NearbySearchResponseDto
import com.example.criteriolocal.data.remote.dto.OpeningHoursDto
import com.example.criteriolocal.data.remote.dto.PlaceResultDto
import com.example.criteriolocal.data.remote.mapper.asDomain
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class RemotePlaceMappersTest {
    @Test
    fun asDomain_mapsValidPlaces_andSkipsIncompleteOnes() {
        val response = NearbySearchResponseDto(
            status = "OK",
            nextPageToken = "next-page-token",
            results = listOf(
                PlaceResultDto(
                    businessStatus = "OPERATIONAL",
                    geometry = GeometryDto(
                        location = LocationDto(
                            lat = 14.7924897,
                            lng = -89.5450458,
                        ),
                    ),
                    icon = "https://maps.gstatic.com/icon.png",
                    internationalPhoneNumber = "+502 3220 3463",
                    name = "Farmavital L&N",
                    placeId = "ChIJ7fWfyIYxYo8R9Gy1r1pI_eI",
                    openingHours = OpeningHoursDto(openNow = true),
                    rating = 5.0,
                    types = listOf("pharmacy", "store"),
                    userRatingsTotal = 10,
                    vicinity = "4a Calle 1-70, Chiquimula",
                ),
                PlaceResultDto(
                    name = null,
                    placeId = "incomplete-place",
                    geometry = GeometryDto(
                        location = LocationDto(
                            lat = 14.8,
                            lng = -89.5,
                        ),
                    ),
                ),
            ),
        )

        val result = response.asDomain()

        assertEquals("OK", result.status)
        assertEquals("next-page-token", result.nextPageToken)
        assertEquals(1, result.places.size)
        assertEquals("Farmavital L&N", result.places.first().name)
        assertEquals("ChIJ7fWfyIYxYo8R9Gy1r1pI_eI", result.places.first().googlePlaceId)
        assertTrue("pharmacy" in result.places.first().types)
    }

    @Test
    fun asDomain_preservesOptionalNullFields() {
        val response = NearbySearchResponseDto(
            status = "OK",
            results = listOf(
                PlaceResultDto(
                    geometry = GeometryDto(
                        location = LocationDto(
                            lat = 14.7999228,
                            lng = -89.5443547,
                        ),
                    ),
                    name = "Farmacia Aquedah",
                    placeId = "ChIJkYUI3X0wYo8R9HRzjveLwFo",
                    types = listOf("pharmacy"),
                ),
            ),
        )

        val place = response.asDomain().places.first()

        assertEquals("Farmacia Aquedah", place.name)
        assertNull(place.phone)
        assertNull(place.rating)
        assertNull(place.isOpenNow)
    }
}
