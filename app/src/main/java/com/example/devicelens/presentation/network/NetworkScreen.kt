package com.example.devicelens.presentation.network

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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Bolt
import androidx.compose.material.icons.outlined.NetworkCheck
import androidx.compose.material.icons.outlined.SignalCellularAlt
import androidx.compose.material.icons.outlined.Speed
import androidx.compose.material.icons.outlined.Wifi
import androidx.compose.material.icons.outlined.WifiTethering
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
import com.example.devicelens.domain.model.ConnectionType
import com.example.devicelens.domain.model.NetworkInfo
import com.example.devicelens.domain.model.SignalQuality
import io.github.sanketnawghare.glassify.compose.GlassButton
import io.github.sanketnawghare.glassify.compose.GlassStyle
import io.github.sanketnawghare.glassify.compose.glassify


@Composable
fun NetworkScreen(
    viewModel: NetworkViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    when {
        uiState.isLoading -> {
            NetworkLoading()
        }

        uiState.error != null -> {
            NetworkError(
                message = uiState.error,
                onRetry = viewModel::loadNetwork
            )
        }

        uiState.networkInfo != null -> {
            uiState.networkInfo?.let {
                NetworkContent(
                    info = it,
                    isTesting = uiState.isTesting,
                    onTestClick = viewModel::testNetwork
                )
            }
        }
    }
}


// ================================================================
// CONTENT
// ================================================================

@Composable
private fun NetworkContent(
    info: NetworkInfo,
    isTesting: Boolean,
    onTestClick: () -> Unit
) {

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(
                MaterialTheme.colorScheme.background
            ),

        contentPadding = PaddingValues(
            start = 20.dp,
            end = 20.dp,
            top = 24.dp,
            bottom = 32.dp
        ),

        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {

        // ---------------------------------------------------------
        // HEADER
        // ---------------------------------------------------------

        item {
            NetworkHeader()
        }

        // ---------------------------------------------------------
        // CONNECTION
        // ---------------------------------------------------------

        item {
            ConnectionCard(info)
        }

        // ---------------------------------------------------------
        // NETWORK QUALITY
        // ---------------------------------------------------------

        item {
            NetworkQualityCard(info)
        }

        // ---------------------------------------------------------
        // PERFORMANCE
        // ---------------------------------------------------------

        item {
            SpeedCard(info)
        }

        // ---------------------------------------------------------
        // SIGNAL
        // ---------------------------------------------------------

        item {
            SignalCard(info)
        }

        // ---------------------------------------------------------
        // TEST BUTTON
        // ---------------------------------------------------------

        item {
            NetworkTestButton(
                isTesting = isTesting,
                onClick = onTestClick
            )
        }

        // ---------------------------------------------------------
        // INSIGHT
        // ---------------------------------------------------------

        item {
            NetworkTroubleshooting(info)
        }
    }
}


// ================================================================
// HEADER
// ================================================================

