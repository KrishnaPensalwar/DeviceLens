package com.example.devicelens.presentation.health.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.devicelens.domain.model.HealthReport
import com.example.devicelens.domain.model.HealthStatus

@Composable
fun HealthScoreCard(report: HealthReport) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.padding(20.dp).fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = "${report.score}/100",
                style = MaterialTheme.typography.displaySmall
            )
            Text(
                text = report.status.label(),
                style = MaterialTheme.typography.titleLarge
            )
            Text(
                text = report.summary,
                style = MaterialTheme.typography.bodyLarge
            )
            if (report.attentionCount > 0) {
                Text(
                    text = if (report.attentionCount == 1) {
                        "1 area needs your attention."
                    } else {
                        "${report.attentionCount} areas need your attention."
                    },
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}

fun HealthStatus.label(): String {
    return when (this) {
        HealthStatus.EXCELLENT -> "Excellent"
        HealthStatus.GOOD -> "Good"
        HealthStatus.NEEDS_ATTENTION -> "Needs Attention"
        HealthStatus.POOR -> "Poor"
    }
}
