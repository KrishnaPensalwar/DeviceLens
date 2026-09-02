package com.example.devicelens.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import com.example.devicelens.themes.DeviceLensAmber
import com.example.devicelens.themes.DeviceLensBackground
import com.example.devicelens.themes.DeviceLensBorder
import com.example.devicelens.themes.DeviceLensCard
import com.example.devicelens.themes.DeviceLensCardSecondary
import com.example.devicelens.themes.DeviceLensCyan
import com.example.devicelens.themes.DeviceLensPurple
import com.example.devicelens.themes.DeviceLensTextPrimary
import com.example.devicelens.themes.DeviceLensTextSecondary
import com.example.devicelens.themes.DeviceLensTypography

private val DeviceLensDarkColors = darkColorScheme(

    primary = DeviceLensCyan,
    onPrimary = DeviceLensBackground,

    secondary = DeviceLensPurple,
    onSecondary = DeviceLensTextPrimary,

    background = DeviceLensBackground,
    onBackground = DeviceLensTextPrimary,

    surface = DeviceLensCard,
    onSurface = DeviceLensTextPrimary,

    surfaceVariant = DeviceLensCardSecondary,
    onSurfaceVariant = DeviceLensTextSecondary,

    outline = DeviceLensBorder,

    error = DeviceLensAmber
)

@Composable
fun DeviceLensTheme(
    content: @Composable () -> Unit
) {

    MaterialTheme(
        colorScheme = DeviceLensDarkColors,
        typography = DeviceLensTypography,
        content = content
    )
}