package com.pastelpro.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

// ══════════════════════════════════════════════════════
// LIGHT SCHEME
// ══════════════════════════════════════════════════════
private val LightColorScheme = lightColorScheme(
    primary = Cocoa,
    onPrimary = White,
    primaryContainer = CocoaLight,
    onPrimaryContainer = White,

    secondary = Berry,
    onSecondary = White,
    secondaryContainer = BerrySoft,
    onSecondaryContainer = White,

    background = Cream,
    onBackground = Neutral900,
    surface = White,
    onSurface = Neutral900,
    surfaceVariant = CreamSoft,
    onSurfaceVariant = Neutral700,

    outline = Neutral300,
    outlineVariant = Neutral100,

    error = ErrorRed,
    onError = White
)

// ══════════════════════════════════════════════════════
// DARK SCHEME · cálido, misma identidad
// ══════════════════════════════════════════════════════
private val DarkColorScheme = darkColorScheme(
    primary = DarkCocoa,
    onPrimary = DarkBackground,
    primaryContainer = DarkCocoaContainer,
    onPrimaryContainer = DarkOnBackground,

    secondary = DarkBerry,
    onSecondary = DarkBackground,
    secondaryContainer = DarkBerryContainer,
    onSecondaryContainer = DarkOnBackground,

    background = DarkBackground,
    onBackground = DarkOnBackground,
    surface = DarkSurface,
    onSurface = DarkOnSurface,
    surfaceVariant = DarkSurfaceVariant,
    onSurfaceVariant = DarkNeutral700,

    outline = DarkOutline,
    outlineVariant = DarkOutlineVariant,

    error = DarkError,
    onError = DarkBackground
)

@Composable
fun PastelProTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme
    MaterialTheme(
        colorScheme = colorScheme,
        typography = PastelProTypography,
        content = content
    )
}
