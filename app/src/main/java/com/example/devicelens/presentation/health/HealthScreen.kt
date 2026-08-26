package com.example.devicelens.presentation.health

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.devicelens.domain.model.HealthIssue
import com.example.devicelens.domain.model.HealthRecommendation
import com.example.devicelens.domain.model.HealthReport
import com.example.devicelens.presentation.health.components.HealthMetricItem
import com.example.devicelens.presentation.health.components.HealthScoreCard
import com.example.devicelens.presentation.health.components.RecommendationCard

@Composable
fun HealthScreen(
    viewModel: HealthViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val error = uiState.error
    val report = uiState.report

    when {
        uiState.isLoading -> {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }

        error != null -> {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(error, color = MaterialTheme.colorScheme.error)
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(onClick = viewModel::checkHealth) {
                        Text("Check again")
                    }
                }
            }
        }

        report != null -> {
            HealthContent(
                report = report,
                recommendations = uiState.recommendations,
                onCheckAgain = viewModel::checkHealth
            )
        }
    }
}

@Composable
private fun HealthContent(
    report: HealthReport,
    recommendations: List<HealthRecommendation>,
    onCheckAgain: () -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Text("Health", style = MaterialTheme.typography.headlineSmall)
        }

        item { HealthScoreCard(report) }

        item {
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Health Overview",
                        style = MaterialTheme.typography.titleMedium
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    report.areas.forEach { area ->
                        HealthMetricItem(area)
                    }
                }
            }
        }

        item {
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text(
                        text = "Things to look at",
                        style = MaterialTheme.typography.titleMedium
                    )
                    if (report.issues.isEmpty()) {
                        Text("Everything looks good.")
                    } else {
                        report.issues.forEach { issue ->
                            IssueRow(issue)
                        }
                    }
                }
            }
        }

        if (recommendations.isNotEmpty()) {
            item {
                Text(
                    text = "Recommendations",
                    style = MaterialTheme.typography.titleMedium
                )
            }
            items(recommendations, key = { it.title }) { recommendation ->
                RecommendationCard(recommendation)
            }
        }

        item {
            Text(
                text = "Last checked: ${formatLastChecked(report.checkedAtMillis)}",
                style = MaterialTheme.typography.bodyMedium
            )
        }

        item {
            Button(
                onClick = onCheckAgain,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Check again")
            }
        }
    }
}

@Composable
private fun IssueRow(issue: HealthIssue) {
    Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
        Text(
            text = issue.title,
            style = MaterialTheme.typography.titleSmall,
            color = MaterialTheme.colorScheme.error
        )
        Text(
            text = issue.detail,
            style = MaterialTheme.typography.bodyMedium
        )
    }
}

private fun formatLastChecked(millis: Long): String {
    val elapsed = System.currentTimeMillis() - millis
    return if (elapsed < 60_000) "Just now" else "${elapsed / 60_000} min ago"
}
