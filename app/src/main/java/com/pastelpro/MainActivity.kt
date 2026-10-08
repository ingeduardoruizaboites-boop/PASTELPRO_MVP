package com.pastelpro

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat
import com.pastelpro.data.repository.RepositorioProvider
import com.pastelpro.diagnostico.DiagnosticLogger
import com.pastelpro.ui.theme.PastelProTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Inicializar el diagnóstico PRIMERO (captura crashes de todo lo demás).
        DiagnosticLogger.init(applicationContext)
        DiagnosticLogger.seccion("MainActivity.onCreate")

        // Inicializar el Service Locator ANTES de setContent.
        RepositorioProvider.init(applicationContext)

        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.auto(
                lightScrim = Color.Transparent.toArgb(),
                darkScrim = Color.Transparent.toArgb()
            ),
            navigationBarStyle = SystemBarStyle.auto(
                lightScrim = Color.Transparent.toArgb(),
                darkScrim = Color.Transparent.toArgb()
            )
        )

        setContent {
            val darkTheme = isSystemInDarkTheme()
            val view = LocalView.current

            LaunchedEffect(darkTheme) {
                val window = (view.context as ComponentActivity).window
                WindowCompat.getInsetsController(window, view).apply {
                    isAppearanceLightStatusBars = !darkTheme
                    isAppearanceLightNavigationBars = !darkTheme
                }
            }

            PastelProTheme(darkTheme = darkTheme) {
                PastelProApp()
            }
        }
    }
}
