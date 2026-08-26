package com.example.devicelens.presentation.usage

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.devicelens.domain.model.UsagePeriod
import com.example.devicelens.domain.model.UsageSortOption
import com.example.devicelens.domain.model.filterAppUsage
import com.example.devicelens.domain.model.isHighUsage
import com.example.devicelens.domain.model.sortAppUsage
import com.example.devicelens.domain.usecase.usage.GetAppUsageUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UsageViewModel @Inject constructor(
    private val getAppUsageUseCase: GetAppUsageUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(UsageUiState())
    val uiState = _uiState.asStateFlow()

    init {
        refreshPermission()
        if (getAppUsageUseCase.hasAccess()) {
            loadUsage(isRefresh = false)
        }
    }

    fun refreshPermission() {
        val granted = getAppUsageUseCase.hasAccess()
        _uiState.update { it.copy(hasUsageAccess = granted) }
        if (granted && _uiState.value.apps.isEmpty() && !_uiState.value.isLoading) {
            loadUsage(isRefresh = false)
        }
    }

    fun refresh() {
        if (!_uiState.value.hasUsageAccess) {
            refreshPermission()
            return
        }
        loadUsage(isRefresh = true)
    }

    fun onPeriodSelected(period: UsagePeriod) {
        _uiState.update { it.copy(period = period) }
        loadUsage(isRefresh = false)
    }

    fun onSortSelected(option: UsageSortOption) {
        _uiState.update { current ->
            val visible = applyFilters(current.apps, current.searchQuery, option)
            current.copy(sortOption = option, visibleApps = visible)
        }
    }

    fun onSearchQueryChanged(query: String) {
        _uiState.update { current ->
            val visible = applyFilters(current.apps, query, current.sortOption)
            current.copy(searchQuery = query, visibleApps = visible)
        }
    }

    private fun loadUsage(isRefresh: Boolean) {
        viewModelScope.launch {
            val period = _uiState.value.period
            val sort = _uiState.value.sortOption
            val query = _uiState.value.searchQuery
            _uiState.update {
                it.copy(
                    isLoading = !isRefresh,
                    isRefreshing = isRefresh,
                    error = null,
                    hasUsageAccess = getAppUsageUseCase.hasAccess()
                )
            }
            if (!_uiState.value.hasUsageAccess) {
                _uiState.update { it.copy(isLoading = false, isRefreshing = false) }
                return@launch
            }
            try {
                val overview = getAppUsageUseCase.invoke(period)
                val highUsage = overview.apps.filter {
                    isHighUsage(it.usageMillis, overview.totalUsageMillis)
                }
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        isRefreshing = false,
                        totalUsageMillis = overview.totalUsageMillis,
                        apps = overview.apps,
                        visibleApps = applyFilters(overview.apps, query, sort),
                        mostUsed = overview.apps.take(3),
                        highUsageApps = highUsage,
                        recommendations = recommendationsFor(highUsage)
                    )
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        isRefreshing = false,
                        error = e.message ?: "Unable to read app usage"
                    )
                }
            }
        }
    }

    private fun applyFilters(
        apps: List<com.example.devicelens.domain.model.AppUsageInfo>,
        query: String,
        sort: UsageSortOption
    ) = sortAppUsage(filterAppUsage(apps, query), sort)

    private fun recommendationsFor(
        highUsage: List<com.example.devicelens.domain.model.AppUsageInfo>
    ): List<String> {
        if (highUsage.isEmpty()) return emptyList()
        return listOf(
            "Some apps were used a lot in this period.",
            "Take a break from ${highUsage.first().appName} if you want more screen-free time."
        )
    }
}
