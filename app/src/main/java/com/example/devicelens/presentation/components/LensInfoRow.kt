package com.example.devicelens.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.devicelens.ui.theme.LensSurfaceAlt
import com.example.devicelens.ui.theme.LensTextPrimary
import com.example.devicelens.ui.theme.LensTextSecondary
import com.example.devicelens.ui.theme.LensType

data class LensInfoItem(
    val label: String,
    val value: String,
    val icon: ImageVector? = null,
    val valueColor: Color? = null
)

@Composable
fun LensInfoRow(
    label: String,
    value: String,
    modifier: Modifier = Modifier,
    icon: ImageVector? = null,
    valueColor: Color = LensTextPrimary,
    monospaceValue: Boolean = true
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(if (icon == null) 35.dp else 42.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (icon != null) {
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(RoundedCornerShape(7.dp))
                    .background(LensSurfaceAlt),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    modifier = Modifier.size(17.dp),
                    tint = LensTextSecondary
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
        }
        Text(
            text = label,
            color = if (icon == null) LensTextSecondary else LensTextPrimary,
            style = LensType.body,
            modifier = Modifier.weight(1f)
        )
        Spacer(modifier = Modifier.width(12.dp))
        Text(
            text = value,
            color = valueColor,
            style = LensType.bodyMedium,
            fontFamily = if (monospaceValue) FontFamily.Monospace else FontFamily.SansSerif,
            textAlign = TextAlign.End,
            maxLines = 1
        )
    }
}
