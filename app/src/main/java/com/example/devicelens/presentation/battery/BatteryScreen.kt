package com.example.devicelens.presentation.battery

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
import androidx.compose.material.icons.outlined.BatteryAlert
import androidx.compose.material.icons.outlined.BatteryStd
import androidx.compose.material.icons.outlined.Bolt
import androidx.compose.material.icons.outlined.DeviceThermostat
import androidx.compose.material.icons.outlined.ElectricBolt
import androidx.compose.material.icons.outlined.HealthAndSafety
import androidx.compose.material.icons.outlined.Power
import androidx.compose.material.icons.outlined.Speed
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
import com.example.devicelens.domain.model.BatteryInfo
import io.github.sanketnawghare.glassify.compose.GlassButton
import io.github.sanketnawghare.glassify.compose.GlassStyle
import io.github.sanketnawghare.glassify.compose.glassify


@Composable
fun BatteryScreen(
    viewModel: BatteryViewModel = hiltViewModel()
) {
    val uiState by viewModel.batteryState.collectAsStateWithLifecycle()

    BatteryContent(
        uiState = uiState
    )
}


@Composable
private fun BatteryContent(
    uiState: BatteryUiState
) {

    when {
        uiState.isLoading -> {
            BatteryLoading()
        }

        uiState.error != null -> {
            BatteryError(
                error = uiState.error,
                onRetry = {
                    viewModelRetry(uiState)
                }
            )
        }

        uiState.batteryInfo != null -> {
            BatteryInfoContent(
                batteryInfo = uiState.batteryInfo
            )
        }
    }
}


// ================================================================
// MAIN CONTENT
// ================================================================

@Composable
private fun BatteryInfoContent(
    batteryInfo: BatteryInfo
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
            BatteryHeader()
        }

        // ---------------------------------------------------------
        // BATTERY OVERVIEW
        // ---------------------------------------------------------

        item {
            BatteryOverviewCard(
                batteryInfo = batteryInfo
            )
        }

        // ---------------------------------------------------------
        // POWER DETAILS
        // ---------------------------------------------------------

        item {
            BatteryDetailsCard(
                batteryInfo = batteryInfo
            )
        }

        // ---------------------------------------------------------
        // BATTERY HEALTH
        // ---------------------------------------------------------

        item {
            BatteryHealthCard(
                batteryInfo = batteryInfo
            )
        }
    }
}


// ================================================================
// HEADER
// ================================================================

