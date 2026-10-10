package com.pastelpro.engine

import java.math.BigDecimal
import java.math.RoundingMode

/**
 * Motor de cálculo de moldes.
 *
 * Fuentes de la tabla de moldes estándar:
 * - Rosiqui Healthy Chef
 * - La Pastelería (blog)
 * - GASDA
 *
 * Reglas:
 * - Volumen geométrico en cm³.
 * - Volumen útil = volumen geométrico × factor de llenado (default 0.8).
 *   Motivo: no se llena el molde al 100% para evitar derrames al hornear.
 * - Porción estándar: 125 cm³ (default configurable).
 *   Rango real: 80 cm³ (porción café) a 200 cm³ (postre principal).
 */
object MotorMoldes {

    class MoldeInvalido(mensaje: String) : IllegalArgumentException(mensaje)

    /** Volumen por porción estándar en cm³. */
    val VOLUMEN_POR_PORCION_DEFAULT = BigDecimal("125")

    /** Factor de llenado: % del volumen geométrico que se llena con mezcla. */
    val FACTOR_LLENADO_DEFAULT = BigDecimal("0.80")

    /** Factor de forma para corazón (aprox 70% de un cuadrado). */
    private val FACTOR_CORAZON = BigDecimal("0.70")

    private const val PRECISION_INTERNA = 4
    private const val PRECISION_VOLUMEN = 2

    /**
     * Dimensiones de un molde.
     * - Redondo: `ancho` = diámetro, `largo` = null.
     * - Cuadrado: `ancho` = lado, `largo` = null.
     * - Rectangular: `ancho` y `largo`.
     * - Corazón: `ancho` = base, `largo` = altura del corazón.
     */
    data class Dimensiones(
        val forma: FormaMolde,
        val anchoCm: Int,
        val largoCm: Int?,
        val altoCm: Int
    ) {
        init {
            if (anchoCm <= 0) throw MoldeInvalido("El ancho debe ser > 0: $anchoCm")
            if (altoCm <= 0) throw MoldeInvalido("El alto debe ser > 0: $altoCm")
            if (forma == FormaMolde.RECTANGULAR && (largoCm == null || largoCm <= 0)) {
                throw MoldeInvalido("Molde rectangular requiere largo > 0")
            }
        }
    }

    /**
     * Volumen geométrico en cm³.
     *
     * Fórmulas:
     * - Redondo:     π × (diámetro/2)² × alto
     * - Cuadrado:    lado × lado × alto
     * - Rectangular: ancho × largo × alto
     * - Corazón:     ancho × largo × alto × 0.70 (aprox forma corazón)
     */
    fun volumenGeometrico(dim: Dimensiones): BigDecimal {
        val alto = BigDecimal(dim.altoCm)

        return when (dim.forma) {
            FormaMolde.REDONDO -> {
                val radio = BigDecimal(dim.anchoCm).divide(BigDecimal(2), PRECISION_INTERNA, RoundingMode.HALF_UP)
                val pi = BigDecimal("3.14159265358979")
                pi.multiply(radio.pow(2)).multiply(alto)
                    .setScale(PRECISION_VOLUMEN, RoundingMode.HALF_UP)
            }
            FormaMolde.CUADRADO -> {
                BigDecimal(dim.anchoCm).pow(2).multiply(alto)
                    .setScale(PRECISION_VOLUMEN, RoundingMode.HALF_UP)
            }
            FormaMolde.RECTANGULAR -> {
                BigDecimal(dim.anchoCm)
                    .multiply(BigDecimal(dim.largoCm!!))
                    .multiply(alto)
                    .setScale(PRECISION_VOLUMEN, RoundingMode.HALF_UP)
            }
            FormaMolde.CORAZON -> {
                val largo = dim.largoCm ?: dim.anchoCm
                BigDecimal(dim.anchoCm)
                    .multiply(BigDecimal(largo))
                    .multiply(alto)
                    .multiply(FACTOR_CORAZON)
                    .setScale(PRECISION_VOLUMEN, RoundingMode.HALF_UP)
            }
        }
    }

    /**
     * Volumen útil = volumenGeometrico × factor de llenado.
     */
    fun volumenUtil(
        dim: Dimensiones,
        factorLlenado: BigDecimal = FACTOR_LLENADO_DEFAULT
    ): BigDecimal {
        require(factorLlenado > BigDecimal.ZERO && factorLlenado <= BigDecimal.ONE) {
            "El factor de llenado debe estar entre 0 y 1: $factorLlenado"
        }
        return volumenGeometrico(dim).multiply(factorLlenado)
            .setScale(PRECISION_VOLUMEN, RoundingMode.HALF_UP)
    }

    /**
     * Porciones sugeridas a partir de un volumen útil.
     * Redondea DOWN (no puede haber 25.6 porciones).
     */
    fun porcionesSugeridas(
        volumenUtil: BigDecimal,
        cm3PorPorcion: BigDecimal = VOLUMEN_POR_PORCION_DEFAULT
    ): Int {
        require(cm3PorPorcion > BigDecimal.ZERO) { "cm3PorPorcion debe ser > 0" }
        return volumenUtil.divide(cm3PorPorcion, 0, RoundingMode.DOWN).toInt()
    }

