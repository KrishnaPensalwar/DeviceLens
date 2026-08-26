package com.example.devicelens.domain.model

data class DeviceInfo(
    val manufacturer: String,
    val model: String,
    val deviceName: String,
    val deviceType: DeviceType,
    val androidVersion: String,
    val securityPatch: String?,
    val memory: MemorySnapshot,
    val storage: StorageSnapshot,
    val display: DisplayInfo,
    val battery: DeviceBatterySnapshot,
    val sensors: List<DeviceSensor>
)

enum class DeviceType {
    PHONE,
    TABLET
}

data class MemorySnapshot(
    val totalBytes: Long,
    val usedBytes: Long,
    val availableBytes: Long,
    val usagePercent: Int
)

data class StorageSnapshot(
    val totalBytes: Long,
    val usedBytes: Long,
    val availableBytes: Long,
    val usagePercent: Int
)

data class DisplayInfo(
    val screenSizeInches: Double?,
    val resolutionWidth: Int,
    val resolutionHeight: Int,
    val refreshRateHz: Int
)

data class DeviceBatterySnapshot(
    val percent: Int?,
    val isCharging: Boolean,
    val status: BatteryStatus,
    val temperatureC: Float?,
    val capacityMah: Int?
)

data class DeviceSensor(
    val name: String,
    val isAvailable: Boolean
)
