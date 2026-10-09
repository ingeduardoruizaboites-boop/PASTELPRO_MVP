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
    val telefonoContacto: String?,
    val porciones: Int,
    val costoTotal: String,
    val precioAcordado: String,
    val fechaEntrega: String?,
    val horaEntrega: String?,
    val direccionEntrega: String?,
    val notas: String?,
    val estado: String,
    val creadoEn: Long
)
