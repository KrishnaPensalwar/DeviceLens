package com.example.devicelens.presentation.health.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.devicelens.domain.model.HealthRecommendation
import com.example.devicelens.domain.model.RecommendationPriority

@Composable
fun RecommendationCard(recommendation: HealthRecommendation) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = recommendation.priority.label(),
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.primary
            )
            Text(
                text = recommendation.title,
                style = MaterialTheme.typography.bodyLarge
            )
        }
    }
}

private fun RecommendationPriority.label(): String {
    return when (this) {
        RecommendationPriority.HIGH -> "HIGH"
        RecommendationPriority.MEDIUM -> "MEDIUM"
        RecommendationPriority.LOW -> "LOW"
    }
}
