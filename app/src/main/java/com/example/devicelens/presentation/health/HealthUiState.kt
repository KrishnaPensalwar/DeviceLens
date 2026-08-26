package com.example.devicelens.presentation.health

import com.example.devicelens.domain.model.HealthRecommendation
import com.example.devicelens.domain.model.HealthReport

data class HealthUiState(
    val isLoading: Boolean = false,
    val report: HealthReport? = null,
    val recommendations: List<HealthRecommendation> = emptyList(),
    val error: String? = null
)
