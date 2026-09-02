package com.example.devicelens.presentation.device

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.material.icons.outlined.BarChart
import androidx.compose.material.icons.outlined.BatteryStd
import androidx.compose.material.icons.outlined.Devices
import androidx.compose.material.icons.outlined.Memory
import androidx.compose.material.icons.outlined.Sensors
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.outlined.Storage
import androidx.compose.material.icons.outlined.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.devicelens.core.util.FileSizeFormatter
import com.example.devicelens.domain.model.BatteryStatus
import com.example.devicelens.domain.model.DeviceInfo
import java.text.SimpleDateFormat
import java.util.Locale


// ================================================================
// COLORS
// ================================================================

private val OuterBackground = Color(0xFF202124)

private val PanelBackground = Color(0xFF0D1110)

private val CardBackground = Color(0xFF1A1F1E)

private val CardBorder = Color(0xFF2A302E)

private val DividerColor = Color(0xFF252B29)

private val TextPrimary = Color(0xFFE4E9E7)

private val TextSecondary = Color(0xFFB4BEBA)

private val TextMuted = Color(0xFF737D79)

private val Cyan = Color(0xFF6BD8D4)

private val Orange = Color(0xFFFFA27D)

private val Blue = Color(0xFFA9B7FF)

private val Green = Color(0xFF00C58A)


// ================================================================
// MAIN SCREEN
// ================================================================

@Composable
fun DeviceInfoScreen(
    viewModel: DeviceInfoViewModel = hiltViewModel(),
    onBackPress: () -> Unit
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
                deviceInfo = uiState.deviceInfo,
                onBackPress
            )
        }
    }
}


// ================================================================
// CONTENT
// ================================================================

@Composable
private fun DeviceInfoContent(
    deviceInfo: DeviceInfo?,
    onBackPress: () -> Unit
) {

    if (deviceInfo == null) return


    // ---------------------------------------------------------
    // DEVICE LENS PANEL
    // ---------------------------------------------------------

    Box(
        modifier = Modifier
            .fillMaxSize()
            .clip(
                RoundedCornerShape(
                    topStart = 14.dp,
                    topEnd = 14.dp
                )
            )
            .background(PanelBackground)
            .border(
                width = 1.dp,
                color = CardBorder,
                shape = RoundedCornerShape(
                    topStart = 14.dp,
                    topEnd = 14.dp
                )
            ),
        contentAlignment = Alignment.BottomCenter
    )
    {

        LazyColumn(
            modifier = Modifier.fillMaxSize(),

            contentPadding = PaddingValues(
                start = 16.dp,
                end = 16.dp,
                top = 0.dp,
                bottom = 90.dp
            ),

            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            // -------------------------------------------------
            // HEADER
            // -------------------------------------------------

            item {
                DeviceInfoTopBar( title = "Device Info"){
                        onBackPress()
                }
            }

            // -------------------------------------------------
            // DEVICE
            // -------------------------------------------------

            item {
                DeviceCard(
                    deviceInfo = deviceInfo
                )
            }

            // -------------------------------------------------
            // SYSTEM
            // -------------------------------------------------

            item {
                SystemCard(
                    deviceInfo = deviceInfo
                )
            }

            // -------------------------------------------------
            // DISPLAY
            // -------------------------------------------------

            item {
                DisplayCard(
                    deviceInfo = deviceInfo
                )
            }

            // -------------------------------------------------
            // MEMORY & STORAGE
            // -------------------------------------------------

            item {
                MemoryStorageCard(
                    deviceInfo = deviceInfo
                )
            }

            // -------------------------------------------------
            // CAMERA
            // -------------------------------------------------

            item {
                CameraCard()
            }

            // -------------------------------------------------
            // BATTERY
            // -------------------------------------------------

            item {
                BatteryCard(
                    deviceInfo = deviceInfo
                )
            }

            // -------------------------------------------------
            // SENSORS
            // -------------------------------------------------

            item {
                SensorsCard(
                    deviceInfo = deviceInfo
                )
            }
        }
    }

}


// ================================================================
// DOTTED BACKGROUND
// ================================================================

@Composable
private fun DottedBackground() {

    Canvas(
        modifier = Modifier.fillMaxSize()
    ) {

        val spacing = 16.dp.toPx()
        val radius = 1.dp.toPx()

        var x = 0f

        while (x <= size.width) {

            var y = 0f

            while (y <= size.height) {

                drawCircle(
                    color = Color(0xFF6B7072).copy(alpha = 0.45f),
                    radius = radius,
                    center = Offset(x, y)
                )

                y += spacing
            }

            x += spacing
        }
    }
}


// ================================================================
// TOP BAR
// ================================================================

