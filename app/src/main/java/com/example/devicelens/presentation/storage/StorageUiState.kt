package com.example.devicelens.presentation.storage

import com.example.devicelens.domain.model.AppStorageInfo
import com.example.devicelens.domain.model.StorageOverview
import com.example.devicelens.domain.model.StorageSortOption
import com.example.devicelens.domain.model.StorageThreshold

data class StorageUiState(
    val isLoading: Boolean = false,
    val isRefreshing: Boolean = false,
    val storageOverview: StorageOverview? = null,
    val applications: List<AppStorageInfo> = emptyList(),
    val highStorageApps: List<AppStorageInfo> = emptyList(),
    val selectedSortOption: StorageSortOption = StorageSortOption.LARGEST_FIRST,
    val threshold: StorageThreshold = StorageThreshold.DEFAULT,
    val error: String? = null
)
