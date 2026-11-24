package com.example.communityshare.theme

import androidx.compose.material3.lightColorScheme
import androidx.compose.ui.graphics.Color

val MintPrimary = Color(0xFFA8E6CF)
val MintDark = Color(0xFF2E5A4C)
val BackgroundOffWhite = Color(0xFFF6F7F5)
val CardSurface = Color(0xFFFFFFFF)
val CoralAccent = Color(0xFFF29B88)
val TextPrimary = Color(0xFF1A1C1B)
val TextSecondary = Color(0xFF5C6360)

val AppColorScheme = lightColorScheme(
    primary = MintPrimary,
    onPrimary = TextPrimary,
    secondary = CoralAccent,
    background = BackgroundOffWhite,
    surface = CardSurface,
    onSurface = TextPrimary
)