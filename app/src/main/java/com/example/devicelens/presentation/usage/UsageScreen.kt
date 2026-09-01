package com.example.devicelens.presentation.usage

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AccessTime
import androidx.compose.material.icons.outlined.Apps
import androidx.compose.material.icons.outlined.BarChart
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Language
import androidx.compose.material.icons.outlined.Memory
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Refresh
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.outlined.ShowChart
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.devicelens.core.util.TimeFormatter
import com.example.devicelens.domain.model.UsagePeriod

// ================================================================
// COLORS
// ================================================================

private val ScreenBackground = Color(0xFF202124)
private val PanelBackground = Color(0xFF0D1110)
private val CardBackground = Color(0xFF1A1F1E)
private val CardBackgroundLight = Color(0xFF202524)
private val BorderColor = Color(0xFF303634)

private val TextPrimary = Color(0xFFE8ECEB)
private val TextSecondary = Color(0xFFB7C0BD)
private val TextMuted = Color(0xFF7E8884)

private val Cyan = Color(0xFF68D5D1)
private val CyanDark = Color(0xFF008E8B)
private val Blue = Color(0xFF8CA8FF)
private val Green = Color(0xFF00C58A)


// ================================================================
// MAIN SCREEN
// ================================================================

@Composable
fun UsageScreen(
    onAppClick: (String) -> Unit,
    viewModel: UsageViewModel = hiltViewModel()
) {

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val lifecycleOwner = LocalLifecycleOwner.current

    DisposableEffect(lifecycleOwner) {

        val observer = LifecycleEventObserver { _, event ->

            if (event == Lifecycle.Event.ON_RESUME) {
                viewModel.refreshPermission()
            }
        }

        lifecycleOwner.lifecycle.addObserver(observer)

        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    when {

        !uiState.hasUsageAccess -> {
            UsageAccessScreen()
        }

        uiState.isLoading -> {
            LoadingContent()
        }

        uiState.error != null -> {
            ErrorContent(
                message = uiState.error!!,
                onRetry = viewModel::refresh
            )
        }

        else -> {
            UsageContent(
                uiState = uiState,
                onPeriodSelected = viewModel::onPeriodSelected,
                onRefresh = viewModel::refresh,
                onAppClick = onAppClick
            )
        }
    }
}


// ================================================================
// MAIN CONTENT
// ================================================================

@Composable
private fun UsageContent(
    uiState: UsageUiState,
    onPeriodSelected: (UsagePeriod) -> Unit,
    onRefresh: () -> Unit,
    onAppClick: (String) -> Unit
) {

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(ScreenBackground)
    ) {

        // ---------------------------------------------------------
        // DOT BACKGROUND
        // ---------------------------------------------------------

        DottedBackground()

        // ---------------------------------------------------------
        // MAIN PANEL
        // ---------------------------------------------------------

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(
                    horizontal = 48.dp,
                    vertical = 0.dp
                )
                .clip(
                    RoundedCornerShape(
                        topStart = 16.dp,
                        topEnd = 16.dp
                    )
                )
                .background(PanelBackground)
                .border(
                    width = 1.dp,
                    color = BorderColor,
                    shape = RoundedCornerShape(
                        topStart = 16.dp,
                        topEnd = 16.dp
                    )
                ),
            contentAlignment = Alignment.BottomCenter
        ) {

            LazyColumn(
                modifier = Modifier.fillMaxSize(),

                contentPadding = PaddingValues(
                    start = 16.dp,
                    end = 16.dp,
                    top = 0.dp,
                    bottom = 95.dp
                ),

                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {

                // -------------------------------------------------
                // DEVICE HEADER
                // -------------------------------------------------

                item {
                    DeviceLensHeader(
                        onRefresh = onRefresh,
                        isRefreshing = uiState.isRefreshing
                    )
                }

                // -------------------------------------------------
                // APP USAGE TITLE
                // -------------------------------------------------

                item {
                    UsageHeader()
                }

                // -------------------------------------------------
                // TOTAL SCREEN TIME
                // -------------------------------------------------

                item {
                    TotalScreenTimeCard(
                        totalUsageMillis = uiState.totalUsageMillis,
                        period = uiState.period
                    )
                }

                // -------------------------------------------------
                // PERIOD SELECTOR
                // -------------------------------------------------

                item {
                    UsagePeriodSelector(
                        selected = uiState.period,
                        onSelected = onPeriodSelected
                    )
                }

                // -------------------------------------------------
                // USAGE SUMMARY
                // -------------------------------------------------

                item {
                    UsageSummaryCard(
                        uiState = uiState
                    )
                }

                // -------------------------------------------------
                // USAGE BREAKDOWN
                // -------------------------------------------------

                item {
                    UsageBreakdownCard(
                        uiState = uiState
                    )
                }

                // -------------------------------------------------
                // TIMELINE
                // -------------------------------------------------

                item {
                    TimelineSection(
                        uiState = uiState
                    )
                }
            }

            // -----------------------------------------------------
            // BOTTOM NAV
            // -----------------------------------------------------

            UsageBottomNavigation()
        }
    }
}


