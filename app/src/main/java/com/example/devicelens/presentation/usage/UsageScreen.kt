package com.example.devicelens.presentation.usage

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AccessTime
import androidx.compose.material.icons.outlined.Apps
import androidx.compose.material.icons.outlined.BarChart
import androidx.compose.material.icons.outlined.CompareArrows
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Refresh
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.outlined.TipsAndUpdates
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.devicelens.core.util.TimeFormatter
import com.example.devicelens.domain.model.AppUsageInfo
import com.example.devicelens.domain.model.UsagePeriod
import com.example.devicelens.domain.model.UsageSortOption
import com.example.devicelens.presentation.device.DeviceInfoTopBar
import com.example.devicelens.presentation.usage.components.periodLabel
import java.util.Locale


// ================================================================
// COLORS
// ================================================================

private val OuterBackground = Color(0xFF202224)

private val PanelBackground = Color(0xFF0D1110)

private val CardBackground = Color(0xFF191E1D)

private val CardBackground2 = Color(0xFF1D2322)

private val BorderColor = Color(0xFF303635)

private val DividerColor = Color(0xFF252B2A)

private val PrimaryText = Color(0xFFE1E6E4)

private val SecondaryText = Color(0xFFB5BFBC)

private val MutedText = Color(0xFF7D8784)

private val Cyan = Color(0xFF69D8D4)

private val CyanDark = Color(0xFF008F8C)

private val Blue = Color(0xFF9DAEFF)

private val InsightBackground = Color(0xFF141E32)

private val InsightBorder = Color(0xFF31466D)


// ================================================================
// MAIN SCREEN
// ================================================================

@Composable
fun UsageScreen(
    onAppClick: (String) -> Unit,
    viewModel: UsageViewModel = hiltViewModel(),
    onBack: () -> Unit
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
            UsageLoading()
        }

        uiState.error != null -> {
            UsageError(
                message = uiState.error!!,
                onRetry = viewModel::refresh
            )
        }

        else -> {

            UsageContent(
                uiState = uiState,
                onPeriodSelected = viewModel::onPeriodSelected,
                onSortSelected = viewModel::onSortSelected,
                onSearchQueryChanged = viewModel::onSearchQueryChanged,
                onRefresh = viewModel::refresh,
                onAppClick = onAppClick,
                onBack
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
    onSortSelected: (UsageSortOption) -> Unit,
    onSearchQueryChanged: (String) -> Unit,
    onRefresh: () -> Unit,
    onAppClick: (String) -> Unit,
    onBack : () -> Unit
) {

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(OuterBackground)
    ) {

        // --------------------------------------------------------
        // RESPONSIVE DEVICE LENS PANEL
        // --------------------------------------------------------

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(
                    RoundedCornerShape(14.dp)
                )
                .background(PanelBackground)
                .border(
                    width = 1.dp,
                    color = BorderColor,
                    shape = RoundedCornerShape(14.dp)
                ),
            contentAlignment = Alignment.BottomCenter
        ) {

            LazyColumn(
                modifier = Modifier.fillMaxSize(),

                contentPadding = PaddingValues(
                    start = 17.dp,
                    end = 17.dp,
                    top = 0.dp,
                    bottom = 90.dp
                ),

                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {

                // ------------------------------------------------
                // DEVICE LENS HEADER
                // ------------------------------------------------

                item {
                    DeviceInfoTopBar("App Usage") { onBack()}
                }

                // ------------------------------------------------
                // TOTAL SCREEN TIME
                // ------------------------------------------------

                item {
                    TotalScreenTimeCard(
                        period = uiState.period,
                        totalUsageMillis = uiState.totalUsageMillis
                    )
                }

                // ------------------------------------------------
                // PERIOD FILTER
                // ------------------------------------------------

                item {
                    PeriodSelector(
                        selected = uiState.period,
                        onSelected = onPeriodSelected
                    )
                }

                // ------------------------------------------------
                // USAGE INSIGHT
                // ------------------------------------------------

                if (uiState.recommendations.isNotEmpty()) {

                    item {
                        UsageInsightCard(
                            recommendations = uiState.recommendations
                        )
                    }
                }

                // ------------------------------------------------
                // SEARCH
                // ------------------------------------------------

                item {
                    SearchApps(
                        value = uiState.searchQuery,
                        onValueChange = onSearchQueryChanged
                    )
                }

                // ------------------------------------------------
                // APPLICATIONS HEADER
                // ------------------------------------------------

                item {
                    ApplicationsHeader(
                        count = uiState.visibleApps.size,
                        sortOption = uiState.sortOption,
                        onSortSelected = onSortSelected
                    )
                }

                // ------------------------------------------------
                // APPLICATIONS
                // ------------------------------------------------

                if (uiState.visibleApps.isEmpty()) {

                    item {
                        EmptyAppsCard()
                    }

                } else {

                    items(
                        items = uiState.visibleApps,
                        key = {
                            it.packageName
                        }
                    ) { app ->

                        NewAppUsageCard(
                            app = app,
                            highlighted = uiState.highUsageApps.any {
                                it.packageName == app.packageName
                            },
                            onClick = {
                                onAppClick(app.packageName)
                            }
                        )
                    }
                }
            }
        }
    }
}


