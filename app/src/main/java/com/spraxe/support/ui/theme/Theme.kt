package com.spraxe.support.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColors = lightColorScheme(
    primary = SpraxeNavy900,
    onPrimary = Color.White,
    secondary = SpraxeOrange500,
    background = SpraxeGray50,
    surface = Color.White,
    onSurface = SpraxeGray900,
    error = SpraxeDestructive
)

private val DarkColors = darkColorScheme(
    primary = SpraxeNavy800,
    secondary = SpraxeOrange500,
    background = Color(0xFF0B1220),
    surface = Color(0xFF111827),
    error = SpraxeDestructive
)

@Composable
fun SpraxeSupportTheme(darkTheme: Boolean = false, content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = if (darkTheme) DarkColors else LightColors,
        typography = SpraxeTypography,
        content = content
    )
}
