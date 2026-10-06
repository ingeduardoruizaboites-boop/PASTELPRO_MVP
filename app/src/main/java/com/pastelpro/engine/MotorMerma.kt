package com.pastelpro.engine

import java.math.BigDecimal
import java.math.RoundingMode

/**
 * Motor de merma (§31 Maestro).
 *
 * Regla: mostrar los 3 valores siempre.
 *   1. Costo antes de merma
 *   2. Costo de merma (diferencia)
 *   3. Costo total (con merma aplicada)
 *
 * Fórmula:
 *   costoTotal = costoBase × (1 + mermaPorcentaje/100)
 *   costoMerma = costoTotal − costoBase
 *
 * Ejemplos:
 *   costoBase=428.50, merma=5%   → merma=21.43, total=449.93
 *   costoBase=100.00, merma=0%   → merma=0,     total=100.00
 *   costoBase=100.00, merma=10%  → merma=10.00, total=110.00
 */
object MotorMerma {

    class MermaInvalida(mensaje: String) : IllegalArgumentException(mensaje)

    private const val CIEN = 100
    private const val PRECISION_INTERNA = 10
    private const val PRECISION_FINAL = 2

    /**
     * Resultado completo del cálculo de merma.
     * La UI puede mostrar los 3 valores tal como pide el Maestro §31.
     */
    data class Resultado(
        val costoAntes: BigDecimal,
        val costoMerma: BigDecimal,
        val costoTotal: BigDecimal
    )

    /**
     * Aplica merma a un costo.
     *
     * @param costoBase costo sin merma
     * @param mermaPorcentaje porcentaje de merma (0 a <100)
     * @return Resultado con los 3 valores
     */
    fun aplicar(costoBase: BigDecimal, mermaPorcentaje: BigDecimal): Resultado {
        if (costoBase < BigDecimal.ZERO) {
            throw MermaInvalida("El costo base no puede ser negativo: $costoBase")
        }
        if (mermaPorcentaje < BigDecimal.ZERO) {
            throw MermaInvalida("El porcentaje de merma no puede ser negativo: $mermaPorcentaje")
        }
        if (mermaPorcentaje >= BigDecimal(CIEN)) {
            throw MermaInvalida(
                "El porcentaje de merma debe ser menor a 100: $mermaPorcentaje. " +
                    "Un valor ≥100% implica que no queda producto vendible."
            )
        }

        val factor = BigDecimal.ONE.add(
            mermaPorcentaje.divide(BigDecimal(CIEN), PRECISION_INTERNA, RoundingMode.HALF_UP)
        )

        val total = costoBase.multiply(factor)
            .setScale(PRECISION_FINAL, RoundingMode.HALF_UP)

        val merma = total.subtract(costoBase)
            .setScale(PRECISION_FINAL, RoundingMode.HALF_UP)

        return Resultado(
            costoAntes = costoBase.setScale(PRECISION_FINAL, RoundingMode.HALF_UP),
            costoMerma = merma,
            costoTotal = total
        )
    }
}
