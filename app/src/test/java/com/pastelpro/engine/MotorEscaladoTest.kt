package com.pastelpro.engine

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.math.BigDecimal

class MotorEscaladoTest {

    private fun assertBD(esperado: String, real: BigDecimal) {
        assertEquals(
            "Esperado <$esperado> pero fue <$real>",
            0,
            BigDecimal(esperado).compareTo(real)
        )
    }

    // ══════════════════════════════════════════════
    // Factores
    // ══════════════════════════════════════════════

    @Test
    fun `8 personas a 100 personas da factor 12 punto 5`() {
        val f = MotorEscalado.factorPorPersonas(8, 100)
        assertBD("12.5", f)
    }

    @Test
    fun `8 personas a 8 personas da factor 1`() {
        val f = MotorEscalado.factorPorPersonas(8, 8)
        assertBD("1", f)
    }

    @Test
    fun `10 personas a 5 personas da factor 0 punto 5`() {
        val f = MotorEscalado.factorPorPersonas(10, 5)
        assertBD("0.5", f)
    }

    @Test
    fun `8 piezas a 3 piezas da factor 0 punto 375`() {
        val f = MotorEscalado.factorPorPiezas(8, 3)
        assertBD("0.375", f)
    }

    // ══════════════════════════════════════════════
    // Escalado de cantidades (sin redondeo de presentación)
    // ══════════════════════════════════════════════

    @Test
    fun `250 g de harina escalado a 100 personas desde 8 queda en 3125 g`() {
        val factor = MotorEscalado.factorPorPersonas(8, 100)
        val r = MotorEscalado.escalar(BigDecimal("250"), factor)
        assertBD("3125", r)
    }

    @Test
    fun `3 huevos escalados a 100 personas desde 8 queda en 37 punto 5`() {
        val factor = MotorEscalado.factorPorPersonas(8, 100)
        val r = MotorEscalado.escalar(BigDecimal("3"), factor)
        assertBD("37.5", r)
    }

    @Test
    fun `500 g de mantequilla escalado a 3 piezas desde 8 queda en 187 punto 5`() {
        val factor = MotorEscalado.factorPorPiezas(8, 3)
        val r = MotorEscalado.escalar(BigDecimal("500"), factor)
        assertBD("187.5", r)
    }

    @Test
    fun `4 huevos escalados a 3 piezas desde 8 queda en 1 punto 5`() {
        val factor = MotorEscalado.factorPorPiezas(8, 3)
        val r = MotorEscalado.escalar(BigDecimal("4"), factor)
        assertBD("1.5", r)
    }

    @Test
    fun `1 L de leche escalado a 100 personas desde 8 queda en 12 punto 5`() {
        val factor = MotorEscalado.factorPorPersonas(8, 100)
        val r = MotorEscalado.escalar(BigDecimal("1"), factor)
        assertBD("12.5", r)
    }

    // ══════════════════════════════════════════════
    // Redondeo usable — FRACCIONABLES
    // ══════════════════════════════════════════════

    @Test
    fun `3125 g redondeado usable da 3125 punto 00`() {
        val r = MotorEscalado.redondearUsable(BigDecimal("3125"), Unidad.GRAMO)
        assertBD("3125.00", r)
    }

    @Test
    fun `3 punto 125 kg redondeado usable da 3 punto 13`() {
        val r = MotorEscalado.redondearUsable(BigDecimal("3.125"), Unidad.KILOGRAMO)
        assertBD("3.13", r)
    }

    @Test
    fun `12 punto 5 L redondeado usable da 12 punto 50`() {
        val r = MotorEscalado.redondearUsable(BigDecimal("12.5"), Unidad.LITRO)
        assertBD("12.50", r)
    }

    // ══════════════════════════════════════════════
    // Redondeo usable — DISCRETAS (siempre arriba)
    // ══════════════════════════════════════════════

    @Test
    fun `37 punto 5 huevos redondeados usables dan 38`() {
        val r = MotorEscalado.redondearUsable(BigDecimal("37.5"), Unidad.PIEZA)
        assertBD("38", r)
    }

    @Test
    fun `1 punto 5 huevos redondeados usables dan 2`() {
        val r = MotorEscalado.redondearUsable(BigDecimal("1.5"), Unidad.PIEZA)
        assertBD("2", r)
    }

    @Test
    fun `37 punto 1 huevos redondeados usables dan 38 (siempre arriba)`() {
        val r = MotorEscalado.redondearUsable(BigDecimal("37.1"), Unidad.PIEZA)
        assertBD("38", r)
    }

    @Test
    fun `37 huevos exactos redondeados usables dan 37`() {
        val r = MotorEscalado.redondearUsable(BigDecimal("37"), Unidad.PIEZA)
        assertBD("37", r)
    }

    @Test
    fun `2 punto 5 docenas redondeadas usables dan 3`() {
        val r = MotorEscalado.redondearUsable(BigDecimal("2.5"), Unidad.DOCENA)
        assertBD("3", r)
    }

    // ══════════════════════════════════════════════
    // Helper esDiscreta
    // ══════════════════════════════════════════════

    @Test
    fun `pieza y docena son discretas`() {
        assertTrue(MotorEscalado.esDiscreta(Unidad.PIEZA))
        assertTrue(MotorEscalado.esDiscreta(Unidad.DOCENA))
        assertTrue(MotorEscalado.esDiscreta(Unidad.UNIDAD))
    }

    @Test
    fun `gramo y mililitro NO son discretas`() {
        assertEquals(false, MotorEscalado.esDiscreta(Unidad.GRAMO))
        assertEquals(false, MotorEscalado.esDiscreta(Unidad.MILILITRO))
        assertEquals(false, MotorEscalado.esDiscreta(Unidad.KILOGRAMO))
    }

    // ══════════════════════════════════════════════
    // Errores controlados
    // ══════════════════════════════════════════════

    @Test(expected = MotorEscalado.EscaladoInvalido::class)
    fun `personas base cero lanza error`() {
        MotorEscalado.factorPorPersonas(0, 100)
    }

    @Test(expected = MotorEscalado.EscaladoInvalido::class)
    fun `personas nuevas cero lanza error`() {
        MotorEscalado.factorPorPersonas(8, 0)
    }

    @Test(expected = MotorEscalado.EscaladoInvalido::class)
    fun `piezas base negativa lanza error`() {
        MotorEscalado.factorPorPiezas(-8, 3)
    }

    @Test(expected = MotorEscalado.EscaladoInvalido::class)
    fun `factor cero en escalar lanza error`() {
        MotorEscalado.escalar(BigDecimal("100"), BigDecimal.ZERO)
    }

    @Test(expected = MotorEscalado.EscaladoInvalido::class)
    fun `cantidad negativa en escalar lanza error`() {
        MotorEscalado.escalar(BigDecimal("-5"), BigDecimal("2"))
    }
}
