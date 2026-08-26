package com.example.devicelens.presentation.network

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.devicelens.domain.usecase.network.GetNetworkInfoUseCase
import com.example.devicelens.domain.usecase.network.RunNetworkTestUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NetworkViewModel @Inject constructor(
    private val getNetworkInfoUseCase: GetNetworkInfoUseCase,
    private val runNetworkTestUseCase: RunNetworkTestUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(NetworkUiState())
    val uiState = _uiState.asStateFlow()

    init {
        loadNetwork()
    }

    fun loadNetwork() {
        viewModelScope.launch {
            _uiState.update {
                it.copy(isLoading = true, error = null)
            }
            try {
                val info = runNetworkTestUseCase.invoke()
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        networkInfo = info
                    )
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        error = e.message ?: "Unable to read network information"
                    )
                }
            }
        }
    }

    fun testNetwork() {
        viewModelScope.launch {
            _uiState.update { it.copy(isTesting = true) }
            try {
                val result = runNetworkTestUseCase.invoke()
                _uiState.update {
                    it.copy(
                        networkInfo = result,
                        isTesting = false
                    )
                }
            } catch (_: Exception) {
                _uiState.update { it.copy(isTesting = false) }
            }
        }
    }
}
