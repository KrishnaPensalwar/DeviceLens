package com.example.devicelens.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

private data class LensPalette(
    val background: Color,
    val surface: Color,
    val surfaceAlt: Color,
    val border: Color,
    val chipBorder: Color,
    val track: Color,
    val divider: Color,
    val dot: Color,
    val primary: Color,
    val onPrimary: Color,
    val ok: Color,
    val warn: Color,
    val bad: Color,
    val info: Color,
    val text: Color,
    val muted: Color
)

private val LightPalette = LensPalette(
    background = Color(0xFFF3F0F8),
    surface = Color(0xFFFCFBFE),
    surfaceAlt = Color(0xFFE7E1F0),
    border = Color(0xFFDAD3E6),
    chipBorder = Color(0xFFDAD3E6),
    track = Color(0xFFE7E1F0),
    divider = Color(0xFFDAD3E6),
    dot = Color(0xFFA99FB8),
    primary = Color(0xFF5A2D6B),
    onPrimary = Color(0xFFFFFFFF),
    ok = Color(0xFF2E9E7A),
    warn = Color(0xFFE08E0B),
    bad = Color(0xFFD64550),
    info = Color(0xFF3B6FD8),
    text = Color(0xFF1E1726),
    muted = Color(0xFF6F677D)
)

private val DarkPalette = LensPalette(
    background = Color(0xFF16111B),
    surface = Color(0xFF211A28),
    surfaceAlt = Color(0xFF2C2335),
    border = Color(0xFF3A2F45),
    chipBorder = Color(0xFF3A2F45),
    track = Color(0xFF2C2335),
    divider = Color(0xFF3A2F45),
    dot = Color(0xFFA99FB8),
    primary = Color(0xFFE2B4EE),
    onPrimary = Color(0xFF2A1233),
    ok = Color(0xFF5FD1A8),
    warn = Color(0xFFF2B04A),
    bad = Color(0xFFF08088),
    info = Color(0xFF8FB0F5),
    text = Color(0xFFF0EAF5),
    muted = Color(0xFFA99FB8)
)

var lensDarkTheme by mutableStateOf(false)
    private set

private var activePalette by mutableStateOf(LightPalette)

fun toggleLensTheme() {
    lensDarkTheme = !lensDarkTheme
    activePalette = if (lensDarkTheme) DarkPalette else LightPalette
}

val LensBackground: Color get() = activePalette.background
val LensSurface: Color get() = activePalette.surface
val LensSurfaceAlt: Color get() = activePalette.surfaceAlt
val LensBorder: Color get() = activePalette.border
val LensTrack: Color get() = activePalette.track
val LensDivider: Color get() = activePalette.divider
val LensDot: Color get() = activePalette.dot

val LensCyan: Color get() = activePalette.primary
val LensCyanDark: Color get() = activePalette.primary.copy(alpha = 0.35f)
val LensGaugeTrack: Color get() = activePalette.surfaceAlt
val LensMint: Color get() = activePalette.ok
val LensGreen: Color get() = activePalette.ok
val LensOrange: Color get() = activePalette.warn
val LensAmber: Color get() = activePalette.warn
val LensAmberDark: Color get() = activePalette.warn.copy(alpha = 0.18f)
val LensCoral: Color get() = activePalette.bad
val LensPurple: Color get() = activePalette.primary
val LensBlue: Color get() = activePalette.info
val LensBlueDeep: Color get() = activePalette.info

val LensTextPrimary: Color get() = activePalette.text
val LensTextSecondary: Color get() = activePalette.muted
val LensTextMuted: Color get() = activePalette.muted
val LensOnAccent: Color get() = activePalette.onPrimary

val LensInsightBg = Color(0xFF141820)
val LensInsightBorder = Color(0xFF2A3A55)
val LensDangerBg = Color(0xFF2A1214)
val LensDangerBorder = Color(0xFF613036)

object LensType {
    private val sans = FontFamily.SansSerif

