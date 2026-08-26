package com.example.devicelens.presentation.storage.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.devicelens.domain.model.AppStorageInfo
import kotlin.math.roundToInt

@Composable
fun AppStorageItem(
    app: AppStorageInfo,
    highlighted: Boolean = false
) {
    var expanded by rememberSaveable(
        key = app.packageName
    ) {
        mutableStateOf(false)
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                expanded = !expanded
            },
        colors = CardDefaults.cardColors(
            containerColor = if (highlighted) {
                MaterialTheme.colorScheme.errorContainer
            } else {
                MaterialTheme.colorScheme.surface
            }
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {

            // Main row
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {

                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = app.appName,
                        style = MaterialTheme.typography.titleMedium
                    )

                    Text(
                        text = app.packageName,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Column(
                    horizontalAlignment = Alignment.End
                ) {
                    Text(
                        text = formatBytes(app.totalBytes),
                        style = MaterialTheme.typography.titleMedium
                    )

                    app.percentOfDevice?.let { percent ->
                        Text(
                            text = "${percent.roundToInt()}% of storage",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Icon(
                    imageVector = if (expanded) {
                        Icons.Default.ExpandLess
                    } else {
                        Icons.Default.ExpandMore
                    },
                    contentDescription = if (expanded) {
                        "Collapse"
                    } else {
                        "Expand"
                    }
                )
            }

            // Expanded details
            AnimatedVisibility(
                visible = expanded
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {

                    StorageDetailRow(
                        label = "Total",
                        value = formatBytes(app.totalBytes)
                    )

                    StorageDetailRow(
                        label = "App",
                        value = app.apkBytes?.let(::formatBytes)
                            ?: "Not available"
                    )

                    StorageDetailRow(
                        label = "Data",
                        value = app.dataBytes?.let(::formatBytes)
                            ?: "Not available"
                    )

                    StorageDetailRow(
                        label = "Cache",
                        value = app.cacheBytes?.let(::formatBytes)
                            ?: "Not available"
                    )

                    app.percentOfDevice?.let { percent ->
                        StorageDetailRow(
                            label = "Device storage",
                            value = String.format(
                                "%.2f%%",
                                percent
                            )
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun StorageDetailRow(
    label: String,
    value: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            modifier = Modifier.weight(1f),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium
        )
    }
}

private fun formatBytes(bytes: Long): String {
    if (bytes <= 0L) return "0 MB"

    val kb = 1024.0
    val mb = kb * 1024.0
    val gb = mb * 1024.0

    return when {
        bytes >= gb -> {
            String.format(
                "%.2f GB",
                bytes / gb
            )
        }

        bytes >= mb -> {
            String.format(
                "%.1f MB",
                bytes / mb
            )
        }

        bytes >= kb -> {
            String.format(
                "%.1f KB",
                bytes / kb
            )
        }

        else -> {
            "$bytes B"
        }
    }
}