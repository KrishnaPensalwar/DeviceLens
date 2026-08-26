package com.example.devicelens.presentation.usage.model

import androidx.annotation.DrawableRes

/**
 * Data class representing usage information for a single app.
 */
data class AppUsageInfo(
    val packageName: String,
    val appName: String,
    @DrawableRes val iconResId: Int?, // Loaded via PackageManager, nullable if not found
    val totalTimeMillis: Long, // total foreground time for the selected period
    val lastTimeUsed: Long?, // timestamp of the last foreground event (ms since epoch)
    val launchCount: Int?, // number of times the app was launched in the period, may be null on older APIs
)

/**
 * Enum representing the time period for which usage statistics are requested.
 */
enum class UsagePeriod {
    TODAY,
    YESTERDAY,
    LAST_7_DAYS
}

/**
 * UI state sealed class for the Usage screen.
 */
sealed class UsageUiState {
    object Loading : UsageUiState()
    data class Success(
        val usageList: List<AppUsageInfo>,
        val totalUsageMillis: Long,
        val period: UsagePeriod,
        val recommendations: List<UsageRecommendation>
    ) : UsageUiState()

    object Empty : UsageUiState()
    object PermissionDenied : UsageUiState()
    data class Error(val message: String) : UsageUiState()
}

/**
 * Simple recommendation data model.
 */
data class UsageRecommendation(
    val priority: Priority,
    val title: String,
    val description: String
) {
    enum class Priority { HIGH, MEDIUM, LOW }
}
