package com.example.criteriolocal.data.remote.api

import com.example.criteriolocal.data.remote.dto.NearbySearchResponseDto
import retrofit2.http.GET
import retrofit2.http.Query

interface GooglePlacesApiService {
    @GET("maps/api/place/nearbysearch/json")
    suspend fun searchNearby(
        @Query("location") location: String,
        @Query("radius") radius: Int,
        @Query("type") type: String,
        @Query("key") apiKey: String,
    ): NearbySearchResponseDto
}
