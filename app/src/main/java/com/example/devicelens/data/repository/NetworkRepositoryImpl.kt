package com.example.devicelens.data.repository

import com.example.devicelens.data.system.ConnectivityProvider
import com.example.devicelens.domain.model.NetworkInfo
import com.example.devicelens.domain.repository.NetworkRepository
import javax.inject.Inject

class NetworkRepositoryImpl @Inject constructor(
    private val connectivityProvider: ConnectivityProvider
) : NetworkRepository {

    override suspend fun getNetworkInfo(): NetworkInfo {
        return connectivityProvider.getNetworkInfo()
    }

    override suspend fun runNetworkTest(): NetworkInfo {
        return connectivityProvider.runNetworkTest()
    }
}
