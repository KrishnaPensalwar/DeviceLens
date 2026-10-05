package com.example.devicelens.presentation.components

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import com.example.devicelens.ui.theme.LensTextMuted
import com.example.devicelens.ui.theme.LensTextPrimary
import com.example.devicelens.ui.theme.LensTextSecondary
import com.example.devicelens.ui.theme.LensType

@Composable
fun LensText(
    text: String,
    style: TextStyle,
    color: Color = LensTextPrimary,
    modifier: Modifier = Modifier,
    textAlign: TextAlign? = null,
    maxLines: Int = Int.MAX_VALUE,
    overflow: TextOverflow = TextOverflow.Clip
) {
    Text(
        text = text,
        modifier = modifier,
        color = color,
        style = style,
        textAlign = textAlign,
        maxLines = maxLines,
        overflow = overflow
    )
}

@Composable
fun LensHeroText(text: String, modifier: Modifier = Modifier, color: Color = LensTextPrimary) {
    LensText(text = text, style = LensType.hero, color = color, modifier = modifier)
}

@Composable
fun LensMetricText(text: String, modifier: Modifier = Modifier, color: Color = LensTextPrimary) {
    LensText(text = text, style = LensType.metric, color = color, modifier = modifier)
}

@Composable
fun LensSectionLabel(text: String, modifier: Modifier = Modifier, color: Color = LensTextSecondary) {
    LensText(text = text, style = LensType.section, color = color, modifier = modifier)
}

@Composable
fun LensCaptionText(text: String, modifier: Modifier = Modifier, color: Color = LensTextMuted) {
    LensText(text = text, style = LensType.caption, color = color, modifier = modifier)
}

@Composable
fun LensOverlineText(text: String, modifier: Modifier = Modifier, color: Color = LensTextMuted) {
    LensText(text = text, style = LensType.overline, color = color, modifier = modifier)
}

@Composable
fun LensBodyText(text: String, modifier: Modifier = Modifier, color: Color = LensTextSecondary) {
    LensText(text = text, style = LensType.body, color = color, modifier = modifier)
}
