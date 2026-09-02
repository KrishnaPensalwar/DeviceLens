package com.example.devicelens.presentation.network

import androidx.compose.foundation.background
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
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.outlined.ArrowDownward
import androidx.compose.material.icons.outlined.ArrowUpward
import androidx.compose.material.icons.outlined.Speed
import androidx.compose.material.icons.outlined.SwapHoriz
import androidx.compose.material.icons.outlined.Warning
import androidx.compose.material.icons.outlined.Wifi
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.devicelens.domain.model.ConnectionType
import com.example.devicelens.domain.model.NetworkInfo
import com.example.devicelens.domain.model.SignalQuality
import com.example.devicelens.presentation.components.GlowProgressBar
import com.example.devicelens.presentation.components.LensCard
import com.example.devicelens.presentation.components.clickableWithoutRipple
import com.example.devicelens.presentation.device.DeviceInfoTopBar
import com.example.devicelens.ui.theme.LensCyan
import com.example.devicelens.ui.theme.LensGreen
import com.example.devicelens.ui.theme.LensMint
import com.example.devicelens.ui.theme.LensOnAccent
import com.example.devicelens.ui.theme.LensOrange
import com.example.devicelens.ui.theme.LensTextMuted
import com.example.devicelens.ui.theme.LensTextPrimary
import com.example.devicelens.ui.theme.LensTextSecondary

@Composable
fun NetworkScreen(
    onBack: () -> Unit = {},
    viewModel: NetworkViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Column(

    ) {
        when {
            uiState.isLoading -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = LensCyan)
                }
            }

            uiState.error != null -> {
                NetworkError(
                    message = uiState.error,
                    onRetry = viewModel::testNetwork
                )
            }

            else -> {
                val info = uiState.networkInfo ?: NetworkInfo()
                NetworkContent(
                    info = info,
                    isTesting = uiState.isTesting,
                    onTestClick = viewModel::testNetwork
                ){
                    onBack()
                }
            }
        }
    }
}

@Composable
private fun NetworkContent(
    info: NetworkInfo,
    isTesting: Boolean,
    onTestClick: () -> Unit,
    onBack: () -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = androidx.compose.foundation.layout.PaddingValues(
            start = 20.dp,
            end = 20.dp,
            top = 4.dp,
            bottom = 16.dp
        ),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {

        item {
            Column {
                DeviceInfoTopBar(title = "Network Diagnostics"){
                    onBack()
                }

                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "Real-time analysis of active connections and network performance.",
                    color = LensTextSecondary,
                    fontSize = 14.sp,
                    lineHeight = 20.sp
                )
            }
        }
        item { WifiStatusCard(info) }
        item {
            SpeedTestCard(
                info = info,
                isTesting = isTesting,
                onTestClick = onTestClick
            )
        }
    }
}