// ================================================================
// DOTTED BACKGROUND
// ================================================================

@Composable
private fun DottedBackground() {

    Canvas(
        modifier = Modifier.fillMaxSize()
    ) {

        val spacing = 16.dp.toPx()
        val radius = 1.dp.toPx()

        var x = 0f

        while (x < size.width) {

            var y = 0f

            while (y < size.height) {

                drawCircle(
                    color = Color(0xFF55585A).copy(alpha = 0.45f),
                    radius = radius,
                    center = Offset(x, y)
                )

                y += spacing
            }

            x += spacing
        }
    }
}


// ================================================================
// DEVICE LENS HEADER
// ================================================================

@Composable
private fun DeviceLensHeader(
    onRefresh: () -> Unit,
    isRefreshing: Boolean
) {

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                top = 16.dp,
                bottom = 4.dp
            )
    ) {

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {

            Icon(
                imageVector = Icons.Outlined.Memory,
                contentDescription = null,
                tint = TextSecondary,
                modifier = Modifier.size(22.dp)
            )

            Spacer(modifier = Modifier.width(10.dp))

            Text(
                text = "DeviceLens",
                color = Cyan,
                fontSize = 23.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.weight(1f)
            )

            Icon(
                imageVector = Icons.Outlined.Settings,
                contentDescription = "Settings",
                tint = TextSecondary,
                modifier = Modifier.size(23.dp)
            )
        }
    }
}


// ================================================================
// USAGE HEADER
// ================================================================

@Composable
private fun UsageHeader() {

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 6.dp),
        verticalAlignment = Alignment.Top
    ) {

        Column(
            modifier = Modifier.weight(1f)
        ) {

            Text(
                text = "App Usage",
                color = TextPrimary,
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium
            )

            Spacer(modifier = Modifier.height(5.dp))

            Text(
                text = "Understand how you spend time on your\napps",
                color = TextSecondary,
                fontSize = 15.sp,
                lineHeight = 20.sp
            )
        }

        Icon(
            imageVector = Icons.Outlined.Refresh,
            contentDescription = "Refresh",
            tint = TextSecondary,
            modifier = Modifier
                .padding(top = 2.dp)
                .size(22.dp)
        )
    }
}


// ================================================================
// TOTAL SCREEN TIME
// ================================================================

