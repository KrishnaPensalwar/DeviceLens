package com.example.devicelens.presentation.health

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.devicelens.domain.usecase.health.CalculateHealthScoreUseCase
import com.example.devicelens.domain.usecase.health.GenerateRecommendationsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class HealthViewModel @Inject constructor(
    private val calculateHealthScoreUseCase: CalculateHealthScoreUseCase,
    private val generateRecommendationsUseCase: GenerateRecommendationsUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(HealthUiState())
    val uiState = _uiState.asStateFlow()

    init {
        checkHealth()
    }

    fun checkHealth() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            try {
                val report = withContext(Dispatchers.Default) {
                    calculateHealthScoreUseCase.invoke()
                }
                val recommendations = generateRecommendationsUseCase.invoke(report)
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        report = report,
                        recommendations = recommendations
                    )
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        error = e.message ?: "Unable to check device health"
                    )
                }
            }
        }
    }
}
