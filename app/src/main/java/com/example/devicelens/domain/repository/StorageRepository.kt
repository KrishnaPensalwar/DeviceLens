package com.example.devicelens.domain.repository

import com.example.devicelens.domain.model.AppStorageInfo
import com.example.devicelens.domain.model.StorageOverview

interface StorageRepository {
    suspend fun getAppStorage(): List<AppStorageInfo>
    suspend fun getStorageOverview(apps: List<AppStorageInfo>): StorageOverview
}
