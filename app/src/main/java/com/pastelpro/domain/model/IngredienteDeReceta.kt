package com.pastelpro.domain.model

import java.math.BigDecimal

/**
 * Un ingrediente usado dentro de una receta.
 * No es lo mismo que un Ingrediente de inventario (que tiene precio, proveedor, etc.).
 * Aquí solo importa: qué ingrediente, cuánto y en qué unidad.
 */
data class IngredienteDeReceta(
    val ingredienteId: String,
    val nombre: String,           // desnormalizado para mostrar sin JOIN
    val cantidad: BigDecimal,
    val unidad: String            // "g", "kg", "ml", "L", "pieza"
)
