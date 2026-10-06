package com.pastelpro.data.local.mapper

import com.pastelpro.data.local.IngredienteEntity
import com.pastelpro.domain.model.Ingrediente
import java.math.BigDecimal

/**
 * Conversión Entity ↔ Domain.
 * Aísla la persistencia de la lógica de negocio.
 */
object IngredienteMapper {

    fun aDominio(entity: IngredienteEntity): Ingrediente = Ingrediente(
        id = entity.id,
        nombre = entity.nombre,
        categoria = entity.categoria,
        presentacionCompra = entity.presentacionCompra,
        cantidadCompra = entity.cantidadCompra.toBigDecimalOrNull() ?: BigDecimal.ZERO,
        unidadCompra = entity.unidadCompra,
        precioCompra = entity.precioCompra.toBigDecimalOrNull() ?: BigDecimal.ZERO,
        proveedor = entity.proveedor,
        notas = entity.notas
    )

    fun aEntity(dominio: Ingrediente): IngredienteEntity = IngredienteEntity(
        id = dominio.id,
        nombre = dominio.nombre,
        categoria = dominio.categoria,
        presentacionCompra = dominio.presentacionCompra,
        cantidadCompra = dominio.cantidadCompra.toPlainString(),
        unidadCompra = dominio.unidadCompra,
        precioCompra = dominio.precioCompra.toPlainString(),
        proveedor = dominio.proveedor,
        notas = dominio.notas
    )
}
