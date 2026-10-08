package com.pastelpro.domain.model

import java.util.UUID

/**
 * Receta del dominio.
 *
 * El rendimiento se separa en cantidad + unidad para poder escalar correctamente:
 *   rendimientoCantidad = 20
 *   rendimientoUnidad = "porciones"
 *
 * El escalado (Bloque 4C) usa MotorEscalado.factorPorPersonas() con estos valores.
 */
data class Receta(
    val id: String = UUID.randomUUID().toString(),
    val nombre: String,
    val tipo: String,
    val rendimientoCantidad: Int,
    val rendimientoUnidad: String,
    val ingredientes: List<IngredienteDeReceta> = emptyList(),
    val notas: String? = null
) {
    /** Texto humano listo para mostrar: "20 porciones" */
    val rendimientoTexto: String
        get() = "$rendimientoCantidad $rendimientoUnidad"
}
