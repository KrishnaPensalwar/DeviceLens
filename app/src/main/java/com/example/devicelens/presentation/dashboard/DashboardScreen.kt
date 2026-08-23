package com.example.devicelens.presentation.dashboard

import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable

@Composable
fun DashboardScreen(
    onDeviceInfoClick: () -> Unit
) {
    Button(
        onClick = onDeviceInfoClick
    ) {
        Text("Device Info")
    }
}