package com.example.devicelens.presentation.device

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.BatteryStd
import androidx.compose.material.icons.outlined.Devices
import androidx.compose.material.icons.outlined.Memory
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.devicelens.domain.model.DeviceInfo
import com.example.devicelens.presentation.components.LensError
import com.example.devicelens.presentation.components.LensLoading
import com.example.devicelens.presentation.device.components.DeviceInfoCard
import com.example.devicelens.presentation.device.components.SensorsCard
import com.example.devicelens.ui.theme.LensBlue
import com.example.devicelens.ui.theme.LensCyan
import com.example.devicelens.ui.theme.LensOrange

// ================================================================
// MAIN SCREEN
// ================================================================

@Composable
fun DeviceInfoScreen(
    viewModel: DeviceInfoViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    when {
        uiState.isLoading -> {
            LensLoading()
        }

        uiState.error != null -> {
            LensError(
                title = "Unable to load device information",
                message = uiState.error,
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
    deviceInfo: DeviceInfo?,
) {
    if (deviceInfo == null) return

//    Box(
//        modifier = Modifier
//            .fillMaxSize()
//            .clip(
//                RoundedCornerShape(
//                    topStart = 14.dp,
//                    topEnd = 14.dp
//                )
//            )
//            .background(LensBackground)
//            .border(
//                width = 1.dp,
//                color = LensBorder,
//                shape = RoundedCornerShape(
//                    topStart = 14.dp,
//                    topEnd = 14.dp
//                )
//            ),
//        contentAlignment = Alignment.BottomCenter
//    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            // -------------------------------------------------
            // DEVICE
            // -------------------------------------------------

            item {
                DeviceInfoCard(
                    title = "DEVICE",
                    tint = LensCyan,
                    icon = Icons.Outlined.Devices,
                    items = getDeviceItems(deviceInfo)
                )
            }

            // -------------------------------------------------
            // SYSTEM
            // -------------------------------------------------

            item {
                DeviceInfoCard(
                    title = "SYSTEM",
                    tint = LensBlue,
                    icon = Icons.Outlined.Memory,
                    items = getSystemItems(deviceInfo)
                )
            }

            // -------------------------------------------------
            // DISPLAY
            // -------------------------------------------------

            item {
                DeviceInfoCard(
                    title = "DISPLAY",
                    tint = LensOrange,
                    icon = Icons.Outlined.Devices,
                    items = getDisplayItems(deviceInfo)
                )
            }

            // -------------------------------------------------
            // MEMORY & STORAGE
            // -------------------------------------------------

            item {
                DeviceInfoCard(
                    title = "MEMORY & STORAGE",
                    tint = LensCyan,
                    icon = Icons.Outlined.Memory,
                    items = getMemoryStorageItems(deviceInfo)
                )
            }

            // -------------------------------------------------
            // CAMERA
            // -------------------------------------------------

            item {
                DeviceInfoCard(
                    title = "CAMERA",
                    tint = LensBlue,
                    icon = Icons.Outlined.Devices,
                    items = getCameraItems(deviceInfo)
                )
            }

            // -------------------------------------------------
            // BATTERY
            // -------------------------------------------------

            item {
                DeviceInfoCard(
                    title = "BATTERY",
                    tint = LensOrange,
                    icon = Icons.Outlined.BatteryStd,
                    items = getBatteryItems(deviceInfo)
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
//    }
}