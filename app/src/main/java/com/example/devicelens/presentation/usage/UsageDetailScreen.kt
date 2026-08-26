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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.devicelens.core.util.TimeFormatter
import com.example.devicelens.domain.model.UsagePeriod
import com.example.devicelens.presentation.usage.components.UsageChart
import com.example.devicelens.presentation.usage.components.periodLabel

@Composable
fun UsageDetailScreen(
    viewModel: UsageDetailViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val error = uiState.error
    val detail = uiState.detail

    when {
        uiState.isLoading -> {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        }

        error != null -> {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(error, color = MaterialTheme.colorScheme.error)
                    Spacer(Modifier.height(16.dp))
                    Button(onClick = viewModel::retry) { Text("Try again") }
                }
            }
        }

        detail != null -> {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                item {
                    Text(detail.app.appName, style = MaterialTheme.typography.headlineSmall)
                    Text(detail.app.packageName, style = MaterialTheme.typography.bodySmall)
                }

                item {
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        UsagePeriod.entries.forEach { period ->
                            FilterChip(
                                selected = uiState.period == period,
                                onClick = { viewModel.onPeriodSelected(period) },
                                label = { Text(periodLabel(period)) }
                            )
                        }
                    }
                }

                item {
                    Card(modifier = Modifier.fillMaxWidth()) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text("Usage", style = MaterialTheme.typography.titleMedium)
                            Text(
                                TimeFormatter.formatDuration(detail.app.usageMillis),
                                style = MaterialTheme.typography.headlineSmall
                            )
                            Text("%.0f%% of total".format(detail.app.percentOfTotal))
                            Text("Last used ${TimeFormatter.formatLastUsed(detail.app.lastUsedMillis)}")
                            detail.app.launchCount?.let {
                                Text("Opened $it times")
                            }
                        }
                    }
                }

                item {
                    Card(modifier = Modifier.fillMaxWidth()) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text("Breakdown", style = MaterialTheme.typography.titleMedium)
                            Spacer(Modifier.height(12.dp))
                            UsageChart(detail.buckets)
                        }
                    }
                }

                item {
                    Text("Timeline", style = MaterialTheme.typography.titleMedium)
                }

                if (detail.sessions.isEmpty()) {
                    item { Text("No session timeline for this period.") }
                } else {
                    items(detail.sessions, key = { it.startMillis }) { session ->
                        Card(modifier = Modifier.fillMaxWidth()) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text(
                                    TimeFormatter.formatSession(
                                        session.startMillis,
                                        session.endMillis
                                    ),
                                    style = MaterialTheme.typography.titleSmall
                                )
                                Text(TimeFormatter.formatDuration(session.durationMillis))
                            }
                        }
                    }
                }
            }
        }
    }
}
