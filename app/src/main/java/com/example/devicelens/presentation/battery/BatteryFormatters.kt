package com.example.devicelens.presentation.battery

import com.example.devicelens.domain.model.BatteryInfo

fun batteryStatusTitle(batteryInfo: BatteryInfo): String {
    return when {
        batteryInfo.level <= 15 -> "Battery is low"
        batteryInfo.level <= 30 -> "Battery getting low"
        batteryInfo.isCharging -> "Battery is charging"
        batteryInfo.level >= 80 -> "Battery level is good"
        else -> "Battery level is normal"
    }
}

fun chargingStatusText(batteryInfo: BatteryInfo): String {
    return when {
        batteryInfo.isCharging -> "Charging"
        batteryInfo.level >= 100 -> "Fully charged"
        else -> "Not charging"
    }
}

fun formatChargingType(value: String): String {
    return value.lowercase().replace("_", " ").replaceFirstChar { it.uppercase() }
}

fun formatBatteryHealth(value: String): String {
    return value.lowercase().replace("_", " ").replaceFirstChar { it.uppercase() }
}

fun formatTemperature(temperature: Float): String {
    val rounded = "%.1f".format(temperature)
    return when {
        temperature >= 40f -> "$rounded °C • Warm"
        temperature >= 35f -> "$rounded °C • Slightly warm"
        temperature <= 10f -> "$rounded °C • Cold"
        else -> "$rounded °C • Normal"
    }
}

fun formatVoltage(voltage: Int): String {
    return if (voltage >= 1000) {
        "%.2f V".format(voltage / 1000f)
    } else {
        "$voltage mV"
    }
}
