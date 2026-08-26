package com.example.devicelens.domain.model

enum class StorageThreshold(
    val bytes: Long,
    val label: String
) {
    MB_500(500L * 1024 * 1024, "500 MB"),
    GB_1(1L * 1024 * 1024 * 1024, "1 GB"),
    GB_2(2L * 1024 * 1024 * 1024, "2 GB"),
    GB_5(5L * 1024 * 1024 * 1024, "5 GB");

    companion object {
        val DEFAULT = GB_1
    }
}

fun isHighStorage(totalBytes: Long, thresholdBytes: Long): Boolean {
    return totalBytes >= thresholdBytes
}

fun highStorageApps(
    apps: List<AppStorageInfo>,
    threshold: StorageThreshold
): List<AppStorageInfo> {
    return apps.filter { isHighStorage(it.totalBytes, threshold.bytes) }
        .sortedByDescending { it.totalBytes }
}