// ================================================================
// DOTTED BACKGROUND
// ================================================================

@Composable
private fun UsageDottedBackground() {

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
                    color = Color(0xFF777B7C).copy(alpha = 0.42f),
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
    isRefreshing: Boolean,
    onRefresh: () -> Unit
) {

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(92.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {

        Icon(
            imageVector = Icons.Outlined.Apps,
            contentDescription = null,
            tint = Cyan,
            modifier = Modifier.size(25.dp)
        )

        Spacer(
            modifier = Modifier.width(12.dp)
        )

        Text(
            text = "DeviceLens",
            color = Cyan,
            fontSize = 27.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.weight(1f)
        )

        if (isRefreshing) {

            CircularProgressIndicator(
                modifier = Modifier.size(22.dp),
                color = Cyan,
                strokeWidth = 2.dp
            )

        } else {

            Icon(
                imageVector = Icons.Outlined.Settings,
                contentDescription = "Settings",
                tint = Cyan,
                modifier = Modifier.size(27.dp)
            )
        }
    }
}


// ================================================================
// USAGE TITLE
// ================================================================

@Composable
private fun UsageTitle(
    onClick : ()  -> Unit
) {

    Column(
        modifier = Modifier.fillMaxWidth()
    ) {

        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {

            Text(
                text = "App Usage",
                color = PrimaryText,
                fontSize = 30.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.weight(1f)
            )

            Icon(
                imageVector = Icons.Outlined.Refresh,
                contentDescription = "Refresh",
                tint = SecondaryText,
                modifier = Modifier
                    .size(24.dp)
                    .clickable{
                        onClick()
                    }
            )
        }

        Spacer(
            modifier = Modifier.height(5.dp)
        )

        Text(
            text = "Understand how you spend time on your apps",
            color = SecondaryText,
            fontSize = 14.sp
        )
    }
}


// ================================================================
// TOTAL SCREEN TIME
// ================================================================

@Composable
private fun TotalScreenTimeCard(
    period: UsagePeriod,
    totalUsageMillis: Long
) {

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(CardBackground)
            .border(
                width = 1.dp,
                color = DividerColor,
                shape = RoundedCornerShape(12.dp)
            )
            .padding(
                horizontal = 20.dp,
                vertical = 18.dp
            )
    ) {

        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {

            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(
                        Color(0xFF28459C)
                    ),
                contentAlignment = Alignment.Center
            ) {

                Icon(
                    imageVector = Icons.Outlined.AccessTime,
                    contentDescription = null,
                    tint = Color(0xFFAFC0FF),
                    modifier = Modifier.size(25.dp)
                )
            }

            Spacer(
                modifier = Modifier.width(16.dp)
            )

            Column {

                Text(
                    text = "TOTAL SCREEN TIME",
                    color = SecondaryText,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace,
                    letterSpacing = 1.4.sp
                )

                Spacer(
                    modifier = Modifier.height(3.dp)
                )

                Text(
                    text = TimeFormatter.formatDuration(
                        totalUsageMillis
                    ),
                    color = PrimaryText,
                    fontSize = 29.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        Spacer(
            modifier = Modifier.height(17.dp)
        )

        Text(
            text = "Usage for ${periodLabel(period)}",
            color = SecondaryText,
            fontSize = 14.sp
        )
    }
}


// ================================================================
// PERIOD SELECTOR
// ================================================================

@Composable
private fun PeriodSelector(
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
                    .height(43.dp)
                    .clip(
                        RoundedCornerShape(11.dp)
                    )
                    .background(
                        if (isSelected) {
                            CyanDark
                        } else {
                            CardBackground2
                        }
                    )
                    .border(
                        width = 1.dp,
                        color = if (isSelected) {
                            CyanDark
                        } else {
                            BorderColor
                        },
                        shape = RoundedCornerShape(11.dp)
                    )
                    .clickable {
                        onSelected(period)
                    },
                contentAlignment = Alignment.Center
            ) {

                Text(
                    text = periodLabel(period),
                    color = if (isSelected) {
                        Color.White
                    } else {
                        PrimaryText
                    },
                    fontSize = 13.sp,
                    fontWeight = if (isSelected) {
                        FontWeight.Bold
                    } else {
                        FontWeight.Medium
                    }
                )
            }
        }
    }
}


