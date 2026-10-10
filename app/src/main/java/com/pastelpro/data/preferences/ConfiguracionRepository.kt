package com.pastelpro.data.preferences

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore(name = "pastelpro_config")

/**
 * Configuración global de la app, persistida en DataStore.
 *
 * Se usa para:
 * - Notificaciones (habilitadas, días antes, hora)
 * - Preferencias futuras (idioma, moneda, valor/hora, etc.)
 *
 * DataStore es más adecuado que Room para valores únicos simples.
 */
class ConfiguracionRepository(private val context: Context) {

    companion object {
        private val KEY_NOTIF_HABILITADAS = booleanPreferencesKey("notificaciones_habilitadas")
        private val KEY_NOTIF_DIAS_ANTES = intPreferencesKey("notificaciones_dias_antes")
        private val KEY_NOTIF_HORA = stringPreferencesKey("notificaciones_hora")
        private val KEY_CM3_POR_PORCION = intPreferencesKey("cm3_por_porcion")
        private val KEY_COSTO_HORNEADA_ACTIVO = booleanPreferencesKey("costo_horneada_activo")
        private val KEY_COSTO_HORNEADA_MONTO = intPreferencesKey("costo_horneada_monto")

        const val DIAS_ANTES_DEFAULT = 1
        const val HORA_DEFAULT = "09:00"
        const val CM3_POR_PORCION_DEFAULT = 125
        const val COSTO_HORNEADA_DEFAULT_ACTIVO = false
        const val COSTO_HORNEADA_DEFAULT_MONTO = 10  // MXN por horneada
    }

    val notificacionesHabilitadas: Flow<Boolean> =
        context.dataStore.data.map { prefs ->
            prefs[KEY_NOTIF_HABILITADAS] ?: true
        }

    val diasAntes: Flow<Int> =
        context.dataStore.data.map { prefs ->
            prefs[KEY_NOTIF_DIAS_ANTES] ?: DIAS_ANTES_DEFAULT
        }

    val horaNotificacion: Flow<String> =
        context.dataStore.data.map { prefs ->
            prefs[KEY_NOTIF_HORA] ?: HORA_DEFAULT
        }

    val cm3PorPorcion: Flow<Int> =
        context.dataStore.data.map { prefs ->
            prefs[KEY_CM3_POR_PORCION] ?: CM3_POR_PORCION_DEFAULT
        }

    val costoHorneadaActivo: Flow<Boolean> =
        context.dataStore.data.map { prefs ->
            prefs[KEY_COSTO_HORNEADA_ACTIVO] ?: COSTO_HORNEADA_DEFAULT_ACTIVO
        }

    val costoHorneadaMonto: Flow<Int> =
        context.dataStore.data.map { prefs ->
            prefs[KEY_COSTO_HORNEADA_MONTO] ?: COSTO_HORNEADA_DEFAULT_MONTO
        }

    suspend fun setNotificacionesHabilitadas(habilitadas: Boolean) {
        context.dataStore.edit { it[KEY_NOTIF_HABILITADAS] = habilitadas }
    }

    suspend fun setDiasAntes(dias: Int) {
        require(dias in 0..30) { "Los días deben estar entre 0 y 30" }
        context.dataStore.edit { it[KEY_NOTIF_DIAS_ANTES] = dias }
    }

    suspend fun setCostoHorneadaActivo(activo: Boolean) {
        context.dataStore.edit { it[KEY_COSTO_HORNEADA_ACTIVO] = activo }
    }

    suspend fun setCostoHorneadaMonto(monto: Int) {
        require(monto in 0..500) { "El monto debe estar entre 0 y 500" }
        context.dataStore.edit { it[KEY_COSTO_HORNEADA_MONTO] = monto }
    }

    suspend fun setCm3PorPorcion(cm3: Int) {
        require(cm3 in 50..300) { "cm3PorPorcion debe estar entre 50 y 300" }
        context.dataStore.edit { it[KEY_CM3_POR_PORCION] = cm3 }
    }

    suspend fun setHoraNotificacion(hora: String) {
        require(Regex("^\\d{2}:\\d{2}$").matches(hora)) { "Formato de hora inválido: $hora" }
        context.dataStore.edit { it[KEY_NOTIF_HORA] = hora }
    }
}
