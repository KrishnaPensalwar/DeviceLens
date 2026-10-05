package com.example.devicelens.presentation.storage

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.devicelens.domain.model.StorageSortOption
import com.example.devicelens.domain.model.StorageThreshold
import com.example.devicelens.presentation.components.LensBackBar
import com.example.devicelens.presentation.components.LensCard
import com.example.devicelens.presentation.components.LensLoading
import com.example.devicelens.ui.theme.LensAmber
import com.example.devicelens.ui.theme.LensAmberDark
import com.example.devicelens.ui.theme.LensBackground
import com.example.devicelens.ui.theme.LensBorder
import com.example.devicelens.ui.theme.LensCyan
import com.example.devicelens.ui.theme.LensCyanDark
import com.example.devicelens.ui.theme.LensSurface
import com.example.devicelens.ui.theme.LensTextMuted
import com.example.devicelens.ui.theme.LensTextPrimary
import com.example.devicelens.ui.theme.LensTextSecondary
import com.example.devicelens.ui.theme.LensTrack
import com.example.devicelens.presentation.storage.components.AppStorageItem
import com.example.devicelens.presentation.storage.components.StorageBreakdown
import com.example.devicelens.presentation.storage.components.StorageOverviewCard
import com.example.devicelens.presentation.storage.components.StorageSortMenu

// ============================================================================
// MAIN SCREEN
// ============================================================================

@Composable
fun StorageScreen(
    viewModel: StorageViewModel = hiltViewModel(),
    onBack: () -> Unit
) {

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(LensBackground)
    ) {

        when {

            uiState.isLoading -> {
                StorageLoading()
            }

            uiState.error != null -> {
                StorageError(
                    message = uiState.error.orEmpty(),
                    onRetry = viewModel::refresh
                )
            }

            else -> {
                StorageContent(
                    uiState = uiState,
                    onRefresh = viewModel::refresh,
                    onSortSelected = viewModel::onSortSelected,
                    onThresholdSelected = viewModel::onThresholdSelected
                ){
                    onBack()
                }
            }
        }
    }
}


// ============================================================================
// CONTENT
// ============================================================================