@Composable
fun DeviceInfoTopBar(
    title: String,
    onBackPress: () -> Unit,
) {

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(65.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {

        Icon(
            imageVector = Icons.Outlined.ArrowBack,
            contentDescription = "Back",
            tint = Cyan,
            modifier = Modifier
                .size(24.dp)
                .clickable {
                    onBackPress()
                }
        )

        Text(
            text = title,
            color = Cyan,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.weight(1f),
            textAlign = androidx.compose.ui.text.style.TextAlign.Center
        )

        Icon(
            imageVector = Icons.Outlined.Settings,
            contentDescription = "Settings",
            tint = TextSecondary,
            modifier = Modifier.size(24.dp)
        )
    }
}


// ================================================================
// DEVICE CARD
// ================================================================

@Composable
private fun DeviceCard(
    deviceInfo: DeviceInfo
) {

    InfoCard {

        SectionHeader(
            icon = Icons.Outlined.Devices,
            title = "DEVICE",
            color = Cyan
        )

        Spacer(modifier = Modifier.height(10.dp))

        InfoRow(
            label = "Manufacturer",
            value = deviceInfo.manufacturer
        )

        InfoDivider()

        InfoRow(
            label = "Model",
            value = deviceInfo.model
        )

        InfoDivider()

        InfoRow(
            label = "Brand",
            value = deviceInfo.brandOrManufacturer()
        )

        InfoDivider()

        InfoRow(
            label = "Board",
            value = deviceInfo.boardName()
        )

        InfoDivider()

        InfoRow(
            label = "Hardware",
            value = deviceInfo.hardwareName()
        )
    }
}


// ================================================================
// SYSTEM
// ================================================================

@Composable
private fun SystemCard(
    deviceInfo: DeviceInfo
) {

    InfoCard {

        SectionHeader(
            icon = Icons.Outlined.Memory,
            title = "SYSTEM",
            color = Blue
        )

        Spacer(modifier = Modifier.height(10.dp))

        InfoRow(
            label = "Android Version",
            value = deviceInfo.androidVersion
        )

        InfoDivider()

        InfoRow(
            label = "SDK Level",
            value = deviceInfo.sdkLevelText()
        )

        InfoDivider()

        InfoRow(
            label = "Security Patch",
            value = formatSecurityPatch(
                deviceInfo.securityPatch
            )
        )

        InfoDivider()

        InfoRow(
            label = "Kernel Version",
            value = deviceInfo.kernelVersionText()
        )

        InfoDivider()

        InfoRow(
            label = "Build Number",
            value = deviceInfo.buildNumberText()
        )
    }
}


// ================================================================
// DISPLAY
// ================================================================

@Composable
private fun DisplayCard(
    deviceInfo: DeviceInfo
) {

    val display = deviceInfo.display

    InfoCard {

        SectionHeader(
            icon = Icons.Outlined.Devices,
            title = "DISPLAY",
            color = Orange
        )

        Spacer(modifier = Modifier.height(10.dp))

        InfoRow(
            label = "Resolution",
            value = "${display.resolutionWidth} × ${display.resolutionHeight}"
        )

        InfoDivider()

        InfoRow(
            label = "Refresh Rate",
            value = "${display.refreshRateHz} Hz"
        )

        InfoDivider()

        InfoRow(
            label = "Density",
            value = displayDensityText(deviceInfo)
        )

        InfoDivider()

        InfoRow(
            label = "Screen Size",
            value = display.screenSizeInches?.let {
                "%.1f inches".format(it)
            } ?: "Not available"
        )
    }
}


// ================================================================
// MEMORY & STORAGE
// ================================================================

@Composable
private fun MemoryStorageCard(
    deviceInfo: DeviceInfo
) {

    val memory = deviceInfo.memory
    val storage = deviceInfo.storage

    InfoCard {

        SectionHeader(
            icon = Icons.Outlined.Memory,
            title = "MEMORY & STORAGE",
            color = Cyan
        )

        Spacer(modifier = Modifier.height(10.dp))

        InfoRow(
            label = "Total RAM",
            value = FileSizeFormatter.format(
                memory.totalBytes
            )
        )

        InfoDivider()

        InfoRow(
            label = "Available RAM",
            value = FileSizeFormatter.format(
                memory.availableBytes
            )
        )

        InfoDivider()

        InfoRow(
            label = "Internal Storage",
            value = FileSizeFormatter.format(
                storage.totalBytes
            )
        )

        InfoDivider()

        InfoRow(
            label = "Available Storage",
            value = FileSizeFormatter.format(
                storage.availableBytes
            )
        )
    }
}


// ================================================================
// CAMERA
// ================================================================
//
// Your current DeviceInfo model does not expose camera
// information in the supplied implementation.
//
// This card is therefore shown as a UI section placeholder.
// Once camera information is added to DeviceInfo, connect it here.
// ================================================================

@Composable
private fun CameraCard() {

    InfoCard {

        SectionHeader(
            icon = Icons.Outlined.Devices,
            title = "CAMERA",
            color = Blue
        )

        Spacer(modifier = Modifier.height(10.dp))

        InfoRow(
            label = "Rear Camera",
            value = "Not available"
        )

        InfoDivider()

        InfoRow(
            label = "Front Camera",
            value = "Not available"
        )

        InfoDivider()

        InfoRow(
            label = "Video",
            value = "Not available"
        )
    }
}


// ================================================================
// BATTERY
// ================================================================

@Composable
private fun BatteryCard(
    deviceInfo: DeviceInfo
) {

    val battery = deviceInfo.battery

    InfoCard {

        SectionHeader(
            icon = Icons.Outlined.BatteryStd,
            title = "BATTERY",
            color = Orange
        )

        Spacer(modifier = Modifier.height(10.dp))

        InfoRow(
            label = "Battery Level",
            value = battery.percent?.let {
                "$it%"
            } ?: "N/A"
        )

        InfoDivider()

        InfoRow(
            label = "Status",
            value = chargingStatusText(
                battery.isCharging,
                battery.status
            )
        )

        InfoDivider()

        InfoRow(
            label = "Temperature",
            value = battery.temperatureC?.let {
                "%.0f°C".format(it)
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
// SENSORS
// ================================================================

@Composable
private fun SensorsCard(
    deviceInfo: DeviceInfo
) {

    InfoCard {

        SectionHeader(
            icon = Icons.Outlined.Sensors,
            title = "SENSORS",
            color = Cyan
        )

        Spacer(modifier = Modifier.height(10.dp))

        deviceInfo.sensors.forEachIndexed { index, sensor ->

            SensorRow(
                name = sensor.name,
                isAvailable = sensor.isAvailable
            )

            if (index != deviceInfo.sensors.lastIndex) {
                InfoDivider()
            }
        }
    }
}


// ================================================================
// CARD
// ================================================================

@Composable
private fun InfoCard(
    content: @Composable ColumnScope.() -> Unit
) {

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(11.dp))
            .background(CardBackground)
            .border(
                width = 1.dp,
                color = CardBorder,
                shape = RoundedCornerShape(11.dp)
            )
            .padding(
                horizontal = 20.dp,
                vertical = 16.dp
            ),
        content = content
    )
}


// ================================================================
// SECTION HEADER
// ================================================================

@Composable
private fun SectionHeader(
    icon: ImageVector,
    title: String,
    color: Color
) {

    Column(
        modifier = Modifier.fillMaxWidth()
    ) {

        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {

            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = color,
                modifier = Modifier.size(22.dp)
            )

            Spacer(modifier = Modifier.width(10.dp))

            Text(
                text = title,
                color = color,
                fontSize = 20.sp,
                fontWeight = FontWeight.Medium
            )
        }

        Spacer(modifier = Modifier.height(11.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(1.dp)
                .background(DividerColor)
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
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 35.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {

        Text(
            text = label,
            color = TextSecondary,
            fontSize = 14.sp,
            fontWeight = FontWeight.Normal,
            modifier = Modifier.weight(1f)
        )

        Spacer(modifier = Modifier.width(12.dp))

        Text(
            text = value,
            color = TextPrimary,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            fontFamily = FontFamily.Monospace,
            textAlign = androidx.compose.ui.text.style.TextAlign.End,
            maxLines = 1
        )
    }
}


// ================================================================
// DIVIDER
// ================================================================

@Composable
private fun InfoDivider() {

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(1.dp)
            .background(DividerColor)
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
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 34.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {

        Box(
            modifier = Modifier
                .size(7.dp)
                .clip(CircleShape)
                .background(
                    if (isAvailable) {
                        Green
                    } else {
                        TextMuted
                    }
                )
        )

        Spacer(modifier = Modifier.width(10.dp))

        Text(
            text = name,
            color = TextSecondary,
            fontSize = 14.sp,
            modifier = Modifier.weight(1f)
        )

        Text(
            text = if (isAvailable) {
                "Available"
            } else {
                "Unavailable"
            },
            color = if (isAvailable) {
                Green
            } else {
                TextMuted
            },
            fontSize = 12.sp,
            fontFamily = FontFamily.Monospace
        )
    }
}


// ================================================================
// BOTTOM NAVIGATION
// ================================================================

@Composable
private fun DeviceBottomNavigation() {

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                PanelBackground.copy(alpha = 0.98f)
            )
            .border(
                width = 1.dp,
                color = CardBorder
            )
            .padding(
                horizontal = 8.dp,
                vertical = 10.dp
            ),
        contentAlignment = Alignment.BottomCenter
    ) {

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {

            BottomNavItem(
                icon = Icons.Outlined.BarChart,
                title = "Diagnostics"
            )

            BottomNavItem(
                icon = Icons.Outlined.Sensors,
                title = "Sensors"
            )

            BottomNavItem(
                icon = Icons.Outlined.Storage,
                title = "Storage"
            )

            BottomNavItem(
                icon = Icons.Outlined.Devices,
                title = "Network"
            )
        }
    }
}


// ================================================================
// BOTTOM NAV ITEM
// ================================================================

@Composable
private fun BottomNavItem(
    icon: ImageVector,
    title: String
) {

    Column(
        modifier = Modifier.padding(
            horizontal = 8.dp
        ),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Icon(
            imageVector = icon,
            contentDescription = title,
            tint = TextSecondary,
            modifier = Modifier.size(21.dp)
        )

        Spacer(modifier = Modifier.height(5.dp))

        Text(
            text = title,
            color = TextSecondary,
            fontSize = 9.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = FontFamily.Monospace
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
            .background(PanelBackground),
        contentAlignment = Alignment.Center
    ) {

        CircularProgressIndicator(
            color = Cyan,
            modifier = Modifier.size(36.dp)
        )
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
            .background(PanelBackground)
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {

        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Icon(
                imageVector = Icons.Outlined.Warning,
                contentDescription = null,
                tint = Orange,
                modifier = Modifier.size(42.dp)
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "Unable to load device information",
                color = TextPrimary,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(7.dp))

            Text(
                text = error ?: "Something went wrong",
                color = TextSecondary,
                fontSize = 14.sp
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


// ================================================================
// HELPERS
// ================================================================

private fun formatSecurityPatch(
    raw: String?
): String {

    if (raw.isNullOrBlank()) {
        return "Not available"
    }

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


private fun chargingStatusText(
    isCharging: Boolean,
    status: BatteryStatus
): String {

    return when {

        status == BatteryStatus.FULL ->
            "Fully charged"

        isCharging || status == BatteryStatus.CHARGING ->
            "Charging"

        status == BatteryStatus.UNKNOWN ->
            "Not available"

        else ->
            "Not charging"
    }
}


// ================================================================
// SAFE DEVICE FIELD HELPERS
// ================================================================
//
// These use reflection so the UI won't fail to compile if your
// current DeviceInfo model doesn't expose these optional fields.
// If those fields already exist, they'll be displayed automatically.
// ================================================================

private fun DeviceInfo.brandOrManufacturer(): String {

    return readStringProperty("brand")
        ?: manufacturer
}


private fun DeviceInfo.boardName(): String {

    return readStringProperty("board")
        ?: "Not available"
}


private fun DeviceInfo.hardwareName(): String {

    return readStringProperty("hardware")
        ?: "Not available"
}


private fun DeviceInfo.sdkLevelText(): String {

    return readAnyProperty(
        "sdkLevel",
        "sdkInt",
        "sdkVersion"
    )?.toString()
        ?: "Not available"
}


private fun DeviceInfo.kernelVersionText(): String {

    return readStringProperty(
        "kernelVersion"
    ) ?: "Not available"
}


private fun DeviceInfo.buildNumberText(): String {

    return readStringProperty(
        "buildNumber",
        "buildId"
    ) ?: "Not available"
}


private fun DeviceInfo.readStringProperty(
    vararg names: String
): String? {

    return names.firstNotNullOfOrNull { name ->

        try {

            javaClass
                .declaredFields
                .firstOrNull {
                    it.name.equals(
                        name,
                        ignoreCase = true
                    )
                }
                ?.apply {
                    isAccessible = true
                }
                ?.get(this)
                ?.toString()
                ?.takeIf {
                    it.isNotBlank()
                }

        } catch (_: Exception) {
            null
        }
    }
}


private fun DeviceInfo.readAnyProperty(
    vararg names: String
): Any? {

    return names.firstNotNullOfOrNull { name ->

        try {

            javaClass
                .declaredFields
                .firstOrNull {
                    it.name.equals(
                        name,
                        ignoreCase = true
                    )
                }
                ?.apply {
                    isAccessible = true
                }
                ?.get(this)

        } catch (_: Exception) {
            null
        }
    }
}


private fun displayDensityText(
    deviceInfo: DeviceInfo
): String {

    return deviceInfo.readAnyProperty(
        "densityDpi",
        "density"
    )?.let {
        if (it is Number) {
            "${it.toInt()} dpi"
        } else {
            it.toString()
        }
    } ?: "Not available"
}