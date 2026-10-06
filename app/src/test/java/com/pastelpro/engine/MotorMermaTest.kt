package com.pastelpro.engine

import org.junit.Assert.assertEquals
import org.junit.Test
import java.math.BigDecimal

class MotorMermaTest {

    private fun assertBD(esperado: String, real: BigDecimal) {
        assertEquals("Esperado <$esperado> pero fue <$real>", 0, BigDecimal(esperado).compareTo(real))
    }

    // ══════════════════════════════════════════════
    // Caso documentado (§31 Maestro: 5%)
    // ══════════════════════════════════════════════

    @Test
    fun `costo 428 punto 50 merma 5 por ciento da total 449 punto 93`() {
        val r = MotorMerma.aplicar(BigDecimal("428.50"), BigDecimal("5"))
        assertBD("428.50", r.costoAntes)
        assertBD("21.43", r.costoMerma)
        assertBD("449.93", r.costoTotal)
    }

    @Test
    fun `costo 100 merma 0 da total 100 sin merma`() {
        val r = MotorMerma.aplicar(BigDecimal("100.00"), BigDecimal.ZERO)
        assertBD("100.00", r.costoAntes)
        assertBD("0.00", r.costoMerma)
        assertBD("100.00", r.costoTotal)
    }

    @Test
    fun `costo 100 merma 10 da total 110 con 10 de merma`() {
        val r = MotorMerma.aplicar(BigDecimal("100.00"), BigDecimal("10"))
        assertBD("100.00", r.costoAntes)
        assertBD("10.00", r.costoMerma)
        assertBD("110.00", r.costoTotal)
    }

    @Test
    fun `costo 200 merma 2 punto 5 da total 205`() {
        val r = MotorMerma.aplicar(BigDecimal("200.00"), BigDecimal("2.5"))
        assertBD("200.00", r.costoAntes)
        assertBD("5.00", r.costoMerma)
        assertBD("205.00", r.costoTotal)
    }

    @Test
    fun `costo cero merma 10 da total 0`() {
        val r = MotorMerma.aplicar(BigDecimal.ZERO, BigDecimal("10"))
        assertBD("0.00", r.costoAntes)
        assertBD("0.00", r.costoMerma)
        assertBD("0.00", r.costoTotal)
    }

    // ══════════════════════════════════════════════
    // Errores
    // ══════════════════════════════════════════════

    @Test(expected = MotorMerma.MermaInvalida::class)
    fun `costo negativo lanza error`() {
        MotorMerma.aplicar(BigDecimal("-100"), BigDecimal("5"))
    }

    @Test(expected = MotorMerma.MermaInvalida::class)
    fun `merma negativa lanza error`() {
        MotorMerma.aplicar(BigDecimal("100"), BigDecimal("-5"))
    }

    @Test(expected = MotorMerma.MermaInvalida::class)
    fun `merma 100 por ciento lanza error`() {
        MotorMerma.aplicar(BigDecimal("100"), BigDecimal("100"))
    }

    @Test(expected = MotorMerma.MermaInvalida::class)
    fun `merma mayor a 100 lanza error`() {
        MotorMerma.aplicar(BigDecimal("100"), BigDecimal("150"))
    }
}
