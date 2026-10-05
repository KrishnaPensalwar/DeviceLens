package com.example.devicelens.presentation.components.vitalCard

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.TextStyle

data class VitalCardModel(
    val title: String? = null,
    val icon: ImageVector? = null,
    val value: String? = null,
    val rightSubheadingColor: Color? = null,
    val left: String? = null,
    val right: String? = null,
    val iconTint: Color? = null,
    val titleStyle: TextStyle? = null,
    val valueStyle: TextStyle? = null,
    val rightStyle: TextStyle? = null,
    val leftStyle: TextStyle? = null,
)
