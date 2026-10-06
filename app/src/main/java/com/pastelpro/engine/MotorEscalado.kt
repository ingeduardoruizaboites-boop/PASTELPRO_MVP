package com.pastelpro.engine

import java.math.BigDecimal
import java.math.RoundingMode

/**
 * Motor puro de escalado de recetas.
 *
 * Casos de uso reales (feedback de campo Sesión 2):
 * - Receta base para 8 personas → pedido para 100 personas → factor 12.5
 * - Receta base para 8 piezas → pedido para 3 piezas → factor 0.375
 *
 * Reglas:
 * - Dinero y cantidades con BigDecimal (nunca Float/Double).
 * - Precisión interna 6 decimales; el redondeo de presentación se aplica aparte.
 * - Unidades discretas (pieza, docena, unidad, paquete, caja) redondean CEILING al presentar.
 * - Unidades fraccionables (g, kg, ml, L) redondean a 2 decimales HALF_UP.
 */
object MotorEscalado {

    class EscaladoInvalido(mensaje: String) : IllegalArgumentException(mensaje)

    private const val PRECISION_INTERNA = 6
    private const val PRECISION_FRACCIONABLE = 2

    /**
     * Factor de escalado basado en cambio de personas.
     * Ejemplo: 8 personas → 100 personas → 12.500000
     */
    fun factorPorPersonas(personasBase: Int, personasNuevas: Int): BigDecimal {
        if (personasBase <= 0) throw EscaladoInvalido("Las personas base deben ser > 0: $personasBase")
        if (personasNuevas <= 0) throw EscaladoInvalido("Las personas nuevas deben ser > 0: $personasNuevas")
        return BigDecimal(personasNuevas).divide(
            BigDecimal(personasBase),
            PRECISION_INTERNA,
            RoundingMode.HALF_UP
        )
    }

    /**
     * Factor de escalado basado en cambio de piezas.
     * Ejemplo: 8 piezas → 3 piezas → 0.375000
     */
    fun factorPorPiezas(piezasBase: Int, piezasNuevas: Int): BigDecimal {
        if (piezasBase <= 0) throw EscaladoInvalido("Las piezas base deben ser > 0: $piezasBase")
        if (piezasNuevas <= 0) throw EscaladoInvalido("Las piezas nuevas deben ser > 0: $piezasNuevas")
        return BigDecimal(piezasNuevas).divide(
            BigDecimal(piezasBase),
            PRECISION_INTERNA,
            RoundingMode.HALF_UP
        )
    }

    /**
     * Escala una cantidad por un factor.
     * Devuelve el resultado con precisión interna (6 decimales), sin redondeo de presentación.
     */
    fun escalar(cantidad: BigDecimal, factor: BigDecimal): BigDecimal {
        if (cantidad < BigDecimal.ZERO) {
            throw EscaladoInvalido("La cantidad no puede ser negativa: $cantidad")
        }
        if (factor <= BigDecimal.ZERO) {
            throw EscaladoInvalido("El factor debe ser > 0: $factor")
        }
        return cantidad.multiply(factor).setScale(PRECISION_INTERNA, RoundingMode.HALF_UP)
    }

    /**
     * Aplica el redondeo de presentación según el tipo de unidad.
     *
     * - Fraccionables (g, kg, ml, L): 2 decimales, HALF_UP.
     * - Discretas (pieza, docena, unidad, paquete, caja): CEILING al entero siguiente.
     *
     * Ejemplos:
     *   redondearUsable(3125, GRAMO)   → 3125.00
     *   redondearUsable(3.125, KILOGRAMO) → 3.13
     *   redondearUsable(37.5, PIEZA)   → 38
     *   redondearUsable(1.5, PIEZA)    → 2
     */
    fun redondearUsable(cantidad: BigDecimal, unidad: Unidad): BigDecimal {
        return when (unidad.categoria) {
            CategoriaUnidad.MASA, CategoriaUnidad.VOLUMEN ->
                cantidad.setScale(PRECISION_FRACCIONABLE, RoundingMode.HALF_UP)

            CategoriaUnidad.CONTEO ->
                cantidad.setScale(0, RoundingMode.CEILING)
        }
    }

    /**
     * Verifica si una unidad es discreta (se cuenta) o fraccionable (se pesa/mide).
     */
    fun esDiscreta(unidad: Unidad): Boolean =
        unidad.categoria == CategoriaUnidad.CONTEO
}
