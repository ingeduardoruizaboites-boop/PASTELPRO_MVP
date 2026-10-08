package com.pastelpro.diagnostico

import android.content.Context
import android.os.Build
import android.os.Environment
import android.util.Log
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Logger de diagnóstico para desarrollo.
 *
 * Escribe a diagnostico_pastelpro.txt en la carpeta "Descargas" externa de la app.
 * También instala un UncaughtExceptionHandler que captura crashes con contexto.
 *
 * Objetivo: reproducir bugs en dispositivo físico sin ADB ni Logcat Reader.
 * En producción (release) NO se usa — solo en debug.
 */
object DiagnosticLogger {

    private const val TAG = "PastelProDiag"
    private const val NOMBRE_ARCHIVO = "diagnostico_pastelpro.txt"

    private var archivo: File? = null
    private var inicializado = false
    private val fechaFmt = SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS", Locale.getDefault())

    fun init(context: Context) {
        if (inicializado) return
        inicializado = true

        try {
            // getExternalFilesDir(DOWNLOADS) → /Android/data/com.pastelpro.debug/files/Download/
            // Accesible con explorador de archivos (Files by Google, MIUI, etc.).
            val dir = context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS)
                ?: context.filesDir
            dir.mkdirs()

            archivo = File(dir, NOMBRE_ARCHIVO)
            archivo?.writeText("") // limpiar

            escribir("═══════════════════════════════════════════════════════")
            escribir("PASTELPRO · DIAGNÓSTICO")
            escribir("Iniciado: ${fechaFmt.format(Date())}")
            escribir("Android: ${Build.VERSION.SDK_INT} (${Build.VERSION.RELEASE})")
            escribir("Dispositivo: ${Build.MANUFACTURER} ${Build.MODEL}")
            escribir("App: ${context.packageName}")
            escribir("Ruta: ${archivo?.absolutePath}")
            escribir("═══════════════════════════════════════════════════════")
            escribir("")

            instalarCrashHandler()
            Log.i(TAG, "DiagnosticLogger inicializado en: ${archivo?.absolutePath}")

        } catch (e: Exception) {
            Log.e(TAG, "No se pudo inicializar DiagnosticLogger", e)
        }
    }

    /**
     * Log de un evento normal.
     */
    fun log(tag: String, mensaje: String) {
        escribir("[${fechaFmt.format(Date())}] $tag | $mensaje")
    }

    /**
     * Log de un error controlado (excepción capturada).
     */
    fun logError(tag: String, mensaje: String, throwable: Throwable? = null) {
        escribir("[${fechaFmt.format(Date())}] $tag | ERROR | $mensaje")
        throwable?.let {
            escribir("  → ${it.javaClass.name}: ${it.message}")
            it.stackTrace.take(5).forEach { el ->
                escribir("     at $el")
            }
        }
    }

    /**
     * Marca de inicio/fin de una sección (útil para ver dónde se quedó antes de un crash).
     */
    fun seccion(nombre: String) {
        escribir("")
        escribir("───── $nombre ─────")
    }

    /**
     * Ruta absoluta del archivo (útil para mostrar al usuario).
     */
    fun rutaArchivo(): String? = archivo?.absolutePath

    fun existe(): Boolean = archivo?.exists() == true
    fun obtenerArchivo(): File? = archivo

    // ═══════════════════════════════════════════════════════════
    // Internos
    // ═══════════════════════════════════════════════════════════

    private fun escribir(linea: String) {
        try {
            // Append atómico: abre + escribe + cierra. No usa buffer que se pierda en crash.
            archivo?.appendText(linea + "\n")
            Log.d(TAG, linea)
        } catch (e: Exception) {
            Log.e(TAG, "Error escribiendo log: $linea", e)
        }
    }

    private fun instalarCrashHandler() {
        val previo = Thread.getDefaultUncaughtExceptionHandler()

        Thread.setDefaultUncaughtExceptionHandler { thread, throwable ->
            try {
                escribir("")
                escribir("═══════════════════════════════════════════════════════")
                escribir("!!! CRASH !!!")
                escribir("Hilo: ${thread.name}")
                escribir("Hora: ${fechaFmt.format(Date())}")
                escribir("Excepción: ${throwable.javaClass.name}")
                escribir("Mensaje: ${throwable.message ?: "(sin mensaje)"}")
                escribir("")
                escribir("Stacktrace completo:")
                throwable.stackTrace.forEach { el ->
                    escribir("  at $el")
                }
                throwable.cause?.let { causa ->
                    escribir("")
                    escribir("Causado por: ${causa.javaClass.name}")
                    escribir("Mensaje: ${causa.message ?: "(sin mensaje)"}")
                    causa.stackTrace.forEach { el ->
                        escribir("    at $el")
                    }
                }
                escribir("═══════════════════════════════════════════════════════")
            } catch (e: Exception) {
                Log.e(TAG, "Error escribiendo crash", e)
            }
            previo?.uncaughtException(thread, throwable)
        }
    }
}
