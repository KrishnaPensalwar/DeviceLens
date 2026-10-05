package com.example.devicelens.presentation.health

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.material.icons.outlined.Refresh
import androidx.compose.material.icons.outlined.Warning
import androidx.compose.material3.Button
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
import com.example.devicelens.domain.model.HealthRecommendation
import com.example.devicelens.domain.model.HealthReport
import com.example.devicelens.domain.model.HealthStatus
import com.example.devicelens.presentation.components.LensLoading
import com.example.devicelens.presentation.components.vitalCard.VitalCard
import com.example.devicelens.presentation.components.vitalCard.VitalCardModel
import com.example.devicelens.presentation.health.components.HealthIssuesDashboard
import com.example.devicelens.presentation.health.components.HealthScoreDashboard
import com.example.devicelens.presentation.health.components.RecommendationDashboardCard
import com.example.devicelens.ui.theme.LensGreen
import com.example.devicelens.ui.theme.LensOrange
import com.example.devicelens.ui.theme.LensTextMuted
import com.example.devicelens.ui.theme.LensTextPrimary

@Composable
fun HealthScreen(
    viewModel: HealthViewModel = hiltViewModel(),
) {

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    when {

        uiState.isLoading -> {
            LensLoading(message = "Checking device health...")
        }

        uiState.error != null -> {
            HealthError(
                message = uiState.error ?: "",
                onRetry = viewModel::checkHealth,
            )
        }

        uiState.report != null -> {
            HealthContent(
                report = uiState.report!!,
                recommendations = uiState.recommendations,
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
) {

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
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
            val statusColor = if ((area.status == HealthStatus.GOOD) || (area.status == HealthStatus.EXCELLENT)) {
                LensGreen
            } else {
                LensOrange
            }
            VitalCard(
                vitalCardModel = VitalCardModel(
                    title = area.title,
                    value = area.description,
                    right = area.status.marker(),
                    rightSubheadingColor = statusColor,
                    titleStyle = MaterialTheme.typography.titleMedium.copy(color = LensTextPrimary),
                    valueStyle = MaterialTheme.typography.bodyMedium.copy(color = LensTextMuted),
                    rightStyle = MaterialTheme.typography.bodyLarge
                )
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

fun healthStatus(
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

private fun HealthStatus.marker(): String {
    return when (this) {
        HealthStatus.EXCELLENT, HealthStatus.GOOD -> "Good"
        HealthStatus.NEEDS_ATTENTION -> "Attention"
        HealthStatus.POOR -> "Poor"
    }
}
