package com.pastelpro.data.repository

import com.pastelpro.data.local.PedidoDao
import com.pastelpro.data.local.mapper.PedidoMapper
import com.pastelpro.domain.model.EstadoPedido
import com.pastelpro.domain.model.Pedido
import com.pastelpro.domain.repository.PedidoRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class PedidoRepositoryRoom(
    private val dao: PedidoDao
) : PedidoRepository {

    override fun observarTodos(): Flow<List<Pedido>> =
        dao.observarTodos().map { entities ->
            entities.map(PedidoMapper::aDominio)
        }

    override suspend fun obtener(id: String): Pedido? =
        dao.obtener(id)?.let(PedidoMapper::aDominio)

    override suspend fun agregar(pedido: Pedido) {
        dao.insertar(PedidoMapper.aEntity(pedido))
    }

    override suspend fun eliminar(id: String) {
        dao.eliminar(id)
    }

    override suspend fun cambiarEstado(id: String, nuevoEstado: EstadoPedido) {
        dao.cambiarEstado(id, nuevoEstado.name)
    }
}
