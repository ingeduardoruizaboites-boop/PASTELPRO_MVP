package com.pastelpro.domain.repository

import com.pastelpro.domain.model.EstadoPedido
import com.pastelpro.domain.model.Pedido
import kotlinx.coroutines.flow.Flow

interface PedidoRepository {
    fun observarTodos(): Flow<List<Pedido>>
    suspend fun obtener(id: String): Pedido?
    suspend fun agregar(pedido: Pedido)
    suspend fun eliminar(id: String)
    suspend fun cambiarEstado(id: String, nuevoEstado: EstadoPedido)
}
