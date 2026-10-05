package com.example.devicelens.presentation.usage.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.devicelens.core.util.TimeFormatter
import com.example.devicelens.domain.model.UsageBucket
import com.example.devicelens.presentation.components.clickableWithoutRipple
import com.example.devicelens.ui.theme.LensPurple
import com.example.devicelens.ui.theme.LensSurfaceAlt
import com.example.devicelens.ui.theme.LensTextMuted
import com.example.devicelens.ui.theme.LensTextPrimary

@Composable
fun UsageChart(buckets: List<UsageBucket>) {
    if (buckets.isEmpty()) return
    val max = buckets.maxOf { it.usageMillis }.coerceAtLeast(1L)
    val showTimeUnderBar = buckets.size <= 8
    var selected by rememberSaveable { mutableIntStateOf(-1) }
    val selectedBucket = buckets.getOrNull(selected)

    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(if (showTimeUnderBar) 148.dp else 128.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            verticalAlignment = Alignment.Bottom
        ) {
            buckets.forEachIndexed { index, bucket ->
                val fraction = bucket.usageMillis.toFloat() / max.toFloat()
                val active = index == selected
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .clickableWithoutRipple {
                            selected = if (selected == index) -1 else index
                        },
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Bottom
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height((96 * fraction).dp.coerceAtLeast(4.dp))
                            .clip(RoundedCornerShape(6.dp))
                            .background(if (active) LensPurple else LensSurfaceAlt)
                    )
                    Text(
                        text = bucket.label,
                        color = LensTextMuted,
                        fontSize = 10.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                    if (showTimeUnderBar) {
                        Text(
                            text = TimeFormatter.formatDuration(bucket.usageMillis),
                            color = if (active) LensTextPrimary else LensTextMuted,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.SemiBold,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        }
        if (!showTimeUnderBar && selectedBucket != null) {
            Text(
                text = "${selectedBucket.label} · ${TimeFormatter.formatDuration(selectedBucket.usageMillis)}",
                color = LensTextPrimary,
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.padding(top = 10.dp)
            )
        }
    }
}
