package com.example.devicelens.presentation.device

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.BatteryStd
import androidx.compose.material.icons.outlined.Devices
import androidx.compose.material.icons.outlined.Memory
import androidx.compose.material.icons.outlined.Security
import androidx.compose.material.icons.outlined.Sensors
import androidx.compose.material.icons.outlined.Smartphone
import androidx.compose.material.icons.outlined.Storage
import androidx.compose.material.icons.outlined.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.devicelens.core.util.FileSizeFormatter
import com.example.devicelens.domain.model.BatteryStatus
import com.example.devicelens.domain.model.DeviceInfo
import com.example.devicelens.domain.model.DeviceType
import io.github.sanketnawghare.glassify.compose.GlassButton
import io.github.sanketnawghare.glassify.compose.GlassStyle
import io.github.sanketnawghare.glassify.compose.glassify
import java.text.SimpleDateFormat
import java.util.Locale


@Composable
fun DeviceInfoScreen(
    viewModel: DeviceInfoViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    when {
        uiState.isLoading -> {
            DeviceInfoLoading()
        }

        uiState.error != null -> {
            DeviceInfoError(
                error = uiState.error,
                onRetry = viewModel::loadDeviceInfo
            )
        }

        uiState.deviceInfo != null -> {
            DeviceInfoContent(
                deviceInfo = uiState.deviceInfo
            )
        }
    }
}


// ================================================================
// CONTENT
// ================================================================

@Composable
private fun DeviceInfoContent(
    deviceInfo: DeviceInfo?
) {
    if (deviceInfo == null) return

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),

        contentPadding = PaddingValues(
            start = 20.dp,
            end = 20.dp,
            top = 24.dp,
            bottom = 32.dp
        ),

        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {

        // Header
        item {
            DeviceInfoHeader()
        }

        // Device
        item {
            DeviceIdentityCard(deviceInfo)
        }

        // Android
        item {
            AndroidCard(deviceInfo)
        }

        // Memory
        item {
            MemoryCard(deviceInfo)
        }

        // Storage
        item {
            StorageCard(deviceInfo)
        }

        // Display
        item {
            DisplayCard(deviceInfo)
        }

        // Battery
        item {
            BatteryCard(deviceInfo)
        }

        // Sensors
        item {
            SensorsCard(deviceInfo)
        }
    }
}


// ================================================================
// HEADER
// ================================================================

