package com.pastelpro.domain.model

import java.math.BigDecimal
import java.util.UUID

/**
 * Pedido cotizado o vendido.
 *
 * Snapshot: guardamos el costo y el precio al momento de crear el pedido.
 * Si los precios de ingredientes cambian después, el pedido histórico NO se modifica.
 *
 * Estados:
 *  - PENDIENTE: cotizado, aún no entregado
 *  - ENTREGADO: entregado y cobrado
 */
data class Pedido(
    val id: String = UUID.randomUUID().toString(),
    val recetaId: String,
    val recetaNombre: String,
    val cliente: String? = null,
    val porciones: Int,
    val costoTotal: BigDecimal,
    val precioAcordado: BigDecimal,
    val fechaEntrega: String? = null,
    val notas: String? = null,
    val estado: EstadoPedido = EstadoPedido.PENDIENTE,
    val creadoEn: Long = System.currentTimeMillis()
) {
    /** Ganancia = precio - costo. */
    val ganancia: BigDecimal
        get() = precioAcordado.subtract(costoTotal)

    /** Margen real = ganancia / precio × 100. */
    val margenReal: BigDecimal
        get() = if (precioAcordado.compareTo(BigDecimal.ZERO) > 0)
            ganancia.multiply(BigDecimal(100)).divide(precioAcordado, 2, java.math.RoundingMode.HALF_UP)
        else BigDecimal.ZERO
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