@Composable
private fun TotalScreenTimeCard(
    totalUsageMillis: Long,
    period: UsagePeriod
) {

    UsageCard {

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {

            Box(
                modifier = Modifier
                    .size(50.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF294CA5)),
                contentAlignment = Alignment.Center
            ) {

                Icon(
                    imageVector = Icons.Outlined.AccessTime,
                    contentDescription = null,
                    tint = Color(0xFF9EB3FF),
                    modifier = Modifier.size(25.dp)
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column {

                Text(
                    text = "TOTAL SCREEN TIME",
                    color = TextSecondary,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.5.sp,
                    fontFamily = FontFamily.Monospace
                )

                Spacer(modifier = Modifier.height(5.dp))

                Text(
                    text = TimeFormatter.formatDuration(totalUsageMillis),
                    color = TextPrimary,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace
                )
            }
        }

        Spacer(modifier = Modifier.height(17.dp))

        Text(
            text = "Usage for ${periodLabel(period)}",
            color = TextSecondary,
            fontSize = 15.sp,
            fontWeight = FontWeight.Medium
        )
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
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {

        UsagePeriod.entries.forEach { period ->

            val isSelected = selected == period

            Box(
                modifier = Modifier
                    .height(44.dp)
                    .weight(1f)
                    .clip(CircleShape)
                    .background(
                        if (isSelected) {
                            CyanDark
                        } else {
                            CardBackground
                        }
                    )
                    .border(
                        width = 1.dp,
                        color = if (isSelected) {
                            CyanDark
                        } else {
                            BorderColor
                        },
                        shape = CircleShape
                    )
                    .clickable {
                        onSelected(period)
                    },
                contentAlignment = Alignment.Center
            ) {

                Text(
                    text = periodLabel(period),
                    color = TextPrimary,
                    fontSize = 14.sp,
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
    uiState: UsageUiState
) {

    UsageCard {

        SectionTitle(
            icon = Icons.Outlined.ShowChart,
            title = "Usage Summary"
        )

        Spacer(modifier = Modifier.height(23.dp))

        Text(
            text = "Total usage",
            color = TextSecondary,
            fontSize = 15.sp,
            fontWeight = FontWeight.Medium
        )

        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.Bottom
        ) {

            Text(
                text = TimeFormatter.formatDuration(
                    uiState.totalUsageMillis
                ),
                color = TextPrimary,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Monospace,
                modifier = Modifier.weight(1f)
            )

            Column(
                horizontalAlignment = Alignment.End
            ) {

                Text(
                    text = "86%",
                    color = Cyan,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = "of total usage",
                    color = TextSecondary,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        UsageProgressBar(
            progress = 0.86f
        )

        Spacer(modifier = Modifier.height(24.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            SmallUsageStatCard(
                modifier = Modifier.weight(1f),
                icon = Icons.Outlined.AccessTime,
                title = "Last used",
                value = "Today 9:13 PM"
            )

            SmallUsageStatCard(
                modifier = Modifier.weight(1f),
                icon = Icons.Outlined.Apps,
                title = "Launches",
                value = "87"
            )
        }
    }
}


// ================================================================
// SMALL STAT
// ================================================================

@Composable
private fun SmallUsageStatCard(
    modifier: Modifier,
    icon: ImageVector,
    title: String,
    value: String
) {

    Column(
        modifier = modifier
            .heightIn(min = 77.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(CardBackgroundLight)
            .padding(11.dp)
    ) {

        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {

            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = TextSecondary,
                modifier = Modifier.size(13.dp)
            )

            Spacer(modifier = Modifier.width(5.dp))

            Text(
                text = title,
                color = TextSecondary,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Monospace
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = value,
            color = TextPrimary,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = FontFamily.Monospace
        )
    }
}


// ================================================================
// PROGRESS BAR
// ================================================================

@Composable
private fun UsageProgressBar(
    progress: Float
) {

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(8.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(Color(0xFF303735))
    ) {

        Box(
            modifier = Modifier
                .fillMaxWidth(progress)
                .height(8.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(Cyan)
        )
    }
}


// ================================================================
// USAGE BREAKDOWN
// ================================================================

@Composable
private fun UsageBreakdownCard(
    uiState: UsageUiState
) {

    UsageCard {

        SectionTitle(
            icon = Icons.Outlined.BarChart,
            title = "Usage Breakdown"
        )

        Spacer(modifier = Modifier.height(24.dp))

        UsageBarChart(
            uiState = uiState
        )
    }
}


// ================================================================
// BAR CHART
// ================================================================

@Composable
private fun UsageBarChart(
    uiState: UsageUiState
) {

    val bars = remember(uiState.visibleApps) {

        val count = uiState.visibleApps.size

        if (count == 0) {
            listOf(
                0.15f,
                0.25f,
                0.35f,
                0.48f,
                0.70f,
                0.90f,
                0.82f,
                0.35f
            )
        } else {

            uiState.visibleApps
                .take(8)
                .map { app ->

                    val maxUsage = uiState.visibleApps
                        .maxOfOrNull { it.usageMillis }
                        ?: 1L

                    (app.usageMillis.toFloat() / maxUsage)
                        .coerceIn(0.08f, 1f)
                }
        }
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(150.dp),
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        verticalAlignment = Alignment.Bottom
    ) {

        bars.forEachIndexed { index, value ->

            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentAlignment = Alignment.BottomCenter
            ) {

                Box(
                    modifier = Modifier
                        .fillMaxWidth(0.6f)
                        .fillMaxHeight(value)
                        .clip(
                            RoundedCornerShape(
                                topStart = 3.dp,
                                topEnd = 3.dp
                            )
                        )
                        .background(
                            if (index >= bars.size - 4) {
                                Cyan
                            } else {
                                Color(0xFF5D9D9B)
                            }
                        )
                )
            }
        }
    }

    Spacer(modifier = Modifier.height(1.dp))

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(1.dp)
            .background(Color(0xFF39403E))
    )
}


// ================================================================
// TIMELINE
// ================================================================

@Composable
private fun TimelineSection(
    uiState: UsageUiState
) {

    Column(
        modifier = Modifier.fillMaxWidth()
    ) {

        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {

            Icon(
                imageVector = Icons.Outlined.AccessTime,
                contentDescription = null,
                tint = TextSecondary,
                modifier = Modifier.size(23.dp)
            )

            Spacer(modifier = Modifier.width(10.dp))

            Column {

                Text(
                    text = "Timeline",
                    color = TextPrimary,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium
                )

                Text(
                    text = "Recent app sessions",
                    color = TextSecondary,
                    fontSize = 14.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        TimelineItem(
            startTime = "8:26 PM",
            endTime = "9:13 PM",
            duration = "47m"
        )

        Spacer(modifier = Modifier.height(10.dp))

        if (uiState.mostUsed.isNotEmpty()) {

            val firstApp = uiState.mostUsed.first()

            TimelineItem(
                startTime = "7:18 PM",
                endTime = "8:03 PM",
                duration = TimeFormatter.formatDuration(
                    firstApp.usageMillis
                )
            )
        }
    }
}


// ================================================================
// TIMELINE ITEM
// ================================================================

@Composable
private fun TimelineItem(
    startTime: String,
    endTime: String,
    duration: String
) {

    UsageCard(
        modifier = Modifier.fillMaxWidth()
    ) {

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {

            Box(
                modifier = Modifier
                    .size(8.dp)
                    .clip(CircleShape)
                    .background(Cyan)
            )

            Spacer(modifier = Modifier.width(12.dp))

            Column(
                modifier = Modifier.weight(1f)
            ) {

                Text(
                    text = "$startTime - $endTime",
                    color = TextPrimary,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "Session duration",
                    color = TextSecondary,
                    fontSize = 13.sp
                )
            }

            Text(
                text = duration,
                color = TextPrimary,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Monospace
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
            tint = TextSecondary,
            modifier = Modifier.size(21.dp)
        )

        Spacer(modifier = Modifier.width(10.dp))

        Text(
            text = title,
            color = TextPrimary,
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium
        )
    }
}


// ================================================================
// GENERIC CARD
// ================================================================

@Composable
private fun UsageCard(
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit
) {

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(CardBackground)
            .border(
                width = 1.dp,
                color = BorderColor,
                shape = RoundedCornerShape(12.dp)
            )
            .padding(20.dp),
        content = content
    )
}


// ================================================================
// BOTTOM NAVIGATION
// ================================================================

@Composable
private fun UsageBottomNavigation() {

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                PanelBackground.copy(alpha = 0.98f)
            )
            .border(
                width = 1.dp,
                color = BorderColor
            )
            .padding(
                horizontal = 12.dp,
                vertical = 10.dp
            ),
        contentAlignment = Alignment.BottomCenter
    ) {

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {

            BottomNavigationItem(
                icon = Icons.Outlined.Home,
                title = "Home"
            )

            BottomNavigationItem(
                icon = Icons.Outlined.BarChart,
                title = "Diagnostics",
                selected = true
            )

            BottomNavigationItem(
                icon = Icons.Outlined.Language,
                title = "Compare"
            )

            BottomNavigationItem(
                icon = Icons.Outlined.Person,
                title = "Profile"
            )
        }
    }
}


// ================================================================
// BOTTOM NAV ITEM
// ================================================================

@Composable
private fun BottomNavigationItem(
    icon: ImageVector,
    title: String,
    selected: Boolean = false
) {

    Box(
        modifier = Modifier
            .clip(CircleShape)
            .background(
                if (selected) {
                    CyanDark
                } else {
                    Color.Transparent
                }
            )
            .padding(
                horizontal = if (selected) 22.dp else 12.dp,
                vertical = 7.dp
            )
    ) {

        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = if (selected) {
                    TextPrimary
                } else {
                    TextSecondary
                },
                modifier = Modifier.size(20.dp)
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = title,
                color = if (selected) {
                    TextPrimary
                } else {
                    TextSecondary
                },
                fontSize = 9.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Monospace
            )
        }
    }
}


// ================================================================
// LOADING
// ================================================================

@Composable
private fun LoadingContent() {

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(PanelBackground),
        contentAlignment = Alignment.Center
    ) {

        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            CircularProgressIndicator(
                color = Cyan
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "Reading app usage...",
                color = TextSecondary
            )
        }
    }
}


// ================================================================
// ERROR
// ================================================================

@Composable
private fun ErrorContent(
    message: String,
    onRetry: () -> Unit
) {

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(PanelBackground)
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {

        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Text(
                text = "Unable to load usage",
                color = TextPrimary,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = message,
                color = TextSecondary
            )

            Spacer(modifier = Modifier.height(20.dp))

            Box(
                modifier = Modifier
                    .clip(CircleShape)
                    .background(CyanDark)
                    .clickable {
                        onRetry()
                    }
                    .padding(
                        horizontal = 25.dp,
                        vertical = 12.dp
                    )
            ) {

                Text(
                    text = "Try Again",
                    color = TextPrimary,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}


// ================================================================
// PERIOD LABEL
// ================================================================

private fun periodLabel(
    period: UsagePeriod
): String {

    return when (period) {

        UsagePeriod.TODAY -> "Today"

        UsagePeriod.YESTERDAY -> "Yesterday"

        UsagePeriod.LAST_7_DAYS -> "Last 7 days"

        else -> period.toString()
    }
}