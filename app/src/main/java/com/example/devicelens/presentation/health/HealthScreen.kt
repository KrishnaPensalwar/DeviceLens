package com.example.devicelens.presentation.health

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material.icons.outlined.BatteryStd
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.CleaningServices
import androidx.compose.material.icons.outlined.DeviceThermostat
import androidx.compose.material.icons.outlined.Memory
import androidx.compose.material.icons.outlined.Refresh
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.outlined.Storage
import androidx.compose.material.icons.outlined.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.devicelens.domain.model.HealthArea
import com.example.devicelens.domain.model.HealthIssue
import com.example.devicelens.domain.model.HealthRecommendation
import com.example.devicelens.domain.model.HealthReport


// ================================================================
// SCREEN
// ================================================================

@Composable
fun HealthScreen(
    viewModel: HealthViewModel = hiltViewModel()
) {

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    when {

        uiState.isLoading -> {
            HealthLoading()
        }

        uiState.error != null -> {
            HealthError(
                message = uiState.error ?: "",
                onRetry = viewModel::checkHealth
            )
        }

        uiState.report != null -> {

            HealthContent(
                report = uiState.report!!,
                recommendations = uiState.recommendations,
                onCheckAgain = viewModel::checkHealth
            )
        }
    }
}


// ================================================================
// MAIN CONTENT
// ================================================================

@Composable
private fun HealthContent(
    report: HealthReport,
    recommendations: List<HealthRecommendation>,
    onCheckAgain: () -> Unit
) {

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(
                MaterialTheme.colorScheme.background
            ),

        contentPadding = PaddingValues(
            start = 16.dp,
            end = 16.dp,
            top = 18.dp,
            bottom = 110.dp
        ),

        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {

        // --------------------------------------------------------
        // TOP APP BAR
        // --------------------------------------------------------

        item {
            HealthTopBar(
                onRefresh = onCheckAgain
            )
        }

        // --------------------------------------------------------
        // TITLE
        // --------------------------------------------------------

        item {

            Text(
                text = "Device Health",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.padding(
                    top = 8.dp
                )
            )
        }

        // --------------------------------------------------------
        // SCORE
        // --------------------------------------------------------

        item {
            HealthScoreDashboard(
                report = report
            )
        }

        // --------------------------------------------------------
        // HEALTH METRICS
        // --------------------------------------------------------

        items(
            items = report.areas,
            key = { area ->
                area.hashCode()
            }
        ) { area ->

            HealthMetricCard(
                area = area
            )
        }

        // --------------------------------------------------------
        // RECOMMENDATIONS
        // --------------------------------------------------------

        if (recommendations.isNotEmpty()) {

            item {

                Text(
                    text = "Recommendations",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground,
                    modifier = Modifier.padding(
                        top = 12.dp
                    )
                )
            }

            items(
                items = recommendations,
                key = { it.title }
            ) { recommendation ->

                RecommendationDashboardCard(
                    recommendation = recommendation
                )
            }
        }

        // --------------------------------------------------------
        // ISSUES
        // --------------------------------------------------------

        if (report.issues.isNotEmpty()) {

            item {

                HealthIssuesDashboard(
                    issues = report.issues
                )
            }
        }
    }
}


// ================================================================
// TOP BAR
// ================================================================

