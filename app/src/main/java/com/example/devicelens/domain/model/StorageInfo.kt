package com.example.devicelens.domain.model

data class StorageOverview(
    val totalBytes: Long,
    val usedBytes: Long,
    val freeBytes: Long,
    val usagePercent: Int,
    val applicationsBytes: Long?,
    val userDataBytes: Long?,
    val cacheBytes: Long?,
    val otherBytes: Long?
)
