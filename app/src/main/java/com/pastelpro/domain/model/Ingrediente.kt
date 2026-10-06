package com.pastelpro.domain.model

import java.math.BigDecimal
import java.util.UUID

/**
 * Modelo de dominio: un ingrediente tal como lo ve la repostera.
 *
 * Regla §64 del Maestro: dinero con representación precisa.
 * Por eso precioCompra y cantidadCompra usan BigDecimal.
 */
data class Ingrediente(
    val id: String = UUID.randomUUID().toString(),
    val nombre: String,
    val categoria: String,
    val presentacionCompra: String,   // ej. "1 kg", "500 g", "1 L"
    val cantidadCompra: BigDecimal,   // ej. 1.0 (kg), 500.0 (g)
    val unidadCompra: String,         // "kg", "g", "L", "ml", "pieza"
    val precioCompra: BigDecimal,     // ej. 28.00 (MXN)
    val proveedor: String? = null,
    val notas: String? = null
) {
    /**
     * Costo por unidad base.
     * NO se almacena: se calcula al vuelo (pertenece al motor de cálculo).
     */
    val costoPorUnidad: BigDecimal
        get() = if (cantidadCompra.compareTo(BigDecimal.ZERO) > 0)
            precioCompra.divide(cantidadCompra, 6, java.math.RoundingMode.HALF_UP)
        else BigDecimal.ZERO
}
