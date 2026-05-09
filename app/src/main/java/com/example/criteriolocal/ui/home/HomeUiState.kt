package com.example.criteriolocal.ui.home

import com.example.criteriolocal.domain.contract.BusinessListItemDto

data class CategoryFilterUi(
    val id: Long?,
    val label: String,
)

data class HomeUiState(
    val isLoading: Boolean = false,
    val query: String = "",
    val selectedCategoryId: Long? = null,
    val categories: List<CategoryFilterUi> = emptyList(),
    val businesses: List<BusinessListItemDto> = emptyList(),
    val errorMessage: String? = null,
) {
    val visibleBusinesses: List<BusinessListItemDto>
        get() = businesses.filter { item ->
            val matchesCategory = selectedCategoryId == null || item.business.categoryId == selectedCategoryId
            val matchesQuery = query.isBlank() ||
                item.business.name.contains(query, ignoreCase = true) ||
                item.business.categoryName.contains(query, ignoreCase = true)
            matchesCategory && matchesQuery
        }
}
