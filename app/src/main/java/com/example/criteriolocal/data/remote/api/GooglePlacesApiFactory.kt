package com.example.criteriolocal.data.remote.api

import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import retrofit2.Retrofit
import retrofit2.create
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory

object GooglePlacesApiFactory {
    private val json = Json {
        ignoreUnknownKeys = true
        isLenient = true
    }

    fun create(
        baseUrl: String,
    ): GooglePlacesApiService {
        return Retrofit.Builder()
            .baseUrl(baseUrl)
            .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
            .build()
            .create()
    }
}
