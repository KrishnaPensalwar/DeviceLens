package com.example.devicelens.presentation.device

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.devicelens.domain.usecase.device.GetDeviceInfoUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DeviceInfoViewModel @Inject constructor(
    private val getDeviceInfoUseCase: GetDeviceInfoUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(DeviceInfoUiState())
    val uiState = _uiState.asStateFlow()

    init {
        loadDeviceInfo()
    }

    fun loadDeviceInfo() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            try {
                val deviceInfo = getDeviceInfoUseCase()
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        deviceInfo = deviceInfo
                    )
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        error = e.message ?: "Unable to load device information"
                    )
                }
            }
        }
    }
}
