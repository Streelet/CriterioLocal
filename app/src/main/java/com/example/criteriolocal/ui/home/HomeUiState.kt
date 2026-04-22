package com.example.criteriolocal.ui.home

import com.example.criteriolocal.domain.catalog.AppCatalogs
import com.example.criteriolocal.domain.model.AvailabilityOption
import com.example.criteriolocal.domain.model.BusinessWithCategory
import com.example.criteriolocal.domain.model.CatalogOption
import com.example.criteriolocal.domain.model.Category
import com.example.criteriolocal.domain.model.NearbyPlace
import com.example.criteriolocal.domain.model.Quality
import com.example.criteriolocal.domain.model.ServiceModeOption
import com.example.criteriolocal.domain.model.UsageFrequencyOption
import com.example.criteriolocal.domain.model.User
import com.example.criteriolocal.domain.model.WaitTimeOption

data class HomeUiState(
    val isLoading: Boolean = true,
    val errorMessage: String? = null,
    val ethicalNotice: String = AppCatalogs.ethicalNotice,
    val categories: List<Category> = emptyList(),
    val businesses: List<BusinessWithCategory> = emptyList(),
    val remotePlaces: List<NearbyPlace> = emptyList(),
    val remotePlacesStatus: String? = null,
    val remotePlacesError: String? = null,
    val qualities: List<Quality> = emptyList(),
    val users: List<User> = emptyList(),
    val ratingScale: List<Int> = AppCatalogs.ratingScale,
    val waitTimeOptions: List<CatalogOption<WaitTimeOption>> = AppCatalogs.waitTimeOptions,
    val availabilityOptions: List<CatalogOption<AvailabilityOption>> = AppCatalogs.availabilityOptions,
    val usageFrequencyOptions: List<CatalogOption<UsageFrequencyOption>> = AppCatalogs.usageFrequencyOptions,
    val serviceModeOptions: List<CatalogOption<ServiceModeOption>> = AppCatalogs.serviceModeOptions,
)
