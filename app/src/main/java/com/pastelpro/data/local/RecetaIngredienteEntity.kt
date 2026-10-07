package com.pastelpro.data.local

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index

/**
 * Tabla relacionada: qué ingredientes tiene cada receta y en qué cantidad.
 * Al borrar la receta, se borran sus ingredientes (CASCADE).
 */
@Entity(
    tableName = "receta_ingredientes",
    primaryKeys = ["recetaId", "ingredienteId"],
    foreignKeys = [
        ForeignKey(
            entity = RecetaEntity::class,
            parentColumns = ["id"],
            childColumns = ["recetaId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("recetaId"), Index("ingredienteId")]
)
data class RecetaIngredienteEntity(
    val recetaId: String,
    val ingredienteId: String,
    val nombre: String,           // desnormalizado
    val cantidad: String,         // BigDecimal serializado
    val unidad: String
)
