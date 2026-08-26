package com.example.devicelens.presentation.usage

import com.example.devicelens.domain.model.AppUsageInfo
import com.example.devicelens.domain.model.UsagePeriod
import com.example.devicelens.domain.model.UsageSortOption

data class UsageUiState(
    val hasUsageAccess: Boolean = false,
    val isLoading: Boolean = false,
    val isRefreshing: Boolean = false,
    val period: UsagePeriod = UsagePeriod.TODAY,
    val sortOption: UsageSortOption = UsageSortOption.MOST_USED,
    val searchQuery: String = "",
    val totalUsageMillis: Long = 0L,
    val apps: List<AppUsageInfo> = emptyList(),
    val visibleApps: List<AppUsageInfo> = emptyList(),
    val mostUsed: List<AppUsageInfo> = emptyList(),
    val highUsageApps: List<AppUsageInfo> = emptyList(),
    val recommendations: List<String> = emptyList(),
    val error: String? = null
)
