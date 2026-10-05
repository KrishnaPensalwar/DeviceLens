package com.example.devicelens.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.devicelens.ui.theme.LensBackground
import com.example.devicelens.ui.theme.LensCyan
import com.example.devicelens.ui.theme.LensTextSecondary

@Composable
fun LensLoading(
    modifier: Modifier = Modifier,
    message: String? = null
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(LensBackground),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            CircularProgressIndicator(
                color = LensCyan,
                modifier = Modifier.size(36.dp)
            )
            if (message != null) {
                Spacer(modifier = Modifier.height(12.dp))
                LensBodyText(text = message, color = LensTextSecondary)
            }
        }
    }
}
