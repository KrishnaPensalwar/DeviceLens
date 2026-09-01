package com.example.devicelens.presentation.battery

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.material.icons.outlined.BatteryAlert
import androidx.compose.material.icons.outlined.BatteryStd
import androidx.compose.material.icons.outlined.Bolt
import androidx.compose.material.icons.outlined.CompareArrows
import androidx.compose.material.icons.outlined.DeviceThermostat
import androidx.compose.material.icons.outlined.ElectricBolt
import androidx.compose.material.icons.outlined.HealthAndSafety
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Power
import androidx.compose.material.icons.outlined.QueryStats
import androidx.compose.material.icons.outlined.Speed
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.devicelens.domain.model.BatteryInfo


// ================================================================
// DEVICE LENS COLORS
// ================================================================

private val Background = Color(0xFF0D1111)

private val Card = Color(0xFF1A2020)
private val CardSecondary = Color(0xFF202626)

private val Border = Color(0xFF303838)

private val Cyan = Color(0xFF00A6A6)
private val CyanLight = Color(0xFF6BDAD8)

private val Blue = Color(0xFF29499F)
private val BlueLight = Color(0xFFA7B7FF)

private val TextPrimary = Color(0xFFE4E8E8)
private val TextSecondary = Color(0xFFBBC4C4)
private val TextMuted = Color(0xFF7F8989)

private val Success = Color(0xFF00D68F)


// ================================================================
// MAIN SCREEN
// ================================================================

@Composable
fun BatteryScreen(
    viewModel: BatteryViewModel = hiltViewModel(),
    onBack: () -> Unit = {},
    onHomeClick: () -> Unit = {},
    onCompareClick: () -> Unit = {},
    onProfileClick: () -> Unit = {}
) {

    val uiState by viewModel.batteryState.collectAsStateWithLifecycle()

    when {

        uiState.isLoading -> {
            BatteryLoading()
        }

        uiState.error != null -> {
            BatteryError(
                error = uiState.error,
                onRetry = {
                    // Replace with your actual ViewModel retry method.
                    viewModel.getBatteryInfo()
                }
            )
        }

        uiState.batteryInfo != null -> {
            BatteryContent(
                batteryInfo = uiState.batteryInfo!!,
                onBack = onBack,
                onHomeClick = onHomeClick,
                onCompareClick = onCompareClick,
                onProfileClick = onProfileClick
            )
        }
    }
}


// ================================================================
// MAIN CONTENT
// ================================================================

@Composable
private fun BatteryContent(
    batteryInfo: BatteryInfo,
    onBack: () -> Unit,
    onHomeClick: () -> Unit,
    onCompareClick: () -> Unit,
    onProfileClick: () -> Unit
) {

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Background)
    ) {

        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),

            contentPadding = PaddingValues(
                start = 16.dp,
                end = 16.dp,
                top = 10.dp,
                bottom = 18.dp
            ),

            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {

            // ----------------------------------------------------
            // TOP BAR
            // ----------------------------------------------------

            item {

                BatteryTopBar(
                    onBack = onBack
                )
            }

            // ----------------------------------------------------
            // BATTERY STATUS
            // ----------------------------------------------------

            item {

                BatteryOverviewCard(
                    batteryInfo = batteryInfo
                )
            }

            // ----------------------------------------------------
            // POWER DETAILS
            // ----------------------------------------------------

            item {

                BatteryDetailsCard(
                    batteryInfo = batteryInfo
                )
            }

            // ----------------------------------------------------
            // BATTERY HEALTH
            // ----------------------------------------------------

            item {

                BatteryHealthCard(
                    batteryInfo = batteryInfo
                )
            }
        }

        // --------------------------------------------------------
        // BOTTOM NAVIGATION
        // --------------------------------------------------------

        BatteryBottomNavigation(
            onHomeClick = onHomeClick,
            onCompareClick = onCompareClick,
            onProfileClick = onProfileClick
        )
    }
}


// ================================================================
// TOP BAR
// ================================================================

