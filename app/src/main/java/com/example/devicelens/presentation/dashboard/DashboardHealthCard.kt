package com.example.devicelens.presentation.dashboard

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.HealthAndSafety
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.devicelens.presentation.components.clickableWithoutRipple
import com.example.devicelens.ui.theme.LensBorder
import com.example.devicelens.ui.theme.LensGreen
import com.example.devicelens.ui.theme.LensOnAccent
import com.example.devicelens.ui.theme.LensPurple
import com.example.devicelens.ui.theme.LensSurfaceAlt
import com.example.devicelens.ui.theme.LensTextMuted
import com.example.devicelens.ui.theme.LensTextPrimary
import kotlin.math.min

@Composable
fun DashboardHealthCard(
    statusLabel: String,
    onHealthClick: () -> Unit,
    score: Int
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .border(1.dp, LensBorder, RoundedCornerShape(18.dp))
            .padding(20.dp)
    )
    {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    "OVERALL HEALTH",
                    color = LensTextMuted,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    letterSpacing = 0.8.sp
                )
                Text(
                    statusLabel,
                    color = LensGreen,
                    fontSize = 38.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    "Everything looks healthy. Storage needs a little attention.",
                    color = LensTextMuted,
                    fontSize = 14.sp,
                    modifier = Modifier.padding(top = 6.dp, bottom = 14.dp)
                )
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(LensPurple)
                        .clickableWithoutRipple(onHealthClick)
                        .padding(horizontal = 16.dp, vertical = 11.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Outlined.HealthAndSafety,
                        null,
                        tint = LensOnAccent,
                        modifier = Modifier.size(18.dp)
                    )
                    Text(
                        "Run full check",
                        color = LensOnAccent,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 14.sp,
                        modifier = Modifier.padding(start = 8.dp)
                    )
                }
            }
            ScoreRing(score)
        }
    }
}

@Composable
private fun ScoreRing(score: Int) {
    val track = LensSurfaceAlt
    val arc = LensGreen
    val label = LensTextPrimary
    Box(modifier = Modifier.size(132.dp), contentAlignment = Alignment.Center) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val stroke = 12.dp.toPx()
            val diameter = min(size.width, size.height) - stroke
            val topLeft = Offset((size.width - diameter) / 2f, (size.height - diameter) / 2f)
            drawArc(
                track,
                0f,
                360f,
                false,
                topLeft,
                Size(diameter, diameter),
                style = Stroke(stroke, cap = StrokeCap.Round)
            )
            drawArc(
                arc,
                -90f,
                360f * (score / 100f),
                false,
                topLeft,
                Size(diameter, diameter),
                style = Stroke(stroke, cap = StrokeCap.Round)
            )
        }
        Text("$score", color = label, fontSize = 34.sp, fontWeight = FontWeight.Bold)
    }
}