package com.example.devicelens.presentation.storage

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.devicelens.domain.model.StorageSortOption
import com.example.devicelens.domain.model.StorageThreshold
import com.example.devicelens.domain.model.highStorageApps
import com.example.devicelens.domain.model.sortAppStorage
import com.example.devicelens.domain.usecase.storage.GetAppStorageUseCase
import com.example.devicelens.domain.usecase.storage.GetStorageInfoUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class StorageViewModel @Inject constructor(
    private val getAppStorageUseCase: GetAppStorageUseCase,
    private val getStorageInfoUseCase: GetStorageInfoUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(StorageUiState())
    val uiState = _uiState.asStateFlow()

    init {
        loadStorage(isRefresh = false)
    }

    fun refresh() {
        loadStorage(isRefresh = true)
    }

    fun onSortSelected(option: StorageSortOption) {
        val current = _uiState.value
        _uiState.update {
            it.copy(
                selectedSortOption = option,
                applications = sortAppStorage(current.applications, option)
            )
        }
    }

    fun onThresholdSelected(threshold: StorageThreshold) {
        val current = _uiState.value
        _uiState.update {
            it.copy(
                threshold = threshold,
                highStorageApps = highStorageApps(current.applications, threshold)
            )
        }
    }

    private fun loadStorage(isRefresh: Boolean) {
        viewModelScope.launch {
            val keepSort = _uiState.value.selectedSortOption
            val keepThreshold = _uiState.value.threshold
            _uiState.update {
                it.copy(
                    isLoading = !isRefresh,
                    isRefreshing = isRefresh,
                    error = null
                )
            }
            try {
                val apps = getAppStorageUseCase.invoke()
                val overview = getStorageInfoUseCase.invoke(apps)
                val sorted = sortAppStorage(apps, keepSort)
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        isRefreshing = false,
                        storageOverview = overview,
                        applications = sorted,
                        highStorageApps = highStorageApps(sorted, keepThreshold),
                        selectedSortOption = keepSort,
                        threshold = keepThreshold
                    )
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        isRefreshing = false,
                        error = e.message ?: "Unable to analyze storage"
                    )
                }
            }
        }
    }
}
