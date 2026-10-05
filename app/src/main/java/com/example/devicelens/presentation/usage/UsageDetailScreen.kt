package com.example.devicelens.presentation.usage

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AccessTime
import androidx.compose.material.icons.outlined.Apps
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.material.icons.outlined.History
import androidx.compose.material.icons.outlined.Launch
import androidx.compose.material.icons.outlined.QueryStats
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.devicelens.ui.theme.LensBackground
import com.example.devicelens.ui.theme.LensBlue
import com.example.devicelens.ui.theme.LensBlueDeep
import com.example.devicelens.ui.theme.LensBorder
import com.example.devicelens.ui.theme.LensCyan
import com.example.devicelens.ui.theme.LensCyanDark
import com.example.devicelens.ui.theme.LensPurple
import com.example.devicelens.ui.theme.LensSurface
import com.example.devicelens.ui.theme.LensSurfaceAlt
import com.example.devicelens.ui.theme.LensTextMuted
import com.example.devicelens.ui.theme.LensTextPrimary
import com.example.devicelens.ui.theme.LensTextSecondary
import com.example.devicelens.ui.theme.LensTrack
import com.example.devicelens.ui.theme.LensType
import com.example.devicelens.core.util.TimeFormatter
import com.example.devicelens.domain.model.AppUsageDetail
import com.example.devicelens.domain.model.UsagePeriod
import com.example.devicelens.presentation.usage.components.UsageChart
import com.example.devicelens.presentation.usage.components.periodLabel

// ================================================================
// MAIN SCREEN
// ================================================================

@Composable
fun UsageDetailScreen(
    onBack: () -> Unit = {},
    viewModel: UsageDetailViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    when {
        uiState.isLoading -> {
            UsageDetailLoading()
        }

        uiState.error != null -> {
            UsageDetailError(
                message = uiState.error!!,
                onRetry = viewModel::retry
            )
        }

        uiState.detail != null -> {
            UsageDetailContent(
                uiState = uiState,
                onPeriodSelected = viewModel::onPeriodSelected,
                onBack = onBack
            )
        }
    }
}


// ================================================================
// LOADING
// ================================================================

@Composable
private fun UsageDetailLoading() {

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(LensBackground),
        contentAlignment = Alignment.Center
    ) {

        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            CircularProgressIndicator(
                color = LensCyan
            )

            Spacer(
                modifier = Modifier.height(14.dp)
            )

            Text(
                text = "Loading usage details...",
                color = LensTextSecondary,
                fontSize = 14.sp
            )
        }
    }
}


// ================================================================
// ERROR
// ================================================================

@Composable
private fun UsageDetailError(
    message: String,
    onRetry: () -> Unit
) {

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(LensBackground)
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {

        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Icon(
                imageVector = Icons.Outlined.QueryStats,
                contentDescription = null,
                modifier = Modifier.size(44.dp),
                tint = LensCyan
            )

            Spacer(
                modifier = Modifier.height(16.dp)
            )

            Text(
                text = "Unable to load usage",
                color = LensTextPrimary,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(
                modifier = Modifier.height(8.dp)
            )

            Text(
                text = message,
                color = LensTextSecondary,
                fontSize = 14.sp
            )

            Spacer(
                modifier = Modifier.height(20.dp)
            )

            Button(
                onClick = onRetry
            ) {
                Text("Try again")
            }
        }
    }
}


// ================================================================
// MAIN CONTENT
// ================================================================

@Composable
private fun UsageDetailContent(
    uiState: UsageDetailUiState,
    onPeriodSelected: (UsagePeriod) -> Unit,
    onBack: () -> Unit
) {

    val detail = uiState.detail ?: return

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(LensBackground),

        contentPadding = PaddingValues(
            start = 16.dp,
            end = 16.dp,
            top = 14.dp,
            bottom = 28.dp
        ),

        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {

        // ========================================================
        // TOP BAR
        // ========================================================

        item {
            Text(
                text = detail.app.appName,
                color = LensTextPrimary,
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = detail.app.packageName,
                color = LensTextSecondary,
                fontSize = 14.sp,
                modifier = Modifier.padding(top = 2.dp)
            )
        }


        // ========================================================
        // APP HEADER
        // ========================================================

        item {

            AppIdentityCard(
                appName = detail.app.appName,
                packageName = detail.app.packageName
            )
        }


        // ========================================================
        // PERIOD SELECTOR
        // ========================================================

        item {

            UsagePeriodSelector(
                selected = uiState.period,
                onSelected = onPeriodSelected
            )
        }


        // ========================================================
        // SUMMARY
        // ========================================================

        item {

            UsageSummaryCard(
                detail = detail
            )
        }


        // ========================================================
        // BREAKDOWN
        // ========================================================

        item {

            UsageBreakdownCard(
                detail = detail
            )
        }


        // ========================================================
        // TIMELINE HEADER
        // ========================================================

        item {

            TimelineHeader()
        }


        // ========================================================
        // TIMELINE
        // ========================================================

        if (detail.sessions.isEmpty()) {

            item {

                EmptyTimelineCard()
            }

        } else {

            itemsIndexed(
                items = detail.sessions,
                key = { _, session ->
                    session.startMillis
                }
            ) { index, session ->

                TimelineSessionItem(
                    startMillis = session.startMillis,
                    endMillis = session.endMillis,
                    durationMillis = session.durationMillis,
                    isFirst = index == 0,
                    isLast = index == detail.sessions.lastIndex
                )
            }
        }
    }
}