// ================================================================
// USAGE INSIGHT
// ================================================================

@Composable
private fun UsageInsightCard(
    recommendations: List<String>
) {

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(11.dp))
            .background(InsightBackground)
            .border(
                width = 1.dp,
                color = InsightBorder,
                shape = RoundedCornerShape(11.dp)
            )
            .padding(18.dp),
        verticalAlignment = Alignment.Top
    ) {

        Icon(
            imageVector = Icons.Outlined.TipsAndUpdates,
            contentDescription = null,
            tint = Blue,
            modifier = Modifier.size(22.dp)
        )

        Spacer(
            modifier = Modifier.width(14.dp)
        )

        Column(
            modifier = Modifier.weight(1f)
        ) {

            Text(
                text = "Usage insight",
                color = PrimaryText,
                fontSize = 15.sp,
                fontWeight = FontWeight.SemiBold
            )

            Spacer(
                modifier = Modifier.height(6.dp)
            )

            recommendations
                .take(2)
                .forEach { recommendation ->

                    Text(
                        text = recommendation,
                        color = SecondaryText,
                        fontSize = 14.sp,
                        lineHeight = 20.sp
                    )
                }
        }
    }
}


// ================================================================
// SEARCH
// ================================================================

@Composable
private fun SearchApps(
    value: String,
    onValueChange: (String) -> Unit
) {

    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = Modifier.fillMaxWidth(),
        singleLine = true,
        shape = RoundedCornerShape(10.dp),
        placeholder = {
            Text(
                text = "Search apps",
                color = MutedText
            )
        },
        leadingIcon = {

            Icon(
                imageVector = Icons.Outlined.Search,
                contentDescription = null,
                tint = SecondaryText
            )
        }
    )
}


// ================================================================
// APPLICATION HEADER
// ================================================================

@Composable
private fun ApplicationsHeader(
    count: Int,
    sortOption: UsageSortOption,
    onSortSelected: (UsageSortOption) -> Unit
) {

    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.Bottom
    ) {

        Column(
            modifier = Modifier.weight(1f)
        ) {

            Text(
                text = "Applications",
                color = PrimaryText,
                fontSize = 21.sp,
                fontWeight = FontWeight.Medium
            )

            Spacer(
                modifier = Modifier.height(2.dp)
            )

            Text(
                text = "$count apps",
                color = SecondaryText,
                fontSize = 12.sp,
                fontFamily = FontFamily.Monospace
            )
        }

        SortMenu(
            selected = sortOption,
            onSelected = onSortSelected
        )
    }
}


// ================================================================
// SORT MENU
// ================================================================

@Composable
private fun SortMenu(
    selected: UsageSortOption,
    onSelected: (UsageSortOption) -> Unit
) {

    var expanded by remember {
        mutableStateOf(false)
    }

    Box {

        Text(
            text = when (selected) {
                UsageSortOption.MOST_USED -> "Most used"
                UsageSortOption.LEAST_USED -> "Least used"
                UsageSortOption.NAME_AZ -> "Name A–Z"
                UsageSortOption.NAME_ZA -> "Name Z–A"
            },
            color = Cyan,
            fontSize = 13.sp,
            modifier = Modifier
                .clickable {
                    expanded = true
                }
                .padding(8.dp)
        )

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = {
                expanded = false
            }
        ) {

            UsageSortOption.entries.forEach { option ->

                DropdownMenuItem(
                    text = {
                        Text(
                            when (option) {
                                UsageSortOption.MOST_USED ->
                                    "Most used"

                                UsageSortOption.LEAST_USED ->
                                    "Least used"

                                UsageSortOption.NAME_AZ ->
                                    "Name A–Z"

                                UsageSortOption.NAME_ZA ->
                                    "Name Z–A"
                            }
                        )
                    },
                    onClick = {
                        expanded = false
                        onSelected(option)
                    }
                )
            }
        }
    }
}


