package com.example.devicelens.presentation.dashboard

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.BatteryStd
import androidx.compose.material.icons.outlined.HealthAndSafety
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Language
import androidx.compose.material.icons.outlined.Memory
import androidx.compose.material.icons.outlined.QueryStats
import androidx.compose.material.icons.outlined.Storage
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import io.github.sanketnawghare.glassify.compose.GlassButton
import io.github.sanketnawghare.glassify.compose.GlassStyle
import io.github.sanketnawghare.glassify.compose.glassify

@Composable
fun DashboardScreen(
    onDeviceInfoClick: () -> Unit,
    onAppsClick: () -> Unit,
    onNetworkClick: () -> Unit,
    onStorageClick: () -> Unit,
    onHealthClick: () -> Unit,
    onUsageClick: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp)
            .padding(top = 24.dp)
    ) {

        // ---------------------------------------------------------
        // HEADER
        // ---------------------------------------------------------

        Text(
            text = "Device Lens",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground
        )

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = "Everything about your device",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(24.dp))

        // ---------------------------------------------------------
        // DEVICE OVERVIEW
        // ---------------------------------------------------------

        DeviceOverviewCard()

        Spacer(modifier = Modifier.height(16.dp))

        // ---------------------------------------------------------
        // DEVICE METRICS
        // ---------------------------------------------------------

        DeviceMetricsGrid()

        Spacer(modifier = Modifier.height(24.dp))

        // ---------------------------------------------------------
        // QUICK ACCESS
        // ---------------------------------------------------------

        Text(
            text = "Quick Access",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onBackground
        )

        Spacer(modifier = Modifier.height(12.dp))

//        LazyVerticalGrid(
//            columns = GridCells.Fixed(2),
//            modifier = Modifier.fillMaxWidth(),
//            horizontalArrangement = Arrangement.spacedBy(12.dp),
//            verticalArrangement = Arrangement.spacedBy(12.dp),
//        )
//        {
//
//            item {
//                DashboardGlassCard(
//                    icon = Icons.Outlined.Info,
//                    title = "Device Info",
//                    description = "Hardware & software",
//                    onClick = onDeviceInfoClick
//                )
//            }
//
//            item {
//                DashboardGlassCard(
//                    icon = Icons.Outlined.BatteryStd,
//                    title = "Battery",
//                    description = "Power information",
//                    onClick = onAppsClick
//                )
//            }
//
//            item {
//                DashboardGlassCard(
//                    icon = Icons.Outlined.Language,
//                    title = "Network",
//                    description = "Connection details",
//                    onClick = onNetworkClick
//                )
//            }
//
//            item {
//                DashboardGlassCard(
//                    icon = Icons.Outlined.Storage,
//                    title = "Storage",
//                    description = "Usage & space",
//                    onClick = onStorageClick
//                )
//            }
//
//            item {
//                DashboardGlassCard(
//                    icon = Icons.Outlined.HealthAndSafety,
//                    title = "Device Health",
//                    description = "Health status",
//                    onClick = onHealthClick
//                )
//            }
//
//            item {
//                DashboardGlassCard(
//                    icon = Icons.Outlined.QueryStats,
//                    title = "App Usage",
//                    description = "Usage statistics",
//                    onClick = onUsageClick
//                )
//            }
//        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            DashboardGlassCard(
                modifier = Modifier.weight(1f),
                icon = Icons.Outlined.Info,
                title = "Device Info",
                description = "Hardware & software",
                onClick = onDeviceInfoClick
            )

            DashboardGlassCard(
                modifier = Modifier.weight(1f),
                icon = Icons.Outlined.BatteryStd,
                title = "Battery",
                description = "Power information",
                onClick = onAppsClick
            )
        }
        Spacer(modifier = Modifier.height(12.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            DashboardGlassCard(
                modifier = Modifier.weight(1f),
                icon = Icons.Outlined.Language,
                title = "Network",
                description = "Connection details",
                onClick = onNetworkClick
            )

            DashboardGlassCard(
                modifier = Modifier.weight(1f),
                icon = Icons.Outlined.Storage,
                title = "Storage",
                description = "Usage & space",
                onClick = onStorageClick
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            DashboardGlassCard(
                modifier = Modifier.weight(1f),
                icon = Icons.Outlined.HealthAndSafety,
                title = "Device Health",
                description = "Health status",
                onClick = onHealthClick
            )

            DashboardGlassCard(
                modifier = Modifier.weight(1f),
                icon = Icons.Outlined.QueryStats,
                title = "App Usage",
                description = "Usage statistics",
                onClick = onUsageClick
            )
        }
        Spacer(modifier = Modifier.height(12.dp))

    }
}


