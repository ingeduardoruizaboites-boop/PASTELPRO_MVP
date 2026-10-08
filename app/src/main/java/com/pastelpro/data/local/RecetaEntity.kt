package com.pastelpro.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "recetas")
data class RecetaEntity(
    @PrimaryKey val id: String,
    val nombre: String,
    val tipo: String,
    val rendimientoCantidad: Int,        // ej. 20
    val rendimientoUnidad: String,        // ej. "porciones", "personas", "piezas"
    val notas: String?
)
