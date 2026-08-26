package com.example.devicelens.presentation.storage.components

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
import com.example.devicelens.core.util.FileSizeFormatter
import com.example.devicelens.domain.model.StorageOverview

@Composable
fun StorageOverviewCard(overview: StorageOverview) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Text(
                text = "Storage",
                style = MaterialTheme.typography.titleMedium
            )
            Text(
                text = "${FileSizeFormatter.format(overview.totalBytes)} Total",
                style = MaterialTheme.typography.headlineSmall
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Used  ${FileSizeFormatter.format(overview.usedBytes)}")
                Text("Free  ${FileSizeFormatter.format(overview.freeBytes)}")
            }
            LinearProgressIndicator(
                progress = { overview.usagePercent / 100f },
                modifier = Modifier.fillMaxWidth()
            )
            Text(
                text = "${overview.usagePercent}% storage used",
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}