@Composable
private fun BatteryTopBar(
    onBack: () -> Unit
) {

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(58.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {

        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape),
            contentAlignment = Alignment.Center
        ) {

            androidx.compose.material3.IconButton(
                onClick = onBack,
                modifier = Modifier.size(40.dp)
            ) {

                androidx.compose.material3.Icon(
                    imageVector = Icons.Outlined.ArrowBack,
                    contentDescription = "Back",
                    modifier = Modifier.size(24.dp),
                    tint = TextPrimary
                )
            }
        }

        Spacer(
            modifier = Modifier.width(8.dp)
        )

        androidx.compose.material3.Text(
            text = "Battery",
            color = TextPrimary,
            fontSize = 26.sp,
            fontWeight = FontWeight.Bold
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

    DeviceCard(
        modifier = Modifier.fillMaxWidth()
    ) {

        // --------------------------------------------------------
        // CARD HEADER
        // --------------------------------------------------------

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {

            AccentIcon(
                icon = Icons.Outlined.BatteryStd
            )

            Spacer(
                modifier = Modifier.width(16.dp)
            )

            Column(
                modifier = Modifier.weight(1f)
            ) {

                androidx.compose.material3.Text(
                    text = "Battery Status",
                    color = TextPrimary,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(
                    modifier = Modifier.height(3.dp)
                )

                androidx.compose.material3.Text(
                    text = batteryStatusTitle(batteryInfo),
                    color = TextSecondary,
                    fontSize = 14.sp
                )
            }
        }

        Spacer(
            modifier = Modifier.height(22.dp)
        )

        androidx.compose.material3.HorizontalDivider(
            color = Border
        )

        Spacer(
            modifier = Modifier.height(22.dp)
        )

        // --------------------------------------------------------
        // LEVEL + CIRCLE
        // --------------------------------------------------------

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {

            Column(
                modifier = Modifier.weight(1f)
            ) {

                androidx.compose.material3.Text(
                    text = "${batteryInfo.level}%",
                    color = TextPrimary,
                    fontSize = 58.sp,
                    lineHeight = 58.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(
                    modifier = Modifier.height(5.dp)
                )

                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .clip(CircleShape)
                            .background(
                                if (batteryInfo.isCharging) {
                                    CyanLight
                                } else {
                                    Success
                                }
                            )
                    )

                    Spacer(
                        modifier = Modifier.width(8.dp)
                    )

                    androidx.compose.material3.Text(
                        text = chargingStatusText(batteryInfo),
                        color = TextSecondary,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            BatteryCircularIndicator(
                level = batteryInfo.level,
                isCharging = batteryInfo.isCharging
            )
        }

        Spacer(
            modifier = Modifier.height(22.dp)
        )

        // --------------------------------------------------------
        // PROGRESS
        // --------------------------------------------------------

        androidx.compose.material3.LinearProgressIndicator(
            progress = {
                batteryInfo.level
                    .coerceIn(0, 100) / 100f
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp)
                .clip(CircleShape),
            color = Cyan,
            trackColor = Color(0xFF353B3B)
        )

        Spacer(
            modifier = Modifier.height(9.dp)
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {

            MonoText("0%")

            MonoText("100%")
        }
    }
}


// ================================================================
// CIRCULAR BATTERY INDICATOR
// ================================================================

@Composable
private fun BatteryCircularIndicator(
    level: Int,
    isCharging: Boolean
) {

    val progress = level
        .coerceIn(0, 100) / 100f

    Box(
        modifier = Modifier.size(70.dp),
        contentAlignment = Alignment.Center
    ) {

        androidx.compose.material3.CircularProgressIndicator(
            progress = {
                progress
            },
            modifier = Modifier.fillMaxSize(),
            color = Cyan,
            trackColor = Color(0xFF293131),
            strokeWidth = 6.dp,
            strokeCap = StrokeCap.Round
        )

        androidx.compose.material3.Icon(
            imageVector = if (isCharging) {
                Icons.Outlined.ElectricBolt
            } else {
                Icons.Outlined.BatteryStd
            },
            contentDescription = null,
            modifier = Modifier.size(25.dp),
            tint = CyanLight
        )
    }
}


// ================================================================
// POWER DETAILS
// ================================================================

@Composable
private fun BatteryDetailsCard(
    batteryInfo: BatteryInfo
) {

    DeviceCard {

        CardHeader(
            icon = Icons.Outlined.Power,
            title = "Power Details",
            subtitle = "Charging and electrical information"
        )

        Spacer(
            modifier = Modifier.height(18.dp)
        )

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
                formatVoltage(it)
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
// BATTERY HEALTH
// ================================================================

@Composable
private fun BatteryHealthCard(
    batteryInfo: BatteryInfo
) {

    DeviceCard {

        CardHeader(
            icon = Icons.Outlined.HealthAndSafety,
            title = "Battery Health",
            subtitle = "Condition and temperature"
        )

        Spacer(
            modifier = Modifier.height(18.dp)
        )

        BatteryInfoRow(
            icon = Icons.Outlined.HealthAndSafety,
            label = "Health",
            value = formatBatteryHealth(
                batteryInfo.health.name
            ),
            valueColor = Success
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
// DEVICE CARD
// ================================================================

@Composable
private fun DeviceCard(
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit
) {

    Column(
        modifier = modifier
            .clip(RoundedCornerShape(14.dp))
            .background(Card)
            .border(
                width = 1.dp,
                color = Border,
                shape = RoundedCornerShape(14.dp)
            )
            .padding(
                horizontal = 19.dp,
                vertical = 20.dp
            ),
        content = content
    )
}


// ================================================================
// CARD HEADER
// ================================================================

@Composable
private fun CardHeader(
    icon: ImageVector,
    title: String,
    subtitle: String
) {

    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {

        AccentIcon(
            icon = icon
        )

        Spacer(
            modifier = Modifier.width(16.dp)
        )

        Column(
            modifier = Modifier.weight(1f)
        ) {

            androidx.compose.material3.Text(
                text = title,
                color = TextPrimary,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(
                modifier = Modifier.height(2.dp)
            )

            androidx.compose.material3.Text(
                text = subtitle,
                color = TextSecondary,
                fontSize = 14.sp
            )
        }
    }
}


// ================================================================
// ACCENT ICON
// ================================================================

@Composable
private fun AccentIcon(
    icon: ImageVector
) {

    Box(
        modifier = Modifier
            .size(40.dp)
            .clip(RoundedCornerShape(10.dp))
            .background(Blue),
        contentAlignment = Alignment.Center
    ) {

        androidx.compose.material3.Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier.size(21.dp),
            tint = BlueLight
        )
    }
}


// ================================================================
// INFO ROW
// ================================================================

@Composable
private fun BatteryInfoRow(
    icon: ImageVector,
    label: String,
    value: String,
    valueColor: Color = TextPrimary
) {

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(42.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {

        Box(
            modifier = Modifier
                .size(32.dp)
                .clip(RoundedCornerShape(7.dp))
                .background(CardSecondary),
            contentAlignment = Alignment.Center
        ) {

            androidx.compose.material3.Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(17.dp),
                tint = TextSecondary
            )
        }

        Spacer(
            modifier = Modifier.width(12.dp)
        )

        androidx.compose.material3.Text(
            text = label,
            modifier = Modifier.weight(1f),
            color = TextPrimary,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium
        )

        androidx.compose.material3.Text(
            text = value,
            color = valueColor,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = FontFamily.Monospace
        )
    }
}


// ================================================================
// DIVIDER
// ================================================================

@Composable
private fun BatteryDivider() {

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(1.dp)
            .background(Border)
    )
}


// ================================================================
// BOTTOM NAVIGATION
// ================================================================

@Composable
private fun BatteryBottomNavigation(
    onHomeClick: () -> Unit,
    onCompareClick: () -> Unit,
    onProfileClick: () -> Unit
) {

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFF151A1A))
            .padding(
                horizontal = 8.dp,
                vertical = 8.dp
            ),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {

        BottomNavItem(
            icon = Icons.Outlined.Home,
            label = "Home",
            selected = false,
            onClick = onHomeClick
        )

        BottomNavItem(
            icon = Icons.Outlined.QueryStats,
            label = "Diagnostics",
            selected = true,
            onClick = {}
        )

        BottomNavItem(
            icon = Icons.Outlined.CompareArrows,
            label = "Compare",
            selected = false,
            onClick = onCompareClick
        )

        BottomNavItem(
            icon = Icons.Outlined.Person,
            label = "Profile",
            selected = false,
            onClick = onProfileClick
        )
    }
}


// ================================================================
// BOTTOM NAV ITEM
// ================================================================

@Composable
private fun BottomNavItem(
    icon: ImageVector,
    label: String,
    selected: Boolean,
    onClick: () -> Unit
) {

    Box(
        modifier = Modifier
            .width(92.dp)
            .height(52.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(
                if (selected) {
                    Blue
                } else {
                    Color.Transparent
                }
            )
            .then(
                Modifier
            ),
        contentAlignment = Alignment.Center
    ) {

        androidx.compose.material3.IconButton(
            onClick = onClick,
            modifier = Modifier.fillMaxSize()
        ) {

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {

                androidx.compose.material3.Icon(
                    imageVector = icon,
                    contentDescription = label,
                    modifier = Modifier.size(20.dp),
                    tint = if (selected) {
                        BlueLight
                    } else {
                        TextSecondary
                    }
                )

                Spacer(
                    modifier = Modifier.height(2.dp)
                )

                androidx.compose.material3.Text(
                    text = label,
                    fontSize = 11.sp,
                    fontWeight = if (selected) {
                        FontWeight.Bold
                    } else {
                        FontWeight.Medium
                    },
                    color = if (selected) {
                        BlueLight
                    } else {
                        TextSecondary
                    }
                )
            }
        }
    }
}


// ================================================================
// LOADING
// ================================================================

@Composable
private fun BatteryLoading() {

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Background),
        contentAlignment = Alignment.Center
    ) {

        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            androidx.compose.material3.CircularProgressIndicator(
                color = CyanLight
            )

            Spacer(
                modifier = Modifier.height(12.dp)
            )

            androidx.compose.material3.Text(
                text = "Loading battery...",
                color = TextSecondary,
                fontSize = 14.sp
            )
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
            .background(Background)
            .padding(20.dp),
        contentAlignment = Alignment.Center
    ) {

        DeviceCard(
            modifier = Modifier.fillMaxWidth()
        ) {

            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                androidx.compose.material3.Icon(
                    imageVector = Icons.Outlined.BatteryAlert,
                    contentDescription = null,
                    modifier = Modifier.size(42.dp),
                    tint = CyanLight
                )

                Spacer(
                    modifier = Modifier.height(14.dp)
                )

                androidx.compose.material3.Text(
                    text = "Battery information unavailable",
                    color = TextPrimary,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(
                    modifier = Modifier.height(6.dp)
                )

                androidx.compose.material3.Text(
                    text = error ?: "Something went wrong",
                    color = TextSecondary,
                    fontSize = 14.sp
                )

                Spacer(
                    modifier = Modifier.height(18.dp)
                )

                androidx.compose.material3.Button(
                    onClick = onRetry
                ) {
                    androidx.compose.material3.Text("Try again")
                }
            }
        }
    }
}


// ================================================================
// TEXT HELPERS
// ================================================================

@Composable
private fun MonoText(
    text: String
) {

    androidx.compose.material3.Text(
        text = text,
        color = TextSecondary,
        fontSize = 11.sp,
        fontWeight = FontWeight.Bold,
        fontFamily = FontFamily.Monospace
    )
}


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
// VOLTAGE
// ================================================================

private fun formatVoltage(
    voltage: Int
): String {

    return if (voltage >= 1000) {
        "%.2f V".format(voltage / 1000f)
    } else {
        "$voltage mV"
    }
}