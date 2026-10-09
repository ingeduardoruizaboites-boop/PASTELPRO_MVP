package com.pastelpro

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.content.ContextCompat
import androidx.core.view.WindowCompat
import com.pastelpro.data.repository.RepositorioProvider
import com.pastelpro.diagnostico.DiagnosticLogger
import com.pastelpro.notificaciones.NotificationHelper
import com.pastelpro.ui.theme.PastelProTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext

class MainActivity : ComponentActivity() {

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        DiagnosticLogger.log("Notif", "POST_NOTIFICATIONS concedido: $granted")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        DiagnosticLogger.init(applicationContext)
        DiagnosticLogger.seccion("MainActivity.onCreate")

        // Init pesado en background
        runBlocking {
            withContext(Dispatchers.IO) {
                RepositorioProvider.init(applicationContext)
            }
        }

        // Canal de notificaciones (idempotente)
        NotificationHelper.crearCanal(applicationContext)

        // Pedir permiso POST_NOTIFICATIONS en Android 13+
        pedirPermisoNotificacionesSiNecesario()

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

    private fun pedirPermisoNotificacionesSiNecesario() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) return

        val permiso = Manifest.permission.POST_NOTIFICATIONS
        val concedido = ContextCompat.checkSelfPermission(this, permiso) ==
                PackageManager.PERMISSION_GRANTED

        if (!concedido) {
            requestPermissionLauncher.launch(permiso)
        }
    }
}
