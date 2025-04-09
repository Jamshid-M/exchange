package com.example.exchange.ui.theme

import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

data class CustomColorsPalette(
    val textColor: Color = Color.Unspecified,
    val colorCardBackground: Color = Color.Unspecified,
    val colorBackground: Color = Color(0xFFF0F0F5)
)

val LightCustomColorsPalette = CustomColorsPalette(
    textColor = Color(0xFF000000),
    colorCardBackground = Color(0xFFFFFFFF)
)

val DarkCustomColorsPalette = CustomColorsPalette(
    textColor = Color(0xFFFFFFFF),
    colorCardBackground = Color(0xFF000000)
)

val LocalCustomColorsPalette = staticCompositionLocalOf { CustomColorsPalette() }