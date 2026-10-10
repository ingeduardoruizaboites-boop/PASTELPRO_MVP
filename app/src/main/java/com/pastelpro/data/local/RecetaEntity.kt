package com.pastelpro.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "recetas")
data class RecetaEntity(
    @PrimaryKey val id: String,
    val nombre: String,
    val tipo: String,
    val rendimientoCantidad: Int,
    val rendimientoUnidad: String,
    val notas: String?,
    // Campos de molde (opcionales)
    val moldeForma: String?,
    val moldeAnchoCm: Int?,
    val moldeLargoCm: Int?,
    val moldeAltoCm: Int?,
    val porcionesPorMolde: Int?
)
