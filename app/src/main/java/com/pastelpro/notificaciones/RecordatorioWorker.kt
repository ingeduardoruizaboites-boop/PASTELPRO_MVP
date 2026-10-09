package com.pastelpro.notificaciones

import android.content.Context
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.pastelpro.data.repository.RepositorioProvider
import com.pastelpro.diagnostico.DiagnosticLogger
import kotlinx.coroutines.runBlocking

/**
 * Worker que dispara la notificación de recordatorio para un pedido.
 *
 * Recibe por inputData:
 *  - PEDIDO_ID: id del pedido
 *  - TIPO: 0 = "días antes", 1 = "mismo día"
 *
 * El worker verifica que el pedido siga PENDIENTE antes de notificar.
 * Si ya fue entregado, no notifica.
 */
class RecordatorioWorker(
    context: Context,
    params: WorkerParameters
) : Worker(context, params) {

    companion object {
        const val KEY_PEDIDO_ID = "pedido_id"
        const val KEY_TIPO = "tipo"
        const val TIPO_DIAS_ANTES = 0
        const val TIPO_MISMO_DIA = 1
    }

    override fun doWork(): Result {
        val pedidoId = inputData.getString(KEY_PEDIDO_ID) ?: return Result.failure()
        val tipo = inputData.getInt(KEY_TIPO, TIPO_DIAS_ANTES)

        DiagnosticLogger.log("Worker", "Ejecutando recordatorio tipo=$tipo pedidoId=$pedidoId")

        return try {
            val repo = RepositorioProvider.pedidoRepository
            val pedido = runBlocking { repo.obtener(pedidoId) }

            if (pedido == null) {
                DiagnosticLogger.log("Worker", "Pedido $pedidoId no encontrado")
                return Result.success()
            }

            // No notificar si ya fue entregado
            if (pedido.estado.name == "ENTREGADO") {
                DiagnosticLogger.log("Worker", "Pedido $pedidoId ya entregado, sin notificación")
                return Result.success()
            }

            val mensaje = when (tipo) {
                TIPO_MISMO_DIA -> "Hoy entregas este pedido"
                else -> "Próxima entrega"
            }

            val notifId = NotificationHelper.generarNotifId(pedidoId, tipo)
            NotificationHelper.notificarPedidoPendiente(
                context = applicationContext,
                pedido = pedido,
                mensaje = mensaje,
                notifId = notifId
            )

            DiagnosticLogger.log("Worker", "Notificación enviada OK")
            Result.success()
        } catch (e: Exception) {
            DiagnosticLogger.logError("Worker", "Error en RecordatorioWorker", e)
            Result.retry()
        }
    }
}
