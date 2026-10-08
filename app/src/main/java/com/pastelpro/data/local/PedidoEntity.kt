package com.pastelpro.data.local

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "pedidos",
    indices = [Index("recetaId")]
)
data class PedidoEntity(
    @PrimaryKey val id: String,
    val recetaId: String,
    val recetaNombre: String,
    val cliente: String?,
    val porciones: Int,
    val costoTotal: String,        // BigDecimal serializado
    val precioAcordado: String,
    val fechaEntrega: String?,
    val notas: String?,
    val estado: String,            // "PENDIENTE" o "ENTREGADO"
    val creadoEn: Long
)