@Composable
private fun HealthTopBar(
    onRefresh: () -> Unit
) {

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(48.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {

        // --------------------------------------------------------
        // DEVICE ICON
        // --------------------------------------------------------

        Box(
            modifier = Modifier
                .size(32.dp),
            contentAlignment = Alignment.Center
        ) {

            Text(
                text = "▣",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
        }

        Spacer(
            modifier = Modifier.width(10.dp)
        )

        Text(
            text = "DeviceLens",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )

        Spacer(
            modifier = Modifier.weight(1f)
        )

        // Refresh

        Box(
            modifier = Modifier
                .size(42.dp)
                .clip(CircleShape)
                .background(
                    MaterialTheme.colorScheme.surfaceVariant
                ),
            contentAlignment = Alignment.Center
        ) {

            Icon(
                imageVector = Icons.Outlined.Refresh,
                contentDescription = "Refresh",
                modifier = Modifier
                    .size(22.dp)
            )
        }

        Spacer(
            modifier = Modifier.width(8.dp)
        )

        // Settings

        Box(
            modifier = Modifier
                .size(42.dp)
                .clip(CircleShape)
                .background(
                    MaterialTheme.colorScheme.surfaceVariant
                ),
            contentAlignment = Alignment.Center
        ) {

            Icon(
                imageVector = Icons.Outlined.Settings,
                contentDescription = "Settings",
                modifier = Modifier.size(23.dp)
            )
        }
    }
}


// ================================================================
// SCORE CARD
// ================================================================

@Composable
private fun HealthScoreDashboard(
    report: HealthReport
) {

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(216.dp)
            .clip(
                RoundedCornerShape(12.dp)
            )
            .background(
                Color(
                    red = 0x15,
                    green = 0x1B,
                    blue = 0x1A
                )
            )
            .border(
                width = 1.dp,
                color = MaterialTheme.colorScheme.outlineVariant,
                shape = RoundedCornerShape(12.dp)
            )
            .padding(18.dp),
        contentAlignment = Alignment.Center
    ) {

        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Text(
                text = "Overall Status",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(
                modifier = Modifier.height(8.dp)
            )

            Row(
                verticalAlignment = Alignment.Bottom
            ) {

                Text(
                    text = report.score.toString(),
                    style = MaterialTheme.typography.displayLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )

                Spacer(
                    modifier = Modifier.width(8.dp)
                )

                Text(
                    text = "/ 100",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(
                        bottom = 10.dp
                    )
                )
            }

            Spacer(
                modifier = Modifier.height(16.dp)
            )

            StatusPill(
                text = healthStatus(report.score),
                icon = Icons.Outlined.CheckCircle
            )
        }
    }
}


// ================================================================
// STATUS PILL
// ================================================================

@Composable
private fun StatusPill(
    text: String,
    icon: ImageVector
) {

    Row(
        modifier = Modifier
            .clip(CircleShape)
            .background(
                MaterialTheme.colorScheme.primary.copy(
                    alpha = 0.15f
                )
            )
            .border(
                width = 1.dp,
                color = MaterialTheme.colorScheme.primary.copy(
                    alpha = 0.25f
                ),
                shape = CircleShape
            )
            .padding(
                horizontal = 18.dp,
                vertical = 9.dp
            ),
        verticalAlignment = Alignment.CenterVertically
    ) {

        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier.size(20.dp),
            tint = MaterialTheme.colorScheme.primary
        )

        Spacer(
            modifier = Modifier.width(8.dp)
        )

        Text(
            text = text.uppercase(),
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )
    }
}


// ================================================================
// METRIC CARD
// ================================================================

@Composable
private fun HealthMetricCard(
    area: Any
) {

    /*
     * We intentionally keep the existing HealthMetricItem here.
     *
     * Your HealthReport.area model already knows how to render the
     * metric. The card around it is redesigned to match the image.
     */

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(
                RoundedCornerShape(10.dp)
            )
            .background(
                Color(
                    red = 0x1A,
                    green = 0x1F,
                    blue = 0x1E
                )
            )
            .border(
                width = 1.dp,
                color = MaterialTheme.colorScheme.outlineVariant,
                shape = RoundedCornerShape(10.dp)
            )
            .padding(
                horizontal = 16.dp,
                vertical = 15.dp
            )
    ) {

        /*
         * Use your existing component.
         *
         * If HealthMetricItem currently has its own Card/GlassButton,
         * remove that outer container from HealthMetricItem so this
         * becomes the only card.
         */

        @Suppress("UNCHECKED_CAST")
        com.example.devicelens.presentation.health.components.HealthMetricItem(
            area = area as HealthArea
        )
    }
}


// ================================================================
// RECOMMENDATION
// ================================================================

