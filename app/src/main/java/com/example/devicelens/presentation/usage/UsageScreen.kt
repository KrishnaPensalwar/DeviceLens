package com.example.devicelens.presentation.usage

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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.devicelens.core.util.TimeFormatter
import com.example.devicelens.domain.model.UsagePeriod
import com.example.devicelens.domain.model.UsageSortOption
import com.example.devicelens.presentation.usage.components.AppUsageItem
import com.example.devicelens.presentation.usage.components.UsageOverviewCard
import com.example.devicelens.presentation.usage.components.periodLabel

@Composable
fun UsageScreen(
    onAppClick: (String) -> Unit,
    viewModel: UsageViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val lifecycleOwner = LocalLifecycleOwner.current

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                viewModel.refreshPermission()
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }

    when {
        !uiState.hasUsageAccess -> UsageAccessScreen()

        uiState.isLoading -> {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    CircularProgressIndicator()
                    Spacer(Modifier.height(12.dp))
                    Text("Reading app usage...")
                }
            }
        }

        uiState.error != null -> {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(uiState.error!!, color = MaterialTheme.colorScheme.error)
                    Spacer(Modifier.height(16.dp))
                    Button(onClick = viewModel::refresh) { Text("Try again") }
                }
            }
        }

        else -> UsageContent(
            uiState = uiState,
            onPeriodSelected = viewModel::onPeriodSelected,
            onSortSelected = viewModel::onSortSelected,
            onSearchQueryChanged = viewModel::onSearchQueryChanged,
            onRefresh = viewModel::refresh,
            onAppClick = onAppClick
        )
    }
}

@Composable
private fun UsageContent(
    uiState: UsageUiState,
    onPeriodSelected: (UsagePeriod) -> Unit,
    onSortSelected: (UsageSortOption) -> Unit,
    onSearchQueryChanged: (String) -> Unit,
    onRefresh: () -> Unit,
    onAppClick: (String) -> Unit
) {
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
                    "App Usage",
                    style = MaterialTheme.typography.headlineSmall,
                    modifier = Modifier.weight(1f)
                )
                if (uiState.isRefreshing) {
                    CircularProgressIndicator(Modifier.size(24.dp))
                } else {
                    IconButton(onClick = onRefresh) {
                        Icon(Icons.Default.Refresh, contentDescription = "Refresh")
                    }
                }
            }
        }

        item {
            UsageOverviewCard(
                period = uiState.period,
                totalUsageMillis = uiState.totalUsageMillis
            )
        }

        item {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                UsagePeriod.entries.forEach { period ->
                    FilterChip(
                        selected = uiState.period == period,
                        onClick = { onPeriodSelected(period) },
                        label = { Text(periodLabel(period)) }
                    )
                }
            }
        }

        if (uiState.mostUsed.isNotEmpty()) {
            item {
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text("Most used", style = MaterialTheme.typography.titleMedium)
                        uiState.mostUsed.forEach { app ->
                            Text(
                                "${app.appName}  ${TimeFormatter.formatDuration(app.usageMillis)}  (${"%.0f".format(app.percentOfTotal)}%)"
                            )
                        }
                    }
                }
            }
        }

        if (uiState.highUsageApps.isNotEmpty()) {
            item {
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            "Used more than usual",
                            style = MaterialTheme.typography.titleMedium
                        )
                        uiState.highUsageApps.forEach { app ->
                            Text("${app.appName}  ${TimeFormatter.formatDuration(app.usageMillis)}")
                        }
                        uiState.recommendations.forEach { tip ->
                            Text(tip, style = MaterialTheme.typography.bodyMedium)
                        }
                    }
                }
            }
        }

        item {
            OutlinedTextField(
                value = uiState.searchQuery,
                onValueChange = onSearchQueryChanged,
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                label = { Text("Search apps") }
            )
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "Apps",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.weight(1f)
                )
                SortMenu(uiState.sortOption, onSortSelected)
            }
        }

        if (uiState.visibleApps.isEmpty()) {
            item { Text("No app usage found for this period.") }
        } else {
            items(uiState.visibleApps, key = { it.packageName }) { app ->
                AppUsageItem(
                    app = app,
                    highlighted = uiState.highUsageApps.any { it.packageName == app.packageName },
                    onClick = { onAppClick(app.packageName) }
                )
            }
        }
    }
}

@Composable
private fun SortMenu(
    selected: UsageSortOption,
    onSelected: (UsageSortOption) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    Box {
        TextButton(onClick = { expanded = true }) {
            Text(selected.label())
        }
        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            UsageSortOption.entries.forEach { option ->
                DropdownMenuItem(
                    text = { Text(option.label()) },
                    onClick = {
                        onSelected(option)
                        expanded = false
                    }
                )
            }
        }
    }
}

private fun UsageSortOption.label(): String {
    return when (this) {
        UsageSortOption.MOST_USED -> "Most used"
        UsageSortOption.LEAST_USED -> "Least used"
        UsageSortOption.NAME_AZ -> "Name A to Z"
        UsageSortOption.NAME_ZA -> "Name Z to A"
    }
}
