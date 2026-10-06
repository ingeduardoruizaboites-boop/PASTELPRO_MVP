package com.pastelpro.engine

import java.math.BigDecimal
import java.math.RoundingMode

/**
 * Motor de energía — MVP con método simple de prorrateo (§30 Maestro).
 *
 * Método:
 *   costoPorPastel = (gastoMensualElectricidad + gastoMensualGas) / produccionMensual
 *
 * ADVERTENCIA: este método es una ESTIMACIÓN.
 * La UI debe presentarlo como tal. V2 tendrá método avanzado por consumo real.
 */
object MotorEnergia {

    class EnergiaInvalida(mensaje: String) : IllegalArgumentException(mensaje)

    private const val PRECISION_INTERNA = 6
    private const val PRECISION_FINAL = 2

    /**
     * Calcula el costo estimado de energía por pastel.
     *
     * @param gastoMensualElectricidad en moneda local
     * @param gastoMensualGas en moneda local
     * @param produccionMensualPasteles número de pasteles producidos en un mes
     * @return costo estimado por pastel, escala 2
     */
    fun calcularPorPastel(
        gastoMensualElectricidad: BigDecimal,
        gastoMensualGas: BigDecimal,
        produccionMensualPasteles: Int
    ): BigDecimal {
        if (gastoMensualElectricidad < BigDecimal.ZERO) {
            throw EnergiaInvalida("Gasto de electricidad no puede ser negativo: $gastoMensualElectricidad")
        }
        if (gastoMensualGas < BigDecimal.ZERO) {
            throw EnergiaInvalida("Gasto de gas no puede ser negativo: $gastoMensualGas")
        }
        if (produccionMensualPasteles <= 0) {
            throw EnergiaInvalida("La producción mensual debe ser > 0: $produccionMensualPasteles")
        }

        val gastoTotalMensual = gastoMensualElectricidad.add(gastoMensualGas)

        return gastoTotalMensual
            .divide(BigDecimal(produccionMensualPasteles), PRECISION_INTERNA, RoundingMode.HALF_UP)
            .setScale(PRECISION_FINAL, RoundingMode.HALF_UP)
    }

    /**
     * Costo total de energía para un pedido con múltiples pasteles.
     * Útil cuando se cotiza un pedido de "100 personas = 3 pasteles".
     */
    fun calcularPorPedido(costoPorPastel: BigDecimal, numeroPasteles: Int): BigDecimal {
        if (numeroPasteles <= 0) {
            throw EnergiaInvalida("El número de pasteles debe ser > 0: $numeroPasteles")
        }
        return costoPorPastel
            .multiply(BigDecimal(numeroPasteles))
            .setScale(PRECISION_FINAL, RoundingMode.HALF_UP)
    }
}
