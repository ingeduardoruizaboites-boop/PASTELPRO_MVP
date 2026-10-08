package com.pastelpro.data.local.mapper

import com.pastelpro.data.local.RecetaEntity
import com.pastelpro.data.local.RecetaIngredienteEntity
import com.pastelpro.domain.model.IngredienteDeReceta
import com.pastelpro.domain.model.Receta
import java.math.BigDecimal

object RecetaMapper {

    fun aDominio(
        receta: RecetaEntity,
        ingredientes: List<RecetaIngredienteEntity>
    ): Receta = Receta(
        id = receta.id,
        nombre = receta.nombre,
        tipo = receta.tipo,
        rendimientoCantidad = receta.rendimientoCantidad,
        rendimientoUnidad = receta.rendimientoUnidad,
        ingredientes = ingredientes.map(::ingredienteADominio),
        notas = receta.notas
    )

    fun aEntity(receta: Receta): RecetaEntity = RecetaEntity(
        id = receta.id,
        nombre = receta.nombre,
        tipo = receta.tipo,
        rendimientoCantidad = receta.rendimientoCantidad,
        rendimientoUnidad = receta.rendimientoUnidad,
        notas = receta.notas
    )

    fun ingredientesAEntities(recetaId: String, items: List<IngredienteDeReceta>): List<RecetaIngredienteEntity> =
        items.map { i ->
            RecetaIngredienteEntity(
                recetaId = recetaId,
                ingredienteId = i.ingredienteId,
                nombre = i.nombre,
                cantidad = i.cantidad.toPlainString(),
                unidad = i.unidad
            )
        }

    private fun ingredienteADominio(e: RecetaIngredienteEntity): IngredienteDeReceta =
        IngredienteDeReceta(
            ingredienteId = e.ingredienteId,
            nombre = e.nombre,
            cantidad = e.cantidad.toBigDecimalOrNull() ?: BigDecimal.ZERO,
            unidad = e.unidad
        )
}
