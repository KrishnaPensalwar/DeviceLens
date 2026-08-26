package com.example.devicelens.presentation.usage

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AccessTime
import androidx.compose.material.icons.outlined.Apps
import androidx.compose.material.icons.outlined.Refresh
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.TipsAndUpdates
import androidx.compose.material.icons.outlined.TrendingUp
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.devicelens.core.util.TimeFormatter
import com.example.devicelens.domain.model.UsagePeriod
import com.example.devicelens.domain.model.UsageSortOption
import com.example.devicelens.presentation.usage.components.AppUsageItem
import com.example.devicelens.presentation.usage.components.periodLabel
import io.github.sanketnawghare.glassify.compose.GlassButton
import io.github.sanketnawghare.glassify.compose.GlassStyle
import io.github.sanketnawghare.glassify.compose.glassify


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
                onSortSelected = viewModel::onSortSelected,
                onSearchQueryChanged = viewModel::onSearchQueryChanged,
                onRefresh = viewModel::refresh,
                onAppClick = onAppClick
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
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            CircularProgressIndicator()

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "Reading app usage...",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
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
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier.padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Unable to load usage",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = message,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(20.dp))

            GlassButton(
                onClick = onRetry,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .glassify(
                        style = GlassStyle.Thick
                    )
            ) {
                Text(
                    text = "Try Again",
                    fontWeight = FontWeight.SemiBold
                )
            }
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
    onAppClick: (String) -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp),
        contentPadding = androidx.compose.foundation.layout.PaddingValues(
            top = 24.dp,
            bottom = 24.dp
        ),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {

        // ---------------------------------------------------------
        // HEADER
        // ---------------------------------------------------------

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = "App Usage",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = "Understand how you spend time on your apps",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                if (uiState.isRefreshing) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        strokeWidth = 2.dp
                    )
                } else {
                    IconButton(
                        onClick = onRefresh
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Refresh,
                            contentDescription = "Refresh"
                        )
                    }
                }
            }
        }


        // ---------------------------------------------------------
        // OVERVIEW
        // ---------------------------------------------------------

        item {
            UsageOverviewGlassCard(
                period = uiState.period,
                totalUsageMillis = uiState.totalUsageMillis
            )
        }


        // ---------------------------------------------------------
        // PERIOD SELECTOR
        // ---------------------------------------------------------

        item {
            PeriodSelector(
                selected = uiState.period,
                onSelected = onPeriodSelected
            )
        }


        // ---------------------------------------------------------
        // MOST USED
        // ---------------------------------------------------------

        if (uiState.mostUsed.isNotEmpty()) {
            item {
                MostUsedCard(
                    apps = uiState.mostUsed
                )
            }
        }


        // ---------------------------------------------------------
        // HIGH USAGE
        // ---------------------------------------------------------

        if (uiState.highUsageApps.isNotEmpty()) {
            item {
                uiState.highUsageApps?.let {
                    HighUsageCard(
                        apps = it,
                        recommendations = uiState.recommendations
                    )
                }
            }
        }


        // ---------------------------------------------------------
        // SEARCH
        // ---------------------------------------------------------

        item {
            OutlinedTextField(
                value = uiState.searchQuery,
                onValueChange = onSearchQueryChanged,
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                shape = RoundedCornerShape(16.dp),
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Outlined.Search,
                        contentDescription = null
                    )
                },
                placeholder = {
                    Text("Search apps")
                }
            )
        }


        // ---------------------------------------------------------
        // APPS HEADER
        // ---------------------------------------------------------

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = "Applications",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )

                    Text(
                        text = "${uiState.visibleApps.size} apps",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                SortMenu(
                    selected = uiState.sortOption,
                    onSelected = onSortSelected
                )
            }
        }


        // ---------------------------------------------------------
        // APP LIST
        // ---------------------------------------------------------

        if (uiState.visibleApps.isEmpty()) {
            item {
                EmptyUsageCard()
            }
        } else {
            items(
                items = uiState.visibleApps,
                key = { it.packageName }
            ) { app ->

                AppUsageItem(
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


// ================================================================
// OVERVIEW CARD
// ================================================================

@Composable
private fun UsageOverviewGlassCard(
    period: UsagePeriod,
    totalUsageMillis: Long
) {
    GlassButton(
        onClick = {},
        modifier = Modifier
            .fillMaxWidth()
            .height(155.dp)
            .glassify(
                style = GlassStyle.Thick
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
//                .padding(20.dp)
        ) {

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(RoundedCornerShape(15.dp))
                        .background(
                            MaterialTheme.colorScheme.primaryContainer
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Outlined.AccessTime,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(25.dp)
                    )
                }

                Spacer(modifier = Modifier.width(14.dp))

                Column {
                    Text(
                        text = "Total Screen Time",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(3.dp))

                    Text(
                        text = TimeFormatter.formatDuration(totalUsageMillis),
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Usage for ${periodLabel(period)}",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
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
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        UsagePeriod.entries.forEach { period ->

            FilterChip(
                selected = selected == period,
                onClick = {
                    onSelected(period)
                },
                label = {
                    Text(
                        text = periodLabel(period)
                    )
                }
            )
        }
    }
}


// ================================================================
// MOST USED CARD
// ================================================================

@Composable
private fun MostUsedCard(
    apps: List<com.example.devicelens.domain.model.AppUsageInfo>
) {
    GlassButton(
        onClick = {},
        modifier = Modifier
            .fillMaxWidth()
            .glassify(
                style = GlassStyle.Thin
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {

            SectionHeader(
                icon = Icons.Outlined.TrendingUp,
                title = "Most Used"
            )

            apps.forEachIndexed { index, app ->

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    Text(
                        text = "${index + 1}",
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.width(28.dp)
                    )

                    Column(
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            text = app.packageName,
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.Medium
                        )

                        Text(
                            text = "${"%.0f".format(app.percentOfTotal)}% of total usage",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    Text(
                        text = TimeFormatter.formatDuration(app.usageMillis),
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }
    }
}


// ================================================================
// HIGH USAGE CARD
// ================================================================

@Composable
private fun HighUsageCard(
    apps: List<com.example.devicelens.domain.model.AppUsageInfo>,
    recommendations: List<String>
) {
    GlassButton(
        onClick = {},
        modifier = Modifier
            .fillMaxWidth()
            .glassify(
                style = GlassStyle.Thick
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {

            SectionHeader(
                icon = Icons.Outlined.TrendingUp,
                title = "Used More Than Usual"
            )

            apps.take(3).forEach { app ->

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            text = app.appName,
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.Medium
                        )

                        Text(
                            text = "High usage detected",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    Text(
                        text = TimeFormatter.formatDuration(app.usageMillis),
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            if (recommendations.isNotEmpty()) {

                Spacer(modifier = Modifier.height(4.dp))

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .background(
                            MaterialTheme.colorScheme.primaryContainer.copy(
                                alpha = 0.45f
                            )
                        )
                        .padding(12.dp)
                ) {

                    Row(
                        verticalAlignment = Alignment.Top
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.TipsAndUpdates,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(20.dp)
                        )

                        Spacer(modifier = Modifier.width(10.dp))

                        Column(
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Text(
                                text = "Usage insight",
                                style = MaterialTheme.typography.labelLarge,
                                fontWeight = FontWeight.SemiBold
                            )

                            recommendations.take(2).forEach { tip ->
                                Text(
                                    text = tip,
                                    style = MaterialTheme.typography.bodySmall
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}


// ================================================================
// SECTION HEADER
// ================================================================

@Composable
private fun SectionHeader(
    icon: ImageVector,
    title: String
) {
    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {

        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(RoundedCornerShape(11.dp))
                .background(
                    MaterialTheme.colorScheme.surfaceVariant
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(19.dp),
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        Spacer(modifier = Modifier.width(10.dp))

        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold
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

        TextButton(
            onClick = {
                expanded = true
            }
        ) {
            Text(
                text = selected.label(),
                fontWeight = FontWeight.Medium
            )
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = {
                expanded = false
            }
        ) {
            UsageSortOption.entries.forEach { option ->

                DropdownMenuItem(
                    text = {
                        Text(option.label())
                    },
                    onClick = {
                        onSelected(option)
                        expanded = false
                    }
                )
            }
        }
    }
}


// ================================================================
// EMPTY STATE
// ================================================================

@Composable
private fun EmptyUsageCard() {
    GlassButton(
        onClick = {},
        modifier = Modifier
            .fillMaxWidth()
            .height(120.dp)
            .glassify(
                style = GlassStyle.Thin
            )
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {

            Icon(
                imageVector = Icons.Outlined.Apps,
                contentDescription = null,
                modifier = Modifier.size(28.dp),
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "No app usage found",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold
            )

            Text(
                text = "Try selecting a different period",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}


// ================================================================
// SORT LABEL
// ================================================================

private fun UsageSortOption.label(): String {
    return when (this) {
        UsageSortOption.MOST_USED -> "Most used"
        UsageSortOption.LEAST_USED -> "Least used"
        UsageSortOption.NAME_AZ -> "Name A–Z"
        UsageSortOption.NAME_ZA -> "Name Z–A"
    }
}