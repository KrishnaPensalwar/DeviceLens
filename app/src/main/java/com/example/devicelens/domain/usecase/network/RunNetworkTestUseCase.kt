package com.example.devicelens.domain.usecase.network

import com.example.devicelens.domain.model.NetworkInfo
import com.example.devicelens.domain.repository.NetworkRepository
import javax.inject.Inject

class RunNetworkTestUseCase @Inject constructor(
    private val networkRepository: NetworkRepository
) {
    suspend fun invoke(): NetworkInfo {
        return networkRepository.runNetworkTest()
    }
}