@Composable
private fun BatteryHeader() {

    Column {

        Text(
            text = "Battery",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground
        )

        Spacer(
            modifier = Modifier.height(4.dp)
        )

        Text(
            text = "Power, charging and battery health",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}


// ================================================================
// BATTERY OVERVIEW
// ================================================================

@Composable
private fun BatteryOverviewCard(
    batteryInfo: BatteryInfo
) {

    GlassCard(
        icon = Icons.Outlined.BatteryStd,
        title = "Battery Status",
        subtitle = batteryStatusTitle(batteryInfo)
    ) {

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {

            Column(
                modifier = Modifier.weight(1f)
            ) {

                Text(
                    text = "${batteryInfo.level}%",
                    style = MaterialTheme.typography.displaySmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Spacer(
                    modifier = Modifier.height(2.dp)
                )

                Text(
                    text = chargingStatusText(batteryInfo),
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            BatteryLevelIndicator(
                level = batteryInfo.level
            )
        }

        Spacer(
            modifier = Modifier.height(20.dp)
        )

        LinearProgressIndicator(
            progress = {
                batteryInfo.level
                    .coerceIn(0, 100) / 100f
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(9.dp)
                .clip(CircleShape)
        )

        Spacer(
            modifier = Modifier.height(10.dp)
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {

            Text(
                text = "0%",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Text(
                text = "100%",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}


// ================================================================
// BATTERY LEVEL INDICATOR
// ================================================================

@Composable
private fun BatteryLevelIndicator(
    level: Int
) {

    Box(
        modifier = Modifier
            .size(58.dp)
            .clip(RoundedCornerShape(18.dp))
            .background(
                MaterialTheme.colorScheme.primaryContainer
            ),
        contentAlignment = Alignment.Center
    ) {

        Icon(
            imageVector = when {
                level <= 20 ->
                    Icons.Outlined.BatteryAlert

                else ->
                    Icons.Outlined.BatteryStd
            },
            contentDescription = null,
            modifier = Modifier.size(30.dp),
            tint = MaterialTheme.colorScheme.onPrimaryContainer
        )
    }
}


// ================================================================
// BATTERY DETAILS
// ================================================================

@Composable
private fun BatteryDetailsCard(
    batteryInfo: BatteryInfo
) {

    GlassCard(
        icon = Icons.Outlined.Power,
        title = "Power Details",
        subtitle = "Charging and electrical information"
    ) {

        BatteryInfoRow(
            icon = Icons.Outlined.Bolt,
            label = "Charging",
            value = if (batteryInfo.isCharging) {
                "Charging"
            } else {
                "Not charging"
            }
        )

        BatteryDivider()

        BatteryInfoRow(
            icon = Icons.Outlined.ElectricBolt,
            label = "Charging type",
            value = formatChargingType(
                batteryInfo.chargingType.name
            )
        )

        BatteryDivider()

        BatteryInfoRow(
            icon = Icons.Outlined.Power,
            label = "Voltage",
            value = batteryInfo.voltage?.let {
                "$it mV"
            } ?: "Unknown"
        )

        BatteryDivider()

        BatteryInfoRow(
            icon = Icons.Outlined.Speed,
            label = "Capacity",
            value = batteryInfo.capacity?.let {
                "$it mAh"
            } ?: "Unknown"
        )

        BatteryDivider()

        BatteryInfoRow(
            icon = Icons.Outlined.BatteryStd,
            label = "Technology",
            value = batteryInfo.technology ?: "Unknown"
        )
    }
}


// ================================================================
// HEALTH CARD
// ================================================================

@Composable
private fun BatteryHealthCard(
    batteryInfo: BatteryInfo
) {

    GlassCard(
        icon = Icons.Outlined.HealthAndSafety,
        title = "Battery Health",
        subtitle = "Condition and temperature"
    ) {

        BatteryInfoRow(
            icon = Icons.Outlined.HealthAndSafety,
            label = "Health",
            value = formatBatteryHealth(
                batteryInfo.health.name
            )
        )

        BatteryDivider()

        BatteryInfoRow(
            icon = Icons.Outlined.DeviceThermostat,
            label = "Temperature",
            value = batteryInfo.temperature?.let {
                formatTemperature(it)
            } ?: "Unknown"
        )

        BatteryDivider()

        BatteryInfoRow(
            icon = Icons.Outlined.Power,
            label = "Battery saver",
            value = if (batteryInfo.isBatterySaverEnabled) {
                "Enabled"
            } else {
                "Disabled"
            }
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
                .padding(vertical = 9.dp)
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

                Spacer(
                    modifier = Modifier.size(12.dp)
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
// INFO ROW
// ================================================================

@Composable
private fun BatteryInfoRow(
    icon: ImageVector,
    label: String,
    value: String
) {

    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {

        Box(
            modifier = Modifier
                .size(34.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(
                    MaterialTheme.colorScheme.surfaceVariant
                ),
            contentAlignment = Alignment.Center
        ) {

            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(18.dp),
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        Spacer(
            modifier = Modifier.size(10.dp)
        )

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
// DIVIDER
// ================================================================

@Composable
private fun BatteryDivider() {

    Spacer(
        modifier = Modifier.height(10.dp)
    )
}


// ================================================================
// LOADING
// ================================================================

@Composable
private fun BatteryLoading() {

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
private fun BatteryError(
    error: String?,
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
                    imageVector = Icons.Outlined.BatteryAlert,
                    contentDescription = null,
                    modifier = Modifier.size(42.dp),
                    tint = MaterialTheme.colorScheme.error
                )

                Spacer(
                    modifier = Modifier.height(12.dp)
                )

                Text(
                    text = "Battery information unavailable",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )

                Spacer(
                    modifier = Modifier.height(6.dp)
                )

                Text(
                    text = error ?: "Something went wrong",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(
                    modifier = Modifier.height(18.dp)
                )

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
// TEXT HELPERS
// ================================================================

private fun batteryStatusTitle(
    batteryInfo: BatteryInfo
): String {

    return when {

        batteryInfo.level <= 15 ->
            "Battery is low"

        batteryInfo.level <= 30 ->
            "Battery getting low"

        batteryInfo.isCharging ->
            "Battery is charging"

        batteryInfo.level >= 80 ->
            "Battery level is good"

        else ->
            "Battery level is normal"
    }
}


private fun chargingStatusText(
    batteryInfo: BatteryInfo
): String {

    return when {

        batteryInfo.isCharging ->
            "Charging"

        batteryInfo.level >= 100 ->
            "Fully charged"

        else ->
            "Not charging"
    }
}


private fun formatChargingType(
    value: String
): String {

    return value
        .lowercase()
        .replace("_", " ")
        .replaceFirstChar {
            it.uppercase()
        }
}


private fun formatBatteryHealth(
    value: String
): String {

    return value
        .lowercase()
        .replace("_", " ")
        .replaceFirstChar {
            it.uppercase()
        }
}


private fun formatTemperature(
    temperature: Float
): String {

    val rounded = "%.1f".format(temperature)

    return when {

        temperature >= 40f ->
            "$rounded °C • Warm"

        temperature >= 35f ->
            "$rounded °C • Slightly warm"

        temperature <= 10f ->
            "$rounded °C • Cold"

        else ->
            "$rounded °C • Normal"
    }
}


// ================================================================
// RETRY
// ================================================================
//
// Replace this with your actual ViewModel retry function if
// BatteryViewModel exposes one.
//

private fun viewModelRetry(
    uiState: BatteryUiState
) {
    // Intentionally left empty.
    //
    // Prefer calling:
    // viewModel.loadBatteryInfo()
    //
    // directly from BatteryContent if your ViewModel exposes it.
}