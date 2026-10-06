package com.pastelpro.engine

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.math.BigDecimal

class MotorPrecioTest {

    private fun assertBD(esperado: String, real: BigDecimal) {
        assertEquals("Esperado <$esperado> pero fue <$real>", 0, BigDecimal(esperado).compareTo(real))
    }

    // ══════════════════════════════════════════════
    // ⚠️ LA PRUEBA CLAVE: margen ≠ markup
    // ══════════════════════════════════════════════

    @Test
    fun `margen 40 por ciento sobre costo 100 da precio 166 punto 67`() {
        val r = MotorPrecio.calcularPorMargen(BigDecimal("100"), BigDecimal("40"))
        assertBD("166.67", r.precio)
        assertBD("66.67", r.ganancia)
        assertBD("40.00", r.margen)
    }

    @Test
    fun `markup 40 por ciento sobre costo 100 da precio 140`() {
        val r = MotorPrecio.calcularPorMarkup(BigDecimal("100"), BigDecimal("40"))
        assertBD("140.00", r.precio)
        assertBD("40.00", r.ganancia)
        assertBD("28.57", r.margen)  // ← margen real es 28.57, NO 40%
    }

    @Test
    fun `margen 40 y markup 40 dan precios distintos sobre mismo costo`() {
        val porMargen = MotorPrecio.calcularPorMargen(BigDecimal("100"), BigDecimal("40"))
        val porMarkup = MotorPrecio.calcularPorMarkup(BigDecimal("100"), BigDecimal("40"))

        // Precio con margen 40% ($166.67) es MAYOR que con markup 40% ($140).
        // compareTo devuelve +1 cuando el primer valor es mayor.
        assertEquals(
            "Margen debe dar precio mayor que markup",
            1,
            porMargen.precio.compareTo(porMarkup.precio)
        )
        assertBD("166.67", porMargen.precio)
        assertBD("140.00", porMarkup.precio)
    }

    // ══════════════════════════════════════════════
    // Caso documentado (§36 Maestro)
    // ══════════════════════════════════════════════

    @Test
    fun `costo 428 punto 50 margen 42 punto 8 por ciento aproxima precio 749`() {
        // Según el Maestro: costo $428.50, precio $749, margen 42.8%
        val margen = MotorPrecio.calcularMargenReal(BigDecimal("428.50"), BigDecimal("749.00"))
        assertBD("42.79", margen)  // redondeado a 2 decimales
    }

    @Test
    fun `costo 428 punto 50 margen 42 punto 79 por ciento da precio cercano a 749`() {
        val r = MotorPrecio.calcularPorMargen(BigDecimal("428.50"), BigDecimal("42.79"))
        val diferencia = BigDecimal("749.00").subtract(r.precio).abs()
        // Tolerancia de ±$1 por redondeos de margen
        assertTrue(
            "Diferencia esperada ≤1.00 pero fue $diferencia",
            diferencia <= BigDecimal("1.00")
        )
    }

    // ══════════════════════════════════════════════
    // Casos normales
    // ══════════════════════════════════════════════

    @Test
    fun `margen 30 por ciento sobre costo 500 da precio 714 punto 29`() {
        val r = MotorPrecio.calcularPorMargen(BigDecimal("500"), BigDecimal("30"))
        assertBD("714.29", r.precio)
        assertBD("214.29", r.ganancia)
        assertBD("30.00", r.margen)
    }

    @Test
    fun `margen 50 por ciento sobre costo 200 da precio 400`() {
        val r = MotorPrecio.calcularPorMargen(BigDecimal("200"), BigDecimal("50"))
        assertBD("400.00", r.precio)
        assertBD("200.00", r.ganancia)
        assertBD("50.00", r.margen)
    }

    @Test
    fun `margen 0 por ciento da precio igual al costo`() {
        val r = MotorPrecio.calcularPorMargen(BigDecimal("100"), BigDecimal.ZERO)
        assertBD("100.00", r.precio)
        assertBD("0.00", r.ganancia)
        assertBD("0.00", r.margen)
    }

