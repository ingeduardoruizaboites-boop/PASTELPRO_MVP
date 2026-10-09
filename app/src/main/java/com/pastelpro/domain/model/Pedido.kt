package com.pastelpro.domain.model

import java.math.BigDecimal
import java.util.UUID

data class Pedido(
    val id: String = UUID.randomUUID().toString(),
    val recetaId: String,
    val recetaNombre: String,
    val cliente: String? = null,
    val telefonoContacto: String? = null,
    val porciones: Int,
    val costoTotal: BigDecimal,
    val precioAcordado: BigDecimal,
    val fechaEntrega: String? = null,      // ISO 8601: "2026-10-15"
    val horaEntrega: String? = null,        // "14:30"
    val direccionEntrega: String? = null,
    val notas: String? = null,
    val estado: EstadoPedido = EstadoPedido.PENDIENTE,
    val creadoEn: Long = System.currentTimeMillis()
) {
    val ganancia: BigDecimal
        get() = precioAcordado.subtract(costoTotal)

    val margenReal: BigDecimal
        get() = if (precioAcordado.compareTo(BigDecimal.ZERO) > 0)
            ganancia.multiply(BigDecimal(100)).divide(precioAcordado, 2, java.math.RoundingMode.HALF_UP)
        else BigDecimal.ZERO

    /**
     * Devuelve "15/10/2026 · 14:30" si hay fecha y hora,
     * "15/10/2026" si solo hay fecha,
     * "14:30" si solo hay hora,
     * null si no hay nada.
     */
    val entregaTexto: String?
        get() {
            val partes = mutableListOf<String>()
            fechaEntrega?.let { partes += fechaAIsoADisplay(it) }
            horaEntrega?.let { partes += it }
            return if (partes.isEmpty()) null else partes.joinToString(" · ")
        }

    companion object {
        /** Convierte ISO "2026-10-15" a display "15/10/2026". */
        fun fechaAIsoADisplay(iso: String): String {
            return try {
                val partes = iso.split("-")
                if (partes.size == 3) "${partes[2]}/${partes[1]}/${partes[0]}" else iso
            } catch (e: Exception) {
                iso
            }
        }

        /** Convierte display "15/10/2026" a ISO "2026-10-15". */
        fun fechaDisplayAIso(display: String): String? {
            return try {
                val partes = display.split("/")
                if (partes.size == 3) "${partes[2]}-${partes[1]}-${partes[0]}" else null
            } catch (e: Exception) {
                null
            }
        }
    }
}

enum class EstadoPedido {
    PENDIENTE,
    ENTREGADO;

    val etiqueta: String
        get() = when (this) {
            PENDIENTE -> "Pendiente"
            ENTREGADO -> "Entregado"
        }
}
