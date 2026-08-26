package com.example.devicelens.domain.usecase.storage

import com.example.devicelens.domain.model.AppStorageInfo
import com.example.devicelens.domain.model.StorageOverview
import com.example.devicelens.domain.repository.StorageRepository
import javax.inject.Inject

class GetStorageInfoUseCase @Inject constructor(
    private val storageRepository: StorageRepository
) {
    suspend fun invoke(apps: List<AppStorageInfo>): StorageOverview {
        return storageRepository.getStorageOverview(apps)
    }
}
