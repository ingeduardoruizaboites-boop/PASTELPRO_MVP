package com.pastelpro.engine

import java.math.BigDecimal
import java.math.RoundingMode

/**
 * Motor puro de conversión de unidades.
 *
 * Reglas (§18 del Maestro):
 * - Solo se convierte dentro de la misma categoría (masa↔masa, volumen↔volumen, conteo↔conteo).
 * - Dinero/cantidades con precisión interna alta; el redondeo se aplica al presentar.
 *
 * NO depende de Android ni de la UI.
 */
object MotorUnidades {

    /** Excepción controlada cuando se intenta una conversión incompatible. */
    class ConversionInvalida(mensaje: String) : IllegalArgumentException(mensaje)

    /**
     * Convierte [cantidad] desde [origen] hasta [destino].
     *
     * Fórmula:
     *   cantidadBase = cantidad * origen.factorBase
     *   resultado    = cantidadBase / destino.factorBase
     *
     * @return BigDecimal con escala 6 (precisión interna, no presentación).
     */
    fun convertir(cantidad: BigDecimal, origen: Unidad, destino: Unidad): BigDecimal {
        if (origen.categoria != destino.categoria) {
            throw ConversionInvalida(
                "No se puede convertir '${origen.simbolo}' a '${destino.simbolo}': " +
                    "categorías distintas (${origen.categoria} vs ${destino.categoria})."
            )
        }
        if (origen == destino) {
            return cantidad
        }
        val cantidadBase = cantidad.multiply(origen.factorBase)
        return cantidadBase.divide(destino.factorBase, 6, RoundingMode.HALF_UP)
    }
}
