package com.example.devicelens.presentation.battery

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.BatteryAlert
import androidx.compose.material.icons.outlined.HealthAndSafety
import androidx.compose.material.icons.outlined.Power
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.devicelens.domain.model.BatteryInfo
import com.example.devicelens.presentation.battery.components.BatteryInfoCard
import com.example.devicelens.presentation.battery.components.BatteryOverviewCard
import com.example.devicelens.presentation.battery.data.batteryHealthItems
import com.example.devicelens.presentation.battery.data.powerDetailItems
import com.example.devicelens.presentation.components.LensCard
import com.example.devicelens.presentation.components.LensLoading
import com.example.devicelens.ui.theme.LensBackground
import com.example.devicelens.ui.theme.LensCyan
import com.example.devicelens.ui.theme.LensTextPrimary
import com.example.devicelens.ui.theme.LensTextSecondary

@Composable
fun BatteryScreen(
    viewModel: BatteryViewModel = hiltViewModel(),
) {
    val uiState by viewModel.batteryState.collectAsStateWithLifecycle()

    when {
        uiState.isLoading -> LensLoading(message = "Loading battery...")
        uiState.error != null -> BatteryError(uiState.error, viewModel::getBatteryInfo)
        uiState.batteryInfo != null -> BatteryContent(uiState.batteryInfo!!)
    }
}

@Composable
private fun BatteryContent(batteryInfo: BatteryInfo) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(LensBackground),
        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 10.dp, bottom = 18.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item { BatteryOverviewCard(batteryInfo) }
        item {
            BatteryInfoCard(
                title = "Power Details",
                subtitle = "Charging and electrical information",
                icon = Icons.Outlined.Power,
                items = powerDetailItems(batteryInfo)
            )
        }
        item {
            BatteryInfoCard(
                title = "Battery Health",
                subtitle = "Condition and temperature",
                icon = Icons.Outlined.HealthAndSafety,
                items = batteryHealthItems(batteryInfo)
            )
        }
    }
}

@Composable
private fun BatteryError(
    error: String?,
    onRetry: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(LensBackground)
            .padding(20.dp),
        contentAlignment = Alignment.Center
    ) {
        LensCard(
            modifier = Modifier.fillMaxWidth(),
            cornerRadius = 14.dp,
            contentPadding = PaddingValues(horizontal = 19.dp, vertical = 20.dp)
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(
                    imageVector = Icons.Outlined.BatteryAlert,
                    contentDescription = null,
                    modifier = Modifier.size(42.dp),
                    tint = LensCyan
                )
                Spacer(modifier = Modifier.height(14.dp))
                Text(
                    text = "Battery information unavailable",
                    color = LensTextPrimary,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = error ?: "Something went wrong",
                    color = LensTextSecondary,
                    fontSize = 14.sp
                )
                Spacer(modifier = Modifier.height(18.dp))
                Button(onClick = onRetry) { Text("Try again") }
            }
        }
    }
}
