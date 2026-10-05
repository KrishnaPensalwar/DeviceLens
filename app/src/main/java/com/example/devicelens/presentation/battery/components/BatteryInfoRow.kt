package com.example.devicelens.presentation.battery.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import com.example.devicelens.presentation.components.LensInfoRow
import com.example.devicelens.ui.theme.LensTextPrimary

@Composable
fun BatteryInfoRow(
    icon: ImageVector,
    label: String,
    value: String,
    modifier: Modifier = Modifier,
    valueColor: Color = LensTextPrimary
) {
    LensInfoRow(
        label = label,
        value = value,
        modifier = modifier,
        icon = icon,
        valueColor = valueColor
    )
}