    val gauge = TextStyle(
        fontFamily = sans,
        fontWeight = FontWeight.Bold,
        fontSize = 52.sp
    )
    val metric = TextStyle(
        fontFamily = sans,
        fontWeight = FontWeight.Bold,
        fontSize = 32.sp
    )
    val hero = TextStyle(
        fontFamily = sans,
        fontWeight = FontWeight.Bold,
        fontSize = 28.sp
    )
    val screenTitle = TextStyle(
        fontFamily = sans,
        fontWeight = FontWeight.Bold,
        fontSize = 24.sp
    )
    val brand = TextStyle(
        fontFamily = sans,
        fontWeight = FontWeight.Bold,
        fontSize = 20.sp
    )
    val cardTitle = TextStyle(
        fontFamily = sans,
        fontWeight = FontWeight.Bold,
        fontSize = 20.sp
    )
    val section = TextStyle(
        fontFamily = sans,
        fontWeight = FontWeight.Medium,
        fontSize = 13.sp
    )
    val action = TextStyle(
        fontFamily = sans,
        fontWeight = FontWeight.SemiBold,
        fontSize = 13.sp
    )
    val body = TextStyle(
        fontFamily = sans,
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp,
        lineHeight = 20.sp
    )
    val bodyMedium = TextStyle(
        fontFamily = sans,
        fontWeight = FontWeight.Medium,
        fontSize = 14.sp
    )
    val caption = TextStyle(
        fontFamily = sans,
        fontWeight = FontWeight.SemiBold,
        fontSize = 11.sp
    )
    val captionBold = TextStyle(
        fontFamily = sans,
        fontWeight = FontWeight.Bold,
        fontSize = 11.sp
    )
    val overline = TextStyle(
        fontFamily = sans,
        fontWeight = FontWeight.Bold,
        fontSize = 9.sp,
        letterSpacing = 1.1.sp
    )
    val status = TextStyle(
        fontFamily = sans,
        fontWeight = FontWeight.Bold,
        fontSize = 12.sp,
        letterSpacing = 2.sp
    )
    val nav = TextStyle(
        fontFamily = sans,
        fontWeight = FontWeight.SemiBold,
        fontSize = 10.sp
    )
}

val LensTypography = Typography(
    displayLarge = LensType.gauge,
    displaySmall = LensType.metric,
    headlineMedium = LensType.hero,
    headlineSmall = LensType.screenTitle,
    titleLarge = LensType.brand,
    titleMedium = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.SemiBold,
        fontSize = 16.sp
    ),
    titleSmall = LensType.action,
    bodyLarge = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 24.sp
    ),
    bodyMedium = LensType.body,
    bodySmall = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.Normal,
        fontSize = 12.sp
    ),
    labelLarge = LensType.captionBold,
    labelMedium = LensType.caption,
    labelSmall = LensType.overline
)

@Composable
fun DeviceLensTheme(content: @Composable () -> Unit) {
    val palette = if (lensDarkTheme) DarkPalette else LightPalette
    val scheme = if (lensDarkTheme) {
        darkColorScheme(
            primary = palette.primary,
            onPrimary = palette.onPrimary,
            background = palette.background,
            onBackground = palette.text,
            surface = palette.surface,
            onSurface = palette.text,
            surfaceVariant = palette.surfaceAlt,
            onSurfaceVariant = palette.muted,
            outline = palette.border,
            error = palette.bad
        )
    } else {
        lightColorScheme(
            primary = palette.primary,
            onPrimary = palette.onPrimary,
            background = palette.background,
            onBackground = palette.text,
            surface = palette.surface,
            onSurface = palette.text,
            surfaceVariant = palette.surfaceAlt,
            onSurfaceVariant = palette.muted,
            outline = palette.border,
            error = palette.bad
        )
    }
    MaterialTheme(
        colorScheme = scheme,
        typography = LensTypography,
        content = content
    )
}
