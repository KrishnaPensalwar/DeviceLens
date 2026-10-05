package com.example.devicelens.presentation.battery.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.BatteryStd
import androidx.compose.material.icons.outlined.ElectricBolt
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.devicelens.domain.model.BatteryInfo
import com.example.devicelens.presentation.battery.batteryStatusTitle
import com.example.devicelens.presentation.battery.chargingStatusText
import com.example.devicelens.presentation.components.LensCard
import com.example.devicelens.ui.theme.LensBorder
import com.example.devicelens.ui.theme.LensCyan
import com.example.devicelens.ui.theme.LensGreen
import com.example.devicelens.ui.theme.LensTextPrimary
import com.example.devicelens.ui.theme.LensTextSecondary
import com.example.devicelens.ui.theme.LensTrack

@Composable
fun BatteryOverviewCard(
    batteryInfo: BatteryInfo,
    modifier: Modifier = Modifier
) {
    LensCard(
        modifier = modifier.fillMaxWidth(),
        cornerRadius = 14.dp,
        contentPadding = PaddingValues(horizontal = 19.dp, vertical = 20.dp)
    ) {
        BatteryCardHeader(
            icon = Icons.Outlined.BatteryStd,
            title = "Battery Status",
            subtitle = batteryStatusTitle(batteryInfo)
        )
        Spacer(modifier = Modifier.height(22.dp))
        HorizontalDivider(color = LensBorder)
        Spacer(modifier = Modifier.height(22.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "${batteryInfo.level}%",
                    color = LensTextPrimary,
                    fontSize = 42.sp,
                    lineHeight = 42.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(5.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .clip(CircleShape)
                            .background(if (batteryInfo.isCharging) LensCyan else LensGreen)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = chargingStatusText(batteryInfo),
                        color = LensTextSecondary,
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
        Spacer(modifier = Modifier.height(22.dp))
        LinearProgressIndicator(
            progress = { batteryInfo.level.coerceIn(0, 100) / 100f },
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp)
                .clip(CircleShape),
            color = LensCyan,
            trackColor = LensTrack
        )
        Spacer(modifier = Modifier.height(9.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "0%",
                color = LensTextSecondary,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Monospace
            )
            Text(
                text = "100%",
                color = LensTextSecondary,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Monospace
            )
        }
    }
}

@Composable
private fun BatteryCircularIndicator(
    level: Int,
    isCharging: Boolean
) {
    Box(
        modifier = Modifier.size(70.dp),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator(
            progress = { level.coerceIn(0, 100) / 100f },
            modifier = Modifier.fillMaxSize(),
            color = LensCyan,
            trackColor = LensTrack,
            strokeWidth = 6.dp,
            strokeCap = StrokeCap.Round
        )
        Icon(
            imageVector = if (isCharging) Icons.Outlined.ElectricBolt else Icons.Outlined.BatteryStd,
            contentDescription = null,
            modifier = Modifier.size(25.dp),
            tint = LensCyan
        )
    }
}
