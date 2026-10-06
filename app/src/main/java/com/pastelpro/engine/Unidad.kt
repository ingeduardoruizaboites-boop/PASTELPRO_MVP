package com.pastelpro.engine

import java.math.BigDecimal

/**
 * Categorías de unidades. Solo se permite conversión dentro de la misma categoría.
 * Regla §18 del Maestro: la app debe manejar kg, g, L, ml, pieza, docena, paquete, caja, unidad.
 */
enum class CategoriaUnidad {
    MASA, VOLUMEN, CONTEO
}

/**
 * Unidades soportadas con su factor de conversión a la unidad base de su categoría.
 *
 * Base MASA    = gramo
 * Base VOLUMEN = mililitro
 * Base CONTEO  = pieza
 *
 * paquete y caja se tratan como CONTEO con factor 1 (su equivalencia depende del contexto;
 * para MVP, la repostera indica directamente la cantidad en piezas si es necesario).
 */
enum class Unidad(
    val simbolo: String,
    val categoria: CategoriaUnidad,
    val factorBase: BigDecimal
) {
    // ── Masa ──
    GRAMO("g", CategoriaUnidad.MASA, BigDecimal("1")),
    KILOGRAMO("kg", CategoriaUnidad.MASA, BigDecimal("1000")),

    // ── Volumen ──
    MILILITRO("ml", CategoriaUnidad.VOLUMEN, BigDecimal("1")),
    LITRO("L", CategoriaUnidad.VOLUMEN, BigDecimal("1000")),

    // ── Conteo ──
    PIEZA("pieza", CategoriaUnidad.CONTEO, BigDecimal("1")),
    DOCENA("docena", CategoriaUnidad.CONTEO, BigDecimal("12")),
    PAQUETE("paquete", CategoriaUnidad.CONTEO, BigDecimal("1")),
    CAJA("caja", CategoriaUnidad.CONTEO, BigDecimal("1")),
    UNIDAD("unidad", CategoriaUnidad.CONTEO, BigDecimal("1"));

    companion object {
        /**
         * Busca una unidad por su símbolo (case-insensitive).
         * Devuelve null si no la reconoce.
         */
        fun desdeSimbolo(simbolo: String): Unidad? =
            values().firstOrNull { it.simbolo.equals(simbolo.trim(), ignoreCase = true) }
    }
}
