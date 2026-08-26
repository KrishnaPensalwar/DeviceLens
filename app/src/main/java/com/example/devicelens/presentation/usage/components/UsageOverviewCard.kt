package com.example.devicelens.presentation.usage.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.devicelens.core.util.TimeFormatter
import com.example.devicelens.domain.model.UsagePeriod

@Composable
fun UsageOverviewCard(
    period: UsagePeriod,
    totalUsageMillis: Long
) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text("App usage", style = MaterialTheme.typography.titleMedium)
            Text(
                text = TimeFormatter.formatDuration(totalUsageMillis),
                style = MaterialTheme.typography.headlineSmall
            )
            Text(
                text = periodLabel(period),
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

fun periodLabel(period: UsagePeriod): String {
    return when (period) {
        UsagePeriod.TODAY -> "Today"
        UsagePeriod.YESTERDAY -> "Yesterday"
        UsagePeriod.LAST_7_DAYS -> "Last 7 days"
    }
}
