package com.example.devicelens.presentation.dashboard

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.BatteryFull
import androidx.compose.material.icons.outlined.Memory
import androidx.compose.material.icons.outlined.Storage
import androidx.compose.material.icons.outlined.Thermostat
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import com.example.devicelens.core.util.FileSizeFormatter
import com.example.devicelens.domain.model.DeviceInfo
import com.example.devicelens.presentation.components.vitalCard.VitalCard
import com.example.devicelens.presentation.components.vitalCard.VitalCardModel
import com.example.devicelens.ui.theme.LensBlue
import com.example.devicelens.ui.theme.LensGreen
import com.example.devicelens.ui.theme.LensOrange

@Composable
fun VitalCardsSection(device: DeviceInfo?) {
    Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
        VitalCard(
            VitalCardModel(
                title = "Battery",
                icon = Icons.Outlined.BatteryFull,
                value = "${device?.battery?.percent ?: 0}%",
                rightSubheadingColor = LensGreen,
                right = "Normal"
            )
        )
        VitalCard(
            VitalCardModel(
                title = "Storage",
                icon = Icons.Outlined.Storage,
                value = "${device?.storage?.usagePercent ?: 0}%",
                rightSubheadingColor = LensOrange,
                right = storageStatus(device)
            )
        )
        VitalCard(
            VitalCardModel(
                "Temperature",
                Icons.Outlined.Thermostat,
                "${device?.battery?.temperatureC?.toInt() ?: 0}°C",
                LensGreen,
                right = if ((device?.battery?.temperatureC ?: 32f) <= 40f) "Optimal" else "Warm"
            )
        )
        VitalCard(
            VitalCardModel(
                "RAM",
                Icons.Outlined.Memory,
                device?.memory?.usedBytes?.let(FileSizeFormatter::format) ?: "—",
                LensBlue,
                right = "Moderate"
            )
        )
    }
}


private fun storageStatus(device: DeviceInfo?): String {
    val percent = device?.storage?.usagePercent ?: 0
    return if (percent >= 80) "Getting full" else "Healthy"
}
