package com.pastelpro.data.local.mapper

import com.pastelpro.data.local.PedidoEntity
import com.pastelpro.domain.model.EstadoPedido
import com.pastelpro.domain.model.Pedido
import java.math.BigDecimal

object PedidoMapper {

    fun aDominio(entity: PedidoEntity): Pedido = Pedido(
        id = entity.id,
        recetaId = entity.recetaId,
        recetaNombre = entity.recetaNombre,
        cliente = entity.cliente,
        porciones = entity.porciones,
        costoTotal = entity.costoTotal.toBigDecimalOrNull() ?: BigDecimal.ZERO,
        precioAcordado = entity.precioAcordado.toBigDecimalOrNull() ?: BigDecimal.ZERO,
        fechaEntrega = entity.fechaEntrega,
        notas = entity.notas,
        estado = try {
            EstadoPedido.valueOf(entity.estado)
        } catch (e: Exception) {
            EstadoPedido.PENDIENTE
        },
        creadoEn = entity.creadoEn
    )

    fun aEntity(pedido: Pedido): PedidoEntity = PedidoEntity(
        id = pedido.id,
        recetaId = pedido.recetaId,
        recetaNombre = pedido.recetaNombre,
        cliente = pedido.cliente,
        porciones = pedido.porciones,
        costoTotal = pedido.costoTotal.toPlainString(),
        precioAcordado = pedido.precioAcordado.toPlainString(),
        fechaEntrega = pedido.fechaEntrega,
        notas = pedido.notas,
        estado = pedido.estado.name,
        creadoEn = pedido.creadoEn
    )
}
