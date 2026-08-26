package com.example.devicelens.domain.model

enum class StorageSortOption {
    LARGEST_FIRST,
    SMALLEST_FIRST,
    NAME_AZ,
    NAME_ZA
}

fun sortAppStorage(
    apps: List<AppStorageInfo>,
    option: StorageSortOption
): List<AppStorageInfo> {
    return when (option) {
        StorageSortOption.LARGEST_FIRST ->
            apps.sortedByDescending { it.totalBytes }

        StorageSortOption.SMALLEST_FIRST ->
            apps.sortedBy { it.totalBytes }

        StorageSortOption.NAME_AZ ->
            apps.sortedBy { it.appName.lowercase() }

        StorageSortOption.NAME_ZA ->
            apps.sortedByDescending { it.appName.lowercase() }
    }
}
