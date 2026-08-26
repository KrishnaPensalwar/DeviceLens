package com.example.devicelens.data.system

import android.app.usage.UsageEvents
import android.app.usage.UsageStatsManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import com.example.devicelens.core.util.PermissionHelper
import com.example.devicelens.domain.model.AppUsageDetail
import com.example.devicelens.domain.model.AppUsageInfo
import com.example.devicelens.domain.model.UsageBucket
import com.example.devicelens.domain.model.UsageOverview
import com.example.devicelens.domain.model.UsagePeriod
import com.example.devicelens.domain.model.UsageSession
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import javax.inject.Inject

class UsageStatsProvider @Inject constructor(
    @ApplicationContext private val context: Context
) {

    private val usageStatsManager: UsageStatsManager
        get() = context.getSystemService(Context.USAGE_STATS_SERVICE) as UsageStatsManager

    fun hasUsageAccess(): Boolean = PermissionHelper.hasUsageAccess(context)

    suspend fun getUsageOverview(period: UsagePeriod): UsageOverview = withContext(Dispatchers.IO) {
        val range = rangeFor(period)
        val usageByPackage = queryUsageByPackage(range.start, range.end)
        val launchCounts = queryLaunchCounts(range.start, range.end)
        val total = usageByPackage.values.sum()
        val packageManager = context.packageManager

        val apps = usageByPackage.mapNotNull { (packageName, usageMillis) ->
            if (usageMillis <= 0) return@mapNotNull null
            val appName = appLabel(packageManager, packageName) ?: return@mapNotNull null
            val lastUsed = lastUsedTime(packageName, range.start, range.end)
            val percent = if (total > 0) usageMillis.toDouble() / total * 100.0 else 0.0
            AppUsageInfo(
                packageName = packageName,
                appName = appName,
                usageMillis = usageMillis,
                lastUsedMillis = lastUsed,
                launchCount = launchCounts[packageName],
                percentOfTotal = percent
            )
        }.sortedByDescending { it.usageMillis }

        UsageOverview(
            period = period,
            totalUsageMillis = total,
            apps = apps
        )
    }

    suspend fun getAppUsageDetail(
        packageName: String,
        period: UsagePeriod
    ): AppUsageDetail = withContext(Dispatchers.IO) {
        val overview = getUsageOverview(period)
        val app = overview.apps.find { it.packageName == packageName }
            ?: AppUsageInfo(
                packageName = packageName,
                appName = appLabel(context.packageManager, packageName) ?: packageName,
                usageMillis = 0,
                lastUsedMillis = 0,
                launchCount = null,
                percentOfTotal = 0.0
            )
        val range = rangeFor(period)
        val sessions = querySessions(packageName, range.start, range.end)
        val buckets = if (period == UsagePeriod.LAST_7_DAYS) {
            dailyBuckets(packageName, range.start, range.end)
        } else {
            hourlyBuckets(packageName, range.start, range.end)
        }
        AppUsageDetail(app = app, buckets = buckets, sessions = sessions)
    }

    private fun queryUsageByPackage(start: Long, end: Long): Map<String, Long> {
        val stats = usageStatsManager.queryUsageStats(
            UsageStatsManager.INTERVAL_BEST,
            start,
            end
        ) ?: return emptyMap()
        return stats
            .filter { it.totalTimeInForeground > 0 }
            .groupBy { it.packageName }
            .mapValues { (_, list) -> list.sumOf { it.totalTimeInForeground } }
    }

    private fun lastUsedTime(packageName: String, start: Long, end: Long): Long {
        val stats = usageStatsManager.queryUsageStats(
            UsageStatsManager.INTERVAL_BEST,
            start,
            end
        ) ?: return 0L
        return stats
            .filter { it.packageName == packageName }
            .maxOfOrNull { it.lastTimeUsed } ?: 0L
    }

    private fun queryLaunchCounts(start: Long, end: Long): Map<String, Int> {
        val events = usageStatsManager.queryEvents(start, end)
        val counts = mutableMapOf<String, Int>()
        val event = UsageEvents.Event()
        val resumeType = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            UsageEvents.Event.ACTIVITY_RESUMED
        } else {
            @Suppress("DEPRECATION")
            UsageEvents.Event.MOVE_TO_FOREGROUND
        }
        while (events.hasNextEvent()) {
            events.getNextEvent(event)
            if (event.eventType == resumeType) {
                counts[event.packageName] = (counts[event.packageName] ?: 0) + 1
            }
        }
        return counts
    }

    private fun querySessions(
        packageName: String,
        start: Long,
        end: Long
    ): List<UsageSession> {
        val events = usageStatsManager.queryEvents(start, end)
        val event = UsageEvents.Event()
        val resumeType = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            UsageEvents.Event.ACTIVITY_RESUMED
        } else {
            @Suppress("DEPRECATION")
            UsageEvents.Event.MOVE_TO_FOREGROUND
        }
        val pauseType = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            UsageEvents.Event.ACTIVITY_PAUSED
        } else {
            @Suppress("DEPRECATION")
            UsageEvents.Event.MOVE_TO_BACKGROUND
        }
        val sessions = mutableListOf<UsageSession>()
        var sessionStart: Long? = null
        while (events.hasNextEvent()) {
            events.getNextEvent(event)
            if (event.packageName != packageName) continue
            when (event.eventType) {
                resumeType -> sessionStart = event.timeStamp
                pauseType -> {
                    val begin = sessionStart ?: continue
                    val finish = event.timeStamp
                    if (finish > begin) {
                        sessions += UsageSession(
                            startMillis = begin,
                            endMillis = finish,
                            durationMillis = finish - begin
                        )
                    }
                    sessionStart = null
                }
            }
        }
        return sessions.sortedByDescending { it.startMillis }.take(20)
    }

    private fun hourlyBuckets(
        packageName: String,
        start: Long,
        end: Long
    ): List<UsageBucket> {
        val byHour = LongArray(24)
        querySessions(packageName, start, end).forEach { session ->
            addDurationToHourBuckets(byHour, session.startMillis, session.endMillis)
        }
        return (0 until 24).map { hour ->
            UsageBucket(
                label = String.format(Locale.getDefault(), "%02d:00", hour),
                usageMillis = byHour[hour]
            )
        }
    }

    private fun dailyBuckets(
        packageName: String,
        start: Long,
        end: Long
    ): List<UsageBucket> {
        val dayFormat = SimpleDateFormat("EEE", Locale.getDefault())
        val calendar = Calendar.getInstance().apply { timeInMillis = start }
        val buckets = mutableListOf<UsageBucket>()
        while (calendar.timeInMillis < end) {
            val dayStart = startOfDay(calendar.timeInMillis)
            val dayEnd = (calendar.clone() as Calendar).apply {
                add(Calendar.DAY_OF_YEAR, 1)
            }.timeInMillis.coerceAtMost(end)
            val usage = queryUsageByPackage(dayStart, dayEnd)[packageName] ?: 0L
            buckets += UsageBucket(
                label = dayFormat.format(Date(dayStart)),
                usageMillis = usage
            )
            calendar.add(Calendar.DAY_OF_YEAR, 1)
        }
        return buckets
    }

    private fun addDurationToHourBuckets(
        byHour: LongArray,
        start: Long,
        end: Long
    ) {
        var cursor = start
        while (cursor < end) {
            val cal = Calendar.getInstance().apply { timeInMillis = cursor }
            val hour = cal.get(Calendar.HOUR_OF_DAY)
            val nextHour = (cal.clone() as Calendar).apply {
                set(Calendar.MINUTE, 0)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
                add(Calendar.HOUR_OF_DAY, 1)
            }.timeInMillis
            val sliceEnd = minOf(end, nextHour)
            byHour[hour] += (sliceEnd - cursor).coerceAtLeast(0)
            cursor = sliceEnd
        }
    }

    private fun appLabel(packageManager: PackageManager, packageName: String): String? {
        return try {
            val info = packageManager.getApplicationInfo(packageName, 0)
            packageManager.getApplicationLabel(info).toString()
        } catch (_: Exception) {
            null
        }
    }

    private fun rangeFor(period: UsagePeriod): TimeRange {
        val now = System.currentTimeMillis()
        val todayStart = startOfDay(now)
        return when (period) {
            UsagePeriod.TODAY -> TimeRange(todayStart, now)
            UsagePeriod.YESTERDAY -> {
                val yesterdayStart = todayStart - DAY_MILLIS
                TimeRange(yesterdayStart, todayStart)
            }

            UsagePeriod.LAST_7_DAYS -> TimeRange(todayStart - 6 * DAY_MILLIS, now)
        }
    }

    private fun startOfDay(millis: Long): Long {
        return Calendar.getInstance().apply {
            timeInMillis = millis
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }.timeInMillis
    }

    private data class TimeRange(val start: Long, val end: Long)

    companion object {
        private const val DAY_MILLIS = 24L * 60 * 60 * 1000
    }
}