@Composable
private fun StorageContent(
    uiState: StorageUiState,
    onRefresh: () -> Unit,
    onSortSelected: (StorageSortOption) -> Unit,
    onThresholdSelected: (StorageThreshold) -> Unit,
    onBack : () -> Unit
) {

    val overview = uiState.storageOverview

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(LensBackground),

        contentPadding = PaddingValues(
            start = 18.dp,
            end = 18.dp,
            top = 22.dp,
            bottom = 90.dp
        ),

        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {

        // ====================================================================
        // HEADER
        // ====================================================================

        // ====================================================================
        // STORAGE OVERVIEW
        // ====================================================================

        if (overview != null) {

            item {

                StorageOverviewCard(
                    overview = overview
                )
            }


            // =================================================================
            // STORAGE BREAKDOWN
            // =================================================================

            item {

                StorageBreakdownCard(
                    overview = overview
                )
            }
        }


        // ====================================================================
        // HIGH STORAGE WARNING
        // ====================================================================

        item {

            HighStorageCard(
                appsCount = uiState.highStorageApps.size,
                threshold = uiState.threshold,
                onThresholdSelected = onThresholdSelected
            )
        }


        // ====================================================================
        // APPLICATION HEADER
        // ====================================================================

        item {

            ApplicationsHeader(
                count = uiState.applications.size,
                selectedSort = uiState.selectedSortOption,
                onSortSelected = onSortSelected
            )
        }


        // ====================================================================
        // APPLICATIONS
        // ====================================================================

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


// ============================================================================
// STORAGE HEADER
// ============================================================================

@Composable
private fun StorageHeader(
    isRefreshing: Boolean,
    onRefresh: () -> Unit
) {

    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {

        Column(
            modifier = Modifier.weight(1f)
        ) {

            Text(
                text = "Storage",
                color = LensTextPrimary,
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(
                modifier = Modifier.height(4.dp)
            )

            Text(
                text = "See what's using your device space",
                color = LensTextSecondary,
                fontSize = 14.sp
            )
        }

        Spacer(
            modifier = Modifier.width(12.dp)
        )

        RefreshButton(
            isRefreshing = isRefreshing,
            onClick = onRefresh
        )
    }
}


// ============================================================================
// REFRESH BUTTON
// ============================================================================

@Composable
private fun RefreshButton(
    isRefreshing: Boolean,
    onClick: () -> Unit
) {

    Box(
        modifier = Modifier
            .size(40.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(LensSurface)
            .border(
                width = 1.dp,
                color = LensBorder,
                shape = RoundedCornerShape(12.dp)
            )
            .clickable {
                if (!isRefreshing) {
                    onClick()
                }
            },
        contentAlignment = Alignment.Center
    ) {

        if (isRefreshing) {

            CircularProgressIndicator(
                modifier = Modifier.size(19.dp),
                strokeWidth = 2.dp,
                color = LensCyan
            )

        } else {

            Icon(
                imageVector = Icons.Outlined.Refresh,
                contentDescription = "Refresh storage",
                tint = LensTextSecondary,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}


// ============================================================================
// STORAGE BREAKDOWN CARD
// ============================================================================

@Composable
private fun StorageBreakdownCard(
    overview: com.example.devicelens.domain.model.StorageOverview
) {

    LensCard {

        Column(
            modifier = Modifier.padding(
                horizontal = 20.dp,
                vertical = 20.dp
            )
        ) {

            Text(
                text = "Storage Breakdown",
                color = LensTextPrimary,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(
                modifier = Modifier.height(18.dp)
            )

            StorageBreakdown(
                overview = overview
            )
        }
    }
}


// ============================================================================
// HIGH STORAGE CARD
// ============================================================================

@Composable
private fun HighStorageCard(
    appsCount: Int,
    threshold: StorageThreshold,
    onThresholdSelected: (StorageThreshold) -> Unit
) {

    var expanded by remember {
        mutableStateOf(false)
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .border(
                width = 1.dp,
                color = LensAmber,
                shape = RoundedCornerShape(14.dp)
            ),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(
            containerColor = LensAmberDark
        )
    ) {

        Column(
            modifier = Modifier.padding(20.dp)
        ) {

            // ----------------------------------------------------------------
            // WARNING HEADER
            // ----------------------------------------------------------------

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {

                Box(
                    modifier = Modifier
                        .size(50.dp)
                        .clip(RoundedCornerShape(9.dp))
                        .background(LensAmberDark),
                    contentAlignment = Alignment.Center
                ) {

                    Icon(
                        imageVector = Icons.Outlined.Warning,
                        contentDescription = null,
                        tint = LensAmber,
                        modifier = Modifier.size(25.dp)
                    )
                }

                Spacer(
                    modifier = Modifier.width(14.dp)
                )

                Column {

                    Text(
                        text = "High Storage Usage",
                        color = LensAmber,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(
                        modifier = Modifier.height(4.dp)
                    )

                    Text(
                        text = if (appsCount == 0) {
                            "Storage usage looks good"
                        } else {
                            "$appsCount apps need attention"
                        },
                        color = LensTextSecondary,
                        fontSize = 14.sp
                    )
                }
            }


            Spacer(
                modifier = Modifier.height(16.dp)
            )


            // ----------------------------------------------------------------
            // DESCRIPTION
            // ----------------------------------------------------------------

            Text(
                text = if (appsCount == 0) {
                    "No applications are using more than ${threshold.label}."
                } else {
                    "$appsCount applications are using more than ${threshold.label}."
                },
                color = LensTextPrimary,
                fontSize = 14.sp
            )


            Spacer(
                modifier = Modifier.height(14.dp)
            )


            // ----------------------------------------------------------------
            // THRESHOLD BUTTON
            // ----------------------------------------------------------------

            Box {

                Text(
                    text = "ALERT AT ${threshold.label}",
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(LensTrack)
                        .clickable {
                            expanded = true
                        }
                        .padding(
                            horizontal = 16.dp,
                            vertical = 11.dp
                        ),
                    color = LensCyan,
                    fontFamily = FontFamily.Monospace,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )


                DropdownMenu(
                    expanded = expanded,
                    onDismissRequest = {
                        expanded = false
                    }
                ) {

                    StorageThreshold.entries.forEach { option ->

                        DropdownMenuItem(
                            text = {
                                Text(
                                    text = option.label
                                )
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


// ============================================================================
// APPLICATION HEADER
// ============================================================================

@Composable
private fun ApplicationsHeader(
    count: Int,
    selectedSort: StorageSortOption,
    onSortSelected: (StorageSortOption) -> Unit
) {

    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.Bottom
    ) {

        Column(
            modifier = Modifier.weight(1f)
        ) {

            Text(
                text = "Applications",
                color = LensTextPrimary,
                fontSize = 25.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(
                modifier = Modifier.height(2.dp)
            )

            Text(
                text = "$count apps",
                color = LensTextSecondary,
                fontSize = 13.sp
            )
        }

        StorageSortMenu(
            selected = selectedSort,
            onSelected = onSortSelected
        )
    }
}


// ============================================================================
// EMPTY APPLICATIONS
// ============================================================================

@Composable
private fun EmptyApplicationsCard() {

    LensCard {

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
                tint = LensTextMuted
            )

            Spacer(
                modifier = Modifier.height(12.dp)
            )

            Text(
                text = "No applications found",
                color = LensTextPrimary,
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(
                modifier = Modifier.height(5.dp)
            )

            Text(
                text = "There are no application storage details to display.",
                color = LensTextSecondary,
                fontSize = 13.sp
            )
        }
    }
}


// ============================================================================
// LOADING
// ============================================================================

@Composable
private fun StorageLoading() {
    LensLoading(message = "Analyzing storage...")
}


// ============================================================================
// ERROR
// ============================================================================

@Composable
private fun StorageError(
    message: String,
    onRetry: () -> Unit
) {

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(LensBackground)
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {

        LensCard {

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(28.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                Icon(
                    imageVector = Icons.Outlined.Warning,
                    contentDescription = null,
                    tint = LensAmber,
                    modifier = Modifier.size(34.dp)
                )

                Spacer(
                    modifier = Modifier.height(14.dp)
                )

                Text(
                    text = "Unable to analyze storage",
                    color = LensTextPrimary,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(
                    modifier = Modifier.height(7.dp)
                )

                Text(
                    text = message,
                    color = LensTextSecondary,
                    fontSize = 13.sp
                )

                Spacer(
                    modifier = Modifier.height(16.dp)
                )

                Text(
                    text = "TAP TO TRY AGAIN",
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(LensCyanDark)
                        .clickable {
                            onRetry()
                        }
                        .padding(
                            horizontal = 16.dp,
                            vertical = 10.dp
                        ),
                    color = LensCyan,
                    fontFamily = FontFamily.Monospace,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )
            }
        }
    }
}
