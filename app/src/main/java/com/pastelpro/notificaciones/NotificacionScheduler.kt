package com.pastelpro.notificaciones

import android.content.Context
import androidx.work.Data
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.pastelpro.diagnostico.DiagnosticLogger
import com.pastelpro.domain.model.Pedido
import java.util.Calendar
import java.util.concurrent.TimeUnit

/**
 * Programa y cancela recordatorios de pedidos usando WorkManager.
 *
 * Estrategia:
 * - Para cada pedido PENDIENTE con fecha de entrega → programa 2 alarmas.
 * - Al marcar ENTREGADO → cancela ambas.
 *
 * Los "work names" son estables: "recordatorio_{pedidoId}_tipo{tipo}".
 * Esto permite reprogramar/cancelar sin miedo a duplicados.
 */
object NotificacionScheduler {

    private fun nombreWork(pedidoId: String, tipo: Int): String =
        "recordatorio_${pedidoId}_tipo$tipo"

    /**
     * Programa las 2 alarmas para un pedido.
     *
     * @param diasAntes cuántos días antes notificar (alarma A)
     * @param horaNotificacion formato "HH:mm" (ej. "09:00")
     * @param habilitadas si el usuario desactivó las notificaciones, no se programa nada
     */
    fun programarRecordatorios(
        context: Context,
        pedido: Pedido,
        diasAntes: Int,
        horaNotificacion: String,
        habilitadas: Boolean
    ) {
        if (!habilitadas) {
            DiagnosticLogger.log("Scheduler", "Notificaciones deshabilitadas, skip")
            return
        }

        val fechaIso = pedido.fechaEntrega
        if (fechaIso.isNullOrBlank()) {
            DiagnosticLogger.log("Scheduler", "Pedido sin fecha, skip")
            return
        }

        DiagnosticLogger.log(
            "Scheduler",
            "Programando: pedido=${pedido.id.take(8)}, fecha=$fechaIso, hora=${pedido.horaEntrega ?: "sin hora"}, diasAntes=$diasAntes, horaGlobal=$horaNotificacion"
        )

        val (horaGlobal, minutoGlobal) = parseHora(horaNotificacion)

        // Parsear la fecha de entrega
        val fechaEntrega = parseFecha(fechaIso) ?: run {
            DiagnosticLogger.log("Scheduler", "Fecha inválida: $fechaIso")
            return
        }

        // ═══ Alarma A: días antes (usa la hora de entrega del pedido) ═══
        // Si el pedido tiene hora de entrega explícita, la usamos como referencia.
        // Si no, usamos la hora global de configuración.
        val (horaA, minutoA) = if (!pedido.horaEntrega.isNullOrBlank()) {
            parseHora(pedido.horaEntrega!!)
        } else {
            horaGlobal to minutoGlobal
        }

        val fechaAlarmaA = (fechaEntrega.clone() as Calendar).apply {
            add(Calendar.DAY_OF_YEAR, -diasAntes)
            set(Calendar.HOUR_OF_DAY, horaA)
            set(Calendar.MINUTE, minutoA)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }

        // Si ya pasó, no programar
        if (fechaAlarmaA.timeInMillis > System.currentTimeMillis()) {
            programar(
                context = context,
                pedidoId = pedido.id,
                tipo = RecordatorioWorker.TIPO_DIAS_ANTES,
                delayMillis = fechaAlarmaA.timeInMillis - System.currentTimeMillis()
            )
        } else {
            DiagnosticLogger.log("Scheduler", "Alarma A ya pasó, skip (fecha=${fechaAlarmaA.time})")
        }

        // ═══ Alarma B: mismo día (usa la hora de entrega del pedido) ═══
        val (horaB, minutoB) = if (!pedido.horaEntrega.isNullOrBlank()) {
            parseHora(pedido.horaEntrega!!)
        } else {
            horaGlobal to minutoGlobal
        }

        val fechaAlarmaB = (fechaEntrega.clone() as Calendar).apply {
            set(Calendar.HOUR_OF_DAY, horaB)
            set(Calendar.MINUTE, minutoB)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }

        if (fechaAlarmaB.timeInMillis > System.currentTimeMillis()) {
            programar(
                context = context,
                pedidoId = pedido.id,
                tipo = RecordatorioWorker.TIPO_MISMO_DIA,
                delayMillis = fechaAlarmaB.timeInMillis - System.currentTimeMillis()
            )
        } else {
            DiagnosticLogger.log("Scheduler", "Alarma B ya pasó, skip (fecha=${fechaAlarmaB.time})")
        }
    }

    fun cancelarRecordatorios(context: Context, pedidoId: String) {
        val wm = WorkManager.getInstance(context)
        wm.cancelUniqueWork(nombreWork(pedidoId, RecordatorioWorker.TIPO_DIAS_ANTES))
        wm.cancelUniqueWork(nombreWork(pedidoId, RecordatorioWorker.TIPO_MISMO_DIA))
        DiagnosticLogger.log("Scheduler", "Recordatorios cancelados para pedido $pedidoId")
    }

    private fun programar(
        context: Context,
        pedidoId: String,
        tipo: Int,
        delayMillis: Long
    ) {
        val datos = Data.Builder()
            .putString(RecordatorioWorker.KEY_PEDIDO_ID, pedidoId)
            .putInt(RecordatorioWorker.KEY_TIPO, tipo)
            .build()

        val request = OneTimeWorkRequestBuilder<RecordatorioWorker>()
            .setInitialDelay(delayMillis, TimeUnit.MILLISECONDS)
            .setInputData(datos)
            .build()

        WorkManager.getInstance(context).enqueueUniqueWork(
            nombreWork(pedidoId, tipo),
            ExistingWorkPolicy.REPLACE,
            request
        )

        val minutos = delayMillis / 1000 / 60
        DiagnosticLogger.log(
            "Scheduler",
            "Programado tipo=$tipo pedido=$pedidoId en $minutos minutos"
        )
    }

    // ═══ Helpers ═══

    private fun parseHora(hora: String): Pair<Int, Int> {
        return try {
            val partes = hora.split(":")
            if (partes.size == 2) partes[0].toInt() to partes[1].toInt()
            else 9 to 0
        } catch (e: Exception) {
            9 to 0
        }
    }

    private fun parseFecha(iso: String): Calendar? {
        return try {
            val partes = iso.split("-")
            if (partes.size != 3) return null
            Calendar.getInstance().apply {
                set(Calendar.YEAR, partes[0].toInt())
                set(Calendar.MONTH, partes[1].toInt() - 1) // Calendar.MONTH es 0-indexed
                set(Calendar.DAY_OF_MONTH, partes[2].toInt())
            }
        } catch (e: Exception) {
            null
        }
    }
}
