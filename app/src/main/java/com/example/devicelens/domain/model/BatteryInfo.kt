package com.example.devicelens.domain.model

data class BatteryInfo(
    val level: Int,
    val isCharging: Boolean,
    val chargingType: ChargingType,
    val batteryStatus: BatteryStatus,
    val temperature: Float?,
    val voltage: Int?,
    val technology: String?,
    val health: BatteryHealth,
    val isBatterySaverEnabled: Boolean
)

enum class ChargingType{
    AC,
    USB,
    WIRELESS,
    UNKNOWN
}

enum class BatteryStatus{
    CHARGING,
    DISCHARGING,
    FULL,
    NOT_CHARGING,
    UNKNOWN
}

enum class BatteryHealth{
    GOOD,
    OVERHEAT,
    DEAD,
    OVER_VOLTAGE,
    COLD,
    UNSPECIFIED_FAILURE,
    UNKNOWN
}