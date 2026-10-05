package com.example.devicelens.presentation.battery.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import com.example.devicelens.presentation.components.LensCardHeader

@Composable
fun BatteryCardHeader(
    icon: ImageVector,
    title: String,
    subtitle: String,
    modifier: Modifier = Modifier
) {
    LensCardHeader(
        icon = icon,
        title = title,
        subtitle = subtitle,
        modifier = modifier
    )
}
