package com.example.devicelens.presentation.dashboard

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun DashboardScreen(
    onDeviceInfoClick: () -> Unit,
    onAppsClick: () -> Unit,
    onNetworkClick: () -> Unit,
    onStorageClick: () -> Unit,
    onHealthClick: () -> Unit,
) {
    Column(
        modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Button(
            onClick = onDeviceInfoClick
        ) {
            Text("Device Info")
        }
        Spacer(Modifier.height(10.dp))
        Button(
            onClick = onAppsClick
        ) {
            Text("Battery")
        }
        Spacer(Modifier.height(10.dp))
        Button(
            onClick = onNetworkClick
        ) {
            Text("Network")
        }
        Spacer(Modifier.height(10.dp))
        Button(
            onClick = onStorageClick
        ) {
            Text("Storage")
        }
        Spacer(Modifier.height(10.dp))
        Button(
            onClick = onHealthClick
        ) {
            Text("Health")
        }
    }

}