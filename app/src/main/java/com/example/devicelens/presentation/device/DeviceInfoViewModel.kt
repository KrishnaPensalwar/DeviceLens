package com.example.devicelens.presentation.device

import androidx.lifecycle.ViewModel
import com.example.devicelens.domain.usecase.device.GetDeviceInfoUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class DeviceInfoViewModel @Inject constructor(
    private val getDeviceInfoUseCase: GetDeviceInfoUseCase
) : ViewModel() {

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