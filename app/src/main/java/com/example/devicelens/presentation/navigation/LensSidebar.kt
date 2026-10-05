package com.example.devicelens.presentation.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Apps
import androidx.compose.material.icons.outlined.HealthAndSafety
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Smartphone
import androidx.compose.material.icons.outlined.Storage
import androidx.compose.material.icons.outlined.Wifi
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.devicelens.presentation.components.clickableWithoutRipple
import com.example.devicelens.ui.theme.LensBorder
import com.example.devicelens.ui.theme.LensSurface
import com.example.devicelens.ui.theme.LensSurfaceAlt
import com.example.devicelens.ui.theme.LensTextMuted
import com.example.devicelens.ui.theme.LensTextPrimary

enum class LensDestination(val route: String, val label: String, val icon: ImageVector) {
    Home(Routes.Dashboard.route, "Home", Icons.Outlined.Home),
    Health(Routes.Health.route, "Health", Icons.Outlined.HealthAndSafety),
    Device(Routes.Device.route, "Device", Icons.Outlined.Smartphone),
    Storage(Routes.Storage.route, "Storage", Icons.Outlined.Storage),
    Network(Routes.Network.route, "Network", Icons.Outlined.Wifi),
    Apps(Routes.Usage.route, "Apps", Icons.Outlined.Apps)
}

@Composable
fun LensSidebar(
    selectedRoute: String?,
    onSelect: (LensDestination) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .width(200.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(LensSurface)
            .border(width = 1.dp, color = LensBorder, shape = RoundedCornerShape(16.dp))
            .padding(horizontal = 8.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        LensDestination.entries.forEach { destination ->
            val selected = destination.route == selectedRoute ||
                (destination == LensDestination.Home && selectedRoute == Routes.Battery.route)
            SidebarItem(
                icon = destination.icon,
                label = destination.label,
                selected = selected,
                onClick = { onSelect(destination) }
            )
        }
    }
}

@Composable
private fun SidebarItem(
    icon: ImageVector,
    label: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    val tint = if (selected) LensTextPrimary else LensTextMuted
    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .background(if (selected) LensSurfaceAlt else LensSurfaceAlt.copy(alpha = 0f))
            .clickableWithoutRipple(onClick)
            .padding(horizontal = 12.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = tint,
            modifier = Modifier.size(22.dp)
        )
        Text(
            text = label,
            color = tint,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.padding(start = 8.dp)
        )
    }
}
