package com.example.devicelens.presentation.components

import android.graphics.BlurMaskFilter
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.outlined.Memory
import androidx.compose.material.icons.outlined.QueryStats
import androidx.compose.material.icons.outlined.Sensors
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.outlined.Storage
import androidx.compose.material.icons.outlined.Wifi
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.devicelens.ui.theme.LensBackground
import com.example.devicelens.ui.theme.LensBorder
import com.example.devicelens.ui.theme.LensCyan
import com.example.devicelens.ui.theme.LensGaugeTrack
import com.example.devicelens.ui.theme.LensGreen
import com.example.devicelens.ui.theme.LensMint
import com.example.devicelens.ui.theme.LensPurple
import com.example.devicelens.ui.theme.LensSurface
import com.example.devicelens.ui.theme.LensTextMuted
import com.example.devicelens.ui.theme.LensTextPrimary
import com.example.devicelens.ui.theme.LensTextSecondary
import com.example.devicelens.ui.theme.LensTrack
import com.example.devicelens.ui.theme.LensType

enum class LensTab {
    Diagnostics,
    Sensors,
    Storage,
    Network
}

@Composable
fun LensScreen(
    modifier: Modifier = Modifier,
    currentTab: LensTab,
    onTabSelected: (LensTab) -> Unit,
    content: @Composable ColumnScope.() -> Unit
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(LensBackground)
            .statusBarsPadding()
    ) {
        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            content = content
        )
        LensBottomBar(
            currentTab = currentTab,
            onTabSelected = onTabSelected
        )
    }
}

@Composable
fun LensTopBar(
    showBack: Boolean = false,
    onBack: (() -> Unit)? = null,
    onSettings: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(RoundedCornerShape(12.dp))
                .clickableWithoutRipple {
                    if (showBack) onBack?.invoke()
                },
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = if (showBack) {
                    Icons.AutoMirrored.Outlined.ArrowBack
                } else {
                    Icons.Outlined.Memory
                },
                contentDescription = if (showBack) "Back" else null,
                tint = LensTextPrimary,
                modifier = Modifier.size(22.dp)
            )
        }

        Text(
            text = "DeviceLens",
            modifier = Modifier.weight(1f),
            style = TextStyle(
                brush = Brush.linearGradient(
                    listOf(LensCyan, LensMint, LensGreen)
                ),
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
        )

        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(RoundedCornerShape(12.dp))
                .clickableWithoutRipple { onSettings?.invoke() },
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Outlined.Settings,
                contentDescription = "Settings",
                tint = LensTextSecondary,
                modifier = Modifier.size(22.dp)
            )
        }
    }
}

@Composable
fun LensCard(
    modifier: Modifier = Modifier,
    cornerRadius: Dp = 18.dp,
    contentPadding: PaddingValues = PaddingValues(0.dp),
    background: Color = LensSurface,
    border: Color = LensBorder,
    content: @Composable ColumnScope.() -> Unit
) {
    val shape = RoundedCornerShape(cornerRadius)
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(shape)
            .background(background)
            .border(1.dp, border, shape)
            .padding(contentPadding),
        content = content
    )
}

@Composable
fun GlowProgressBar(
    progress: Float,
    colors: List<Color>,
    modifier: Modifier = Modifier,
    height: Dp = 8.dp
) {
    val animated by animateFloatAsState(
        targetValue = progress.coerceIn(0f, 1f),
        animationSpec = tween(900),
        label = "glow-bar"
    )
    val glow = colors.lastOrNull() ?: LensCyan

    Canvas(
        modifier = modifier
            .fillMaxWidth()
            .height(height)
    ) {
        val radius = size.height / 2f
        val barWidth = size.width * animated

        drawRoundRect(
            color = LensTrack,
            cornerRadius = CornerRadius(radius, radius)
        )

        if (barWidth > 0f) {
            drawGlowRoundRect(
                color = glow,
                width = barWidth,
                height = size.height,
                radius = radius,
                blur = 16f
            )
            drawRoundRect(
                brush = Brush.horizontalGradient(colors),
                size = Size(barWidth, size.height),
                cornerRadius = CornerRadius(radius, radius)
            )
        }
    }
}