// ================================================================
// APP CARD
// ================================================================

@Composable
private fun NewAppUsageCard(
    app: AppUsageInfo,
    highlighted: Boolean,
    onClick: () -> Unit
) {

    val appColor = appCardColor(
        app = app,
        highlighted = highlighted
    )

    val iconColor = appIconColor(
        app = app
    )

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(11.dp))
            .background(appColor)
            .border(
                width = 1.dp,
                color = if (highlighted) {
                    Color(0xFF613036)
                } else {
                    Color(0xFF222827)
                },
                shape = RoundedCornerShape(11.dp)
            )
            .clickable {
                onClick()
            }
            .padding(
                horizontal = 20.dp,
                vertical = 17.dp
            )
    ) {

        // --------------------------------------------------------
        // APP HEADER
        // --------------------------------------------------------

        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {

            AppIcon(
                app = app,
                tint = iconColor
            )

            Spacer(
                modifier = Modifier.width(16.dp)
            )

            Column(
                modifier = Modifier.weight(1f)
            ) {

                Text(
                    text = app.appName,
                    color = PrimaryText,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Medium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(
                    modifier = Modifier.height(3.dp)
                )

                Text(
                    text = app.packageName,
                    color = SecondaryText,
                    fontSize = 11.sp,
                    fontFamily = FontFamily.Monospace,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Spacer(
                modifier = Modifier.width(10.dp)
            )

            Column(
                horizontalAlignment = Alignment.End
            ) {

                Text(
                    text = TimeFormatter.formatDuration(
                        app.usageMillis
                    ),
                    color = PrimaryText,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Medium
                )

                Spacer(
                    modifier = Modifier.height(2.dp)
                )

                Text(
                    text = "${app.percentOfTotal.toInt()}%",
                    color = SecondaryText,
                    fontSize = 11.sp,
                    fontFamily = FontFamily.Monospace
                )
            }
        }

        Spacer(
            modifier = Modifier.height(17.dp)
        )

        // --------------------------------------------------------
        // PROGRESS
        // --------------------------------------------------------

        UsageProgressBar(
            percent = app.percentOfTotal
        )

        Spacer(
            modifier = Modifier.height(16.dp)
        )

        // --------------------------------------------------------
        // APP METADATA
        // --------------------------------------------------------

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {

            Icon(
                imageVector = Icons.Outlined.AccessTime,
                contentDescription = null,
                tint = SecondaryText,
                modifier = Modifier.size(15.dp)
            )

            Spacer(
                modifier = Modifier.width(5.dp)
            )

            Text(
                text = "Last used ${formatLastUsed(app)}",
                color = SecondaryText,
                fontSize = 11.sp,
                fontFamily = FontFamily.Monospace,
                modifier = Modifier.weight(1f),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Icon(
                imageVector = Icons.Outlined.BarChart,
                contentDescription = null,
                tint = SecondaryText,
                modifier = Modifier.size(15.dp)
            )

            Spacer(
                modifier = Modifier.width(5.dp)
            )

            Text(
                text = "${app.launchCount} launches",
                color = SecondaryText,
                fontSize = 11.sp,
                fontFamily = FontFamily.Monospace
            )
        }
    }
}


// ================================================================
// APP ICON
// ================================================================

@Composable
private fun AppIcon(
    app: AppUsageInfo,
    tint: Color
) {

    val firstLetter = app.appName
        .trim()
        .firstOrNull()
        ?.uppercase()
        ?: "?"

    Box(
        modifier = Modifier
            .size(48.dp)
            .clip(CircleShape)
            .background(
                tint.copy(alpha = 0.18f)
            ),
        contentAlignment = Alignment.Center
    ) {

        Text(
            text = firstLetter,
            color = tint,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold
        )
    }
}


// ================================================================
// PROGRESS BAR
// ================================================================

@Composable
private fun UsageProgressBar(
    percent: Double
) {

    val progress = (
            percent / 100.0
            ).coerceIn(0.0, 1.0).toFloat()

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(7.dp)
            .clip(CircleShape)
            .background(
                Color(0xFF292F2E)
            )
    ) {

        Box(
            modifier = Modifier
                .fillMaxWidth(progress)
                .height(7.dp)
                .clip(CircleShape)
                .background(
                    if (percent >= 50) {
                        Color(0xFFFFA7A7)
                    } else {
                        Cyan
                    }
                )
        )
    }
}


// ================================================================
// APP COLORS
// ================================================================

private fun appCardColor(
    app: AppUsageInfo,
    highlighted: Boolean
): Color {

    return when {

        highlighted ->
            Color(0xFF300E13)

        app.percentOfTotal >= 10 ->
            Color(0xFF211214)

        else ->
            CardBackground
    }
}


private fun appIconColor(
    app: AppUsageInfo
): Color {

    return when {

        app.percentOfTotal >= 50 ->
            Color(0xFFFFE000)

        app.percentOfTotal >= 10 ->
            Color(0xFF5B98FF)

        else ->
            Cyan
    }
}


// ================================================================
// LAST USED
// ================================================================

private fun formatLastUsed(
    app: AppUsageInfo
): String {

    return try {

        app.lastUsedMillis?.let {
            val formatter = java.text.SimpleDateFormat(
                "h:mm a",
                Locale.getDefault()
            )

            formatter.format(
                java.util.Date(it)
            )
        } ?: "Unknown"

    } catch (_: Exception) {
        "Unknown"
    }
}


// ================================================================
// EMPTY
// ================================================================

@Composable
private fun EmptyAppsCard() {

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(11.dp))
            .background(CardBackground)
            .padding(30.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Icon(
            imageVector = Icons.Outlined.Apps,
            contentDescription = null,
            tint = MutedText,
            modifier = Modifier.size(32.dp)
        )

        Spacer(
            modifier = Modifier.height(10.dp)
        )

        Text(
            text = "No applications found",
            color = PrimaryText,
            fontSize = 16.sp,
            fontWeight = FontWeight.SemiBold
        )

        Spacer(
            modifier = Modifier.height(5.dp)
        )

        Text(
            text = "Try another period or search query.",
            color = SecondaryText,
            fontSize = 13.sp
        )
    }
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
                PanelBackground
            )
            .border(
                width = 1.dp,
                color = BorderColor
            )
            .padding(
                horizontal = 8.dp,
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
                label = "Home"
            )

            BottomNavigationItem(
                icon = Icons.Outlined.BarChart,
                label = "Diagnostics"
            )

            BottomNavigationItem(
                icon = Icons.Outlined.CompareArrows,
                label = "Compare",
                selected = true
            )

            BottomNavigationItem(
                icon = Icons.Outlined.Person,
                label = "Profile"
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
    label: String,
    selected: Boolean = false
) {

    Column(
        modifier = Modifier
            .clip(RoundedCornerShape(25.dp))
            .background(
                if (selected) {
                    Color(0xFF009B98)
                } else {
                    Color.Transparent
                }
            )
            .padding(
                horizontal = if (selected) 20.dp else 13.dp,
                vertical = 7.dp
            ),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = if (selected) {
                Color.White
            } else {
                SecondaryText
            },
            modifier = Modifier.size(21.dp)
        )

        Spacer(
            modifier = Modifier.height(4.dp)
        )

        Text(
            text = label,
            color = if (selected) {
                Color.White
            } else {
                SecondaryText
            },
            fontSize = 9.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = FontFamily.Monospace
        )
    }
}


// ================================================================
// LOADING
// ================================================================

@Composable
private fun UsageLoading() {

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(OuterBackground),
        contentAlignment = Alignment.Center
    ) {

        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            CircularProgressIndicator(
                color = Cyan
            )

            Spacer(
                modifier = Modifier.height(12.dp)
            )

            Text(
                text = "Reading app usage...",
                color = SecondaryText,
                fontSize = 14.sp
            )
        }
    }
}


// ================================================================
// ERROR
// ================================================================

@Composable
private fun UsageError(
    message: String,
    onRetry: () -> Unit
) {

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(OuterBackground)
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {

        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Text(
                text = "Unable to load usage",
                color = PrimaryText,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(
                modifier = Modifier.height(8.dp)
            )

            Text(
                text = message,
                color = SecondaryText,
                fontSize = 14.sp
            )

            Spacer(
                modifier = Modifier.height(20.dp)
            )

            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(10.dp))
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
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}