package com.example.criteriolocal.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class NearbySearchResponseDto(
    @SerialName("html_attributions")
    val htmlAttributions: List<String> = emptyList(),
    @SerialName("next_page_token")
    val nextPageToken: String? = null,
    val results: List<PlaceResultDto> = emptyList(),
    val status: String,
)

@Serializable
data class PlaceResultDto(
    @SerialName("business_status")
    val businessStatus: String? = null,
    val geometry: GeometryDto? = null,
    val icon: String? = null,
    @SerialName("international_phone_number")
    val internationalPhoneNumber: String? = null,
    val name: String? = null,
    @SerialName("place_id")
    val placeId: String? = null,
    @SerialName("opening_hours")
    val openingHours: OpeningHoursDto? = null,
    val photos: List<PhotoDto> = emptyList(),
    val rating: Double? = null,
    val types: List<String> = emptyList(),
    @SerialName("user_ratings_total")
    val userRatingsTotal: Int? = null,
    val vicinity: String? = null,
)

@Serializable
data class GeometryDto(
    val location: LocationDto? = null,
)

@Serializable
data class LocationDto(
    val lat: Double? = null,
    val lng: Double? = null,
)

@Serializable
data class OpeningHoursDto(
    @SerialName("open_now")
    val openNow: Boolean? = null,
)

@Serializable
data class PhotoDto(
    val height: Int? = null,
    @SerialName("html_attributions")
    val htmlAttributions: List<String> = emptyList(),
    @SerialName("photo_reference")
    val photoReference: String? = null,
    val width: Int? = null,
)
