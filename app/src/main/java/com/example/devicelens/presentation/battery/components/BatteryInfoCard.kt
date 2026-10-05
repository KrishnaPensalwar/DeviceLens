package com.example.devicelens.presentation.battery.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import com.example.devicelens.presentation.components.LensInfoCard
import com.example.devicelens.presentation.components.LensInfoItem

@Composable
fun BatteryInfoCard(
    title: String,
    subtitle: String,
    icon: ImageVector,
    items: List<LensInfoItem>,
    modifier: Modifier = Modifier
) {
    LensInfoCard(
        title = title,
        subtitle = subtitle,
        icon = icon,
        items = items,
        modifier = modifier
    )
}