@Composable
private fun WifiStatusCard(info: NetworkInfo) {
    val connected = info.isInternetAvailable
    val ssid = info.networkName
        ?.removeSurrounding("\"")
        ?.takeIf { it.isNotBlank() && it != "<unknown ssid>" }
        ?: when (info.connectionType) {
            ConnectionType.WIFI -> "Wi-Fi"
            ConnectionType.MOBILE -> "Mobile Data"
            ConnectionType.ETHERNET -> "Ethernet"
            ConnectionType.VPN -> "VPN"
            else -> "—"
        }
    val score = info.qualityScore.coerceIn(0, 100)
    val qualityLabel = when {
        !connected -> "Offline"
        score >= 85 -> "Excellent"
        score >= 70 -> "Good"
        score >= 50 -> "Fair"
        else -> "Poor"
    }
    val signalLabel = when (info.signalQuality) {
        SignalQuality.EXCELLENT -> "Excellent"
        SignalQuality.GOOD -> "Good"
        SignalQuality.FAIR -> "Fair"
        SignalQuality.POOR -> "Poor"
        SignalQuality.UNKNOWN -> "—"
    }
    val signalProgress = info.signalDbm?.let { ((it + 100) / 50f).coerceIn(0f, 1f) } ?: 0f
    val dbm = info.signalDbm?.let { "$it dBm" } ?: "—"

    LensCard {
        Column(modifier = Modifier.padding(18.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Outlined.Wifi,
                    contentDescription = null,
                    tint = LensCyan,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Wi-Fi Status",
                    color = LensTextPrimary,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.weight(1f)
                )
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(LensGreen.copy(alpha = 0.18f))
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = if (connected) "Connected" else "Offline",
                        color = if (connected) LensGreen else LensOrange,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
            InfoRow("SSID", ssid)
            InfoRow("IP Address", info.ipAddress ?: "—")
            InfoRow(
                "Link Speed",
                info.downloadMbps?.let { "${it.toInt()} Mbps" } ?: "—"
            )
            InfoRow("Security", "—")

            Spacer(modifier = Modifier.height(16.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "Network Quality",
                    color = LensTextSecondary,
                    fontSize = 13.sp,
                    modifier = Modifier.weight(1f)
                )
                Text(
                    text = "$score/100",
                    color = LensCyan,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.width(8.dp))
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(LensCyan.copy(alpha = 0.16f))
                        .padding(horizontal = 8.dp, vertical = 3.dp)
                ) {
                    Text(
                        text = qualityLabel,
                        color = LensCyan,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Signal Strength ($dbm)",
                color = LensTextSecondary,
                fontSize = 13.sp
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                GlowProgressBar(
                    progress = signalProgress,
                    colors = listOf(LensMint, LensGreen),
                    modifier = Modifier.weight(1f),
                    height = 10.dp
                )
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = signalLabel,
                    color = LensGreen,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
private fun SpeedTestCard(
    info: NetworkInfo,
    isTesting: Boolean,
    onTestClick: () -> Unit
) {
    LensCard {
        Column(modifier = Modifier.padding(18.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Outlined.Speed,
                    contentDescription = null,
                    tint = LensTextPrimary,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Speed Test",
                    color = LensTextPrimary,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.weight(1f)
                )
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(LensCyan)
                        .clickableWithoutRipple(onClick = onTestClick)
                        .padding(horizontal = 12.dp, vertical = 7.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (isTesting) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(14.dp),
                            strokeWidth = 2.dp,
                            color = LensOnAccent
                        )
                    } else {
                        Icon(
                            imageVector = Icons.Filled.PlayArrow,
                            contentDescription = null,
                            tint = LensOnAccent,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = if (isTesting) "Running" else "Run Test",
                        color = LensOnAccent,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
            SpeedRow(
                accent = LensCyan,
                icon = Icons.Outlined.ArrowDownward,
                iconTint = LensCyan,
                label = "Download",
                value = info.downloadMbps?.let { "${it.toInt()} Mbps" } ?: "—"
            )
            SpeedRow(
                accent = LensOrange,
                icon = Icons.Outlined.ArrowUpward,
                iconTint = LensOrange,
                label = "Upload",
                value = info.uploadMbps?.let { "${it.toInt()} Mbps" } ?: "—"
            )
            SpeedRow(
                accent = LensTextPrimary,
                icon = Icons.Outlined.SwapHoriz,
                iconTint = LensTextPrimary,
                label = "Latency",
                value = info.latencyMs?.let { "$it ms" } ?: "—"
            )
        }
    }
}

@Composable
private fun SpeedRow(
    accent: Color,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    iconTint: Color,
    label: String,
    value: String
) {
    Column {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(2.dp)
                .background(accent.copy(alpha = 0.55f))
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = iconTint,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(10.dp))
            Text(
                text = label,
                color = LensTextSecondary,
                fontSize = 14.sp,
                modifier = Modifier.weight(1f)
            )
            Text(
                text = value,
                color = LensTextPrimary,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
private fun InfoRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp)
    ) {
        Text(
            text = label,
            color = LensTextMuted,
            fontSize = 13.sp,
            modifier = Modifier.weight(1f)
        )
        Text(
            text = value,
            color = LensTextPrimary,
            fontSize = 13.sp,
            fontFamily = FontFamily.Monospace,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
private fun NetworkError(
    message: String?,
    onRetry: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Outlined.Warning,
            contentDescription = null,
            tint = LensOrange,
            modifier = Modifier.size(40.dp)
        )
        Spacer(modifier = Modifier.height(12.dp))
        Text("Unable to load network", color = LensTextPrimary, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(6.dp))
        Text(message ?: "Something went wrong", color = LensTextSecondary)
        Spacer(modifier = Modifier.height(16.dp))
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(12.dp))
                .background(LensCyan)
                .clickableWithoutRipple(onRetry)
                .padding(horizontal = 18.dp, vertical = 10.dp)
        ) {
            Text("Try Again", color = LensOnAccent, fontWeight = FontWeight.Bold)
        }
    }
}
