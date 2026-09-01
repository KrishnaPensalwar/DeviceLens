package com.example.devicelens.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val LensColorScheme = darkColorScheme(
    primary = LensCyan,
    onPrimary = LensOnAccent,
    secondary = LensMint,
    tertiary = LensPurple,
    background = LensBackground,
    onBackground = LensTextPrimary,
    surface = LensSurface,
    onSurface = LensTextPrimary,
    onSurfaceVariant = LensTextSecondary,
    outline = LensBorder,
    error = LensCoral
)

@Composable
fun DeviceLensTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = LensColorScheme,
        typography = Typography,
        content = content
    )
}
