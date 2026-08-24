package com.example.devicelens.presentation.battery

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.devicelens.domain.usecase.battery.GetBatteryInfoUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BatteryViewModel @Inject constructor(
    private val getBatteryInfoUseCase: GetBatteryInfoUseCase
) : ViewModel() {

    private val _batteryState = MutableStateFlow(BatteryUiState())
    val batteryState = _batteryState.asStateFlow()

    init {
        getBatteryInfo()
    }

    fun getBatteryInfo() {
        viewModelScope.launch {
            _batteryState.update {
                it.copy(
                    isLoading = true
                )
            }
            val info = getBatteryInfoUseCase.invoke()
            _batteryState.update {
                it.copy(
                    isLoading = false,
                    batteryInfo = info
                )
            }

        }
    }

}