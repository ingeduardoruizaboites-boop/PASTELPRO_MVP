package com.pastelpro.engine

import java.math.BigDecimal
import java.math.RoundingMode

/**
 * Motor puro de mano de obra.
 *
 * §29 del Maestro:
 * - costo = horas × valor_hora
 * - Desglose opcional: preparación, horneado/control, decoración, limpieza
 *
 * Dinero con BigDecimal, escala final 2.
 */
object MotorManoObra {

    class ManoObraInvalida(mensaje: String) : IllegalArgumentException(mensaje)

    private const val PRECISION_INTERNA = 6
    private const val PRECISION_FINAL = 2

    /**
     * Desglose de horas por fase. Todas opcionales — se suman las que existan.
     */
    data class DesgloseHoras(
        val preparacion: BigDecimal = BigDecimal.ZERO,
        val horneado: BigDecimal = BigDecimal.ZERO,
        val decoracion: BigDecimal = BigDecimal.ZERO,
        val limpieza: BigDecimal = BigDecimal.ZERO
    ) {
        val total: BigDecimal
            get() = preparacion.add(horneado).add(decoracion).add(limpieza)
    }

    /**
     * Calcula el costo total de mano de obra simple.
     *
     * @param horas total de horas trabajadas
     * @param valorHora tarifa por hora (ej. 80.00)
     * @return costo total, escala 2
     */
    fun calcular(horas: BigDecimal, valorHora: BigDecimal): BigDecimal {
        validar(horas, valorHora)
        return horas.multiply(valorHora)
            .setScale(PRECISION_FINAL, RoundingMode.HALF_UP)
    }

    /**
     * Calcula el costo a partir de un desglose de horas.
     * Útil para pantallas que muestran el detalle al usuario (§29).
     */
    fun calcularConDesglose(desglose: DesgloseHoras, valorHora: BigDecimal): BigDecimal {
        if (valorHora < BigDecimal.ZERO) {
            throw ManoObraInvalida("El valor por hora no puede ser negativo: $valorHora")
        }
        return calcular(desglose.total, valorHora)
    }

    /**
     * Calcula la ganancia neta por hora.
     * Regla §29: "También mostrar ganancia real por hora cuando se conozca el precio de venta."
     *
     * gananciaPorHora = (precioVenta - costoTotal) / horasTotales
     */
    fun gananciaPorHora(
        precioVenta: BigDecimal,
        costoTotal: BigDecimal,
        horasTotales: BigDecimal
    ): BigDecimal {
        if (horasTotales <= BigDecimal.ZERO) {
            throw ManoObraInvalida("Las horas totales deben ser > 0: $horasTotales")
        }
        val ganancia = precioVenta.subtract(costoTotal)
        return ganancia.divide(horasTotales, PRECISION_INTERNA, RoundingMode.HALF_UP)
            .setScale(PRECISION_FINAL, RoundingMode.HALF_UP)
    }

    private fun validar(horas: BigDecimal, valorHora: BigDecimal) {
        if (horas < BigDecimal.ZERO) {
            throw ManoObraInvalida("Las horas no pueden ser negativas: $horas")
        }
        if (valorHora < BigDecimal.ZERO) {
            throw ManoObraInvalida("El valor por hora no puede ser negativo: $valorHora")
        }
    }
}
