package com.pastelpro.domain.model

import java.util.UUID

data class Receta(
    val id: String = UUID.randomUUID().toString(),
    val nombre: String,
    val tipo: String,
    val rendimientoCantidad: Int,
    val rendimientoUnidad: String,
    val ingredientes: List<IngredienteDeReceta> = emptyList(),
    val notas: String? = null,
    // Campos de molde (opcionales)
    val moldeForma: String? = null,        // "Redondo", "Cuadrado", "Rectangular", "Corazón"
    val moldeAnchoCm: Int? = null,
    val moldeLargoCm: Int? = null,
    val moldeAltoCm: Int? = null,
    val porcionesPorMolde: Int? = null     // calculado o manual
) {
    val rendimientoTexto: String
        get() = "$rendimientoCantidad $rendimientoUnidad"

    /**
     * Descripción del molde si está definido, ej. "Cuadrado 20×20×8 cm".
     */
    val moldeTexto: String?
        get() {
            val forma = moldeForma ?: return null
            val ancho = moldeAnchoCm ?: return null
            val alto = moldeAltoCm ?: return null
            return when (forma) {
                "Rectangular" -> {
                    val largo = moldeLargoCm ?: return null
                    "$forma ${ancho}×${largo}×${alto} cm"
                }
                "Redondo" -> "$forma ${ancho} cm × ${alto} cm alto"
                else -> "$forma ${ancho}×${ancho}×${alto} cm"
            }
        }

    val tieneMolde: Boolean
        get() = moldeForma != null && moldeAnchoCm != null && moldeAltoCm != null
}
