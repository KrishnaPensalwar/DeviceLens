package com.example.devicelens.presentation.battery


import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.devicelens.domain.model.BatteryInfo

@Composable
fun BatteryScreen(
    viewModel: BatteryViewModel = hiltViewModel()
) {
    val uiState by viewModel.batteryState.collectAsStateWithLifecycle()

    BatteryContent(
        uiState = uiState
    )
}

@Composable
private fun BatteryContent(
    uiState: BatteryUiState
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {

        Text(
            text = "Battery Information",
            style = MaterialTheme.typography.headlineSmall
        )

        when {
            uiState.isLoading -> {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    CircularProgressIndicator()

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = "Loading battery information..."
                    )
                }
            }

            uiState.error != null -> {
                Text(
                    text = uiState.error,
                    color = MaterialTheme.colorScheme.error
                )
            }

            uiState.batteryInfo != null -> {
                BatteryInfoCard(
                    batteryInfo = uiState.batteryInfo
                )
            }
        }
    }
}
@Composable
private fun BatteryInfoCard(
    batteryInfo: BatteryInfo
) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {

            BatteryInfoRow(
                label = "Battery Level",
                value = "${batteryInfo.level}%"
            )

            BatteryInfoRow(
                label = "Capacity",
                value = batteryInfo.capacity?.let {
                    "$it mAh"
                } ?: "Unknown"
            )

            BatteryInfoRow(
                label = "Charging",
                value = if (batteryInfo.isCharging) {
                    "Yes"
                } else {
                    "No"
                }
            )

            BatteryInfoRow(
                label = "Charging Type",
                value = batteryInfo.chargingType.name
            )

            BatteryInfoRow(
                label = "Battery Status",
                value = batteryInfo.batteryStatus.name
            )

            BatteryInfoRow(
                label = "Health",
                value = batteryInfo.health.name
            )

            BatteryInfoRow(
                label = "Temperature",
                value = batteryInfo.temperature?.let {
                    "$it °C"
                } ?: "Unknown"
            )

            BatteryInfoRow(
                label = "Voltage",
                value = batteryInfo.voltage?.let {
                    "$it mV"
                } ?: "Unknown"
            )

            BatteryInfoRow(
                label = "Technology",
                value = batteryInfo.technology ?: "Unknown"
            )

            BatteryInfoRow(
                label = "Battery Saver",
                value = if (batteryInfo.isBatterySaverEnabled) {
                    "Enabled"
                } else {
                    "Disabled"
                }
            )
        }
    }
}
@Composable
private fun BatteryInfoRow(
    label: String,
    value: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyLarge
        )

        Text(
            text = value,
            style = MaterialTheme.typography.bodyLarge
        )
    }
}