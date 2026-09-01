package com.example.devicelens.presentation.dashboard

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Android
import androidx.compose.material.icons.outlined.BatteryStd
import androidx.compose.material.icons.outlined.GridView
import androidx.compose.material.icons.outlined.HealthAndSafety
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Memory
import androidx.compose.material.icons.outlined.Storage
import androidx.compose.material.icons.outlined.Thermostat
import androidx.compose.material.icons.outlined.Wifi
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.devicelens.core.util.FileSizeFormatter
import com.example.devicelens.domain.model.DeviceInfo
import com.example.devicelens.domain.model.HealthStatus
import com.example.devicelens.presentation.components.GlowProgressBar
import com.example.devicelens.presentation.components.HealthGauge
import com.example.devicelens.presentation.components.LensCard
import com.example.devicelens.presentation.components.LensScreen
import com.example.devicelens.presentation.components.LensTab
import com.example.devicelens.presentation.components.LensTopBar
import com.example.devicelens.presentation.components.clickableWithoutRipple
import com.example.devicelens.presentation.device.DeviceInfoViewModel
import com.example.devicelens.presentation.health.HealthViewModel
import com.example.devicelens.ui.theme.LensAmber
import com.example.devicelens.ui.theme.LensBlue
import com.example.devicelens.ui.theme.LensCoral
import com.example.devicelens.ui.theme.LensCyan
import com.example.devicelens.ui.theme.LensGreen
import com.example.devicelens.ui.theme.LensMint
import com.example.devicelens.ui.theme.LensOrange
import com.example.devicelens.ui.theme.LensPurple
import com.example.devicelens.ui.theme.LensSurfaceAlt
import com.example.devicelens.ui.theme.LensTextMuted
import com.example.devicelens.ui.theme.LensTextPrimary
import com.example.devicelens.ui.theme.LensTextSecondary

@Composable
fun DashboardScreen(
    onDeviceInfoClick: () -> Unit,
    onAppsClick: () -> Unit,
    onNetworkClick: () -> Unit,
    onStorageClick: () -> Unit,
    onHealthClick: () -> Unit,
    onUsageClick: () -> Unit,
    onTabSelected: (LensTab) -> Unit = {},
    deviceViewModel: DeviceInfoViewModel = hiltViewModel(),
    healthViewModel: HealthViewModel = hiltViewModel()
) {
    val deviceState by deviceViewModel.uiState.collectAsStateWithLifecycle()
    val healthState by healthViewModel.uiState.collectAsStateWithLifecycle()
    val device = deviceState.deviceInfo
    val score = healthState.report?.score ?: 87
    val statusLabel = when (healthState.report?.status) {
        HealthStatus.EXCELLENT -> "Excellent"
        HealthStatus.GOOD, null -> "Good"
        HealthStatus.NEEDS_ATTENTION -> "Fair"
        HealthStatus.POOR -> "Poor"
    }

    LensScreen(
        currentTab = LensTab.Diagnostics,
        onTabSelected = onTabSelected
    ) {
        LensTopBar()
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = androidx.compose.foundation.layout.PaddingValues(
                start = 20.dp,
                end = 20.dp,
                top = 4.dp,
                bottom = 16.dp
            ),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            item {
                HeroSection(
                    deviceName = device?.let { "${it.manufacturer} ${it.model}" } ?: "Google Pixel",
                    androidVersion = device?.androidVersion ?: "15",
                    score = score,
                    statusLabel = statusLabel
                )
            }
            item {
                QuickActionsGrid(
                    onDeviceInfoClick = onDeviceInfoClick,
                    onUsageClick = onUsageClick,
                    onBatteryClick = onAppsClick,
                    onStorageClick = onStorageClick,
                    onHealthClick = onHealthClick,
                    onNetworkClick = onNetworkClick
                )
            }
            item { BatteryMetricCard(device) }
            item { StorageMetricCard(device) }
            item { TemperatureMetricCard(device) }
            item { RamMetricCard(device) }
        }
    }
}

@Composable
private fun HeroSection(
    deviceName: String,
    androidVersion: String,
    score: Int,
    statusLabel: String
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = deviceName,
            color = LensTextPrimary,
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(10.dp))
        Row(
            modifier = Modifier
                .clip(RoundedCornerShape(20.dp))
                .background(LensSurfaceAlt)
                .border(1.dp, Color(0xFF2E2E2E), RoundedCornerShape(20.dp))
                .padding(horizontal = 12.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Outlined.Android,
                contentDescription = null,
                tint = LensTextPrimary,
                modifier = Modifier.size(14.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = "ANDROID $androidVersion",
                color = LensTextSecondary,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.2.sp
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        HealthGauge(score = score, label = statusLabel)
    }
}

@Composable
private fun QuickActionsGrid(
    onDeviceInfoClick: () -> Unit,
    onUsageClick: () -> Unit,
    onBatteryClick: () -> Unit,
    onStorageClick: () -> Unit,
    onHealthClick: () -> Unit,
    onNetworkClick: () -> Unit
) {
    val actions = listOf(
        QuickAction("Device Info", Icons.Outlined.Info, LensBlue, onDeviceInfoClick),
        QuickAction("App Usage", Icons.Outlined.GridView, LensCoral, onUsageClick),
        QuickAction("Battery Health", Icons.Outlined.BatteryStd, LensAmber, onBatteryClick),
        QuickAction("Storage Analyzer", Icons.Outlined.Storage, LensMint, onStorageClick),
        QuickAction("Device Health", Icons.Outlined.HealthAndSafety, LensGreen, onHealthClick),
        QuickAction("Network", Icons.Outlined.Wifi, LensPurple, onNetworkClick)
    )
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        actions.chunked(2).forEach { row ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                row.forEach { action ->
                    QuickActionCard(modifier = Modifier.weight(1f), action = action)
                }
            }
        }
    }
}

