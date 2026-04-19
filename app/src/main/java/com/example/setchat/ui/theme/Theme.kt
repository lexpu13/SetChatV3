package com.example.setchat.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColors = lightColorScheme(
    primary = Color(0xFF0B8F68),
    onPrimary = Color.White,
    secondary = Color(0xFFE7F6F0),
    onSecondary = Color(0xFF12352A),
    background = Color(0xFFF0F2F5),
    onBackground = Color(0xFF111B21),
    surface = Color.White,
    onSurface = Color(0xFF111B21),
    outline = Color(0xFF8696A0)
)

private val DarkColors = darkColorScheme(
    primary = Color(0xFF1F7AE0),
    onPrimary = Color.White,
    secondary = Color(0xFF2B3F5A),
    onSecondary = Color(0xFFEAF2FF),
    background = Color(0xFF0B141A),
    onBackground = Color(0xFFEAF2FF),
    surface = Color(0xFF111B21),
    onSurface = Color(0xFFEAF2FF),
    outline = Color(0xFF6B7B8A)
)

@Composable
fun SetChatTheme(
    isDarkTheme: Boolean = true,
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = if (isDarkTheme) DarkColors else LightColors,
        typography = Typography(),
        content = content
    )
}
