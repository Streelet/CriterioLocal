package com.example.criteriolocal.ui.home

import com.example.criteriolocal.domain.contract.BusinessListItemDto

data class CategoryFilterUi(
    val id: Long?,
    val label: String,
)

data class HomeUiState(
    val isLoading: Boolean = true,
    val query: String = "",
    val selectedCategoryId: Long? = null,
    val categories: List<CategoryFilterUi> = emptyList(),
    val businesses: List<BusinessListItemDto> = emptyList(),
    val errorMessage: String? = null,
)
