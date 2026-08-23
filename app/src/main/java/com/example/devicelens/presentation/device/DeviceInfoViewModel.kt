package com.example.devicelens.presentation.device

import androidx.lifecycle.ViewModel
import com.example.devicelens.data.repository.DeviceRepositoryImpl
import com.example.devicelens.data.system.DeviceInfoProvider
import com.example.devicelens.domain.usecase.device.GetDeviceInfoUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class DeviceInfoViewModel : ViewModel() {

    private val deviceInfoProvider = DeviceInfoProvider()

    private val repository = DeviceRepositoryImpl(
        deviceInfoProvider = deviceInfoProvider
    )

    private val getDeviceInfoUseCase = GetDeviceInfoUseCase(
        repository = repository
    )

    private val _uiState = MutableStateFlow(DeviceInfoUiState())
    val uiState: StateFlow<DeviceInfoUiState> = _uiState.asStateFlow()

    init {
        loadDeviceInfo()
    }

    private fun loadDeviceInfo() {
        _uiState.value = DeviceInfoUiState(
            isLoading = true
        )

        try {
            val deviceInfo = getDeviceInfoUseCase.invoke()

            _uiState.value = DeviceInfoUiState(
                isLoading = false,
                deviceInfo = deviceInfo
            )
        } catch (e: Exception) {
            _uiState.value = DeviceInfoUiState(
                isLoading = false,
                error = e.message ?: "Unable to load device information"
            )
        }
    }
}