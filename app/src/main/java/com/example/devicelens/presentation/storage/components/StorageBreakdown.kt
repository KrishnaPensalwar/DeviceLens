package com.example.devicelens.presentation.storage.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.devicelens.core.util.FileSizeFormatter
import com.example.devicelens.domain.model.StorageOverview

@Composable
fun StorageBreakdown(overview: StorageOverview) {
    Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(
        containerColor = Color.Transparent
    )) {
        Column(
            modifier = Modifier,
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Text(
                text = "Apps",
                style = MaterialTheme.typography.titleMedium
            )
            BreakdownRow("Applications", overview.applicationsBytes)
            BreakdownRow("User Data", overview.userDataBytes)
            BreakdownRow("Cache", overview.cacheBytes)
            BreakdownRow("Other", overview.otherBytes)
        }
    }
}

@Composable
private fun BreakdownRow(
    label: String,
    bytes: Long?
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, style = MaterialTheme.typography.bodyLarge)
        Text(
            text = bytes?.let { FileSizeFormatter.format(it) } ?: "Not available",
            style = MaterialTheme.typography.bodyLarge
        )
    }
}
