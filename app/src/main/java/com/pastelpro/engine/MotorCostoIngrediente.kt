package com.pastelpro.engine

import com.pastelpro.domain.model.Ingrediente
import java.math.BigDecimal
import java.math.RoundingMode

/**
 * Calcula el costo de usar una cantidad específica de un ingrediente.
 *
 * Ejemplo:
 *   Harina: 1 kg comprado a $28.
 *   Usar 250 g → costo = $28 * (250 g / 1 kg) = $28 * 0.25 = $7.00
 *
 * Reglas (§7 del Prompt de Continuidad):
 * - Dinero con BigDecimal, escala final 2.
 * - Conversiones validadas por MotorUnidades.
 * - Nunca ocultar supuestos: aquí NO se aplica merma ni overhead — eso es en el siguiente nivel.
 */
object MotorCostoIngrediente {

    class CostoInvalido(mensaje: String) : IllegalArgumentException(mensaje)

    /**
     * @param ingrediente el ingrediente con su presentación de compra y precio
     * @param cantidadUsada cantidad usada en la receta (ej. 250)
     * @param unidadUsada unidad de la cantidad usada (ej. "g")
     * @return costo en la moneda del ingrediente, escala 2 (ej. 7.00)
     */
    fun calcular(
        ingrediente: Ingrediente,
        cantidadUsada: BigDecimal,
        unidadUsada: String
    ): BigDecimal {
        if (cantidadUsada < BigDecimal.ZERO) {
            throw CostoInvalido("La cantidad usada no puede ser negativa: $cantidadUsada")
        }
        if (ingrediente.cantidadCompra <= BigDecimal.ZERO) {
            throw CostoInvalido(
                "La cantidad de compra debe ser mayor a cero (ingrediente: ${ingrediente.nombre})"
            )
        }
        if (ingrediente.precioCompra < BigDecimal.ZERO) {
            throw CostoInvalido(
                "El precio de compra no puede ser negativo (ingrediente: ${ingrediente.nombre})"
            )
        }

        val unidadCompra = Unidad.desdeSimbolo(ingrediente.unidadCompra)
            ?: throw CostoInvalido("Unidad de compra desconocida: '${ingrediente.unidadCompra}'")
        val unidadUso = Unidad.desdeSimbolo(unidadUsada)
            ?: throw CostoInvalido("Unidad de uso desconocida: '$unidadUsada'")

        // Convertir la cantidad usada a la unidad en que se compró el ingrediente.
        val cantidadEnUnidadCompra = MotorUnidades.convertir(cantidadUsada, unidadUso, unidadCompra)

        // Proporción de lo que se usa respecto a lo que se compró.
        val proporcion = cantidadEnUnidadCompra.divide(
            ingrediente.cantidadCompra,
            10,
            RoundingMode.HALF_UP
        )

        return ingrediente.precioCompra
            .multiply(proporcion)
            .setScale(2, RoundingMode.HALF_UP)
    }
}
