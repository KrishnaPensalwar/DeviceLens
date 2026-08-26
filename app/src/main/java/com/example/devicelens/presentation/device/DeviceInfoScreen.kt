package com.example.devicelens.presentation.device

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.devicelens.core.util.FileSizeFormatter
import com.example.devicelens.domain.model.BatteryStatus
import com.example.devicelens.domain.model.DeviceInfo
import com.example.devicelens.domain.model.DeviceType
import java.text.SimpleDateFormat
import java.util.Locale

@Composable
fun DeviceInfoScreen(
    viewModel: DeviceInfoViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val error = uiState.error
    val deviceInfo = uiState.deviceInfo

    when {
        uiState.isLoading -> {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }

        error != null -> {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = error,
                        color = MaterialTheme.colorScheme.error
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(onClick = viewModel::loadDeviceInfo) {
                        Text("Try again")
                    }
                }
            }
        }

        deviceInfo != null -> {
            DeviceInfoContent(deviceInfo)
        }
    }
}

@Composable
private fun DeviceInfoContent(deviceInfo: DeviceInfo) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Text(
                text = "Device Info",
                style = MaterialTheme.typography.headlineSmall
            )
        }

        item {
            InfoCard(title = "My Device") {
                InfoRow("Manufacturer", deviceInfo.manufacturer)
                InfoRow("Model", deviceInfo.model)
                InfoRow("Device name", deviceInfo.deviceName)
                InfoRow(
                    "Device type",
                    if (deviceInfo.deviceType == DeviceType.TABLET) "Tablet" else "Phone"
                )
            }
        }

        item {
            InfoCard(title = "Android") {
                InfoRow("Android version", "Android ${deviceInfo.androidVersion}")
                InfoRow(
                    "Security update",
                    formatSecurityPatch(deviceInfo.securityPatch)
                )
            }
        }

        item {
            val memory = deviceInfo.memory
            InfoCard(title = "Memory") {
                UsageSummary(
                    usedLabel = FileSizeFormatter.format(memory.usedBytes),
                    totalLabel = FileSizeFormatter.format(memory.totalBytes),
                    percent = memory.usagePercent,
                    status = memoryStatus(memory.usagePercent)
                )
                InfoRow("Available", FileSizeFormatter.format(memory.availableBytes))
            }
        }

        item {
            val storage = deviceInfo.storage
            InfoCard(title = "Storage") {
                UsageSummary(
                    usedLabel = FileSizeFormatter.format(storage.usedBytes),
                    totalLabel = FileSizeFormatter.format(storage.totalBytes),
                    percent = storage.usagePercent,
                    status = storageStatus(storage.usagePercent)
                )
                InfoRow("Available", FileSizeFormatter.format(storage.availableBytes))
            }
        }

        item {
            val display = deviceInfo.display
            InfoCard(title = "Display") {
                InfoRow(
                    "Screen size",
                    display.screenSizeInches?.let { "%.1f inches".format(it) } ?: "Not available"
                )
                InfoRow(
                    "Screen resolution",
                    "${display.resolutionWidth} × ${display.resolutionHeight}"
                )
                InfoRow("Refresh rate", "${display.refreshRateHz} Hz")
            }
        }

        item {
            val battery = deviceInfo.battery
            InfoCard(title = "Battery") {
                InfoRow(
                    "Battery",
                    batteryPercentText(battery.percent, battery.isCharging, battery.status)
                )
                InfoRow("Charging status", chargingStatusText(battery.isCharging, battery.status))
                InfoRow(
                    "Temperature",
                    battery.temperatureC?.let { temperatureText(it) } ?: "Not available"
                )
                InfoRow(
                    "Battery capacity",
                    battery.capacityMah?.let { "$it mAh" } ?: "Not available"
                )
            }
        }

        item {
            InfoCard(title = "Sensors") {
                deviceInfo.sensors.forEach { sensor ->
                    InfoRow(
                        sensor.name,
                        if (sensor.isAvailable) "Available" else "Not available"
                    )
                }
            }
        }
    }
}

@Composable
private fun InfoCard(
    title: String,
    content: @Composable () -> Unit
) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium
            )
            content()
        }
    }
}

@Composable
private fun InfoRow(
    label: String,
    value: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.padding(end = 12.dp)
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyLarge
        )
    }
}

@Composable
private fun UsageSummary(
    usedLabel: String,
    totalLabel: String,
    percent: Int,
    status: String
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(
            text = "$usedLabel of $totalLabel used — $status",
            style = MaterialTheme.typography.bodyLarge
        )
        LinearProgressIndicator(
            progress = { percent / 100f },
            modifier = Modifier.fillMaxWidth()
        )
        InfoRow("Used", "$percent%")
    }
}

private fun memoryStatus(percent: Int): String {
    return when {
        percent >= 85 -> "Memory usage is high"
        percent >= 70 -> "Memory usage is moderate"
        else -> "Memory usage is normal"
    }
}

private fun storageStatus(percent: Int): String {
    return when {
        percent >= 90 -> "Storage is almost full"
        percent >= 75 -> "Storage is getting full"
        else -> "Plenty of space"
    }
}

private fun chargingStatusText(
    isCharging: Boolean,
    status: BatteryStatus
): String {
    return when {
        status == BatteryStatus.FULL -> "Fully charged"
        isCharging || status == BatteryStatus.CHARGING -> "Charging"
        status == BatteryStatus.UNKNOWN -> "Not available"
        else -> "Not charging"
    }
}

private fun batteryPercentText(
    percent: Int?,
    isCharging: Boolean,
    status: BatteryStatus
): String {
    if (percent == null) return "Not available"
    val charge = chargingStatusText(isCharging, status)
    return "$percent% — $charge"
}

private fun temperatureText(celsius: Float): String {
    val rounded = "%.0f".format(celsius)
    val status = when {
        celsius >= 40f -> "Warm — keep it cool"
        celsius >= 35f -> "A bit warm"
        celsius <= 10f -> "Cold"
        else -> "Normal temperature"
    }
    return "$rounded°C — $status"
}

private fun formatSecurityPatch(raw: String?): String {
    if (raw.isNullOrBlank()) return "Not available"
    return try {
        val parsed = SimpleDateFormat("yyyy-MM-dd", Locale.US).parse(raw)
            ?: return raw
        SimpleDateFormat("MMMM yyyy", Locale.getDefault()).format(parsed)
    } catch (_: Exception) {
        raw
    }
}
