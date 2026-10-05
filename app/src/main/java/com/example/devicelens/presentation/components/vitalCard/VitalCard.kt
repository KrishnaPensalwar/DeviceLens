package com.example.devicelens.presentation.components.vitalCard

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.devicelens.ui.theme.LensBorder
import com.example.devicelens.ui.theme.LensPurple
import com.example.devicelens.ui.theme.LensTextMuted
import com.example.devicelens.ui.theme.LensTextPrimary

@Composable
fun VitalCard(
    vitalCardModel: VitalCardModel,
    modifier: Modifier = Modifier,
    titleStyle: TextStyle = TextStyle(
        color = LensTextPrimary,
        fontWeight = FontWeight.SemiBold,
        fontSize = 16.sp,
    ),
    valueStyle: TextStyle = TextStyle(
        color = LensTextPrimary,
        fontSize = 24.sp,
        fontWeight = FontWeight.Bold,
    ),
    rightStyle: TextStyle = TextStyle(
        color = LensTextPrimary,
        fontSize = 16.sp,
        fontWeight = FontWeight.Medium,
    ),
    leftStyle: TextStyle = TextStyle(
        color = LensTextMuted,
        fontSize = 12.sp,
        fontWeight = FontWeight.Medium,
    ),
) {
    val finalTitleStyle = vitalCardModel.titleStyle ?: titleStyle
    val finalValueStyle = vitalCardModel.valueStyle ?: valueStyle
    val finalRightStyle = (vitalCardModel.rightStyle ?: rightStyle).let { style ->
        if (vitalCardModel.rightSubheadingColor != null) {
            style.copy(color = vitalCardModel.rightSubheadingColor)
        } else {
            style
        }
    }
    val finalLeftStyle = vitalCardModel.leftStyle ?: leftStyle

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .border(1.dp, LensBorder, RoundedCornerShape(16.dp))
            .padding(20.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            vitalCardModel.icon?.let { icon ->
                Box(
                    modifier = Modifier
                        .size(46.dp)
                        .clip(RoundedCornerShape(10.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = vitalCardModel.iconTint ?: LensPurple,
                        modifier = Modifier.size(26.dp)
                    )
                }
                Spacer(modifier = Modifier.width(10.dp))
            }

            Column(modifier = Modifier.weight(1f)) {
                vitalCardModel.title?.let { titleText ->
                    Text(
                        text = titleText,
                        style = finalTitleStyle
                    )
                }
                vitalCardModel.value?.let { valueText ->
                    Text(
                        text = valueText,
                        style = finalValueStyle
                    )
                }
            }

            vitalCardModel.right?.let { rightText ->
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = rightText,
                    style = finalRightStyle
                )
            }
        }

        if (vitalCardModel.left != null) {
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = vitalCardModel.left.uppercase(),
                    style = finalLeftStyle
                )
            }
        }
    }
}
