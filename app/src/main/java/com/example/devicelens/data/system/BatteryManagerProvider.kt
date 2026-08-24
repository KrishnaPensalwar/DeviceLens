package com.example.devicelens.data.system

import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.BatteryManager
import android.os.PowerManager
import com.example.devicelens.domain.model.BatteryHealth
import com.example.devicelens.domain.model.BatteryInfo
import com.example.devicelens.domain.model.BatteryStatus
import com.example.devicelens.domain.model.ChargingType
import kotlin.jvm.Throws

class BatteryManagerProvider(
    private val context: Context
) {

    private val batteryManager =
        context.getSystemService(Context.BATTERY_SERVICE) as BatteryManager
    private val powerManager =
        context.getSystemService(Context.POWER_SERVICE) as PowerManager


    fun getBatteryInfo(): BatteryInfo {
        val batteryIntent = getBatteryIntent()
        val level = batteryIntent.getIntExtra(
            BatteryManager.EXTRA_LEVEL,
            -1
        )

        val scale = batteryIntent.getIntExtra(
            BatteryManager.EXTRA_SCALE,
            -1
        )

        val batteryLevel = if (level >= 0 && scale > 0) {
            (level * 100) / scale
        } else {
            -1
        }
        val status = batteryIntent.getIntExtra(
            BatteryManager.EXTRA_STATUS,
            BatteryManager.BATTERY_STATUS_UNKNOWN
        )
        val batteryStatus = when (status) {
            BatteryManager.BATTERY_STATUS_CHARGING ->
                BatteryStatus.CHARGING

            BatteryManager.BATTERY_STATUS_DISCHARGING ->
                BatteryStatus.DISCHARGING

            BatteryManager.BATTERY_STATUS_FULL ->
                BatteryStatus.FULL

            BatteryManager.BATTERY_STATUS_NOT_CHARGING ->
                BatteryStatus.NOT_CHARGING

            else ->
                BatteryStatus.UNKNOWN
        }

        val plugged = batteryIntent.getIntExtra(
            BatteryManager.EXTRA_PLUGGED,
            0
        )

        val chargingType = when (plugged) {
            BatteryManager.BATTERY_PLUGGED_AC ->
                ChargingType.AC

            BatteryManager.BATTERY_PLUGGED_USB ->
                ChargingType.USB

            BatteryManager.BATTERY_PLUGGED_WIRELESS ->
                ChargingType.WIRELESS

            else ->
                ChargingType.UNKNOWN
        }
        val temperatureRaw = batteryIntent.getIntExtra(
            BatteryManager.EXTRA_TEMPERATURE,
            -1
        )

        val temperature = if (temperatureRaw >= 0) {
            temperatureRaw / 10f
        } else {
            null
        }
        val voltage = batteryIntent.getIntExtra(
            BatteryManager.EXTRA_VOLTAGE,
            -1
        ).takeIf { it >= 0 }
        val technology = batteryIntent.getStringExtra(
            BatteryManager.EXTRA_TECHNOLOGY
        )
        val health = batteryIntent.getIntExtra(
            BatteryManager.EXTRA_HEALTH,
            BatteryManager.BATTERY_HEALTH_UNKNOWN
        )

        val batteryHealth = when (health) {
            BatteryManager.BATTERY_HEALTH_GOOD ->
                BatteryHealth.GOOD

            BatteryManager.BATTERY_HEALTH_OVERHEAT ->
                BatteryHealth.OVERHEAT

            BatteryManager.BATTERY_HEALTH_DEAD ->
                BatteryHealth.DEAD

            BatteryManager.BATTERY_HEALTH_OVER_VOLTAGE ->
                BatteryHealth.OVER_VOLTAGE

            BatteryManager.BATTERY_HEALTH_COLD ->
                BatteryHealth.COLD

            BatteryManager.BATTERY_HEALTH_UNSPECIFIED_FAILURE ->
                BatteryHealth.UNSPECIFIED_FAILURE

            else ->
                BatteryHealth.UNKNOWN
        }

        val isBatterySaverEnabled =
            powerManager.isPowerSaveMode

        val isCharging =
            batteryStatus == BatteryStatus.CHARGING

        return BatteryInfo(
            level = batteryLevel,
            isCharging = isCharging,
            chargingType = chargingType,
            batteryStatus = batteryStatus,
            temperature = temperature,
            voltage = voltage,
            technology = technology,
            health = batteryHealth,
            isBatterySaverEnabled = isBatterySaverEnabled
        )


    }

    private fun getBatteryIntent(): Intent {
        return context.registerReceiver(
            null,
            IntentFilter(Intent.ACTION_BATTERY_CHANGED)
        ) ?: throw IllegalStateException("Unable to get battery information")
    }

}