@Composable
private fun QuickActionCard(
    modifier: Modifier,
    action: QuickAction
) {
    Column(
        modifier = modifier
            .height(92.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(LensSurfaceAlt)
            .border(1.dp, Color(0xFF2E2E2E), RoundedCornerShape(16.dp))
            .clickableWithoutRipple(action.onClick)
            .padding(12.dp)
    ) {
        Icon(
            imageVector = action.icon,
            contentDescription = null,
            tint = action.color,
            modifier = Modifier.size(18.dp)
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = action.title,
            color = LensTextPrimary,
            fontSize = 13.sp,
            fontWeight = FontWeight.SemiBold
        )
        Spacer(modifier = Modifier.weight(1f))
        Text(
            text = "EXPLORE",
            color = LensTextMuted,
            fontSize = 9.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.1.sp
        )
    }
}

@Composable
private fun BatteryMetricCard(device: DeviceInfo?) {
    val percent = device?.battery?.percent ?: 85
    MetricCard(
        title = "Battery Health",
        watermark = Icons.Outlined.BatteryStd,
        watermarkTint = LensOrange.copy(alpha = 0.12f)
    ) {
        Text(
            text = "$percent%",
            color = LensTextPrimary,
            fontSize = 32.sp,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(10.dp))
        GlowProgressBar(
            progress = percent / 100f,
            colors = listOf(LensOrange, LensAmber)
        )
        Spacer(modifier = Modifier.height(8.dp))
        MetricFooter("Degradation", "Normal", LensAmber)
    }
}

@Composable
private fun StorageMetricCard(device: DeviceInfo?) {
    val percent = device?.storage?.usagePercent ?: 82
    val used = device?.storage?.usedBytes?.let(FileSizeFormatter::format) ?: "216GB"
    val total = device?.storage?.totalBytes?.let(FileSizeFormatter::format) ?: "256GB"
    MetricCard(
        title = "Storage",
        watermark = Icons.Outlined.Storage,
        watermarkTint = LensCyan.copy(alpha = 0.12f)
    ) {
        Text(
            text = "$percent%",
            color = LensTextPrimary,
            fontSize = 32.sp,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(10.dp))
        GlowProgressBar(
            progress = percent / 100f,
            colors = listOf(LensCyan, LensMint)
        )
        Spacer(modifier = Modifier.height(8.dp))
        MetricFooter("Used", "$used / $total", LensTextPrimary)
    }
}

@Composable
private fun TemperatureMetricCard(device: DeviceInfo?) {
    val temp = device?.battery?.temperatureC ?: 32f
    val progress = (temp / 55f).coerceIn(0.15f, 1f)
    val status = if (temp <= 40f) "Optimal" else "Warm"
    val statusColor = if (temp <= 40f) LensGreen else LensOrange
    MetricCard(
        title = "Temperature",
        watermark = Icons.Outlined.Thermostat,
        watermarkTint = LensGreen.copy(alpha = 0.12f)
    ) {
        Text(
            text = "${temp.toInt()} °C",
            color = LensTextPrimary,
            fontSize = 32.sp,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(10.dp))
        GlowProgressBar(
            progress = progress,
            colors = listOf(LensGreen, LensAmber, LensCoral)
        )
        Spacer(modifier = Modifier.height(8.dp))
        MetricFooter("Status", status, statusColor)
    }
}

@Composable
private fun RamMetricCard(device: DeviceInfo?) {
    val used = device?.memory?.usedBytes ?: 0L
    val total = device?.memory?.totalBytes ?: 0L
    val usedLabel = if (total > 0) FileSizeFormatter.format(used) else "5.2 GB"
    val totalLabel = if (total > 0) FileSizeFormatter.format(total) else "8GB"
    val percent = device?.memory?.usagePercent ?: 65
    MetricCard(
        title = "RAM Usage",
        watermark = Icons.Outlined.Memory,
        watermarkTint = LensPurple.copy(alpha = 0.14f)
    ) {
        Text(
            text = "$usedLabel / $totalLabel",
            color = LensTextPrimary,
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(10.dp))
        GlowProgressBar(
            progress = percent / 100f,
            colors = listOf(LensPurple, LensBlue)
        )
    }
}

@Composable
private fun MetricCard(
    title: String,
    watermark: ImageVector,
    watermarkTint: Color,
    content: @Composable androidx.compose.foundation.layout.ColumnScope.() -> Unit
) {
    LensCard {
        Box(modifier = Modifier.fillMaxWidth()) {
            Icon(
                imageVector = watermark,
                contentDescription = null,
                tint = watermarkTint,
                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .padding(end = 8.dp)
                    .size(88.dp)
                    .alpha(0.9f)
            )
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(18.dp)
            ) {
                Text(
                    text = title,
                    color = LensTextSecondary,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium
                )
                Spacer(modifier = Modifier.height(6.dp))
                content()
            }
        }
    }
}

@Composable
private fun MetricFooter(
    left: String,
    right: String,
    rightColor: Color
) {
    Row(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = left,
            color = LensTextMuted,
            fontSize = 11.sp,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.weight(1f)
        )
        Text(
            text = right,
            color = rightColor,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

private data class QuickAction(
    val title: String,
    val icon: ImageVector,
    val color: Color,
    val onClick: () -> Unit
)