@Composable
fun HealthGauge(
    score: Int,
    label: String,
    modifier: Modifier = Modifier
) {
    val animated by animateFloatAsState(
        targetValue = (score / 100f).coerceIn(0f, 1f),
        animationSpec = tween(1100),
        label = "gauge"
    )
    val pulse by rememberInfiniteTransition(label = "gauge-pulse").animateFloat(
        initialValue = 0.35f,
        targetValue = 0.75f,
        animationSpec = infiniteRepeatable(
            animation = tween(1600, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse"
    )

    Box(
        modifier = modifier.size(210.dp),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val strokeOuter = 3.dp.toPx()
            val strokeInner = 12.dp.toPx()
            val pad = 18.dp.toPx()
            val innerPad = 28.dp.toPx()
            val start = -215f
            val sweepMax = 250f

            drawArc(
                color = Color.White.copy(alpha = 0.18f),
                startAngle = start,
                sweepAngle = sweepMax,
                useCenter = false,
                topLeft = Offset(pad, pad),
                size = Size(size.width - pad * 2, size.height - pad * 2),
                style = Stroke(width = strokeOuter, cap = StrokeCap.Round)
            )
            drawArc(
                color = Color.White.copy(alpha = 0.85f),
                startAngle = start,
                sweepAngle = sweepMax * animated,
                useCenter = false,
                topLeft = Offset(pad, pad),
                size = Size(size.width - pad * 2, size.height - pad * 2),
                style = Stroke(width = strokeOuter, cap = StrokeCap.Round)
            )

            val innerSize = Size(size.width - innerPad * 2, size.height - innerPad * 2)
            val innerOrigin = Offset(innerPad, innerPad)
            drawArc(
                color = LensGaugeTrack,
                startAngle = start,
                sweepAngle = sweepMax,
                useCenter = false,
                topLeft = innerOrigin,
                size = innerSize,
                style = Stroke(width = strokeInner, cap = StrokeCap.Round)
            )
            drawGlowArc(
                color = LensGreen.copy(alpha = pulse),
                startAngle = start,
                sweepAngle = sweepMax * animated,
                topLeft = innerOrigin,
                arcSize = innerSize,
                stroke = strokeInner,
                blur = 28f
            )
            drawArc(
                brush = Brush.sweepGradient(
                    listOf(LensMint, LensGreen, LensCyan)
                ),
                startAngle = start,
                sweepAngle = sweepMax * animated,
                useCenter = false,
                topLeft = innerOrigin,
                size = innerSize,
                style = Stroke(width = strokeInner, cap = StrokeCap.Round)
            )
        }

        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = score.toString(),
                color = LensTextPrimary,
                style = LensType.gauge
            )
            Text(
                text = label.uppercase(),
                color = LensCyan,
                style = LensType.status
            )
        }
    }
}

@Composable
private fun LensBottomBar(
    currentTab: LensTab,
    onTabSelected: (LensTab) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(LensBackground)
            .navigationBarsPadding()
            .padding(horizontal = 12.dp, vertical = 10.dp),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        LensNavItem(
            tab = LensTab.Diagnostics,
            icon = Icons.Outlined.QueryStats,
            selected = currentTab == LensTab.Diagnostics,
            onClick = { onTabSelected(LensTab.Diagnostics) }
        )
        LensNavItem(
            tab = LensTab.Sensors,
            icon = Icons.Outlined.Sensors,
            selected = currentTab == LensTab.Sensors,
            onClick = { onTabSelected(LensTab.Sensors) }
        )
        LensNavItem(
            tab = LensTab.Storage,
            icon = Icons.Outlined.Storage,
            selected = currentTab == LensTab.Storage,
            onClick = { onTabSelected(LensTab.Storage) }
        )
        LensNavItem(
            tab = LensTab.Network,
            icon = Icons.Outlined.Wifi,
            selected = currentTab == LensTab.Network,
            onClick = { onTabSelected(LensTab.Network) }
        )
    }
}

@Composable
private fun LensNavItem(
    tab: LensTab,
    icon: ImageVector,
    selected: Boolean,
    onClick: () -> Unit
) {
    val pill = if (tab == LensTab.Network) {
        LensPurple.copy(alpha = 0.28f)
    } else {
        LensCyan.copy(alpha = 0.18f)
    }
    val tint = when {
        selected && tab == LensTab.Network -> LensPurple
        selected -> LensCyan
        else -> LensTextMuted
    }

    Column(
        modifier = Modifier
            .clip(RoundedCornerShape(18.dp))
            .background(if (selected) pill else Color.Transparent)
            .clickableWithoutRipple(onClick)
            .padding(horizontal = 14.dp, vertical = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = icon,
            contentDescription = tab.name,
            tint = tint,
            modifier = Modifier.size(20.dp)
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = tab.name,
            color = tint,
            style = LensType.nav
        )
    }
}

@Composable
fun Modifier.clickableWithoutRipple(onClick: () -> Unit): Modifier = this.clickable(
    indication = null,
    interactionSource = remember { MutableInteractionSource() }
) {
    onClick()
}

private fun androidx.compose.ui.graphics.drawscope.DrawScope.drawGlowRoundRect(
    color: Color,
    width: Float,
    height: Float,
    radius: Float,
    blur: Float
) {
    val paint = androidx.compose.ui.graphics.Paint().asFrameworkPaint().apply {
        isAntiAlias = true
        this.color = color.toArgb()
        maskFilter = BlurMaskFilter(blur, BlurMaskFilter.Blur.NORMAL)
    }
    drawIntoCanvas { canvas ->
        canvas.nativeCanvas.drawRoundRect(0f, 0f, width, height, radius, radius, paint)
    }
}

private fun androidx.compose.ui.graphics.drawscope.DrawScope.drawGlowArc(
    color: Color,
    startAngle: Float,
    sweepAngle: Float,
    topLeft: Offset,
    arcSize: Size,
    stroke: Float,
    blur: Float
) {
    val paint = android.graphics.Paint().apply {
        isAntiAlias = true
        style = android.graphics.Paint.Style.STROKE
        strokeCap = android.graphics.Paint.Cap.ROUND
        strokeWidth = stroke
        this.color = color.toArgb()
        maskFilter = BlurMaskFilter(blur, BlurMaskFilter.Blur.NORMAL)
    }
    val oval = android.graphics.RectF(
        topLeft.x,
        topLeft.y,
        topLeft.x + arcSize.width,
        topLeft.y + arcSize.height
    )
    drawIntoCanvas { canvas ->
        canvas.nativeCanvas.drawArc(oval, startAngle, sweepAngle, false, paint)
    }
}