// ================================================================
// DEVICE OVERVIEW
// ================================================================

@Composable
private fun DeviceOverviewCard(
    modifier: Modifier = Modifier
) {
    GlassButton(
        onClick = {},
        modifier = modifier
            .fillMaxWidth()
            .height(150.dp)
            .glassify(
                style = GlassStyle.Thick
            )
    ) {

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp)
        ) {

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {

                // Device icon
                Box(
                    modifier = Modifier
                        .size(54.dp)
                        .clip(RoundedCornerShape(18.dp))
                        .background(
                            MaterialTheme.colorScheme.primaryContainer
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "📱",
                        style = MaterialTheme.typography.headlineSmall
                    )
                }

                Spacer(modifier = Modifier.size(14.dp))

                Column(
                    modifier = Modifier.weight(1f)
                ) {

                    Text(
                        text = "Your Device",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(2.dp))

                    Text(
                        text = "Android Device",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Text(
                        text = "Android 15 • SDK 35",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {

                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .clip(RoundedCornerShape(50))
                        .background(
                            MaterialTheme.colorScheme.primary
                        )
                )

                Spacer(modifier = Modifier.size(8.dp))

                Text(
                    text = "Device Healthy",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        }
    }
}


// ================================================================
// METRICS GRID
// ================================================================

@Composable
private fun DeviceMetricsGrid() {

    val metrics = listOf(
        MetricItem(
            icon = Icons.Outlined.BatteryStd,
            title = "Battery",
            value = "78%",
            subtitle = "Charging"
        ),
        MetricItem(
            icon = Icons.Outlined.Memory,
            title = "Memory",
            value = "5.2 GB",
            subtitle = "of 8 GB"
        ),
        MetricItem(
            icon = Icons.Outlined.Storage,
            title = "Storage",
            value = "92 GB",
            subtitle = "of 256 GB"
        ),
        MetricItem(
            icon = Icons.Outlined.Language,
            title = "Network",
            value = "Wi-Fi",
            subtitle = "Connected"
        )
    )

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {

        MetricCard(
            metric = metrics[0],
            modifier = Modifier.weight(1f)
        )

        MetricCard(
            metric = metrics[1],
            modifier = Modifier.weight(1f)
        )
    }

    Spacer(modifier = Modifier.height(12.dp))

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {

        MetricCard(
            metric = metrics[2],
            modifier = Modifier.weight(1f)
        )

        MetricCard(
            metric = metrics[3],
            modifier = Modifier.weight(1f)
        )
    }
}


// ================================================================
// METRIC CARD
// ================================================================

@Composable
private fun MetricCard(
    metric: MetricItem,
    modifier: Modifier = Modifier
) {

    GlassButton(
        onClick = {},
        modifier = modifier
            .height(130.dp)
            .glassify(
                style = GlassStyle.Thin
            )
    ) {

        Column(
            modifier = Modifier
                .fillMaxSize()
        ) {

            Row(
//                modifier = Modifier.fillMaxHeight(),
                verticalAlignment = Alignment.CenterVertically
            ) {

                Box(
                    modifier = Modifier
                        .size(34.dp)
                        .clip(RoundedCornerShape(11.dp))
                        .background(
                            MaterialTheme.colorScheme.surfaceVariant
                        ),
                    contentAlignment = Alignment.Center
                ) {

                    Icon(
                        imageVector = metric.icon,
                        contentDescription = null,
                        modifier = Modifier.size(19.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Spacer(modifier = Modifier.size(8.dp))

                Text(
                    text = metric.title,
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = metric.value,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )

            Text(
                text = metric.subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}


// ================================================================
// QUICK ACCESS CARD
// ================================================================

@Composable
private fun DashboardGlassCard(
    modifier: Modifier,
    icon: ImageVector,
    title: String,
    description: String,
    onClick: () -> Unit,
) {

    GlassButton(
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth()
            .height(120.dp)
            .glassify(
                style = GlassStyle.Thick
            )
    ) {

        Column(
            modifier = Modifier
                .fillMaxSize(),
            verticalArrangement = Arrangement.SpaceBetween
        ) {

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {

                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(RoundedCornerShape(13.dp))
                        .background(
                            MaterialTheme.colorScheme.surfaceVariant
                        ),
                    contentAlignment = Alignment.Center
                ) {

                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        modifier = Modifier.size(21.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Text(
                    text = "›",
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Column {

                Text(
                    text = title,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Spacer(modifier = Modifier.height(2.dp))

                Text(
                    text = description,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}


// ================================================================
// DATA
// ================================================================

private data class MetricItem(
    val icon: ImageVector,
    val title: String,
    val value: String,
    val subtitle: String
)