@Composable
private fun DeviceInfoHeader() {

    Column {

        Text(
            text = "Device Info",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground
        )

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = "Everything you need to know about your device",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}


// ================================================================
// DEVICE CARD
// ================================================================

@Composable
private fun DeviceIdentityCard(
    deviceInfo: DeviceInfo
) {
    GlassCard(
        icon = Icons.Outlined.Smartphone,
        title = "My Device",
        subtitle = "Device identity"
    ) {

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {

            Column(
                modifier = Modifier.weight(1f)
            ) {

                Text(
                    text = deviceInfo.manufacturer,
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(2.dp))

                Text(
                    text = deviceInfo.model,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Spacer(modifier = Modifier.height(2.dp))

                Text(
                    text = deviceInfo.deviceName,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {

            DeviceBadge(
                text = if (deviceInfo.deviceType == DeviceType.TABLET) {
                    "Tablet"
                } else {
                    "Phone"
                }
            )

            DeviceBadge(
                text = "Android ${deviceInfo.androidVersion}"
            )
        }
    }
}


// ================================================================
// ANDROID CARD
// ================================================================

@Composable
private fun AndroidCard(
    deviceInfo: DeviceInfo
) {
    GlassCard(
        icon = Icons.Outlined.Security,
        title = "Android",
        subtitle = "Software & security"
    ) {

        InfoRow(
            label = "Android version",
            value = "Android ${deviceInfo.androidVersion}"
        )

        InfoDivider()

        InfoRow(
            label = "Security update",
            value = formatSecurityPatch(deviceInfo.securityPatch)
        )
    }
}


// ================================================================
// MEMORY CARD
// ================================================================

@Composable
private fun MemoryCard(
    deviceInfo: DeviceInfo
) {
    val memory = deviceInfo.memory

    GlassCard(
        icon = Icons.Outlined.Memory,
        title = "Memory",
        subtitle = "RAM usage"
    ) {

        UsageHeader(
            used = FileSizeFormatter.format(memory.usedBytes),
            total = FileSizeFormatter.format(memory.totalBytes),
            percent = memory.usagePercent
        )

        Spacer(modifier = Modifier.height(14.dp))

        UsageProgress(
            percent = memory.usagePercent
        )

        Spacer(modifier = Modifier.height(14.dp))

        InfoRow(
            label = "Available",
            value = FileSizeFormatter.format(memory.availableBytes)
        )

        Spacer(modifier = Modifier.height(12.dp))

        StatusText(
            text = memoryStatus(memory.usagePercent)
        )
    }
}


// ================================================================
// STORAGE CARD
// ================================================================

@Composable
private fun StorageCard(
    deviceInfo: DeviceInfo
) {
    val storage = deviceInfo.storage

    GlassCard(
        icon = Icons.Outlined.Storage,
        title = "Storage",
        subtitle = "Internal storage usage"
    ) {

        UsageHeader(
            used = FileSizeFormatter.format(storage.usedBytes),
            total = FileSizeFormatter.format(storage.totalBytes),
            percent = storage.usagePercent
        )

        Spacer(modifier = Modifier.height(14.dp))

        UsageProgress(
            percent = storage.usagePercent
        )

        Spacer(modifier = Modifier.height(14.dp))

        InfoRow(
            label = "Available",
            value = FileSizeFormatter.format(storage.availableBytes)
        )

        Spacer(modifier = Modifier.height(12.dp))

        StatusText(
            text = storageStatus(storage.usagePercent)
        )
    }
}


// ================================================================
// DISPLAY CARD
// ================================================================

@Composable
private fun DisplayCard(
    deviceInfo: DeviceInfo
) {
    val display = deviceInfo.display

    GlassCard(
        icon = Icons.Outlined.Devices,
        title = "Display",
        subtitle = "Screen specifications"
    ) {

        InfoRow(
            label = "Screen size",
            value = display.screenSizeInches?.let {
                "%.1f inches".format(it)
            } ?: "Not available"
        )

        InfoDivider()

        InfoRow(
            label = "Resolution",
            value = "${display.resolutionWidth} × ${display.resolutionHeight}"
        )

        InfoDivider()

        InfoRow(
            label = "Refresh rate",
            value = "${display.refreshRateHz} Hz"
        )
    }
}


// ================================================================
// BATTERY CARD
// ================================================================

@Composable
private fun BatteryCard(
    deviceInfo: DeviceInfo
) {
    val battery = deviceInfo.battery

    GlassCard(
        icon = Icons.Outlined.BatteryStd,
        title = "Battery",
        subtitle = "Power & charging"
    ) {

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {

            Column(
                modifier = Modifier.weight(1f)
            ) {

                Text(
                    text = battery.percent?.let {
                        "$it%"
                    } ?: "N/A",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Text(
                    text = chargingStatusText(
                        battery.isCharging,
                        battery.status
                    ),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            BatteryStatusIndicator(
                isCharging = battery.isCharging
            )
        }

        Spacer(modifier = Modifier.height(18.dp))

        InfoRow(
            label = "Temperature",
            value = battery.temperatureC?.let {
                temperatureText(it)
            } ?: "Not available"
        )

        InfoDivider()

        InfoRow(
            label = "Capacity",
            value = battery.capacityMah?.let {
                "$it mAh"
            } ?: "Not available"
        )
    }
}


// ================================================================
// SENSORS CARD
// ================================================================

@Composable
private fun SensorsCard(
    deviceInfo: DeviceInfo
) {
    GlassCard(
        icon = Icons.Outlined.Sensors,
        title = "Sensors",
        subtitle = "${deviceInfo.sensors.size} sensors detected"
    ) {

        deviceInfo.sensors.forEachIndexed { index, sensor ->

            SensorRow(
                name = sensor.name,
                isAvailable = sensor.isAvailable
            )

            if (index != deviceInfo.sensors.lastIndex) {
                Spacer(modifier = Modifier.height(12.dp))
            }
        }
    }
}


// ================================================================
// GLASS CARD
// ================================================================

@Composable
private fun GlassCard(
    icon: ImageVector,
    title: String,
    subtitle: String,
    content: @Composable ColumnScope.() -> Unit
) {
    GlassButton(
        onClick = {},
        modifier = Modifier
            .fillMaxWidth()
            .glassify(
                style = GlassStyle.Thick
            )
    ) {

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
        ) {

            // -----------------------------------------------------
            // CARD HEADER
            // -----------------------------------------------------

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {

                Box(
                    modifier = Modifier
                        .size(42.dp)
                        .clip(RoundedCornerShape(13.dp))
                        .background(
                            MaterialTheme.colorScheme.primaryContainer
                        ),
                    contentAlignment = Alignment.Center
                ) {

                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        modifier = Modifier.size(22.dp),
                        tint = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }

                Spacer(modifier = Modifier.size(12.dp))

                Column(
                    modifier = Modifier.weight(1f)
                ) {

                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Text(
                        text = subtitle,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            content()
        }
    }
}


// ================================================================
// USAGE HEADER
// ================================================================

@Composable
private fun UsageHeader(
    used: String,
    total: String,
    percent: Int
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.Bottom
    ) {

        Column(
            modifier = Modifier.weight(1f)
        ) {

            Text(
                text = used,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )

            Text(
                text = "of $total used",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        Text(
            text = "$percent%",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )
    }
}


// ================================================================
// PROGRESS
// ================================================================

@Composable
private fun UsageProgress(
    percent: Int
) {
    LinearProgressIndicator(
        progress = {
            percent.coerceIn(0, 100) / 100f
        },
        modifier = Modifier
            .fillMaxWidth()
            .height(8.dp)
            .clip(CircleShape)
    )
}


// ================================================================
// SENSOR ROW
// ================================================================

@Composable
private fun SensorRow(
    name: String,
    isAvailable: Boolean
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {

        Box(
            modifier = Modifier
                .size(9.dp)
                .clip(CircleShape)
                .background(
                    if (isAvailable) {
                        MaterialTheme.colorScheme.primary
                    } else {
                        MaterialTheme.colorScheme.outline
                    }
                )
        )

        Spacer(modifier = Modifier.size(10.dp))

        Text(
            text = name,
            modifier = Modifier.weight(1f),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface
        )

        Text(
            text = if (isAvailable) {
                "Available"
            } else {
                "Unavailable"
            },
            style = MaterialTheme.typography.labelMedium,
            color = if (isAvailable) {
                MaterialTheme.colorScheme.primary
            } else {
                MaterialTheme.colorScheme.onSurfaceVariant
            }
        )
    }
}


// ================================================================
// BATTERY INDICATOR
// ================================================================

@Composable
private fun BatteryStatusIndicator(
    isCharging: Boolean
) {
    Box(
        modifier = Modifier
            .size(46.dp)
            .clip(RoundedCornerShape(15.dp))
            .background(
                MaterialTheme.colorScheme.primaryContainer
            ),
        contentAlignment = Alignment.Center
    ) {

        Icon(
            imageVector = Icons.Outlined.BatteryStd,
            contentDescription = null,
            modifier = Modifier.size(25.dp),
            tint = MaterialTheme.colorScheme.onPrimaryContainer
        )
    }
}


// ================================================================
// INFO ROW
// ================================================================

@Composable
private fun InfoRow(
    label: String,
    value: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {

        Text(
            text = label,
            modifier = Modifier.weight(1f),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.size(12.dp))

        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}


// ================================================================
// DIVIDER
// ================================================================

@Composable
private fun InfoDivider() {
    Spacer(modifier = Modifier.height(10.dp))
}


// ================================================================
// STATUS
// ================================================================

@Composable
private fun StatusText(
    text: String
) {
    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {

        Box(
            modifier = Modifier
                .size(7.dp)
                .clip(CircleShape)
                .background(
                    MaterialTheme.colorScheme.primary
                )
        )

        Spacer(modifier = Modifier.size(8.dp))

        Text(
            text = text,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}


// ================================================================
// BADGE
// ================================================================

@Composable
private fun DeviceBadge(
    text: String
) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(50))
            .background(
                MaterialTheme.colorScheme.surfaceVariant
            )
            .padding(
                horizontal = 10.dp,
                vertical = 5.dp
            )
    ) {

        Text(
            text = text,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}


// ================================================================
// LOADING
// ================================================================

@Composable
private fun DeviceInfoLoading() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentAlignment = Alignment.Center
    ) {

        GlassButton(
            onClick = {},
            modifier = Modifier
                .size(100.dp)
                .glassify(
                    style = GlassStyle.Thick
                )
        ) {

            CircularProgressIndicator(
                modifier = Modifier.size(32.dp)
            )
        }
    }
}


// ================================================================
// ERROR
// ================================================================

@Composable
private fun DeviceInfoError(
    error: String?,
    onRetry: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {

        GlassButton(
            onClick = {},
            modifier = Modifier
                .fillMaxWidth()
                .glassify(
                    style = GlassStyle.Thick
                )
        ) {

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                Icon(
                    imageVector = Icons.Outlined.Warning,
                    contentDescription = null,
                    modifier = Modifier.size(40.dp),
                    tint = MaterialTheme.colorScheme.error
                )

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "Unable to load device information",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = error ?: "Something went wrong",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(18.dp))

                Button(
                    onClick = onRetry
                ) {
                    Text("Try again")
                }
            }
        }
    }
}


// ================================================================
// HELPERS
// ================================================================

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

private fun temperatureText(
    celsius: Float
): String {
    val rounded = "%.0f".format(celsius)

    val status = when {
        celsius >= 40f -> "Warm — keep it cool"
        celsius >= 35f -> "A bit warm"
        celsius <= 10f -> "Cold"
        else -> "Normal temperature"
    }

    return "$rounded°C — $status"
}

private fun formatSecurityPatch(
    raw: String?
): String {
    if (raw.isNullOrBlank()) return "Not available"

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