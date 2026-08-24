package com.example.devicelens.presentation.battery


import com.example.devicelens.domain.model.BatteryInfo

data class BatteryUiState(
    val isLoading: Boolean = false,
    val batteryInfo: BatteryInfo? = null,
    val error: String? = null
)