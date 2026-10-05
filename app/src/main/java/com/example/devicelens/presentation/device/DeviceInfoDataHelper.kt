package com.example.devicelens.presentation.device

import com.example.devicelens.core.util.FileSizeFormatter
import com.example.devicelens.domain.model.BatteryStatus
import com.example.devicelens.domain.model.DeviceInfo
import java.text.SimpleDateFormat
import java.util.Locale

fun getDeviceItems(
    deviceInfo: DeviceInfo
): List<com.example.devicelens.presentation.device.components.InfoItem> {
    return listOf(
        _root_ide_package_.com.example.devicelens.presentation.device.components.InfoItem(
            label = "Manufacturer",
            value = deviceInfo.manufacturer
        ),
        _root_ide_package_.com.example.devicelens.presentation.device.components.InfoItem(
            label = "Model",
            value = deviceInfo.model
        ),
        _root_ide_package_.com.example.devicelens.presentation.device.components.InfoItem(
            label = "Brand",
            value = deviceInfo.brand
        ),
        _root_ide_package_.com.example.devicelens.presentation.device.components.InfoItem(
            label = "Board",
            value = deviceInfo.board
        ),
        _root_ide_package_.com.example.devicelens.presentation.device.components.InfoItem(
            label = "Hardware",
            value = deviceInfo.hardware
        )
    )
}

fun getSystemItems(
    deviceInfo: DeviceInfo
): List<com.example.devicelens.presentation.device.components.InfoItem> {
    return listOf(
        _root_ide_package_.com.example.devicelens.presentation.device.components.InfoItem(
            label = "Android Version",
            value = deviceInfo.androidVersion
        ),
        _root_ide_package_.com.example.devicelens.presentation.device.components.InfoItem(
            label = "SDK Level",
            value = deviceInfo.sdkInt.toString()
        ),
        _root_ide_package_.com.example.devicelens.presentation.device.components.InfoItem(
            label = "Security Patch",
            value = formatSecurityPatch(
                deviceInfo.securityPatch
            )
        ),
        _root_ide_package_.com.example.devicelens.presentation.device.components.InfoItem(
            label = "Build Number",
            value = deviceInfo.buildId
        )
    )
}

fun getDisplayItems(
    deviceInfo: DeviceInfo
): List<com.example.devicelens.presentation.device.components.InfoItem> {

    val display = deviceInfo.display

    return listOf(
        _root_ide_package_.com.example.devicelens.presentation.device.components.InfoItem(
            label = "Resolution",
            value = "${display.resolutionWidth} × ${display.resolutionHeight}"
        ),
        _root_ide_package_.com.example.devicelens.presentation.device.components.InfoItem(
            label = "Refresh Rate",
            value = "${display.refreshRateHz} Hz"
        ),
        _root_ide_package_.com.example.devicelens.presentation.device.components.InfoItem(
            label = "Density",
            value = "${deviceInfo.densityDpi} dpi"
        ),
        _root_ide_package_.com.example.devicelens.presentation.device.components.InfoItem(
            label = "Screen Size",
            value = display.screenSizeInches?.let {
                "%.1f inches".format(it)
            } ?: "Not available"
        )
    )
}

fun getMemoryStorageItems(
    deviceInfo: DeviceInfo
): List<com.example.devicelens.presentation.device.components.InfoItem> {

    val memory = deviceInfo.memory
    val storage = deviceInfo.storage

    return listOf(
        _root_ide_package_.com.example.devicelens.presentation.device.components.InfoItem(
            label = "Total RAM",
            value = FileSizeFormatter.format(
                memory.totalBytes
            )
        ),
        _root_ide_package_.com.example.devicelens.presentation.device.components.InfoItem(
            label = "Available RAM",
            value = FileSizeFormatter.format(
                memory.availableBytes
            )
        ),
        _root_ide_package_.com.example.devicelens.presentation.device.components.InfoItem(
            label = "Internal Storage",
            value = FileSizeFormatter.format(
                storage.totalBytes
            )
        ),
        _root_ide_package_.com.example.devicelens.presentation.device.components.InfoItem(
            label = "Available Storage",
            value = FileSizeFormatter.format(
                storage.availableBytes
            )
        )
    )
}

fun getCameraItems(
    deviceInfo: DeviceInfo
): List<com.example.devicelens.presentation.device.components.InfoItem> {

    val camera = deviceInfo.camera

    return listOf(
        _root_ide_package_.com.example.devicelens.presentation.device.components.InfoItem(
            label = "Rear Camera",
            value = camera.rear ?: "Not available"
        ),
        _root_ide_package_.com.example.devicelens.presentation.device.components.InfoItem(
            label = "Front Camera",
            value = camera.front ?: "Not available"
        ),
        _root_ide_package_.com.example.devicelens.presentation.device.components.InfoItem(
            label = "Video",
            value = camera.video ?: "Not available"
        )
    )
}

fun getBatteryItems(
    deviceInfo: DeviceInfo
): List<com.example.devicelens.presentation.device.components.InfoItem> {

    val battery = deviceInfo.battery

    return listOf(
        _root_ide_package_.com.example.devicelens.presentation.device.components.InfoItem(
            label = "Battery Level",
            value = battery.percent?.let {
                "$it%"
            } ?: "N/A"
        ),
        _root_ide_package_.com.example.devicelens.presentation.device.components.InfoItem(
            label = "Status",
            value = chargingStatusText(
                battery.isCharging,
                battery.status
            )
        ),
        _root_ide_package_.com.example.devicelens.presentation.device.components.InfoItem(
            label = "Temperature",
            value = battery.temperatureC?.let {
                "%.0f°C".format(it)
            } ?: "Not available"
        ),
        _root_ide_package_.com.example.devicelens.presentation.device.components.InfoItem(
            label = "Capacity",
            value = battery.capacityMah?.let {
                "$it mAh"
            } ?: "Not available"
        )
    )
}

private fun formatSecurityPatch(
    raw: String?
): String {

    if (raw.isNullOrBlank()) {
        return "Not available"
    }

    return try {
        val parsed = SimpleDateFormat(
            "yyyy-MM-dd",
            Locale.US
        ).parse(raw) ?: return raw

        SimpleDateFormat(
            "MMMM yyyy",
            Locale.getDefault()
        ).format(parsed)

    } catch (_: Exception) {
        raw
    }
}

private fun chargingStatusText(
    isCharging: Boolean,
    status: BatteryStatus
): String {
    return when {
        status == BatteryStatus.FULL ->
            "Fully charged"

        isCharging || status == BatteryStatus.CHARGING ->
            "Charging"

        status == BatteryStatus.UNKNOWN ->
            "Not available"

        else ->
            "Not charging"
    }
}