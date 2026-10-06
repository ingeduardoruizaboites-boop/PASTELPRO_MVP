package com.pastelpro.engine

import java.math.BigDecimal
import java.math.RoundingMode

/**
 * Motor de precios (§35 Maestro, §6 Prompt de Continuidad).
 *
 * REGLA CRÍTICA: margen ≠ markup. Nunca confundirlos.
 *
 *   Markup:  precio = costo × (1 + markup/100)
 *   Margen:  precio = costo / (1 − margen/100)
 *
 * Ejemplo con costo $100:
 *   - Markup 40%  → precio $140.00 → margen real 28.57%
 *   - Margen 40%  → precio $166.67 → margen real 40.00%
 *
 * La UI debe hablar en MARGEN porque es lo que la usuaria espera
 * cuando dice "quiero ganar 40%".
 */
object MotorPrecio {

    class PrecioInvalido(mensaje: String) : IllegalArgumentException(mensaje)

    private const val CIEN = 100
    private const val PRECISION_INTERNA = 10
    private const val PRECISION_FINAL = 2

    /**
     * Resultado completo del cálculo de precio.
     * La pantalla de resultado (§36) muestra: costo, precio, ganancia $, margen %.
     */
    data class Resultado(
        val costo: BigDecimal,
        val precio: BigDecimal,
        val ganancia: BigDecimal,     // precio − costo
        val margen: BigDecimal        // ganancia / precio × 100
    )

    /**
     * Calcula el precio aplicando un MARGEN sobre el precio final.
     *
     * Fórmula: precio = costo / (1 − margen/100)
     *
     * @param costo costo total del pastel
     * @param margenPorcentaje margen deseado (0 a <100)
     * @return Resultado con precio, ganancia y margen real
     */
    fun calcularPorMargen(costo: BigDecimal, margenPorcentaje: BigDecimal): Resultado {
        validar(costo, margenPorcentaje, "margen")

        val margenDecimal = margenPorcentaje.divide(
            BigDecimal(CIEN), PRECISION_INTERNA, RoundingMode.HALF_UP
        )
        val divisor = BigDecimal.ONE.subtract(margenDecimal)

        val precio = costo.divide(divisor, PRECISION_INTERNA, RoundingMode.HALF_UP)
            .setScale(PRECISION_FINAL, RoundingMode.HALF_UP)

        return resultado(costo, precio)
    }

    /**
     * Calcula el precio aplicando un MARKUP sobre el costo.
     *
     * Fórmula: precio = costo × (1 + markup/100)
     *
     * @param costo costo total del pastel
     * @param markupPorcentaje markup deseado (≥ 0)
     */
    fun calcularPorMarkup(costo: BigDecimal, markupPorcentaje: BigDecimal): Resultado {
        validar(costo, markupPorcentaje, "markup")

        val factor = BigDecimal.ONE.add(
            markupPorcentaje.divide(BigDecimal(CIEN), PRECISION_INTERNA, RoundingMode.HALF_UP)
        )
        val precio = costo.multiply(factor)
            .setScale(PRECISION_FINAL, RoundingMode.HALF_UP)

        return resultado(costo, precio)
    }

    /**
     * Calcula el margen real (%) a partir de un costo y un precio dados.
     * Útil para verificar cuánto margen tiene un precio que ya está fijado.
     *
     * Fórmula: margen = (precio − costo) / precio × 100
     */
    fun calcularMargenReal(costo: BigDecimal, precio: BigDecimal): BigDecimal {
        if (precio <= BigDecimal.ZERO) {
            throw PrecioInvalido("El precio debe ser > 0: $precio")
        }
        val ganancia = precio.subtract(costo)
        return ganancia.divide(precio, PRECISION_INTERNA, RoundingMode.HALF_UP)
            .multiply(BigDecimal(CIEN))
            .setScale(PRECISION_FINAL, RoundingMode.HALF_UP)
    }

    /**
     * Calcula el markup real (%) a partir de un costo y un precio dados.
     *
     * Fórmula: markup = (precio − costo) / costo × 100
     */
    fun calcularMarkupReal(costo: BigDecimal, precio: BigDecimal): BigDecimal {
        if (costo <= BigDecimal.ZERO) {
            throw PrecioInvalido("El costo debe ser > 0: $costo")
        }
        val ganancia = precio.subtract(costo)
        return ganancia.divide(costo, PRECISION_INTERNA, RoundingMode.HALF_UP)
            .multiply(BigDecimal(CIEN))
            .setScale(PRECISION_FINAL, RoundingMode.HALF_UP)
    }

    /**
     * Genera los 3 precios sugeridos: mínimo, recomendado, premium.
     * Los márgenes son configurables; los defaults son:
     *   mínimo=20%, recomendado=40%, premium=60%.
     *
     * La UI puede mostrarlos y la usuaria elige cuál usar.
     */
    fun calcularTresPrecios(
        costo: BigDecimal,
        margenMinimo: BigDecimal = BigDecimal("20"),
        margenRecomendado: BigDecimal = BigDecimal("40"),
        margenPremium: BigDecimal = BigDecimal("60")
    ): TresPrecios {
        return TresPrecios(
            minimo = calcularPorMargen(costo, margenMinimo),
            recomendado = calcularPorMargen(costo, margenRecomendado),
            premium = calcularPorMargen(costo, margenPremium)
        )
    }

    data class TresPrecios(
        val minimo: Resultado,
        val recomendado: Resultado,
        val premium: Resultado
    )

    // ─── Internos ───

    private fun resultado(costo: BigDecimal, precio: BigDecimal): Resultado {
        val ganancia = precio.subtract(costo).setScale(PRECISION_FINAL, RoundingMode.HALF_UP)

        // Caso borde: costo=0 → precio=0 → margen no está definido (0/0). Devolvemos 0.
        // Sin esta rama, calcularMargenReal lanzaría por precio=0.
        val margen = if (precio.compareTo(BigDecimal.ZERO) == 0) {
            BigDecimal.ZERO.setScale(PRECISION_FINAL, RoundingMode.HALF_UP)
        } else {
            calcularMargenReal(costo, precio)
        }

        return Resultado(costo = costo, precio = precio, ganancia = ganancia, margen = margen)
    }

    private fun validar(costo: BigDecimal, porcentaje: BigDecimal, nombre: String) {
        if (costo < BigDecimal.ZERO) {
            throw PrecioInvalido("El costo no puede ser negativo: $costo")
        }
        if (porcentaje < BigDecimal.ZERO) {
            throw PrecioInvalido("El $nombre no puede ser negativo: $porcentaje")
        }
        if (nombre == "margen" && porcentaje >= BigDecimal(CIEN)) {
            throw PrecioInvalido(
                "El margen debe ser < 100%: $porcentaje. " +
                    "Un margen de 100% implica precio infinito."
            )
        }
    }
}
