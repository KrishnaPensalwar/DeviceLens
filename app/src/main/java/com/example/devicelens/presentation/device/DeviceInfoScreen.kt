package com.example.devicelens.presentation.device

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.devicelens.domain.model.DeviceInfo

@Composable
fun DeviceInfoScreen(
    viewModel: DeviceInfoViewModel
) {
    val data by viewModel.uiState.collectAsStateWithLifecycle()

    when {
        data.isLoading -> {
            Column(
                modifier = Modifier.fillMaxSize()
            ) {
                CircularProgressIndicator()
            }
        }

        data.error != null -> {
            Column(
                modifier = Modifier.fillMaxSize()
            ) {
                data.error?.let {
                    Text(
                        it
                    )
                }
            }
        }

        else -> {
            Column(
                modifier = Modifier.fillMaxSize()
            ) {
                data.deviceInfo?.let {
                    DeviceInfoContent(it)
                }
            }
        }
    }
}


@Composable
private fun DeviceInfoContent(
    deviceInfo: DeviceInfo
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Text(
                text = "My Phone"
            )
        }

        item {
            DeviceInfoItem(
                label = "Phone", value = deviceInfo.phoneModel
            )
        }

        item {
            DeviceInfoItem(
                label = "Brand", value = deviceInfo.brand
            )
        }

        item {
            DeviceInfoItem(
                label = "Android Version", value = deviceInfo.androidVersion
            )
        }

        item {
            DeviceInfoItem(
                label = "Security Update", value = deviceInfo.securityUpdate
            )
        }
    }
}

@Composable
private fun DeviceInfoItem(
    label: String, value: String
) {
    Column {
        Text(text = label)
        Text(text = value)
    }
}
