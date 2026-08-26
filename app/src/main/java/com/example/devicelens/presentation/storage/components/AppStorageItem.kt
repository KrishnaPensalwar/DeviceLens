package com.example.devicelens.presentation.storage.components

import android.graphics.drawable.Drawable
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Storage
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.graphics.drawable.toBitmap
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

    val context = LocalContext.current

    val appIcon = remember(app.packageName) {
        runCatching {
            context.packageManager.getApplicationIcon(app.packageName)
        }.getOrNull()
    }

    val percent = app.percentOfDevice
        ?.coerceIn(0.0, 100.0)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .animateContentSize()
            .clickable {
                expanded = !expanded
            },
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (highlighted) {
                MaterialTheme.colorScheme.errorContainer
            } else {
                MaterialTheme.colorScheme.surfaceContainer
            }
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 2.dp
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {

            /*
             * Header
             */
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {

                AppIcon(
                    drawable = appIcon
                )

                Spacer(modifier = Modifier.width(12.dp))

                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = app.appName,
                        style = MaterialTheme.typography.titleMedium,
                        maxLines = 1
                    )

                    Spacer(modifier = Modifier.size(2.dp))

                    Text(
                        text = app.packageName,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1
                    )
                }

                Spacer(modifier = Modifier.width(8.dp))

                Column(
                    horizontalAlignment = Alignment.End
                ) {
                    Text(
                        text = formatBytes(app.totalBytes),
                        style = MaterialTheme.typography.titleMedium
                    )

                    app.percentOfDevice?.let {
                        Text(
                            text = "${it.roundToInt()}% of device",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            /*
             * Storage usage indicator
             */
            if (percent != null) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Storage usage",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        Text(
                            text = String.format("%.1f%%", percent),
                            style = MaterialTheme.typography.labelMedium
                        )
                    }

                    LinearProgressIndicator(
                        progress = {
                            percent.toFloat() / 100f
                        },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }

            /*
             * Expanded section
             */
            AnimatedVisibility(
                visible = expanded
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {

                    HorizontalDivider()

                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Storage,
                            contentDescription = null,
                            modifier = Modifier.size(20.dp),
                            tint = MaterialTheme.colorScheme.primary
                        )

                        Spacer(modifier = Modifier.width(8.dp))

                        Text(
                            text = "Storage details",
                            style = MaterialTheme.typography.titleSmall
                        )
                    }

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

                    app.percentOfDevice?.let { value ->
                        StorageDetailRow(
                            label = "Device storage",
                            value = String.format(
                                "%.2f%%",
                                value
                            )
                        )
                    }
                }
            }

            /*
             * Expand / collapse hint
             */
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (expanded) {
                        "Hide details"
                    } else {
                        "View details"
                    },
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.primary
                )

                Spacer(modifier = Modifier.width(4.dp))

                Icon(
                    imageVector = if (expanded) {
                        Icons.Default.ExpandLess
                    } else {
                        Icons.Default.ExpandMore
                    },
                    contentDescription = null,
                    modifier = Modifier.size(20.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}

@Composable
private fun AppIcon(
    drawable: Drawable?
) {
    if (drawable == null) {
        Icon(
            imageVector = Icons.Default.Storage,
            contentDescription = null,
            modifier = Modifier.size(44.dp),
            tint = MaterialTheme.colorScheme.primary
        )
        return
    }

    val bitmap = remember(drawable) {
        drawable
            .toBitmap()
            .asImageBitmap()
    }

    Image(
        bitmap = bitmap,
        contentDescription = null,
        modifier = Modifier.size(44.dp)
    )
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