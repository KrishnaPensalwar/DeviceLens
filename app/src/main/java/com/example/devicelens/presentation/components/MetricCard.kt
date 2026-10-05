package com.example.devicelens.presentation.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.example.devicelens.ui.theme.LensTextSecondary

@Composable
fun LensMetricCard(
    title: String,
    watermark: ImageVector,
    watermarkTint: Color,
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit
) {
    LensCard(modifier = modifier) {
        Box(modifier = Modifier.fillMaxWidth()) {
            Icon(
                imageVector = watermark,
                contentDescription = null,
                tint = watermarkTint,
                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .padding(end = 8.dp)
                    .size(88.dp)
                    .alpha(0.9f)
            )
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(18.dp)
            ) {
                LensSectionLabel(text = title, color = LensTextSecondary)
                Spacer(modifier = Modifier.height(6.dp))
                content()
            }
        }
    }
}
