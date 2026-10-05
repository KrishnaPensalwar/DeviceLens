package com.example.devicelens.presentation.dashboard

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Apps
import androidx.compose.material.icons.outlined.BatteryChargingFull
import androidx.compose.material.icons.outlined.BatteryFull
import androidx.compose.material.icons.outlined.HealthAndSafety
import androidx.compose.material.icons.outlined.Memory
import androidx.compose.material.icons.outlined.Smartphone
import androidx.compose.material.icons.outlined.Storage
import androidx.compose.material.icons.outlined.Thermostat
import androidx.compose.material.icons.outlined.Wifi
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.devicelens.core.util.FileSizeFormatter
import com.example.devicelens.domain.model.DeviceInfo
import com.example.devicelens.domain.model.HealthStatus
import com.example.devicelens.presentation.components.vitalCard.VitalCard
import com.example.devicelens.presentation.components.clickableWithoutRipple
import com.example.devicelens.presentation.components.vitalCard.VitalCardModel
import com.example.devicelens.presentation.device.DeviceInfoViewModel
import com.example.devicelens.presentation.health.HealthViewModel
import com.example.devicelens.ui.theme.LensBackground
import com.example.devicelens.ui.theme.LensBlue
import com.example.devicelens.ui.theme.LensBorder
import com.example.devicelens.ui.theme.LensGreen
import com.example.devicelens.ui.theme.LensOnAccent
import com.example.devicelens.ui.theme.LensOrange
import com.example.devicelens.ui.theme.LensPurple
import com.example.devicelens.ui.theme.LensSurfaceAlt
import com.example.devicelens.ui.theme.LensTextMuted
import com.example.devicelens.ui.theme.LensTextPrimary
import kotlin.math.min

@Composable
fun DashboardScreen(
    onDeviceInfoClick: () -> Unit,
    onAppsClick: () -> Unit,
    onNetworkClick: () -> Unit,
    onStorageClick: () -> Unit,
    onHealthClick: () -> Unit,
    onUsageClick: () -> Unit,
    deviceViewModel: DeviceInfoViewModel = hiltViewModel(),
    healthViewModel: HealthViewModel = hiltViewModel()
) {
    val deviceState by deviceViewModel.uiState.collectAsStateWithLifecycle()
    val healthState by healthViewModel.uiState.collectAsStateWithLifecycle()
    val device = deviceState.deviceInfo
    val score = healthState.report?.score ?: 87
    val statusLabel = when (healthState.report?.status) {
        HealthStatus.EXCELLENT, HealthStatus.GOOD, null -> "Good"
        HealthStatus.NEEDS_ATTENTION -> "Fair"
        HealthStatus.POOR -> "Poor"
    }
    val deviceName = device?.let { "${it.manufacturer} ${it.model}" } ?: "Device"
    val subtitle = device?.let { "Android ${it.androidVersion} · ${it.hardware}" } ?: "Android"

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(LensBackground),
        contentPadding = PaddingValues(
            start = 8.dp,
            end = 16.dp,
            bottom = 24.dp
        ),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            DeviceNameCard(deviceName, subtitle)
        }
        item {
            DashboardHealthCard(statusLabel, onHealthClick, score)
        }
        item {
            VitalCardsSection(device)
        }
        item {
            ExploreSection(
                onDeviceInfoClick,
                onAppsClick,
                onStorageClick,
                onNetworkClick,
                onUsageClick,
                onHealthClick
            )
        }
    }
}

@Composable
fun DeviceNameCard(deviceName: String, subtitle: String) {
    Column(modifier = Modifier.padding(horizontal = 8.dp)) {
        Text(
            deviceName,
            color = LensTextPrimary,
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold
        )
        Text(
            subtitle,
            color = LensTextMuted,
            fontSize = 15.sp,
            modifier = Modifier.padding(top = 2.dp, bottom = 6.dp)
        )
    }
}
