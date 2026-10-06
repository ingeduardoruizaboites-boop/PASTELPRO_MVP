package com.pastelpro.engine

import org.junit.Assert.assertEquals
import org.junit.Test
import java.math.BigDecimal

class MotorEnergiaTest {

    private fun assertBD(esperado: String, real: BigDecimal) {
        assertEquals("Esperado <$esperado> pero fue <$real>", 0, BigDecimal(esperado).compareTo(real))
    }

    // ══════════════════════════════════════════════
    // Caso documentado (§30 Maestro)
    // ══════════════════════════════════════════════

    @Test
    fun `electricidad 900 gas 500 produccion 30 da 46 punto 67`() {
        val r = MotorEnergia.calcularPorPastel(
            gastoMensualElectricidad = BigDecimal("900"),
            gastoMensualGas = BigDecimal("500"),
            produccionMensualPasteles = 30
        )
        assertBD("46.67", r)
    }

    @Test
    fun `electricidad 500 gas 300 produccion 20 da 40`() {
        val r = MotorEnergia.calcularPorPastel(
            gastoMensualElectricidad = BigDecimal("500"),
            gastoMensualGas = BigDecimal("300"),
            produccionMensualPasteles = 20
        )
        assertBD("40.00", r)
    }

    @Test
    fun `solo electricidad 300 produccion 10 da 30`() {
        val r = MotorEnergia.calcularPorPastel(
            gastoMensualElectricidad = BigDecimal("300"),
            gastoMensualGas = BigDecimal.ZERO,
            produccionMensualPasteles = 10
        )
        assertBD("30.00", r)
    }

    // ══════════════════════════════════════════════
    // Múltiples pasteles
    // ══════════════════════════════════════════════

    @Test
    fun `3 pasteles por pedido a 46 punto 67 da 140 punto 01`() {
        val r = MotorEnergia.calcularPorPedido(BigDecimal("46.67"), 3)
        assertBD("140.01", r)
    }

    // ══════════════════════════════════════════════
    // Errores
    // ══════════════════════════════════════════════

    @Test(expected = MotorEnergia.EnergiaInvalida::class)
    fun `electricidad negativa lanza error`() {
        MotorEnergia.calcularPorPastel(BigDecimal("-100"), BigDecimal("500"), 30)
    }

    @Test(expected = MotorEnergia.EnergiaInvalida::class)
    fun `produccion cero lanza error`() {
        MotorEnergia.calcularPorPastel(BigDecimal("900"), BigDecimal("500"), 0)
    }

    @Test(expected = MotorEnergia.EnergiaInvalida::class)
    fun `numero de pasteles cero en pedido lanza error`() {
        MotorEnergia.calcularPorPedido(BigDecimal("46.67"), 0)
    }
}