    /**
     * Atajo: porciones sugeridas desde un molde directamente.
     */
    fun porcionesDesdeMolde(
        dim: Dimensiones,
        cm3PorPorcion: BigDecimal = VOLUMEN_POR_PORCION_DEFAULT,
        factorLlenado: BigDecimal = FACTOR_LLENADO_DEFAULT
    ): Int {
        val util = volumenUtil(dim, factorLlenado)
        return porcionesSugeridas(util, cm3PorPorcion)
    }

    // ═══════════════════════════════════════════════════════════
    // TABLA DE MOLDES ESTÁNDAR (basada en investigación)
    // ═══════════════════════════════════════════════════════════

    data class MoldeEstandar(
        val descripcion: String,
        val dimensiones: Dimensiones,
        val porcionesMin: Int,
        val porcionesMax: Int
    ) {
        val rangoPorciones: String
            get() = if (porcionesMin == porcionesMax) "$porcionesMin"
            else "$porcionesMin-$porcionesMax"
    }

    /**
     * Moldes redondos estándar (alto 7-9 cm).
     * Fuente: Rosiqui Healthy Chef + La Pastelería.
     */
    fun moldesRedondosEstandar(): List<MoldeEstandar> = listOf(
        MoldeEstandar("Redondo 16 cm", Dimensiones(FormaMolde.REDONDO, 16, null, 8), 8, 10),
        MoldeEstandar("Redondo 18 cm", Dimensiones(FormaMolde.REDONDO, 18, null, 8), 12, 14),
        MoldeEstandar("Redondo 20 cm", Dimensiones(FormaMolde.REDONDO, 20, null, 8), 16, 18),
        MoldeEstandar("Redondo 22 cm", Dimensiones(FormaMolde.REDONDO, 22, null, 8), 18, 22),
        MoldeEstandar("Redondo 24 cm", Dimensiones(FormaMolde.REDONDO, 24, null, 8), 25, 28),
        MoldeEstandar("Redondo 26 cm", Dimensiones(FormaMolde.REDONDO, 26, null, 8), 30, 32),
        MoldeEstandar("Redondo 28 cm", Dimensiones(FormaMolde.REDONDO, 28, null, 8), 35, 40),
        MoldeEstandar("Redondo 30 cm", Dimensiones(FormaMolde.REDONDO, 30, null, 8), 45, 50)
    )

    /**
     * Moldes cuadrados estándar.
     */
    fun moldesCuadradosEstandar(): List<MoldeEstandar> = listOf(
        MoldeEstandar("Cuadrado 15×15", Dimensiones(FormaMolde.CUADRADO, 15, null, 8), 12, 14),
        MoldeEstandar("Cuadrado 18×18", Dimensiones(FormaMolde.CUADRADO, 18, null, 8), 18, 20),
        MoldeEstandar("Cuadrado 20×20", Dimensiones(FormaMolde.CUADRADO, 20, null, 8), 22, 24),
        MoldeEstandar("Cuadrado 23×23", Dimensiones(FormaMolde.CUADRADO, 23, null, 8), 25, 30),
        MoldeEstandar("Cuadrado 28×28", Dimensiones(FormaMolde.CUADRADO, 28, null, 8), 35, 38),
        MoldeEstandar("Cuadrado 30×30", Dimensiones(FormaMolde.CUADRADO, 30, null, 8), 40, 42)
    )

    /**
     * Moldes rectangulares estándar.
     */
    fun moldesRectangularesEstandar(): List<MoldeEstandar> = listOf(
        MoldeEstandar("Rectangular 18×28", Dimensiones(FormaMolde.RECTANGULAR, 18, 28, 8), 12, 15),
        MoldeEstandar("Rectangular 23×33", Dimensiones(FormaMolde.RECTANGULAR, 23, 33, 8), 20, 24)
    )

    /**
     * Moldes de corazón estándar (aprox 70% del volumen de un cuadrado).
     */
    fun moldesCorazonEstandar(): List<MoldeEstandar> = listOf(
        MoldeEstandar("Corazón 18×18", Dimensiones(FormaMolde.CORAZON, 18, 18, 8), 8, 10),
        MoldeEstandar("Corazón 22×22", Dimensiones(FormaMolde.CORAZON, 22, 22, 8), 12, 15),
        MoldeEstandar("Corazón 25×25", Dimensiones(FormaMolde.CORAZON, 25, 25, 8), 18, 22)
    )

    /**
     * Devuelve todos los moldes estándar agrupados.
     */
    fun todosLosEstandar(): List<MoldeEstandar> =
        moldesRedondosEstandar() + moldesCuadradosEstandar() + moldesRectangularesEstandar() + moldesCorazonEstandar()

    /**
     * Busca el molde estándar más cercano a las dimensiones dadas.
     * Útil para sugerir "usa un molde redondo 24 cm".
     */
    fun buscarMoldeEstandarMasCercano(dim: Dimensiones): MoldeEstandar? {
        val todos = todosLosEstandar()
        val volumenObjetivo = volumenUtil(dim).toDouble()

        return todos
            .filter { it.dimensiones.forma == dim.forma }
            .minByOrNull { molde ->
                val volMolde = volumenUtil(molde.dimensiones).toDouble()
                kotlin.math.abs(volMolde - volumenObjetivo)
            }
    }
}