    @Test
    fun `costo 0 con margen 40 da precio 0`() {
        val r = MotorPrecio.calcularPorMargen(BigDecimal.ZERO, BigDecimal("40"))
        assertBD("0.00", r.precio)
        assertBD("0.00", r.ganancia)
    }

    // ══════════════════════════════════════════════
    // Cálculos inversos
    // ══════════════════════════════════════════════

    @Test
    fun `margen real de costo 100 precio 140 es 28 punto 57`() {
        val m = MotorPrecio.calcularMargenReal(BigDecimal("100"), BigDecimal("140"))
        assertBD("28.57", m)
    }

    @Test
    fun `markup real de costo 100 precio 140 es 40`() {
        val m = MotorPrecio.calcularMarkupReal(BigDecimal("100"), BigDecimal("140"))
        assertBD("40.00", m)
    }

    @Test
    fun `margen real de costo 100 precio 166 punto 67 es 40`() {
        val m = MotorPrecio.calcularMargenReal(BigDecimal("100"), BigDecimal("166.67"))
        assertBD("40.00", m)
    }

    // ══════════════════════════════════════════════
    // Tres precios sugeridos
    // ══════════════════════════════════════════════

    @Test
    fun `tres precios sobre costo 500 dan minimo recomendado y premium`() {
        val t = MotorPrecio.calcularTresPrecios(BigDecimal("500"))
        // mínimo 20%  → 500 / 0.80 = 625.00
        assertBD("625.00", t.minimo.precio)
        // recomendado 40% → 500 / 0.60 = 833.33
        assertBD("833.33", t.recomendado.precio)
        // premium 60% → 500 / 0.40 = 1250.00
        assertBD("1250.00", t.premium.precio)
    }

    @Test
    fun `tres precios con margenes personalizados respetan los valores`() {
        val t = MotorPrecio.calcularTresPrecios(
            costo = BigDecimal("1000"),
            margenMinimo = BigDecimal("15"),
            margenRecomendado = BigDecimal("35"),
            margenPremium = BigDecimal("55")
        )
        // mínimo 15% → 1000 / 0.85 = 1176.47
        assertBD("1176.47", t.minimo.precio)
        // recomendado 35% → 1000 / 0.65 = 1538.46
        assertBD("1538.46", t.recomendado.precio)
        // premium 55% → 1000 / 0.45 = 2222.22
        assertBD("2222.22", t.premium.precio)
    }

    // ══════════════════════════════════════════════
    // Errores controlados
    // ══════════════════════════════════════════════

    @Test(expected = MotorPrecio.PrecioInvalido::class)
    fun `margen 100 por ciento lanza error`() {
        MotorPrecio.calcularPorMargen(BigDecimal("100"), BigDecimal("100"))
    }

    @Test(expected = MotorPrecio.PrecioInvalido::class)
    fun `margen mayor a 100 lanza error`() {
        MotorPrecio.calcularPorMargen(BigDecimal("100"), BigDecimal("120"))
    }

    @Test(expected = MotorPrecio.PrecioInvalido::class)
    fun `costo negativo lanza error`() {
        MotorPrecio.calcularPorMargen(BigDecimal("-10"), BigDecimal("40"))
    }

    @Test(expected = MotorPrecio.PrecioInvalido::class)
    fun `margen negativo lanza error`() {
        MotorPrecio.calcularPorMargen(BigDecimal("100"), BigDecimal("-5"))
    }

    @Test(expected = MotorPrecio.PrecioInvalido::class)
    fun `margen real con precio cero lanza error`() {
        MotorPrecio.calcularMargenReal(BigDecimal("100"), BigDecimal.ZERO)
    }

    @Test(expected = MotorPrecio.PrecioInvalido::class)
    fun `markup real con costo cero lanza error`() {
        MotorPrecio.calcularMarkupReal(BigDecimal.ZERO, BigDecimal("100"))
    }
}
