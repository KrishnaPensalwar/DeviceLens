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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.NetworkCheck
import androidx.compose.material.icons.filled.SignalCellularAlt
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.devicelens.domain.model.ConnectionType
import com.example.devicelens.domain.model.NetworkInfo
import com.example.devicelens.domain.model.SignalQuality

@Composable
fun NetworkScreen(
    viewModel: NetworkViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val error = uiState.error
    val networkInfo = uiState.networkInfo

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
            ErrorContent(
                message = error,
                onRetry = viewModel::loadNetwork
            )
        }

        networkInfo != null -> {
            NetworkContent(
                info = networkInfo,
                isTesting = uiState.isTesting,
                onTestClick = viewModel::testNetwork
            )
        }
    }
}

@Composable
private fun NetworkContent(
    info: NetworkInfo,
    isTesting: Boolean,
    onTestClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp)
    ) {
        Text(
            text = "Network",
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(modifier = Modifier.height(20.dp))

        ConnectionCard(info)

        Spacer(modifier = Modifier.height(16.dp))

        SpeedCard(info)

        Spacer(modifier = Modifier.height(16.dp))

        SignalCard(info)

        Spacer(modifier = Modifier.height(16.dp))

        NetworkQualityCard(info)

        Spacer(modifier = Modifier.height(20.dp))

        Button(
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
            enabled = !isTesting,
            onClick = onTestClick
        ) {
            if (isTesting) {
                CircularProgressIndicator(
                    modifier = Modifier.size(20.dp),
                    strokeWidth = 2.dp
                )
                Spacer(modifier = Modifier.width(10.dp))
                Text("Testing...")
            } else {
                Icon(
                    imageVector = Icons.Default.NetworkCheck,
                    contentDescription = null
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("Test My Network")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        NetworkTroubleshooting(info)
    }
}

@Composable
private fun ConnectionCard(info: NetworkInfo) {
    val icon = when (info.connectionType) {
        ConnectionType.WIFI -> Icons.Default.Wifi
        ConnectionType.MOBILE -> Icons.Default.SignalCellularAlt
        else -> Icons.Default.NetworkCheck
    }

    val connectionName = when (info.connectionType) {
        ConnectionType.WIFI ->
            info.networkName?.takeIf {
                it.isNotBlank() && it != "<unknown ssid>"
            } ?: "Wi-Fi"

        ConnectionType.MOBILE -> "Mobile Data"
        ConnectionType.ETHERNET -> "Ethernet"
        ConnectionType.VPN -> "VPN"
        ConnectionType.NONE -> "No Connection"
        ConnectionType.UNKNOWN -> "Unknown"
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(52.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primaryContainer),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary
                    )
                }

                Spacer(modifier = Modifier.width(16.dp))

                Column {
                    Text(
                        text = connectionName,
                        style = MaterialTheme.typography.titleLarge
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(
                                    if (info.isInternetAvailable) {
                                        MaterialTheme.colorScheme.primary
                                    } else {
                                        MaterialTheme.colorScheme.error
                                    }
                                )
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (info.isInternetAvailable) "Connected" else "No Internet",
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }

            if (info.ipAddress != null) {
                Spacer(modifier = Modifier.height(16.dp))
                HorizontalDivider()
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = "IP Address",
                    style = MaterialTheme.typography.labelMedium
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = info.ipAddress,
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        }
    }
}

@Composable
private fun SpeedCard(info: NetworkInfo) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Speed,
                    contentDescription = null
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Network Performance",
                    style = MaterialTheme.typography.titleMedium
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                SpeedItem(
                    title = "Download",
                    value = info.downloadMbps?.let { "%.1f Mbps".format(it) } ?: "--"
                )
                SpeedItem(
                    title = "Latency",
                    value = info.latencyMs?.let { "$it ms" } ?: "--"
                )
                SpeedItem(
                    title = "Upload",
                    value = info.uploadMbps?.let { "%.1f Mbps".format(it) } ?: "--"
                )
            }
        }
    }
}

@Composable
private fun SpeedItem(
    title: String,
    value: String
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = value,
            style = MaterialTheme.typography.titleMedium
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = title,
            style = MaterialTheme.typography.bodySmall
        )
    }
}

@Composable
private fun SignalCard(info: NetworkInfo) {
    val quality = when (info.signalQuality) {
        SignalQuality.EXCELLENT -> "Excellent"
        SignalQuality.GOOD -> "Good"
        SignalQuality.FAIR -> "Fair"
        SignalQuality.POOR -> "Poor"
        SignalQuality.UNKNOWN -> "Not available"
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text(
                text = "Signal",
                style = MaterialTheme.typography.titleMedium
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = quality,
                style = MaterialTheme.typography.headlineSmall
            )
            Spacer(modifier = Modifier.height(12.dp))
            if (info.signalDbm != null) {
                Text(
                    text = "${info.signalDbm} dBm",
                    style = MaterialTheme.typography.bodyMedium
                )
                Spacer(modifier = Modifier.height(8.dp))
                SignalProgress(info.signalDbm)
            }
        }
    }
}

@Composable
private fun SignalProgress(dbm: Int) {
    val progress = ((dbm + 100) / 50f).coerceIn(0f, 1f)
    LinearProgressIndicator(
        progress = { progress },
        modifier = Modifier.fillMaxWidth()
    )
}

@Composable
private fun NetworkQualityCard(info: NetworkInfo) {
    val title = when {
        !info.isInternetAvailable -> "No Internet"
        info.qualityScore >= 85 -> "Excellent"
        info.qualityScore >= 70 -> "Good"
        info.qualityScore >= 50 -> "Fair"
        else -> "Poor"
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Network Quality",
                style = MaterialTheme.typography.titleMedium
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = "${info.qualityScore}/100",
                style = MaterialTheme.typography.displaySmall
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium
            )
            Spacer(modifier = Modifier.height(12.dp))
            LinearProgressIndicator(
                progress = { info.qualityScore / 100f },
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
private fun NetworkTroubleshooting(info: NetworkInfo) {
    val message = when {
        !info.isInternetAvailable ->
            "Your device is connected to a network, but internet access is unavailable. Try reconnecting to Wi-Fi or switching to mobile data."

        info.signalQuality == SignalQuality.POOR ->
            "Your signal is weak. Try moving closer to your Wi-Fi router or moving to an area with better mobile coverage."

        info.latencyMs != null && info.latencyMs > 200 ->
            "Your connection has high latency. Online games and video calls may be affected."

        info.downloadMbps != null && info.downloadMbps < 5 ->
            "Your download speed is low. Streaming and large downloads may be slow."

        else -> "Your network looks healthy."
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        ),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Network insight",
                style = MaterialTheme.typography.titleSmall
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = message,
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

@Composable
private fun ErrorContent(
    message: String,
    onRetry: () -> Unit
) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(24.dp)
        ) {
            Text(
                text = "Unable to check network",
                style = MaterialTheme.typography.titleLarge
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = message,
                style = MaterialTheme.typography.bodyMedium
            )
            Spacer(modifier = Modifier.height(20.dp))
            Button(onClick = onRetry) {
                Text("Try Again")
            }
        }
    }
}
