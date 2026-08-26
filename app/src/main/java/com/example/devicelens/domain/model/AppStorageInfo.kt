package com.example.devicelens.domain.model

data class AppStorageInfo(
    val packageName: String,
    val appName: String,
    val totalBytes: Long,
    val apkBytes: Long?,
    val dataBytes: Long?,
    val cacheBytes: Long?,
    val percentOfDevice: Double?
)
