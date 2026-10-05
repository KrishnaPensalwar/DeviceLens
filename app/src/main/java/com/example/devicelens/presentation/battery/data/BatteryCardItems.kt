package com.example.devicelens.presentation.battery.data

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.BatteryStd
import androidx.compose.material.icons.outlined.Bolt
import androidx.compose.material.icons.outlined.DeviceThermostat
import androidx.compose.material.icons.outlined.ElectricBolt
import androidx.compose.material.icons.outlined.HealthAndSafety
import androidx.compose.material.icons.outlined.Power
import androidx.compose.material.icons.outlined.Speed
import com.example.devicelens.domain.model.BatteryInfo
import com.example.devicelens.presentation.battery.formatBatteryHealth
import com.example.devicelens.presentation.battery.formatChargingType
import com.example.devicelens.presentation.battery.formatTemperature
import com.example.devicelens.presentation.battery.formatVoltage
import com.example.devicelens.presentation.components.LensInfoItem
import com.example.devicelens.ui.theme.LensGreen

fun powerDetailItems(batteryInfo: BatteryInfo): List<LensInfoItem> = listOf(
    LensInfoItem(
        icon = Icons.Outlined.Bolt,
        label = "Charging",
        value = if (batteryInfo.isCharging) "Charging" else "Not charging"
    ),
    LensInfoItem(
        icon = Icons.Outlined.ElectricBolt,
        label = "Charging type",
        value = formatChargingType(batteryInfo.chargingType.name)
    ),
    LensInfoItem(
        icon = Icons.Outlined.Power,
        label = "Voltage",
        value = batteryInfo.voltage?.let(::formatVoltage) ?: "Unknown"
    ),
    LensInfoItem(
        icon = Icons.Outlined.Speed,
        label = "Capacity",
        value = batteryInfo.capacity?.let { "$it mAh" } ?: "Unknown"
    ),
    LensInfoItem(
        icon = Icons.Outlined.BatteryStd,
        label = "Technology",
        value = batteryInfo.technology ?: "Unknown"
    )
)

fun batteryHealthItems(batteryInfo: BatteryInfo): List<LensInfoItem> = listOf(
    LensInfoItem(
        icon = Icons.Outlined.HealthAndSafety,
        label = "Health",
        value = formatBatteryHealth(batteryInfo.health.name),
        valueColor = LensGreen
    ),
    LensInfoItem(
        icon = Icons.Outlined.DeviceThermostat,
        label = "Temperature",
        value = batteryInfo.temperature?.let(::formatTemperature) ?: "Unknown"
    ),
    LensInfoItem(
        icon = Icons.Outlined.Power,
        label = "Battery saver",
        value = if (batteryInfo.isBatterySaverEnabled) "Enabled" else "Disabled"
    )
)
