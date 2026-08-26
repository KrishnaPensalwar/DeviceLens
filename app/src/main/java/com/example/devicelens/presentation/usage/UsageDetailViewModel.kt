package com.example.devicelens.presentation.usage

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.devicelens.domain.model.AppUsageDetail
import com.example.devicelens.domain.model.UsagePeriod
import com.example.devicelens.domain.usecase.usage.GetUsageHistoryUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class UsageDetailUiState(
    val isLoading: Boolean = false,
    val period: UsagePeriod = UsagePeriod.TODAY,
    val detail: AppUsageDetail? = null,
    val error: String? = null
)

@HiltViewModel
class UsageDetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val getUsageHistoryUseCase: GetUsageHistoryUseCase
) : ViewModel() {

    private val packageName: String =
        android.net.Uri.decode(savedStateHandle.get<String>("packageName").orEmpty())

    private val _uiState = MutableStateFlow(UsageDetailUiState())
    val uiState = _uiState.asStateFlow()

    init {
        load()
    }

    fun onPeriodSelected(period: UsagePeriod) {
        _uiState.update { it.copy(period = period) }
        load()
    }

    fun retry() = load()

    private fun load() {
        if (packageName.isBlank()) {
            _uiState.update { it.copy(error = "App not found") }
            return
        }
        viewModelScope.launch {
            val period = _uiState.value.period
            _uiState.update { it.copy(isLoading = true, error = null) }
            try {
                val detail = getUsageHistoryUseCase.invoke(packageName, period)
                _uiState.update { it.copy(isLoading = false, detail = detail) }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        error = e.message ?: "Unable to load app usage"
                    )
                }
            }
        }
    }
}
