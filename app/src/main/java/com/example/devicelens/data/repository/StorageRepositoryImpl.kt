package com.example.devicelens.data.repository

import com.example.devicelens.data.system.StorageStatsProvider
import com.example.devicelens.domain.model.AppStorageInfo
import com.example.devicelens.domain.model.StorageOverview
import com.example.devicelens.domain.repository.StorageRepository
import javax.inject.Inject

class StorageRepositoryImpl @Inject constructor(
    private val storageStatsProvider: StorageStatsProvider
) : StorageRepository {

    override suspend fun getStorageOverview(apps: List<AppStorageInfo>): StorageOverview {
        return storageStatsProvider.getStorageOverview(apps)
    }

    override suspend fun getAppStorage(): List<AppStorageInfo> {
        return storageStatsProvider.getAppStorage()
    }
}
