package com.example.devicelens.presentation.health

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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.Refresh
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.devicelens.domain.model.HealthIssue
import com.example.devicelens.domain.model.HealthRecommendation
import com.example.devicelens.domain.model.HealthReport
import com.example.devicelens.presentation.health.components.HealthMetricItem
import com.example.devicelens.presentation.health.components.HealthScoreCard
import com.example.devicelens.presentation.health.components.RecommendationCard
import io.github.sanketnawghare.glassify.compose.GlassButton
import io.github.sanketnawghare.glassify.compose.GlassStyle
import io.github.sanketnawghare.glassify.compose.glassify


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
            uiState.report?.let {
                HealthContent(
                    report = it,
                    recommendations = uiState.recommendations,
                    onCheckAgain = viewModel::checkHealth
                )
            }
        }
    }
}


// ================================================================
// LOADING
// ================================================================

@Composable
private fun HealthLoading() {

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {

        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            CircularProgressIndicator()

            Spacer(modifier = Modifier.height(14.dp))

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
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {

        GlassButton(
            onClick = onRetry,
            modifier = Modifier
                .fillMaxWidth()
                .height(190.dp)
                .glassify(
                    style = GlassStyle.Thick
                )
        ) {

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {

                Icon(
                    imageVector = Icons.Outlined.Warning,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.error,
                    modifier = Modifier.size(34.dp)
                )

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "Health check failed",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = message,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    text = "Tap to try again",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.primary
                )
            }
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

    androidx.compose.foundation.lazy.LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 20.dp),
        contentPadding = androidx.compose.foundation.layout.PaddingValues(
            top = 24.dp,
            bottom = 32.dp
        ),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {

        // --------------------------------------------------------
        // HEADER
        // --------------------------------------------------------

        item {

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {

                Column(
                    modifier = Modifier.weight(1f)
                ) {

                    Text(
                        text = "Device Health",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = "A quick check of your device condition",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                GlassRefreshButton(
                    onClick = onCheckAgain
                )
            }
        }


        // --------------------------------------------------------
        // SCORE
        // --------------------------------------------------------

        item {

            HealthScoreCard(
                report = report
            )
        }


        // --------------------------------------------------------
        // OVERVIEW
        // --------------------------------------------------------

        item {

            HealthOverviewCard(
                report = report
            )
        }


        // --------------------------------------------------------
        // ISSUES
        // --------------------------------------------------------

        item {

            HealthIssuesCard(
                issues = report.issues
            )
        }


        // --------------------------------------------------------
        // RECOMMENDATIONS
        // --------------------------------------------------------

        if (recommendations.isNotEmpty()) {

            item {

                SectionHeader(
                    title = "Recommendations",
                    subtitle = "Suggestions to keep your device healthy"
                )
            }

            items(
                recommendations,
                key = { it.title }
            ) { recommendation ->

                RecommendationCard(
                    recommendation = recommendation
                )
            }
        }

    }
}


// ================================================================
// REFRESH BUTTON
// ================================================================

@Composable
private fun GlassRefreshButton(
    onClick: () -> Unit
) {

//    GlassButton(
//        onClick = onClick,
//        modifier = Modifier
////            .size(48.dp)
//            .glassify(
//                style = GlassStyle.Regular
//            )
//    ) {
//
//        Icon(
//            imageVector = Icons.Outlined.Refresh,
//            contentDescription = "Refresh health",
//            modifier = Modifier.size(31.dp)
//        )
//
//    }
    Box(
        modifier = Modifier
            .size(44.dp)
            .clip(CircleShape)
            .background(
                MaterialTheme.colorScheme.surfaceVariant
            ),
        contentAlignment = Alignment.Center
    ) {

        Icon(
            imageVector = Icons.Outlined.Refresh,
            contentDescription = "Refresh health",
            modifier = Modifier.size(31.dp)
        )
    }
}


// ================================================================
// HEALTH OVERVIEW
// ================================================================

@Composable
private fun HealthOverviewCard(
    report: HealthReport
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
                .padding(12.dp)
        ) {

            Text(
                text = "Health Overview",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )

            Spacer(modifier = Modifier.height(14.dp))

            report.areas.forEachIndexed { index, area ->

                HealthMetricItem(
                    area = area
                )

                if (index != report.areas.lastIndex) {
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
        }
    }
}


// ================================================================
// ISSUES CARD
// ================================================================

@Composable
private fun HealthIssuesCard(
    issues: List<HealthIssue>
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
                .padding(12.dp)
        ) {

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {

                Box(
                    modifier = Modifier
                        .size(42.dp)
                        .clip(RoundedCornerShape(13.dp))
                        .background(
                            if (issues.isEmpty()) {
                                MaterialTheme.colorScheme.primaryContainer
                            } else {
                                MaterialTheme.colorScheme.errorContainer
                            }
                        ),
                    contentAlignment = Alignment.Center
                ) {

                    Icon(
                        imageVector = if (issues.isEmpty()) {
                            Icons.Outlined.CheckCircle
                        } else {
                            Icons.Outlined.Warning
                        },
                        contentDescription = null,
                        tint = if (issues.isEmpty()) {
                            MaterialTheme.colorScheme.primary
                        } else {
                            MaterialTheme.colorScheme.error
                        },
                        modifier = Modifier.size(22.dp)
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column {

                    Text(
                        text = "Things to Look At",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )

                    Spacer(modifier = Modifier.height(2.dp))

                    Text(
                        text = if (issues.isEmpty()) {
                            "No issues detected"
                        } else {
                            "${issues.size} issue${if (issues.size > 1) "s" else ""} detected"
                        },
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            if (issues.isEmpty()) {

                Text(
                    text = "Everything looks good. Your device is operating normally.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

            } else {

                issues.forEachIndexed { index, issue ->

                    IssueRow(issue)

                    if (index != issues.lastIndex) {
                        Spacer(modifier = Modifier.height(12.dp))
                    }
                }
            }
        }
    }
}


// ================================================================
// ISSUE ROW
// ================================================================

@Composable
private fun IssueRow(
    issue: HealthIssue
) {

    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.Top
    ) {

        Box(
            modifier = Modifier
                .padding(top = 5.dp)
                .size(7.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.error)
        )

        Spacer(modifier = Modifier.width(10.dp))

        Column(
            modifier = Modifier.weight(1f)
        ) {

            Text(
                text = issue.title,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.error
            )

            Spacer(modifier = Modifier.height(3.dp))

            Text(
                text = issue.detail,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}


// ================================================================
// SECTION HEADER
// ================================================================

@Composable
private fun SectionHeader(
    title: String,
    subtitle: String
) {

    Column {

        Text(
            text = title,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.SemiBold
        )

        Spacer(modifier = Modifier.height(3.dp))

        Text(
            text = subtitle,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}


// ================================================================
// LAST CHECKED
// ================================================================

@Composable
private fun LastCheckedCard(
    checkedAtMillis: Long
) {

    GlassButton(
        onClick = {},
        modifier = Modifier
            .fillMaxWidth()
            .glassify(
                style = GlassStyle.Thin
            )
    ) {

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
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
                    imageVector = Icons.Outlined.Refresh,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column {

                Text(
                    text = "Last checked",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(2.dp))

                Text(
                    text = formatLastChecked(checkedAtMillis),
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}


// ================================================================
// TIME FORMATTER
// ================================================================

private fun formatLastChecked(
    millis: Long
): String {

    val elapsed = System.currentTimeMillis() - millis

    return when {

        elapsed < 60_000 ->
            "Just now"

        elapsed < 3_600_000 ->
            "${elapsed / 60_000} min ago"

        elapsed < 86_400_000 ->
            "${elapsed / 3_600_000} hr ago"

        else ->
            "${elapsed / 86_400_000} days ago"
    }
}