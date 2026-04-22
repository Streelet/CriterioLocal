package com.example.criteriolocal.domain.model

data class NearbyPlaceSearchRequest(
    val latitude: Double,
    val longitude: Double,
    val radiusMeters: Int,
    val type: String,
)

data class NearbyPlace(
    val googlePlaceId: String,
    val name: String,
    val businessStatus: String?,
    val latitude: Double,
    val longitude: Double,
    val address: String?,
    val phone: String?,
    val types: List<String>,
    val rating: Double?,
    val userRatingsTotal: Int?,
    val isOpenNow: Boolean?,
    val iconUrl: String?,
    val photoUrl: String?,
)

data class NearbyPlaceSearchResult(
    val status: String,
    val nextPageToken: String?,
    val places: List<NearbyPlace>,
)
