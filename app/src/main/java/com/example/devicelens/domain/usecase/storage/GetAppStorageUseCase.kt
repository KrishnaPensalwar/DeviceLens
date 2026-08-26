package com.example.devicelens.domain.usecase.storage

import com.example.devicelens.domain.model.AppStorageInfo
import com.example.devicelens.domain.repository.StorageRepository
import javax.inject.Inject

class GetAppStorageUseCase @Inject constructor(
    private val storageRepository: StorageRepository
) {
    suspend fun invoke(): List<AppStorageInfo> {
        return storageRepository.getAppStorage()
    }
}