@Composable
private fun NetworkHeader() {

    Column {

        Text(
            text = "Network",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground
        )

        Spacer(
            modifier = Modifier.height(4.dp)
        )

        Text(
            text = "Connection, speed and network health",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}


// ================================================================
// CONNECTION CARD
// ================================================================

@Composable
private fun ConnectionCard(
    info: NetworkInfo
) {

    val icon = when (info.connectionType) {
        ConnectionType.WIFI ->
            Icons.Outlined.Wifi

        ConnectionType.MOBILE ->
            Icons.Outlined.SignalCellularAlt

        ConnectionType.ETHERNET ->
            Icons.Outlined.NetworkCheck

        ConnectionType.VPN ->
            Icons.Outlined.WifiTethering

        else ->
            Icons.Outlined.NetworkCheck
    }

    val connectionName = when (info.connectionType) {

        ConnectionType.WIFI ->
            info.networkName
                ?.takeIf {
                    it.isNotBlank() &&
                            it != "<unknown ssid>"
                }
                ?: "Wi-Fi"

        ConnectionType.MOBILE ->
            "Mobile Data"

        ConnectionType.ETHERNET ->
            "Ethernet"

        ConnectionType.VPN ->
            "VPN"

        ConnectionType.NONE ->
            "No Connection"

        ConnectionType.UNKNOWN ->
            "Unknown"
    }

    GlassCard(
        icon = icon,
        title = "Connection",
        subtitle = connectionName
    ) {

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {

            ConnectionStatusIndicator(
                connected = info.isInternetAvailable
            )

            Spacer(
                modifier = Modifier.width(12.dp)
            )

            Column(
                modifier = Modifier.weight(1f)
            ) {

                Text(
                    text = if (info.isInternetAvailable) {
                        "Connected to Internet"
                    } else {
                        "No Internet Access"
                    },
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Spacer(
                    modifier = Modifier.height(3.dp)
                )

                Text(
                    text = connectionDescription(info),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        if (!info.ipAddress.isNullOrBlank()) {

            Spacer(
                modifier = Modifier.height(18.dp)
            )

            NetworkInfoRow(
                label = "IP Address",
                value = info.ipAddress
            )
        }
    }
}


// ================================================================
// CONNECTION STATUS
// ================================================================

@Composable
private fun ConnectionStatusIndicator(
    connected: Boolean
) {

    Box(
        modifier = Modifier
            .size(48.dp)
            .clip(RoundedCornerShape(15.dp))
            .background(
                if (connected) {
                    MaterialTheme.colorScheme.primaryContainer
                } else {
                    MaterialTheme.colorScheme.errorContainer
                }
            ),
        contentAlignment = Alignment.Center
    ) {

        Box(
            modifier = Modifier
                .size(11.dp)
                .clip(CircleShape)
                .background(
                    if (connected) {
                        MaterialTheme.colorScheme.primary
                    } else {
                        MaterialTheme.colorScheme.error
                    }
                )
        )
    }
}


// ================================================================
// NETWORK QUALITY
// ================================================================

@Composable
private fun NetworkQualityCard(
    info: NetworkInfo
) {

    val score = info.qualityScore.coerceIn(0, 100)

    val quality = when {

        !info.isInternetAvailable ->
            "No Internet"

        score >= 85 ->
            "Excellent"

        score >= 70 ->
            "Good"

        score >= 50 ->
            "Fair"

        else ->
            "Poor"
    }

    GlassCard(
        icon = Icons.Outlined.NetworkCheck,
        title = "Network Quality",
        subtitle = quality
    ) {

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {

            Column(
                modifier = Modifier.weight(1f)
            ) {

                Text(
                    text = "$score",
                    style = MaterialTheme.typography.displaySmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Text(
                    text = "out of 100",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Box(
                modifier = Modifier
                    .size(58.dp)
                    .clip(RoundedCornerShape(18.dp))
                    .background(
                        MaterialTheme.colorScheme.primaryContainer
                    ),
                contentAlignment = Alignment.Center
            ) {

                Text(
                    text = when {
                        score >= 85 -> "✓"
                        score >= 50 -> "~"
                        else -> "!"
                    },
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
        }

        Spacer(
            modifier = Modifier.height(18.dp)
        )

        LinearProgressIndicator(
            progress = {
                score / 100f
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp)
                .clip(CircleShape)
        )

        Spacer(
            modifier = Modifier.height(8.dp)
        )

        Text(
            text = qualityDescription(score, info.isInternetAvailable),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}


// ================================================================
// SPEED CARD
// ================================================================

@Composable
private fun SpeedCard(
    info: NetworkInfo
) {

    GlassCard(
        icon = Icons.Outlined.Speed,
        title = "Performance",
        subtitle = "Network speed and latency"
    ) {

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {

            NetworkMetric(
                icon = Icons.Outlined.Bolt,
                value = info.downloadMbps
                    ?.let { "%.1f".format(it) }
                    ?: "--",
                unit = "Mbps",
                label = "Download"
            )

            NetworkMetric(
                icon = Icons.Outlined.Speed,
                value = info.latencyMs
                    ?.toString()
                    ?: "--",
                unit = "ms",
                label = "Latency"
            )

            NetworkMetric(
                icon = Icons.Outlined.Bolt,
                value = info.uploadMbps
                    ?.let { "%.1f".format(it) }
                    ?: "--",
                unit = "Mbps",
                label = "Upload"
            )
        }
    }
}


// ================================================================
// NETWORK METRIC
// ================================================================

@Composable
private fun NetworkMetric(
    icon: ImageVector,
    value: String,
    unit: String,
    label: String
) {

    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(RoundedCornerShape(11.dp))
                .background(
                    MaterialTheme.colorScheme.surfaceVariant
                ),
            contentAlignment = Alignment.Center
        ) {

            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(19.dp),
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        Spacer(
            modifier = Modifier.height(8.dp)
        )

        Row(
            verticalAlignment = Alignment.Bottom
        ) {

            Text(
                text = value,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )

            if (value != "--") {

                Spacer(
                    modifier = Modifier.width(3.dp)
                )

                Text(
                    text = unit,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        Spacer(
            modifier = Modifier.height(3.dp)
        )

        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}


// ================================================================
// SIGNAL CARD
// ================================================================

@Composable
private fun SignalCard(
    info: NetworkInfo
) {

    val quality = when (info.signalQuality) {

        SignalQuality.EXCELLENT ->
            "Excellent"

        SignalQuality.GOOD ->
            "Good"

        SignalQuality.FAIR ->
            "Fair"

        SignalQuality.POOR ->
            "Poor"

        SignalQuality.UNKNOWN ->
            "Not available"
    }

    GlassCard(
        icon = Icons.Outlined.SignalCellularAlt,
        title = "Signal",
        subtitle = quality
    ) {

        if (info.signalDbm != null) {

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {

                Column(
                    modifier = Modifier.weight(1f)
                ) {

                    Text(
                        text = "${info.signalDbm} dBm",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Spacer(
                        modifier = Modifier.height(3.dp)
                    )

                    Text(
                        text = signalDescription(
                            info.signalDbm
                        ),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                SignalIndicator(
                    dbm = info.signalDbm
                )
            }

            Spacer(
                modifier = Modifier.height(16.dp)
            )

            LinearProgressIndicator(
                progress = {
                    signalProgress(
                        info.signalDbm
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(CircleShape)
            )

        } else {

            Text(
                text = "Signal information is not available for this connection.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}


// ================================================================
// SIGNAL INDICATOR
// ================================================================

@Composable
private fun SignalIndicator(
    dbm: Int
) {

    val progress = signalProgress(dbm)

    Box(
        modifier = Modifier
            .size(58.dp)
            .clip(RoundedCornerShape(18.dp))
            .background(
                MaterialTheme.colorScheme.primaryContainer
            ),
        contentAlignment = Alignment.Center
    ) {

        Text(
            text = "${(progress * 100).toInt()}%",
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onPrimaryContainer
        )
    }
}


// ================================================================
// TEST BUTTON
// ================================================================

@Composable
private fun NetworkTestButton(
    isTesting: Boolean,
    onClick: () -> Unit
) {

    GlassButton(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(58.dp)
            .glassify(
                style = GlassStyle.Thick
            )
    ) {

        Row(
            modifier = Modifier.fillMaxSize(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {

            if (isTesting) {

                CircularProgressIndicator(
                    modifier = Modifier.size(21.dp),
                    strokeWidth = 2.dp
                )

                Spacer(
                    modifier = Modifier.width(10.dp)
                )

                Text(
                    text = "Testing network..."
                )

            } else {

                Icon(
                    imageVector = Icons.Outlined.NetworkCheck,
                    contentDescription = null
                )

                Spacer(
                    modifier = Modifier.width(8.dp)
                )

                Text(
                    text = "Test My Network",
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}


// ================================================================
// TROUBLESHOOTING / INSIGHT
// ================================================================

@Composable
private fun NetworkTroubleshooting(
    info: NetworkInfo
) {

    val message = when {

        !info.isInternetAvailable ->
            "Your device is connected to a network, but internet access is unavailable. Try reconnecting or switching networks."

        info.signalQuality == SignalQuality.POOR ->
            "Your signal is weak. Try moving closer to your Wi-Fi router or moving to an area with better coverage."

        info.latencyMs != null &&
                info.latencyMs > 200 ->
            "Your connection has high latency. Online gaming and video calls may be affected."

        info.downloadMbps != null &&
                info.downloadMbps < 5 ->
            "Your download speed is low. Streaming and large downloads may be slower."

        else ->
            "Your network looks healthy. Connection quality and performance are within a good range."
    }

    GlassCard(
        icon = if (
            !info.isInternetAvailable ||
            info.signalQuality == SignalQuality.POOR
        ) {
            Icons.Outlined.Warning
        } else {
            Icons.Outlined.NetworkCheck
        },
        title = "Network Insight",
        subtitle = "Connection analysis"
    ) {

        Text(
            text = message,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface
        )
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
                .padding(18.dp)
        ) {

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

                Spacer(
                    modifier = Modifier.width(12.dp)
                )

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

            Spacer(
                modifier = Modifier.height(18.dp)
            )

            content()
        }
    }
}


// ================================================================
// NETWORK INFO ROW
// ================================================================

@Composable
private fun NetworkInfoRow(
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

        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}


// ================================================================
// LOADING
// ================================================================

@Composable
private fun NetworkLoading() {

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                MaterialTheme.colorScheme.background
            ),
        contentAlignment = Alignment.Center
    ) {

        GlassButton(
            onClick = {},
            modifier = Modifier
                .size(120.dp)
                .glassify(
                    style = GlassStyle.Thick
                )
        ) {

            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                CircularProgressIndicator(
                    modifier = Modifier.size(30.dp)
                )

                Spacer(
                    modifier = Modifier.height(10.dp)
                )

                Text(
                    text = "Loading...",
                    style = MaterialTheme.typography.labelMedium
                )
            }
        }
    }
}


// ================================================================
// ERROR
// ================================================================

@Composable
private fun NetworkError(
    message: String?,
    onRetry: () -> Unit
) {

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp),
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
                    modifier = Modifier.size(42.dp),
                    tint = MaterialTheme.colorScheme.error
                )

                Spacer(
                    modifier = Modifier.height(12.dp)
                )

                Text(
                    text = "Unable to load network",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )

                Spacer(
                    modifier = Modifier.height(6.dp)
                )

                Text(
                    text = message ?: "Something went wrong",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(
                    modifier = Modifier.height(18.dp)
                )

                Button(
                    onClick = onRetry
                ) {
                    Text("Try Again")
                }
            }
        }
    }
}


// ================================================================
// HELPERS
// ================================================================

private fun connectionDescription(
    info: NetworkInfo
): String {

    return when {

        !info.isInternetAvailable ->
            "Network connected, internet unavailable"

        info.connectionType == ConnectionType.WIFI ->
            "Connected through Wi-Fi"

        info.connectionType == ConnectionType.MOBILE ->
            "Connected through mobile data"

        info.connectionType == ConnectionType.ETHERNET ->
            "Connected through Ethernet"

        else ->
            "Internet connection available"
    }
}


private fun qualityDescription(
    score: Int,
    internetAvailable: Boolean
): String {

    if (!internetAvailable) {
        return "Internet access is currently unavailable"
    }

    return when {

        score >= 85 ->
            "Your connection is performing excellently"

        score >= 70 ->
            "Your connection is performing well"

        score >= 50 ->
            "Your connection is acceptable"

        else ->
            "Your connection may need attention"
    }
}


private fun signalProgress(
    dbm: Int
): Float {
    return ((dbm + 100) / 50f)
        .coerceIn(0f, 1f)
}


private fun signalDescription(
    dbm: Int
): String {

    return when {

        dbm >= -50 ->
            "Very strong signal"

        dbm >= -60 ->
            "Strong signal"

        dbm >= -70 ->
            "Good signal"

        dbm >= -85 ->
            "Weak signal"

        else ->
            "Very weak signal"
    }
}