package com.example.criteriolocal.ui.business

import com.example.criteriolocal.domain.contract.BusinessDetailDto
import com.example.criteriolocal.domain.contract.BusinessMetricsSummaryDto

data class BusinessDetailUiState(
    val businessId: Long,
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val detail: BusinessDetailDto? = null,
    val metrics: BusinessMetricsSummaryDto? = null,
)
