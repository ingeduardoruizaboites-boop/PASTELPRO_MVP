package com.pastelpro.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Entidad de persistencia.
 *
 * Decisiones (§64 del Maestro):
 * - Dinero como TEXT (BigDecimal serializado) para evitar errores de precisión.
 * - cantidadCompra y precioCompra son strings que se convierten a BigDecimal
 *   en el mapper. Room no soporta BigDecimal nativamente.
 */
@Entity(tableName = "ingredientes")
data class IngredienteEntity(
    @PrimaryKey val id: String,
    val nombre: String,
    val categoria: String,
    val presentacionCompra: String,
    val cantidadCompra: String,   // BigDecimal serializado
    val unidadCompra: String,
    val precioCompra: String,     // BigDecimal serializado
    val proveedor: String?,
    val notas: String?
)
