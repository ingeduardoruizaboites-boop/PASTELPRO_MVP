package com.pastelpro.notificaciones

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.pastelpro.MainActivity
import com.pastelpro.R
import com.pastelpro.domain.model.Pedido

/**
 * Gestiona canales y notificaciones de pedidos.
 *
 * Canal único "Recordatorios de pedidos".
 * Dos tipos de notificación:
 *  1. Días antes (con texto "En X días entregas...")
 *  2. Mismo día (con texto "Hoy entregas...")
 */
object NotificationHelper {

    const val CANAL_ID = "pedidos_recordatorios"
    const val CANAL_NOMBRE = "Recordatorios de pedidos"
    const val CANAL_DESC = "Avisos de pedidos próximos a entregar"

    /**
     * Crea el canal de notificaciones. Llamar en MainActivity.onCreate.
     * Es idempotente: si ya existe, no hace nada.
     */
    fun crearCanal(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val canal = NotificationChannel(
                CANAL_ID,
                CANAL_NOMBRE,
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = CANAL_DESC
                enableVibration(true)
                enableLights(true)
            }
            val manager = context.getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(canal)
        }
    }

    /**
     * Muestra una notificación de pedido pendiente.
     *
     * @param pedido el pedido a recordar
     * @param mensaje texto específico ("En 1 día...", "Hoy...")
     * @param notifId id único por pedido+tipo
     */
    fun notificarPedidoPendiente(
        context: Context,
        pedido: Pedido,
        mensaje: String,
        notifId: Int
    ) {
        // Intent principal → abre la app
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            putExtra("pedido_id", pedido.id)
        }
        val pendingIntent = PendingIntent.getActivity(
            context,
            notifId,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notif = NotificationCompat.Builder(context, CANAL_ID)
            .setSmallIcon(R.drawable.logo_pastelpro)
            .setContentTitle("📅 Pedido para ${pedido.cliente ?: "entregar"}")
            .setContentText("${pedido.recetaNombre} · ${pedido.porciones} porciones")
            .setStyle(
                NotificationCompat.BigTextStyle().bigText(
                    "$mensaje\n${pedido.recetaNombre} · ${pedido.porciones} porciones" +
                            (pedido.direccionEntrega?.let { "\n📍 $it" } ?: "")
                )
            )
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .build()

        try {
            NotificationManagerCompat.from(context).notify(notifId, notif)
        } catch (e: SecurityException) {
            // Permiso denegado por el usuario — ignorar silenciosamente
        }
    }

    /**
     * Genera un notifId estable a partir del id del pedido + tipo de alarma.
     * Tipo: 0 = días antes, 1 = mismo día.
     */
    fun generarNotifId(pedidoId: String, tipo: Int): Int {
        return (pedidoId.hashCode() * 10 + tipo).let {
            if (it < 0) -it else it
        }.mod(2_000_000_000)
    }
}
