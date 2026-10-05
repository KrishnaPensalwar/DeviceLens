package com.example.devicelens.presentation.dashboard

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Apps
import androidx.compose.material.icons.outlined.BatteryChargingFull
import androidx.compose.material.icons.outlined.HealthAndSafety
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
import com.example.devicelens.ui.theme.LensPurple
import com.example.devicelens.ui.theme.LensTextMuted
import com.example.devicelens.ui.theme.LensTextPrimary

@Composable
fun ExploreSection(
    onDeviceInfoClick: () -> Unit,
    onAppsClick: () -> Unit,
    onStorageClick: () -> Unit,
    onNetworkClick: () -> Unit,
    onUsageClick: () -> Unit,
    onHealthClick: () -> Unit
) {
    Text(
        "Explore",
        color = LensTextPrimary,
        fontSize = 18.sp,
        fontWeight = FontWeight.SemiBold,
        modifier = Modifier.padding(top = 8.dp, bottom = 16.dp)
    )
    Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
        listOf(
            Triple("Device info", "Specs", onDeviceInfoClick) to Icons.Outlined.Smartphone,
            Triple(
                "Battery",
                "Power & health",
                onAppsClick
            ) to Icons.Outlined.BatteryChargingFull,
            Triple("Storage", "Analyzer", onStorageClick) to Icons.Outlined.Storage,
            Triple("Network", "Speed & signal", onNetworkClick) to Icons.Outlined.Wifi,
            Triple("App usage", "Screen time", onUsageClick) to Icons.Outlined.Apps,
            Triple(
                "Device health",
                "Full report",
                onHealthClick
            ) to Icons.Outlined.HealthAndSafety
        ).forEach { (meta, icon) ->
            ExploreTile(icon, meta.first, meta.second, meta.third)
        }
    }
}


@Composable
private fun ExploreTile(icon: ImageVector, title: String, subtitle: String, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
//            .background(LensSurface)
            .border(1.dp, LensBorder, RoundedCornerShape(18.dp))
            .clickableWithoutRipple(onClick)
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(RoundedCornerShape(10.dp)),
            contentAlignment = Alignment.Center
        ) {
            Icon(icon, null, tint = LensPurple, modifier = Modifier.size(20.dp))
        }
        Column(modifier = Modifier.padding(start = 10.dp)) {
            Text(title, color = LensTextPrimary, fontWeight = FontWeight.SemiBold, fontSize = 16.sp)
            Text(
                subtitle.uppercase(),
                color = LensTextMuted,
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
                letterSpacing = 0.6.sp
            )
        }
    }
}
