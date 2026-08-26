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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AccessTime
import androidx.compose.material.icons.outlined.Apps
import androidx.compose.material.icons.outlined.History
import androidx.compose.material.icons.outlined.Launch
import androidx.compose.material.icons.outlined.QueryStats
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.devicelens.core.util.TimeFormatter
import com.example.devicelens.domain.model.UsagePeriod
import com.example.devicelens.presentation.usage.components.UsageChart
import com.example.devicelens.presentation.usage.components.periodLabel

@Composable
fun UsageDetailScreen(
    viewModel: UsageDetailViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    when {
        uiState.isLoading -> {
            LoadingContent()
        }

        uiState.error != null -> {
            ErrorContent(
                message = uiState.error!!,
                onRetry = viewModel::retry
            )
        }

        uiState.detail != null -> {
            UsageDetailContent(
                uiState = uiState,
                onPeriodSelected = viewModel::onPeriodSelected
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
                text = "Loading usage details...",
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

            Button(onClick = onRetry) {
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
    onPeriodSelected: (UsagePeriod) -> Unit
) {
    val detail = uiState.detail ?: return

    androidx.compose.foundation.lazy.LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {

        item {
            Spacer(modifier = Modifier.height(8.dp))
        }

        // ---------------------------------------------------------
        // APP HEADER
        // ---------------------------------------------------------

        item {
            AppHeaderCard(
                appName = detail.app.appName,
                packageName = detail.app.packageName
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
        // USAGE SUMMARY
        // ---------------------------------------------------------

        item {
            UsageSummaryCard(detail)
        }

        // ---------------------------------------------------------
        // BREAKDOWN
        // ---------------------------------------------------------

        item {
            BreakdownCard(detail)
        }

        // ---------------------------------------------------------
        // TIMELINE HEADER
        // ---------------------------------------------------------

        item {
            SectionHeader(
                icon = Icons.Outlined.History,
                title = "Timeline",
                subtitle = "Recent app sessions"
            )
        }

        // ---------------------------------------------------------
        // TIMELINE
        // ---------------------------------------------------------

        if (detail.sessions.isEmpty()) {

            item {
                EmptyTimelineCard()
            }

        } else {

            items(
                count = detail.sessions.size,
                key = { index ->
                    detail.sessions[index].startMillis
                }
            ) { index ->

                val session = detail.sessions[index]

                SessionCard(
                    startMillis = session.startMillis,
                    endMillis = session.endMillis,
                    durationMillis = session.durationMillis
                )
            }
        }

        item {
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}


// ================================================================
// APP HEADER CARD
// ================================================================

@Composable
private fun AppHeaderCard(
    appName: String,
    packageName: String
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 2.dp
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            Box(
                modifier = Modifier
                    .size(54.dp)
                    .clip(RoundedCornerShape(17.dp))
                    .background(
                        MaterialTheme.colorScheme.surface
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Outlined.Apps,
                    contentDescription = null,
                    modifier = Modifier.size(28.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
            }

            Spacer(modifier = Modifier.size(14.dp))

            Column(
                modifier = Modifier.weight(1f)
            ) {

                Text(
                    text = appName,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(3.dp))

                Text(
                    text = packageName,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
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
                    Text(periodLabel(period))
                }
            )
        }
    }
}


// ================================================================
// USAGE SUMMARY CARD
// ================================================================

@Composable
private fun UsageSummaryCard(
    detail: com.example.devicelens.domain.model.AppUsageDetail
) {
    val percentage = detail.app.percentOfTotal
        .coerceIn(0.0, 100.0).toDouble()

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(22.dp),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 2.dp
        )
    ) {

        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {

            SectionTitle(
                icon = Icons.Outlined.QueryStats,
                title = "Usage Summary"
            )

            // -----------------------------------------------------
            // MAIN USAGE
            // -----------------------------------------------------

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.Bottom
            ) {

                Column(
                    modifier = Modifier.weight(1f)
                ) {

                    Text(
                        text = "Total usage",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(3.dp))

                    Text(
                        text = TimeFormatter.formatDuration(
                            detail.app.usageMillis
                        ),
                        style = MaterialTheme.typography.displaySmall,
                        fontWeight = FontWeight.Bold
                    )
                }

                Column(
                    horizontalAlignment = Alignment.End
                ) {

                    Text(
                        text = "%.0f%%".format(
                            detail.app.percentOfTotal
                        ),
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )

                    Text(
                        text = "of total usage",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            // -----------------------------------------------------
            // PROGRESS
            // -----------------------------------------------------

            LinearProgressIndicator(
                progress = {
                    percentage.toFloat() / 100f
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(7.dp)
                    .clip(CircleShape)
            )

            // -----------------------------------------------------
            // DETAILS
            // -----------------------------------------------------

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {

                DetailMetric(
                    modifier = Modifier.weight(1f),
                    icon = Icons.Outlined.AccessTime,
                    label = "Last used",
                    value = TimeFormatter.formatLastUsed(
                        detail.app.lastUsedMillis
                    )
                )

                detail.app.launchCount?.let { count ->

                    DetailMetric(
                        modifier = Modifier.weight(1f),
                        icon = Icons.Outlined.Launch,
                        label = "Launches",
                        value = count.toString()
                    )
                }
            }
        }
    }
}


// ================================================================
// DETAIL METRIC
// ================================================================

@Composable
private fun DetailMetric(
    modifier: Modifier,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    value: String
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(15.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {

        Column(
            modifier = Modifier.padding(12.dp)
        ) {

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {

                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.size(5.dp))

                Text(
                    text = label,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.height(5.dp))

            Text(
                text = value,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}


// ================================================================
// BREAKDOWN CARD
// ================================================================

@Composable
private fun BreakdownCard(
    detail: com.example.devicelens.domain.model.AppUsageDetail
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(22.dp),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 2.dp
        )
    ) {

        Column(
            modifier = Modifier.padding(20.dp)
        ) {

            SectionTitle(
                icon = Icons.Outlined.QueryStats,
                title = "Usage Breakdown"
            )

            Spacer(modifier = Modifier.height(16.dp))

            UsageChart(
                detail.buckets
            )
        }
    }
}


// ================================================================
// SECTION HEADER
// ================================================================

@Composable
private fun SectionHeader(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    subtitle: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                horizontal = 4.dp,
                vertical = 4.dp
            ),
        verticalAlignment = Alignment.CenterVertically
    ) {

        Box(
            modifier = Modifier
                .size(38.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(
                    MaterialTheme.colorScheme.primaryContainer
                ),
            contentAlignment = Alignment.Center
        ) {

            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(20.dp),
                tint = MaterialTheme.colorScheme.primary
            )
        }

        Spacer(modifier = Modifier.size(10.dp))

        Column {

            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )

            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}


// ================================================================
// SECTION TITLE
// ================================================================

@Composable
private fun SectionTitle(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String
) {
    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {

        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier.size(21.dp),
            tint = MaterialTheme.colorScheme.primary
        )

        Spacer(modifier = Modifier.size(8.dp))

        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold
        )
    }
}


// ================================================================
// SESSION CARD
// ================================================================

@Composable
private fun SessionCard(
    startMillis: Long,
    endMillis: Long,
    durationMillis: Long
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 1.dp
        )
    ) {

        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            // Timeline indicator

            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                Box(
                    modifier = Modifier
                        .size(12.dp)
                        .clip(CircleShape)
                        .background(
                            MaterialTheme.colorScheme.primary
                        )
                )
            }

            Spacer(modifier = Modifier.size(12.dp))

            Column(
                modifier = Modifier.weight(1f)
            ) {

                Text(
                    text = TimeFormatter.formatSession(
                        startMillis,
                        endMillis
                    ),
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold
                )

                Spacer(modifier = Modifier.height(3.dp))

                Text(
                    text = "Session duration",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Text(
                text = TimeFormatter.formatDuration(
                    durationMillis
                ),
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}


// ================================================================
// EMPTY TIMELINE
// ================================================================

@Composable
private fun EmptyTimelineCard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Icon(
                imageVector = Icons.Outlined.History,
                contentDescription = null,
                modifier = Modifier.size(32.dp),
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = "No sessions found",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "There is no session timeline for this period.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}