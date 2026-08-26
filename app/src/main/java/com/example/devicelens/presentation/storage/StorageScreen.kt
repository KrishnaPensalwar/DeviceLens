package com.example.devicelens.presentation.storage

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Refresh
import androidx.compose.material.icons.outlined.Storage
import androidx.compose.material.icons.outlined.Warning
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.devicelens.domain.model.StorageSortOption
import com.example.devicelens.domain.model.StorageThreshold
import com.example.devicelens.presentation.storage.components.AppStorageItem
import com.example.devicelens.presentation.storage.components.StorageBreakdown
import com.example.devicelens.presentation.storage.components.StorageOverviewCard
import com.example.devicelens.presentation.storage.components.StorageSortMenu
import io.github.sanketnawghare.glassify.compose.GlassButton
import io.github.sanketnawghare.glassify.compose.GlassStyle
import io.github.sanketnawghare.glassify.compose.glassify


@Composable
fun StorageScreen(
    viewModel: StorageViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    when {
        uiState.isLoading -> {
            StorageLoading()
        }

        uiState.error != null -> {
            StorageError(
                message = uiState.error ?: "",
                onRetry = viewModel::refresh
            )
        }

        else -> {
            StorageContent(
                uiState = uiState,
                onRefresh = viewModel::refresh,
                onSortSelected = viewModel::onSortSelected,
                onThresholdSelected = viewModel::onThresholdSelected
            )
        }
    }
}


// ================================================================
// LOADING
// ================================================================

