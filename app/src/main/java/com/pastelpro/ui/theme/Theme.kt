package com.pastelpro.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val PastelProColorScheme = lightColorScheme(
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

@Composable
fun PastelProTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    // MVP 1: solo tema claro (cálido-premium). El oscuro llega post-MVP.
    MaterialTheme(
        colorScheme = PastelProColorScheme,
        typography = PastelProTypography,
        content = content
    )
}