// ================================================================
// TOP BAR
// ================================================================

@Composable
private fun DeviceLensTopBar(
    onBack: () -> Unit
) {

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(54.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {

        IconButton(
            onClick = onBack,
            modifier = Modifier.size(42.dp)
        ) {

            Icon(
                imageVector = Icons.Outlined.ArrowBack,
                contentDescription = "Back",
                tint = LensCyan,
                modifier = Modifier.size(24.dp)
            )
        }

        Box(
            modifier = Modifier.weight(1f),
            contentAlignment = Alignment.Center
        ) {

            Text(
                text = "DeviceLens",
                color = LensCyan,
                fontSize = 26.sp,
                fontWeight = FontWeight.Bold
            )
        }

        IconButton(
            onClick = {},
            modifier = Modifier.size(42.dp)
        ) {

            Icon(
                imageVector = Icons.Outlined.Settings,
                contentDescription = "Settings",
                tint = LensTextSecondary,
                modifier = Modifier.size(24.dp)
            )
        }
    }
}


// ================================================================
// APP IDENTITY CARD
// ================================================================

@Composable
private fun AppIdentityCard(
    appName: String,
    packageName: String
) {

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(22.dp))
            .background(LensSurfaceAlt)
            .border(
                width = 1.dp,
                color = LensBorder,
                shape = RoundedCornerShape(22.dp)
            )
            .padding(
                horizontal = 22.dp,
                vertical = 22.dp
            ),
        verticalAlignment = Alignment.CenterVertically
    ) {

        // --------------------------------------------------------
        // APP ICON
        // --------------------------------------------------------

        Box(
            modifier = Modifier
                .size(80.dp)
                .clip(RoundedCornerShape(17.dp))
                .background(LensBackground),
            contentAlignment = Alignment.Center
        ) {

            Icon(
                imageVector = Icons.Outlined.Apps,
                contentDescription = null,
                modifier = Modifier.size(34.dp),
                tint = LensCyan
            )
        }

        Spacer(
            modifier = Modifier.width(22.dp)
        )

        // --------------------------------------------------------
        // APP NAME
        // --------------------------------------------------------

        Column(
            modifier = Modifier.weight(1f)
        ) {

            Text(
                text = appName,
                color = LensTextPrimary,
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(
                modifier = Modifier.height(4.dp)
            )

            Text(
                text = packageName,
                color = LensTextSecondary,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                fontFamily = FontFamily.Monospace,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}


// ================================================================
// PERIOD SELECTOR
// ================================================================

@Composable
private fun UsagePeriodSelector(
    selected: UsagePeriod,
    onSelected: (UsagePeriod) -> Unit
) {

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {

        UsagePeriod.entries.forEach { period ->

            val isSelected = selected == period

            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(38.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .background(
                        if (isSelected) {
                            LensCyanDark
                        } else {
                            LensSurfaceAlt
                        }
                    )
                    .border(
                        width = 1.dp,
                        color = if (isSelected) {
                            LensCyanDark
                        } else {
                            LensBorder
                        },
                        shape = RoundedCornerShape(20.dp)
                    )
                    .clickableWithoutRipple {
                        onSelected(period)
                    },
                contentAlignment = Alignment.Center
            ) {

                Text(
                    text = periodLabel(period),
                    color = if (isSelected) {
                        Color.White
                    } else {
                        LensTextSecondary
                    },
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace
                )
            }
        }
    }
}


// ================================================================
// USAGE SUMMARY
// ================================================================

@Composable
private fun UsageSummaryCard(
    detail: AppUsageDetail
) {

    val percentage = detail.app.percentOfTotal
        .coerceIn(0.0, 100.0)

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(24.dp))
            .background(LensSurface)
            .border(
                width = 1.dp,
                color = LensBorder,
                shape = RoundedCornerShape(24.dp)
            )
            .padding(22.dp)
    ) {

        // --------------------------------------------------------
        // TITLE
        // --------------------------------------------------------

        SectionTitle(
            icon = Icons.Outlined.QueryStats,
            title = "Usage Summary"
        )

        Spacer(
            modifier = Modifier.height(24.dp)
        )

        // --------------------------------------------------------
        // USAGE + PERCENTAGE
        // --------------------------------------------------------

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.Bottom
        ) {

            Column(
                modifier = Modifier.weight(1f)
            ) {

                Text(
                    text = "TOTAL USAGE",
                    color = LensTextSecondary,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace,
                    letterSpacing = 1.5.sp
                )

                Spacer(
                    modifier = Modifier.height(4.dp)
                )

                Text(
                    text = TimeFormatter.formatDuration(
                        detail.app.usageMillis
                    ),
                    color = LensCyan,
                    fontSize = 58.sp,
                    lineHeight = 58.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Column(
                horizontalAlignment = Alignment.End
            ) {

                Text(
                    text = "%.0f%%".format(
                        percentage
                    ),
                    color = LensBlue,
                    fontSize = 27.sp,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = "of total\nusage",
                    color = LensTextSecondary,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace
                )
            }
        }

        Spacer(
            modifier = Modifier.height(20.dp)
        )

        // --------------------------------------------------------
        // PROGRESS
        // --------------------------------------------------------

        LinearProgressIndicator(
            progress = {
                (percentage / 100f).toFloat()
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp)
                .clip(CircleShape),
            color = LensCyan,
            trackColor = LensBackground
        )

        Spacer(
            modifier = Modifier.height(20.dp)
        )

        // --------------------------------------------------------
        // METRICS
        // --------------------------------------------------------

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {

            DetailMetricCard(
                modifier = Modifier.weight(1f),
                icon = Icons.Outlined.AccessTime,
                label = "Last used",
                value = TimeFormatter.formatLastUsed(
                    detail.app.lastUsedMillis
                )
            )

            detail.app.launchCount?.let { count ->

                DetailMetricCard(
                    modifier = Modifier.weight(1f),
                    icon = Icons.Outlined.Launch,
                    label = "Launches",
                    value = count.toString()
                )
            }
        }
    }
}


// ================================================================
// DETAIL METRIC
// ================================================================

@Composable
private fun DetailMetricCard(
    modifier: Modifier,
    icon: ImageVector,
    label: String,
    value: String
) {

    Column(
        modifier = modifier
            .heightIn(min = 78.dp)
            .clip(RoundedCornerShape(14.dp))
            .background(LensSurfaceAlt)
            .border(
                width = 1.dp,
                color = LensBorder,
                shape = RoundedCornerShape(14.dp)
            )
            .padding(13.dp)
    ) {

        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {

            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(15.dp),
                tint = LensTextSecondary
            )

            Spacer(
                modifier = Modifier.width(6.dp)
            )

            Text(
                text = label,
                color = LensTextSecondary,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Monospace
            )
        }

        Spacer(
            modifier = Modifier.height(8.dp)
        )

        Text(
            text = value,
            color = LensTextPrimary,
            fontSize = 15.sp,
            fontWeight = FontWeight.SemiBold
        )
    }
}


// ================================================================
// BREAKDOWN
// ================================================================

@Composable
private fun UsageBreakdownCard(
    detail: AppUsageDetail
) {

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(24.dp))
            .background(LensSurface)
            .border(
                width = 1.dp,
                color = LensBorder,
                shape = RoundedCornerShape(24.dp)
            )
            .padding(
                horizontal = 22.dp,
                vertical = 22.dp
            )
    ) {

        SectionTitle(
            icon = Icons.Outlined.QueryStats,
            title = "Usage Breakdown"
        )

        Spacer(
            modifier = Modifier.height(18.dp)
        )

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 180.dp)
        ) {

            UsageChart(
                detail.buckets
            )
        }
    }
}


