package com.example.devicelens.presentation.device.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Sensors
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.devicelens.domain.model.DeviceInfo
import com.example.devicelens.presentation.components.LensCard
import com.example.devicelens.ui.theme.LensCyan
import com.example.devicelens.ui.theme.LensGreen
import com.example.devicelens.ui.theme.LensTextMuted
import com.example.devicelens.ui.theme.LensTextSecondary


// ================================================================
// SENSORS CARD
// ================================================================

@Composable
fun SensorsCard(
    deviceInfo: DeviceInfo
) {
    LensCard(
        cornerRadius = 11.dp,
        contentPadding = PaddingValues(
            horizontal = 20.dp,
            vertical = 16.dp
        )
    ) {
        LensSectionHeader(
            icon = Icons.Outlined.Sensors,
            title = "SENSORS",
            color = LensCyan
        )

        Spacer(modifier = Modifier.height(10.dp))

        deviceInfo.sensors.forEachIndexed { index, sensor ->
            SensorRow(
                name = sensor.name,
                isAvailable = sensor.isAvailable
            )

            if (index != deviceInfo.sensors.lastIndex) {
                LensHairline()
            }
        }
    }
}


@Composable
fun SensorRow(
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
                        LensGreen
                    } else {
                        LensTextMuted
                    }
                )
        )

        Spacer(modifier = Modifier.width(10.dp))

        Text(
            text = name,
            color = LensTextSecondary,
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
                LensGreen
            } else {
                LensTextMuted
            },
            fontSize = 12.sp,
            fontFamily = FontFamily.Monospace
        )
    }
}