@Composable
private fun StorageLoading() {

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            CircularProgressIndicator()

            Spacer(modifier = Modifier.height(14.dp))

            Text(
                text = "Analyzing storage...",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}


// ================================================================
// ERROR
// ================================================================

@Composable
private fun StorageError(
    message: String,
    onRetry: () -> Unit
) {

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {

        GlassButton(
            onClick = onRetry,
            modifier = Modifier
                .fillMaxWidth()
                .height(180.dp)
                .glassify(
                    style = GlassStyle.Thick
                )
        ) {

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {

                Icon(
                    imageVector = Icons.Outlined.Warning,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.error,
                    modifier = Modifier.size(32.dp)
                )

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "Unable to analyze storage",
                    style = MaterialTheme.typography.titleMedium
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = message,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "Tap to try again",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}


// ================================================================
// MAIN CONTENT
// ================================================================

@Composable
private fun StorageContent(
    uiState: StorageUiState,
    onRefresh: () -> Unit,
    onSortSelected: (StorageSortOption) -> Unit,
    onThresholdSelected: (StorageThreshold) -> Unit
) {

    val overview = uiState.storageOverview

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 20.dp),
        contentPadding = androidx.compose.foundation.layout.PaddingValues(
            top = 24.dp,
            bottom = 32.dp
        ),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {

        // --------------------------------------------------------
        // HEADER
        // --------------------------------------------------------

        item {

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {

                Column(
                    modifier = Modifier.weight(1f)
                ) {

                    Text(
                        text = "Storage",
                        style = MaterialTheme.typography.headlineMedium,
                        color = MaterialTheme.colorScheme.onBackground
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = "See what's using your device space",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                RefreshButton(
                    isRefreshing = uiState.isRefreshing,
                    onClick = onRefresh
                )
            }
        }


        // --------------------------------------------------------
        // STORAGE OVERVIEW
        // --------------------------------------------------------

        if (overview != null) {

            item {

                StorageOverviewCard(
                    overview = overview
                )
            }


            // ----------------------------------------------------
            // STORAGE BREAKDOWN
            // ----------------------------------------------------

            item {

                StorageSectionCard(
                    title = "Storage Breakdown"
                ) {

                    StorageBreakdown(
                        overview = overview
                    )
                }
            }
        }


        // --------------------------------------------------------
        // HIGH STORAGE WARNING
        // --------------------------------------------------------

        item {

            HighStorageCard(
                appsCount = uiState.highStorageApps.size,
                threshold = uiState.threshold,
                onThresholdSelected = onThresholdSelected
            )
        }


        // --------------------------------------------------------
        // HIGH STORAGE APPS
        // --------------------------------------------------------

        if (uiState.highStorageApps.isNotEmpty()) {

            item {

                SectionHeader(
                    title = "Storage Heavy Apps",
                    subtitle = "Apps using more than your selected limit"
                )
            }

            items(
                items = uiState.highStorageApps,
                key = {
                    "high-${it.packageName}"
                }
            ) { app ->

                AppStorageItem(
                    app = app,
                    highlighted = true
                )
            }
        }


        // --------------------------------------------------------
        // APPLICATIONS HEADER
        // --------------------------------------------------------

        item {

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {

                Column(
                    modifier = Modifier.weight(1f)
                ) {

                    Text(
                        text = "Applications",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = androidx.compose.ui.text.font.FontWeight.SemiBold
                    )

                    Spacer(modifier = Modifier.height(2.dp))

                    Text(
                        text = "${uiState.applications.size} apps",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                StorageSortMenu(
                    selected = uiState.selectedSortOption,
                    onSelected = onSortSelected
                )
            }
        }


        // --------------------------------------------------------
        // APPLICATION LIST
        // --------------------------------------------------------

        if (uiState.applications.isEmpty()) {

            item {

                EmptyApplicationsCard()
            }

        } else {

            items(
                items = uiState.applications,
                key = {
                    it.packageName
                }
            ) { app ->

                AppStorageItem(
                    app = app
                )
            }
        }
    }
}


// ================================================================
// REFRESH BUTTON
// ================================================================

@Composable
private fun RefreshButton(
    isRefreshing: Boolean,
    onClick: () -> Unit
) {

    Box(
        modifier = Modifier
            .size(36.dp)
            .clip(RoundedCornerShape(11.dp))
            .background(
                MaterialTheme.colorScheme.surfaceVariant
            )
            .clickable{
                onClick.invoke()
            },
        contentAlignment = Alignment.Center
    ) {

        if (isRefreshing) {

            CircularProgressIndicator(
                modifier = Modifier.size(20.dp),
                strokeWidth = 2.dp
            )

        } else {

            Icon(
                imageVector = Icons.Outlined.Refresh,
                contentDescription = "Refresh storage",
                modifier = Modifier.size(21.dp)
            )
        }
    }
}


// ================================================================
// STORAGE SECTION CARD
// ================================================================

@Composable
private fun StorageSectionCard(
    title: String,
    content: @Composable () -> Unit
) {

    GlassButton(
        onClick = {},
        modifier = Modifier
            .fillMaxWidth()
            .glassify(
                style = GlassStyle.Thin
            )
    ) {

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
        ) {

            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = androidx.compose.ui.text.font.FontWeight.SemiBold
            )

            Spacer(modifier = Modifier.height(14.dp))

            content()
        }
    }
}


// ================================================================
// HIGH STORAGE CARD
// ================================================================

@Composable
private fun HighStorageCard(
    appsCount: Int,
    threshold: StorageThreshold,
    onThresholdSelected: (StorageThreshold) -> Unit
) {

    var expanded by remember {
        mutableStateOf(false)
    }

    GlassButton(
        onClick = {},
        modifier = Modifier
            .fillMaxWidth()
            .glassify(
                style = GlassStyle.Thick
            )
    ) {

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
        ) {

            // Header
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {

                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .background(
                            MaterialTheme.colorScheme.errorContainer
                        ),
                    contentAlignment = Alignment.Center
                ) {

                    Icon(
                        imageVector = Icons.Outlined.Warning,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.error,
                        modifier = Modifier.size(22.dp)
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column {

                    Text(
                        text = "High Storage Usage",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = androidx.compose.ui.text.font.FontWeight.SemiBold
                    )

                    Spacer(modifier = Modifier.height(2.dp))

                    Text(
                        text = if (appsCount == 0) {
                            "Storage usage looks good"
                        } else {
                            "$appsCount apps need attention"
                        },
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }


            Spacer(modifier = Modifier.height(14.dp))


            Text(
                text = if (appsCount == 0) {
                    "No applications are using more than ${threshold.label}."
                } else {
                    "$appsCount applications are using more than ${threshold.label}."
                },
                style = MaterialTheme.typography.bodyMedium
            )


            Spacer(modifier = Modifier.height(8.dp))


            Box {

                TextButton(
                    onClick = {
                        expanded = true
                    }
                ) {

                    Text(
                        text = "Alert at ${threshold.label}"
                    )
                }


                DropdownMenu(
                    expanded = expanded,
                    onDismissRequest = {
                        expanded = false
                    }
                ) {

                    StorageThreshold.entries.forEach { option ->

                        DropdownMenuItem(
                            text = {
                                Text(option.label)
                            },
                            onClick = {

                                onThresholdSelected(option)

                                expanded = false
                            }
                        )
                    }
                }
            }
        }
    }
}


// ================================================================
// SECTION HEADER
// ================================================================

@Composable
private fun SectionHeader(
    title: String,
    subtitle: String
) {

    Column {

        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = androidx.compose.ui.text.font.FontWeight.SemiBold
        )

        Spacer(modifier = Modifier.height(3.dp))

        Text(
            text = subtitle,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}


// ================================================================
// EMPTY STATE
// ================================================================

@Composable
private fun EmptyApplicationsCard() {

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp)
    ) {

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(28.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Icon(
                imageVector = Icons.Outlined.Storage,
                contentDescription = null,
                modifier = Modifier.size(34.dp),
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = "No applications found",
                style = MaterialTheme.typography.titleMedium
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "There are no application storage details to display.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}