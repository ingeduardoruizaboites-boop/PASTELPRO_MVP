package com.pastelpro.domain.model

import java.util.UUID

/**
 * Receta del dominio.
 *
 * El "rendimiento" es la cantidad que produce la receta base.
 * Ejemplo: "1 pastel de 20cm" o "8 porciones".
 *
 * El escalado (Bloque 4C) trabaja multiplicando esta base.
 */
data class Receta(
    val id: String = UUID.randomUUID().toString(),
    val nombre: String,
    val tipo: String,                       // "3 leches", "Chocolate", "Vainilla", etc.
    val rendimiento: String,                // "8 porciones", "1 pastel 20cm"
    val ingredientes: List<IngredienteDeReceta> = emptyList(),
    val notas: String? = null
)
