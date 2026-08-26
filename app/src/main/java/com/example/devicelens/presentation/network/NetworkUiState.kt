package com.example.devicelens.presentation.network

import com.example.devicelens.domain.model.NetworkInfo

data class NetworkUiState(
    val isLoading: Boolean = false,
    val networkInfo: NetworkInfo? = null,
    val isTesting: Boolean = false,
    val error: String? = null
)
