package com.example.devicelens.presentation.health.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Lightbulb
import androidx.compose.material.icons.outlined.PriorityHigh
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.devicelens.domain.model.HealthRecommendation
import com.example.devicelens.domain.model.RecommendationPriority
import io.github.sanketnawghare.glassify.compose.GlassButton
import io.github.sanketnawghare.glassify.compose.GlassStyle
import io.github.sanketnawghare.glassify.compose.glassify

@Composable
fun RecommendationCard(
    recommendation: HealthRecommendation
) {
    val priorityColor = when (recommendation.priority) {
        RecommendationPriority.HIGH ->
            MaterialTheme.colorScheme.error

        RecommendationPriority.MEDIUM ->
            MaterialTheme.colorScheme.tertiary

        RecommendationPriority.LOW ->
            MaterialTheme.colorScheme.primary
    }

    GlassButton(
        onClick = {},
        modifier = Modifier
            .fillMaxWidth()
            .glassify(
                style = GlassStyle.Thick
            )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {

            // Recommendation icon
            Box(
                modifier = Modifier
                    .size(46.dp)
                    .clip(RoundedCornerShape(15.dp))
                    .background(
                        priorityColor.copy(alpha = 0.12f)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = when (recommendation.priority) {
                        RecommendationPriority.HIGH ->
                            Icons.Outlined.PriorityHigh

                        else ->
                            Icons.Outlined.Lightbulb
                    },
                    contentDescription = null,
                    modifier = Modifier.size(23.dp),
                    tint = priorityColor
                )
            }

            Spacer(modifier = Modifier.size(14.dp))

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(5.dp)
            ) {

                // Priority badge
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(
                            priorityColor.copy(alpha = 0.12f)
                        )
                        .padding(
                            horizontal = 8.dp,
                            vertical = 4.dp
                        )
                ) {
                    Text(
                        text = recommendation.priority.label(),
                        style = MaterialTheme.typography.labelSmall,
                        color = priorityColor
                    )
                }

                Text(
                    text = recommendation.title,
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        }
    }
}

private fun RecommendationPriority.label(): String {
    return when (this) {
        RecommendationPriority.HIGH -> "HIGH PRIORITY"
        RecommendationPriority.MEDIUM -> "MEDIUM PRIORITY"
        RecommendationPriority.LOW -> "LOW PRIORITY"
    }
}