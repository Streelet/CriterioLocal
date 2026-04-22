package com.example.criteriolocal.data.remote.mapper

import com.example.criteriolocal.data.remote.dto.NearbySearchResponseDto
import com.example.criteriolocal.domain.model.NearbyPlace
import com.example.criteriolocal.domain.model.NearbyPlaceSearchResult

internal fun NearbySearchResponseDto.asDomain(
    photoUrlBuilder: (String) -> String,
): NearbyPlaceSearchResult {
    return NearbyPlaceSearchResult(
        status = status,
        nextPageToken = nextPageToken,
        places = results.mapNotNull { result ->
            val placeId = result.placeId ?: return@mapNotNull null
            val name = result.name ?: return@mapNotNull null
            val location = result.geometry?.location ?: return@mapNotNull null
            val latitude = location.lat ?: return@mapNotNull null
            val longitude = location.lng ?: return@mapNotNull null

            NearbyPlace(
                googlePlaceId = placeId,
                name = name,
                businessStatus = result.businessStatus,
                latitude = latitude,
                longitude = longitude,
                address = result.vicinity,
                phone = result.internationalPhoneNumber,
                types = result.types,
                rating = result.rating,
                userRatingsTotal = result.userRatingsTotal,
                isOpenNow = result.openingHours?.openNow,
                iconUrl = result.icon,
                photoUrl = result.photos.firstOrNull()?.photoReference?.let(photoUrlBuilder),
            )
        },
    )
}
