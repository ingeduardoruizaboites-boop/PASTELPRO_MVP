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

        const val DIAS_ANTES_DEFAULT = 1
        const val HORA_DEFAULT = "09:00"
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

    suspend fun setNotificacionesHabilitadas(habilitadas: Boolean) {
        context.dataStore.edit { it[KEY_NOTIF_HABILITADAS] = habilitadas }
    }

    suspend fun setDiasAntes(dias: Int) {
        require(dias in 0..30) { "Los días deben estar entre 0 y 30" }
        context.dataStore.edit { it[KEY_NOTIF_DIAS_ANTES] = dias }
    }

    suspend fun setHoraNotificacion(hora: String) {
        require(Regex("^\\d{2}:\\d{2}$").matches(hora)) { "Formato de hora inválido: $hora" }
        context.dataStore.edit { it[KEY_NOTIF_HORA] = hora }
    }
}
