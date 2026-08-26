package com.example.devicelens.domain.repository

import com.example.devicelens.domain.model.NetworkInfo

interface NetworkRepository {
    suspend fun getNetworkInfo(): NetworkInfo
    suspend fun runNetworkTest(): NetworkInfo
}
