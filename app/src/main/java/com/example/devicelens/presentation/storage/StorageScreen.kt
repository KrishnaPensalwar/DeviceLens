package com.example.devicelens.presentation.storage

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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.devicelens.domain.model.StorageThreshold
import com.example.devicelens.presentation.storage.components.AppStorageItem
import com.example.devicelens.presentation.storage.components.StorageBreakdown
import com.example.devicelens.presentation.storage.components.StorageOverviewCard
import com.example.devicelens.presentation.storage.components.StorageSortMenu

@Composable
fun StorageScreen(
    viewModel: StorageViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val error = uiState.error

    when {
        uiState.isLoading -> {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    CircularProgressIndicator()
                    Spacer(modifier = Modifier.height(12.dp))
                    Text("Analyzing storage...")
                }
            }
        }

        error != null -> {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(error, color = MaterialTheme.colorScheme.error)
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(onClick = viewModel::refresh) {
                        Text("Try again")
                    }
                }
            }
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

@Composable
private fun StorageContent(
    uiState: StorageUiState,
    onRefresh: () -> Unit,
    onSortSelected: (com.example.devicelens.domain.model.StorageSortOption) -> Unit,
    onThresholdSelected: (StorageThreshold) -> Unit
) {
    val overview = uiState.storageOverview
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Storage Analyzer",
                    style = MaterialTheme.typography.headlineSmall,
                    modifier = Modifier.weight(1f)
                )
                if (uiState.isRefreshing) {
                    CircularProgressIndicator(modifier = Modifier.size(24.dp))
                } else {
                    IconButton(onClick = onRefresh) {
                        Icon(Icons.Default.Refresh, contentDescription = "Refresh")
                    }
                }
            }
        }

        if (overview != null) {
            item { StorageOverviewCard(overview) }
            item { StorageBreakdown(overview) }
        }

        item {
            HighStorageCard(
                appsCount = uiState.highStorageApps.size,
                threshold = uiState.threshold,
                onThresholdSelected = onThresholdSelected
            )
        }

        items(
            items = uiState.highStorageApps,
            key = { "high-${it.packageName}" }
        ) { app ->
            AppStorageItem(app = app, highlighted = true)
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Applications",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.weight(1f)
                )
                StorageSortMenu(
                    selected = uiState.selectedSortOption,
                    onSelected = onSortSelected
                )
            }
        }

        if (uiState.applications.isEmpty()) {
            item {
                Text("No applications found.")
            }
        } else {
            items(
                items = uiState.applications,
                key = { it.packageName }
            ) { app ->
                AppStorageItem(app = app)
            }
        }
    }
}

@Composable
private fun HighStorageCard(
    appsCount: Int,
    threshold: StorageThreshold,
    onThresholdSelected: (StorageThreshold) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.errorContainer
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Warning,
                    contentDescription = null
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "High Storage Usage",
                    style = MaterialTheme.typography.titleMedium
                )
            }
            Text(
                text = if (appsCount == 0) {
                    "No apps are using more than ${threshold.label}."
                } else {
                    "$appsCount applications are using more than ${threshold.label}."
                }
            )
            Box {
                TextButton(onClick = { expanded = true }) {
                    Text("Alert at: ${threshold.label}")
                }
                DropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    StorageThreshold.entries.forEach { option ->
                        DropdownMenuItem(
                            text = { Text(option.label) },
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
