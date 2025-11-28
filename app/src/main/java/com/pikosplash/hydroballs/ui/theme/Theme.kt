package com.pikosplash.hydroballs.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val HydroBallsColorScheme = darkColorScheme(
    primary = BrightPurple,
    secondary = LightBubblePurple,
    tertiary = GoldenYellow,
    background = DeepPurple,
    surface = MediumPurple,
    onPrimary = DeepPurple,
    onSecondary = PrimaryWhite,
    onTertiary = DeepPurple,
    onBackground = PrimaryWhite,
    onSurface = PrimaryWhite
)

@Composable
fun HydroBallsTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = HydroBallsColorScheme,
        typography = Typography,
        content = content
    )
}