@Composable
private fun RecommendationDashboardCard(
    recommendation: HealthRecommendation
) {

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(
                RoundedCornerShape(10.dp)
            )
            .background(
                Color(
                    red = 0x19,
                    green = 0x1D,
                    blue = 0x1B
                )
            )
            .border(
                width = 1.dp,
                color = MaterialTheme.colorScheme.outlineVariant,
                shape = RoundedCornerShape(10.dp)
            )
            .padding(16.dp)
    ) {

        Row(
            verticalAlignment = Alignment.Top
        ) {

            Box(
                modifier = Modifier
                    .size(42.dp)
                    .clip(CircleShape)
                    .background(
                        MaterialTheme.colorScheme.tertiaryContainer
                    ),
                contentAlignment = Alignment.Center
            ) {

                Icon(
                    imageVector = Icons.Outlined.Warning,
                    contentDescription = null,
                    modifier = Modifier.size(22.dp),
                    tint = MaterialTheme.colorScheme.tertiary
                )
            }

            Spacer(
                modifier = Modifier.width(14.dp)
            )

            Column(
                modifier = Modifier.weight(1f)
            ) {

                Text(
                    text = recommendation.title,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Spacer(
                    modifier = Modifier.height(5.dp)
                )

                Text(
                    text = recommendation.title,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(
                    modifier = Modifier.height(12.dp)
                )

                Button(
                    onClick = {},
                    modifier = Modifier
                        .height(40.dp)
                ) {

                    Text(
                        text = "Clean Up"
                    )
                }
            }
        }
    }
}


// ================================================================
// ISSUES
// ================================================================

@Composable
private fun HealthIssuesDashboard(
    issues: List<HealthIssue>
) {

    Column {

        Text(
            text = "Things to Look At",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )

        Spacer(
            modifier = Modifier.height(10.dp)
        )

        issues.forEach { issue ->

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(
                        RoundedCornerShape(10.dp)
                    )
                    .background(
                        MaterialTheme.colorScheme.errorContainer
                            .copy(alpha = 0.25f)
                    )
                    .padding(14.dp),
                verticalAlignment = Alignment.Top
            ) {

                Icon(
                    imageVector = Icons.Outlined.Warning,
                    contentDescription = null,
                    modifier = Modifier.size(22.dp),
                    tint = MaterialTheme.colorScheme.error
                )

                Spacer(
                    modifier = Modifier.width(10.dp)
                )

                Column {

                    Text(
                        text = issue.title,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.error
                    )

                    Spacer(
                        modifier = Modifier.height(3.dp)
                    )

                    Text(
                        text = issue.detail,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(
                modifier = Modifier.height(8.dp)
            )
        }
    }
}


// ================================================================
// LOADING
// ================================================================

@Composable
private fun HealthLoading() {

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                MaterialTheme.colorScheme.background
            ),
        contentAlignment = Alignment.Center
    ) {

        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            CircularProgressIndicator(
                modifier = Modifier.size(38.dp)
            )

            Spacer(
                modifier = Modifier.height(14.dp)
            )

            Text(
                text = "Checking device health...",
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
private fun HealthError(
    message: String,
    onRetry: () -> Unit
) {

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp),
        contentAlignment = Alignment.Center
    ) {

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(
                    RoundedCornerShape(12.dp)
                )
                .background(
                    MaterialTheme.colorScheme.surface
                )
                .border(
                    1.dp,
                    MaterialTheme.colorScheme.outlineVariant,
                    RoundedCornerShape(12.dp)
                )
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Icon(
                imageVector = Icons.Outlined.Warning,
                contentDescription = null,
                modifier = Modifier.size(42.dp),
                tint = MaterialTheme.colorScheme.error
            )

            Spacer(
                modifier = Modifier.height(12.dp)
            )

            Text(
                text = "Health check failed",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            Spacer(
                modifier = Modifier.height(6.dp)
            )

            Text(
                text = message,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(
                modifier = Modifier.height(16.dp)
            )

            Button(
                onClick = onRetry
            ) {

                Icon(
                    imageVector = Icons.Outlined.Refresh,
                    contentDescription = null
                )

                Spacer(
                    modifier = Modifier.width(6.dp)
                )

                Text(
                    text = "Try again"
                )
            }
        }
    }
}


// ================================================================
// HELPERS
// ================================================================

private fun healthStatus(
    score: Int
): String {

    return when {

        score >= 90 ->
            "Excellent"

        score >= 80 ->
            "Good"

        score >= 60 ->
            "Fair"

        score >= 40 ->
            "Warning"

        else ->
            "Critical"
    }
}