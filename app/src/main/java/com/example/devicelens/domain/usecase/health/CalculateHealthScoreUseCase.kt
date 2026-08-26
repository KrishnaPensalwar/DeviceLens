package com.example.devicelens.domain.usecase.health

import com.example.devicelens.domain.model.BatteryHealth
import com.example.devicelens.domain.model.BatteryInfo
import com.example.devicelens.domain.model.DeviceInfo
import com.example.devicelens.domain.model.HealthArea
import com.example.devicelens.domain.model.HealthIssue
import com.example.devicelens.domain.model.HealthReport
import com.example.devicelens.domain.model.HealthStatus
import com.example.devicelens.domain.model.RecommendationPriority
import com.example.devicelens.domain.usecase.battery.GetBatteryInfoUseCase
import com.example.devicelens.domain.usecase.device.GetDeviceInfoUseCase
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class CalculateHealthScoreUseCase @Inject constructor(
    private val getBatteryInfoUseCase: GetBatteryInfoUseCase,
    private val getDeviceInfoUseCase: GetDeviceInfoUseCase
) {

    fun invoke(): HealthReport {
        val battery = getBatteryInfoUseCase.invoke()
        val device = getDeviceInfoUseCase()
        val issues = mutableListOf<HealthIssue>()

        val batteryArea = batteryArea(battery, issues)
        val performanceArea = performanceArea(device, issues)
        val temperatureArea = temperatureArea(battery, issues)
        val securityArea = securityArea(device, issues)
        val systemArea = systemArea(device, issues)
        val storageArea = storageArea(device, issues)

        val weightedScore = (
            batteryArea.score * WEIGHT_BATTERY +
                performanceArea.score * WEIGHT_PERFORMANCE +
                temperatureArea.score * WEIGHT_TEMPERATURE +
                securityArea.score * WEIGHT_SECURITY +
                systemArea.score * WEIGHT_SYSTEM
            ).toInt()

        val score = (weightedScore - storagePenalty(device.storage.usagePercent))
            .coerceIn(0, 100)
        val status = overallStatus(score)
        val areas = listOf(
            batteryArea,
            storageArea,
            performanceArea,
            temperatureArea,
            securityArea,
            systemArea
        )
        val attentionCount = areas.count {
            it.status == HealthStatus.NEEDS_ATTENTION || it.status == HealthStatus.POOR
        }

        return HealthReport(
            score = score,
            status = status,
            summary = overallSummary(status),
            attentionCount = attentionCount,
            areas = areas,
            issues = issues.sortedBy { it.priority },
            checkedAtMillis = System.currentTimeMillis()
        )
    }

    private fun batteryArea(
        battery: BatteryInfo,
        issues: MutableList<HealthIssue>
    ): HealthArea {
        val (score, status, description) = when (battery.health) {
            BatteryHealth.DEAD -> Triple(
                20,
                HealthStatus.POOR,
                "Your battery may need replacing."
            )

            BatteryHealth.OVERHEAT -> Triple(
                40,
                HealthStatus.NEEDS_ATTENTION,
                "Your battery is getting too warm."
            )

            BatteryHealth.OVER_VOLTAGE, BatteryHealth.UNSPECIFIED_FAILURE -> Triple(
                40,
                HealthStatus.NEEDS_ATTENTION,
                "Your battery may have a problem."
            )

            BatteryHealth.COLD -> Triple(
                70,
                HealthStatus.NEEDS_ATTENTION,
                "Your battery is colder than usual."
            )

            BatteryHealth.UNKNOWN -> Triple(
                80,
                HealthStatus.GOOD,
                "Your battery is performing normally."
            )

            BatteryHealth.GOOD -> Triple(
                100,
                HealthStatus.GOOD,
                "Your battery is performing normally."
            )
        }

        if (status == HealthStatus.POOR || status == HealthStatus.NEEDS_ATTENTION) {
            issues += HealthIssue(
                title = "Battery needs attention",
                detail = description,
                priority = if (status == HealthStatus.POOR) {
                    RecommendationPriority.HIGH
                } else {
                    RecommendationPriority.MEDIUM
                }
            )
        }

        return HealthArea("Battery", status, description, score)
    }

    private fun performanceArea(
        device: DeviceInfo,
        issues: MutableList<HealthIssue>
    ): HealthArea {
        val percent = device.memory.usagePercent
        val (score, status, description) = when {
            percent >= 85 -> Triple(
                50,
                HealthStatus.POOR,
                "Your phone may feel slower because memory use is high."
            )

            percent >= 70 -> Triple(
                75,
                HealthStatus.NEEDS_ATTENTION,
                "Memory use is higher than usual."
            )

            else -> Triple(
                100,
                HealthStatus.GOOD,
                "Your phone is responding normally."
            )
        }
        if (status != HealthStatus.GOOD) {
            issues += HealthIssue(
                title = "Memory use is high",
                detail = "Review apps using excessive resources.",
                priority = if (percent >= 85) {
                    RecommendationPriority.HIGH
                } else {
                    RecommendationPriority.MEDIUM
                }
            )
        }
        return HealthArea("Performance", status, description, score)
    }

    private fun temperatureArea(
        battery: BatteryInfo,
        issues: MutableList<HealthIssue>
    ): HealthArea {
        val temp = battery.temperature
        val (score, status, description) = when {
            temp == null -> Triple(
                80,
                HealthStatus.GOOD,
                "Temperature looks normal."
            )

            temp >= 40f || battery.health == BatteryHealth.OVERHEAT -> Triple(
                40,
                HealthStatus.POOR,
                "Your phone is running warmer than usual."
            )

            temp >= 35f -> Triple(
                70,
                HealthStatus.NEEDS_ATTENTION,
                "Your phone is getting warmer than usual."
            )

            else -> Triple(
                100,
                HealthStatus.GOOD,
                "Your phone is running normally."
            )
        }
        if (status != HealthStatus.GOOD) {
            issues += HealthIssue(
                title = "Phone is running warm",
                detail = "Let the device cool down.",
                priority = if (status == HealthStatus.POOR) {
                    RecommendationPriority.HIGH
                } else {
                    RecommendationPriority.MEDIUM
                }
            )
        }
        return HealthArea("Temperature", status, description, score)
    }

    private fun securityArea(
        device: DeviceInfo,
        issues: MutableList<HealthIssue>
    ): HealthArea {
        val ageDays = securityPatchAgeDays(device.securityPatch)
        val (score, status, description) = when {
            ageDays == null -> Triple(
                70,
                HealthStatus.NEEDS_ATTENTION,
                "Security update information is not available."
            )

            ageDays > 365 -> Triple(
                40,
                HealthStatus.POOR,
                "Your device may need a security update."
            )

            ageDays > 180 -> Triple(
                55,
                HealthStatus.NEEDS_ATTENTION,
                "Your device may need a security update."
            )

            else -> Triple(
                100,
                HealthStatus.GOOD,
                "No major security issues detected."
            )
        }
        if (status != HealthStatus.GOOD) {
            issues += HealthIssue(
                title = "System update may be available",
                detail = "Consider updating your device.",
                priority = if (ageDays != null && ageDays > 365) {
                    RecommendationPriority.HIGH
                } else {
                    RecommendationPriority.MEDIUM
                }
            )
        }
        return HealthArea("Security", status, description, score)
    }

    private fun systemArea(
        device: DeviceInfo,
        issues: MutableList<HealthIssue>
    ): HealthArea {
        val ageDays = securityPatchAgeDays(device.securityPatch)
        val alreadyHasUpdateIssue = issues.any { it.title.contains("update", ignoreCase = true) }
        val (score, status, description) = when {
            ageDays == null -> Triple(
                75,
                HealthStatus.GOOD,
                "Your system looks okay."
            )

            ageDays > 180 -> Triple(
                55,
                HealthStatus.NEEDS_ATTENTION,
                "Your device may need an update."
            )

            else -> Triple(
                100,
                HealthStatus.GOOD,
                "Your system looks up to date."
            )
        }
        if (status != HealthStatus.GOOD && !alreadyHasUpdateIssue) {
            issues += HealthIssue(
                title = "System update may be available",
                detail = "Check for system updates.",
                priority = RecommendationPriority.MEDIUM
            )
        }
        return HealthArea("System", status, description, score)
    }

    private fun storageArea(
        device: DeviceInfo,
        issues: MutableList<HealthIssue>
    ): HealthArea {
        val percent = device.storage.usagePercent
        val (score, status, description) = when {
            percent >= 90 -> Triple(
                40,
                HealthStatus.POOR,
                "Storage is almost full."
            )

            percent >= 75 -> Triple(
                65,
                HealthStatus.NEEDS_ATTENTION,
                "Storage is getting full."
            )

            else -> Triple(
                100,
                HealthStatus.GOOD,
                "You have plenty of storage space."
            )
        }
        if (status != HealthStatus.GOOD) {
            issues += HealthIssue(
                title = if (percent >= 90) "Storage is almost full" else "Storage is getting full",
                detail = "Free up some storage.",
                priority = if (percent >= 90) {
                    RecommendationPriority.HIGH
                } else {
                    RecommendationPriority.MEDIUM
                }
            )
        }
        return HealthArea("Storage", status, description, score)
    }

    private fun storagePenalty(usagePercent: Int): Int {
        return when {
            usagePercent >= 90 -> 8
            usagePercent >= 75 -> 4
            else -> 0
        }
    }

    private fun securityPatchAgeDays(patch: String?): Long? {
        if (patch.isNullOrBlank()) return null
        return try {
            val parsed = SimpleDateFormat("yyyy-MM-dd", Locale.US).parse(patch) ?: return null
            TimeUnit.MILLISECONDS.toDays(Date().time - parsed.time)
        } catch (_: Exception) {
            null
        }
    }

    private fun overallStatus(score: Int): HealthStatus {
        return when {
            score >= 85 -> HealthStatus.EXCELLENT
            score >= 70 -> HealthStatus.GOOD
            score >= 50 -> HealthStatus.NEEDS_ATTENTION
            else -> HealthStatus.POOR
        }
    }

    private fun overallSummary(status: HealthStatus): String {
        return when (status) {
            HealthStatus.EXCELLENT -> "Your phone is in excellent condition."
            HealthStatus.GOOD -> "Your phone is in good condition."
            HealthStatus.NEEDS_ATTENTION -> "A few things on your phone need attention."
            HealthStatus.POOR -> "Your phone needs some care."
        }
    }

    companion object {
        const val WEIGHT_BATTERY = 0.20
        const val WEIGHT_PERFORMANCE = 0.20
        const val WEIGHT_TEMPERATURE = 0.20
        const val WEIGHT_SECURITY = 0.20
        const val WEIGHT_SYSTEM = 0.20
    }
}
