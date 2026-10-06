package com.pastelpro.engine

import org.junit.Assert.assertEquals
import org.junit.Test
import java.math.BigDecimal

class MotorManoObraTest {

    private fun assertBD(esperado: String, real: BigDecimal) {
        assertEquals("Esperado <$esperado> pero fue <$real>", 0, BigDecimal(esperado).compareTo(real))
    }

    // ══════════════════════════════════════════════
    // Cálculo simple
    // ══════════════════════════════════════════════

    @Test
    fun `4 punto 5 horas a 80 por hora da 360`() {
        val r = MotorManoObra.calcular(BigDecimal("4.5"), BigDecimal("80"))
        assertBD("360.00", r)
    }

    @Test
    fun `1 hora a 100 por hora da 100`() {
        val r = MotorManoObra.calcular(BigDecimal("1"), BigDecimal("100"))
        assertBD("100.00", r)
    }

    @Test
    fun `0 horas a 80 por hora da 0`() {
        val r = MotorManoObra.calcular(BigDecimal("0"), BigDecimal("80"))
        assertBD("0.00", r)
    }

    // ══════════════════════════════════════════════
    // Desglose (§29 Maestro)
    // ══════════════════════════════════════════════

    @Test
    fun `desglose tipico 1 mas 1 mas 2 mas 0 punto 5 por 80 da 360`() {
        val d = MotorManoObra.DesgloseHoras(
            preparacion = BigDecimal("1"),
            horneado = BigDecimal("1"),
            decoracion = BigDecimal("2"),
            limpieza = BigDecimal("0.5")
        )
        val r = MotorManoObra.calcularConDesglose(d, BigDecimal("80"))
        assertBD("360.00", r)
    }

    @Test
    fun `desglose suma correctamente el total de horas`() {
        val d = MotorManoObra.DesgloseHoras(
            preparacion = BigDecimal("1.5"),
            horneado = BigDecimal("0.75"),
            decoracion = BigDecimal("2.25"),
            limpieza = BigDecimal("0.5")
        )
        assertBD("5", d.total)
    }

    // ══════════════════════════════════════════════
    // Ganancia por hora
    // ══════════════════════════════════════════════

    @Test
    fun `ganancia por hora precio 750 costo 500 horas 5 da 50`() {
        val r = MotorManoObra.gananciaPorHora(
            precioVenta = BigDecimal("750"),
            costoTotal = BigDecimal("500"),
            horasTotales = BigDecimal("5")
        )
        assertBD("50.00", r)
    }

    @Test
    fun `ganancia por hora negativa cuando costo supera precio`() {
        val r = MotorManoObra.gananciaPorHora(
            precioVenta = BigDecimal("400"),
            costoTotal = BigDecimal("500"),
            horasTotales = BigDecimal("5")
        )
        assertBD("-20.00", r)
    }

    // ══════════════════════════════════════════════
    // Errores
    // ══════════════════════════════════════════════

    @Test(expected = MotorManoObra.ManoObraInvalida::class)
    fun `horas negativas lanza error`() {
        MotorManoObra.calcular(BigDecimal("-1"), BigDecimal("80"))
    }

    @Test(expected = MotorManoObra.ManoObraInvalida::class)
    fun `valor hora negativo lanza error`() {
        MotorManoObra.calcular(BigDecimal("1"), BigDecimal("-80"))
    }

    @Test(expected = MotorManoObra.ManoObraInvalida::class)
    fun `horas cero en ganancia por hora lanza error`() {
        MotorManoObra.gananciaPorHora(
            precioVenta = BigDecimal("100"),
            costoTotal = BigDecimal("50"),
            horasTotales = BigDecimal.ZERO
        )
    }
}
