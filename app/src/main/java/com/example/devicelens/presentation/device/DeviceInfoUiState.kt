package com.example.devicelens.presentation.device

import com.example.devicelens.domain.model.DeviceInfo

data class DeviceInfoUiState(
    val isLoading : Boolean = false,
    val deviceInfo: DeviceInfo?=null,
    val error : String ?= null
)