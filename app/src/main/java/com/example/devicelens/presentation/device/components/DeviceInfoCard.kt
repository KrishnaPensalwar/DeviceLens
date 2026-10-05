package com.example.devicelens.presentation.device.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.example.devicelens.presentation.components.LensCard
import com.example.devicelens.presentation.components.LensInfoRow
import com.example.devicelens.ui.theme.LensDivider
import com.example.devicelens.ui.theme.LensType

data class InfoItem(
    val label: String,
    val value: String
)

@Composable
fun DeviceInfoCard(
    title: String,
    tint: Color,
    items: List<InfoItem>,
    icon: ImageVector
) {
    LensCard(
        cornerRadius = 11.dp,
        contentPadding = PaddingValues(
            horizontal = 20.dp,
            vertical = 16.dp
        )
    ) {
        LensSectionHeader(
            icon = icon,
            title = title,
            color = tint
        )

        Spacer(modifier = Modifier.height(10.dp))

        items.forEachIndexed { index, item ->

            LensInfoRow(
                label = item.label,
                value = item.value
            )

            if (index != items.lastIndex) {
                LensHairline()
            }
        }
    }
}


@Composable
fun LensSectionHeader(
    icon: ImageVector,
    title: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = color,
                modifier = Modifier.size(22.dp)
            )
            Spacer(modifier = Modifier.width(10.dp))
            Text(
                text = title,
                color = color,
                style = LensType.cardTitle
            )
        }
        Spacer(modifier = Modifier.height(11.dp))
        LensHairline()
    }
}

@Composable
fun LensHairline(modifier: Modifier = Modifier, color: Color = LensDivider) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(1.dp)
            .background(color)
    )
}
