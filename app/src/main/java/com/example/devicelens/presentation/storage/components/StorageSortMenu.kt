package com.example.devicelens.presentation.storage.components

import androidx.compose.foundation.layout.Box
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.example.devicelens.domain.model.StorageSortOption

@Composable
fun StorageSortMenu(
    selected: StorageSortOption,
    onSelected: (StorageSortOption) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    Box {
        TextButton(onClick = { expanded = true }) {
            Text("Sort by: ${selected.label()}")
        }
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            StorageSortOption.entries.forEach { option ->
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

fun StorageSortOption.label(): String {
    return when (this) {
        StorageSortOption.LARGEST_FIRST -> "Largest first"
        StorageSortOption.SMALLEST_FIRST -> "Smallest first"
        StorageSortOption.NAME_AZ -> "Name A to Z"
        StorageSortOption.NAME_ZA -> "Name Z to A"
    }
}
