package com.example.devicelens.domain.model

enum class UsagePeriod {
    TODAY,
    YESTERDAY,
    LAST_7_DAYS
}

enum class UsageSortOption {
    MOST_USED,
    LEAST_USED,
    NAME_AZ,
    NAME_ZA
}

data class AppUsageInfo(
    val packageName: String,
    val appName: String,
    val usageMillis: Long,
    val lastUsedMillis: Long,
    val launchCount: Int?,
    val percentOfTotal: Double
)

data class UsageOverview(
    val period: UsagePeriod,
    val totalUsageMillis: Long,
    val apps: List<AppUsageInfo>
)

data class UsageSession(
    val startMillis: Long,
    val endMillis: Long,
    val durationMillis: Long
)

data class UsageBucket(
    val label: String,
    val usageMillis: Long
)

data class AppUsageDetail(
    val app: AppUsageInfo,
    val buckets: List<UsageBucket>,
    val sessions: List<UsageSession>
)

fun isHighUsage(usageMillis: Long, totalMillis: Long): Boolean {
    if (usageMillis >= HIGH_USAGE_MILLIS) return true
    if (totalMillis <= 0) return false
    return (usageMillis.toDouble() / totalMillis) * 100.0 >= HIGH_USAGE_PERCENT
}

fun sortAppUsage(
    apps: List<AppUsageInfo>,
    option: UsageSortOption
): List<AppUsageInfo> {
    return when (option) {
        UsageSortOption.MOST_USED -> apps.sortedByDescending { it.usageMillis }
        UsageSortOption.LEAST_USED -> apps.sortedBy { it.usageMillis }
        UsageSortOption.NAME_AZ -> apps.sortedBy { it.appName.lowercase() }
        UsageSortOption.NAME_ZA -> apps.sortedByDescending { it.appName.lowercase() }
    }
}

fun filterAppUsage(
    apps: List<AppUsageInfo>,
    query: String
): List<AppUsageInfo> {
    val q = query.trim().lowercase()
    if (q.isEmpty()) return apps
    return apps.filter {
        it.appName.lowercase().contains(q) || it.packageName.lowercase().contains(q)
    }
}

const val HIGH_USAGE_MILLIS = 2L * 60 * 60 * 1000
const val HIGH_USAGE_PERCENT = 25.0
