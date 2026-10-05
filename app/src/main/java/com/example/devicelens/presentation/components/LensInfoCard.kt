package com.example.devicelens.presentation.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.devicelens.presentation.device.components.LensHairline
import com.example.devicelens.ui.theme.LensBlue
import com.example.devicelens.ui.theme.LensTextPrimary
import com.example.devicelens.ui.theme.LensTextSecondary

@Composable
fun LensCardHeader(
    icon: ImageVector,
    title: String,
    subtitle: String,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {

        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier.size(31.dp),
            tint = LensBlue
        )

        Spacer(modifier = Modifier.width(16.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                color = LensTextPrimary,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = subtitle,
                color = LensTextSecondary,
                fontSize = 14.sp
            )
        }
    }
}

@Composable
fun LensInfoCard(
    title: String,
    subtitle: String,
    icon: ImageVector,
    items: List<LensInfoItem>,
    modifier: Modifier = Modifier
) {
    LensCard(
        modifier = modifier,
        cornerRadius = 14.dp,
        contentPadding = PaddingValues(horizontal = 19.dp, vertical = 20.dp)
    ) {
        LensCardHeader(icon = icon, title = title, subtitle = subtitle)
        Spacer(modifier = Modifier.height(18.dp))
        items.forEachIndexed { index, item ->
            LensInfoRow(
                label = item.label,
                value = item.value,
                icon = item.icon,
                valueColor = item.valueColor ?: LensTextPrimary
            )
            if (index != items.lastIndex) {
                LensHairline()
            }
        }
    }
}
