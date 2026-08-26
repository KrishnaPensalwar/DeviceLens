package com.example.devicelens.presentation.health.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.devicelens.domain.model.HealthArea
import com.example.devicelens.domain.model.HealthStatus

@Composable
fun HealthMetricItem(area: HealthArea) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(area.title, style = MaterialTheme.typography.titleMedium)
            Text(
                text = area.description,
                style = MaterialTheme.typography.bodyMedium
            )
        }
        Text(
            text = area.status.marker(),
            style = MaterialTheme.typography.bodyLarge,
            color = if (area.status == HealthStatus.GOOD || area.status == HealthStatus.EXCELLENT) {
                MaterialTheme.colorScheme.primary
            } else {
                MaterialTheme.colorScheme.error
            }
        )
    }
}

private fun HealthStatus.marker(): String {
    return when (this) {
        HealthStatus.EXCELLENT, HealthStatus.GOOD -> "Good"
        HealthStatus.NEEDS_ATTENTION -> "Attention"
        HealthStatus.POOR -> "Poor"
    }
}
