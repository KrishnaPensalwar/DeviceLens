package com.example.devicelens.presentation.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.devicelens.ui.theme.LensCyan
import com.example.devicelens.ui.theme.LensTextSecondary
import com.example.devicelens.ui.theme.LensType

@Composable
fun LensBackBar(
    title: String,
    modifier: Modifier = Modifier,
    onBackPress: () -> Unit
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(65.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.Outlined.ArrowBack,
            contentDescription = "Back",
            tint = LensCyan,
            modifier = Modifier
                .size(24.dp)
                .clickable(onClick = onBackPress)
        )
        Text(
            text = title,
            color = LensCyan,
            style = LensType.screenTitle,
            modifier = Modifier.weight(1f),
            textAlign = TextAlign.Center
        )
        Icon(
            imageVector = Icons.Outlined.Settings,
            contentDescription = "Settings",
            tint = LensTextSecondary,
            modifier = Modifier.size(24.dp)
        )
    }
}