// ================================================================
// SECTION TITLE
// ================================================================

@Composable
private fun SectionTitle(
    icon: ImageVector,
    title: String
) {

    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {

        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier.size(21.dp),
            tint = LensTextSecondary
        )

        Spacer(
            modifier = Modifier.width(9.dp)
        )

        Text(
            text = title,
            color = LensTextSecondary,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold
        )
    }
}


// ================================================================
// TIMELINE HEADER
// ================================================================

@Composable
private fun TimelineHeader() {

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                top = 2.dp,
                bottom = 2.dp
            ),
        verticalAlignment = Alignment.CenterVertically
    ) {

        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(LensBlueDeep),
            contentAlignment = Alignment.Center
        ) {

            Icon(
                imageVector = Icons.Outlined.History,
                contentDescription = null,
                modifier = Modifier.size(23.dp),
                tint = LensBlue
            )
        }

        Spacer(
            modifier = Modifier.width(16.dp)
        )

        Column {

            Text(
                text = "Timeline",
                color = LensTextPrimary,
                fontSize = 19.sp,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = "Recent app sessions",
                color = LensTextSecondary,
                fontSize = 13.sp
            )
        }
    }
}


// ================================================================
// TIMELINE SESSION
// ================================================================

@Composable
private fun TimelineSessionItem(
    startMillis: Long,
    endMillis: Long,
    durationMillis: Long,
    isFirst: Boolean,
    isLast: Boolean
) {

    Row(
        modifier = Modifier.fillMaxWidth()
    ) {

        // --------------------------------------------------------
        // TIMELINE RAIL
        // --------------------------------------------------------

        Box(
            modifier = Modifier
                .width(32.dp)
                .height(76.dp),
            contentAlignment = Alignment.TopCenter
        ) {

            if (!isFirst) {

                Box(
                    modifier = Modifier
                        .width(2.dp)
                        .fillMaxHeight()
                        .align(Alignment.TopCenter)
                        .background(
                            LensTrack
                        )
                )
            }

            Box(
                modifier = Modifier
                    .padding(top = 13.dp)
                    .size(8.dp)
                    .clip(CircleShape)
                    .background(
                        if (isFirst) {
                            LensCyan
                        } else {
                            LensPurple
                        }
                    )
            )
        }

        Spacer(
            modifier = Modifier.width(4.dp)
        )

        // --------------------------------------------------------
        // SESSION CARD
        // --------------------------------------------------------

        Column(
            modifier = Modifier
                .weight(1f)
                .clip(RoundedCornerShape(14.dp))
                .background(LensSurfaceAlt)
                .border(
                    width = 1.dp,
                    color = LensBorder,
                    shape = RoundedCornerShape(14.dp)
                )
                .padding(
                    horizontal = 15.dp,
                    vertical = 13.dp
                )
        ) {

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {

                Column(
                    modifier = Modifier.weight(1f)
                ) {

                    Text(
                        text = TimeFormatter.formatSession(
                            startMillis,
                            endMillis
                        ),
                        color = LensTextPrimary,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(
                        modifier = Modifier.height(3.dp)
                    )

                    Text(
                        text = "Session duration",
                        color = LensTextSecondary,
                        fontSize = 12.sp
                    )
                }

                Text(
                    text = TimeFormatter.formatDuration(
                        durationMillis
                    ),
                    color = if (isFirst) {
                        LensCyan
                    } else {
                        LensBlue
                    },
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace
                )
            }
        }
    }
}


// ================================================================
// EMPTY TIMELINE
// ================================================================

@Composable
private fun EmptyTimelineCard() {

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(LensSurfaceAlt)
            .border(
                width = 1.dp,
                color = LensBorder,
                shape = RoundedCornerShape(16.dp)
            )
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {

        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Icon(
                imageVector = Icons.Outlined.History,
                contentDescription = null,
                modifier = Modifier.size(30.dp),
                tint = LensTextMuted
            )

            Spacer(
                modifier = Modifier.height(10.dp)
            )

            Text(
                text = "No sessions found",
                color = LensTextPrimary,
                fontSize = 15.sp,
                fontWeight = FontWeight.SemiBold
            )

            Spacer(
                modifier = Modifier.height(4.dp)
            )

            Text(
                text = "There is no session timeline for this period.",
                color = LensTextMuted,
                fontSize = 12.sp
            )
        }
    }
}


// ================================================================
// CLICKABLE WITHOUT RIPPLE
// ================================================================

@Composable
private fun Modifier.clickableWithoutRipple(
    onClick: () -> Unit
): Modifier {

    val interactionSource = remember {
        MutableInteractionSource()
    }

    return this.clickable(
        interactionSource = interactionSource,
        indication = null,
        onClick = onClick
    )
}