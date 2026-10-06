package com.pastelpro.engine

import org.junit.Assert.assertEquals
import org.junit.Test
import java.math.BigDecimal

class MotorUnidadesTest {

    private fun assertBigDecimalEquals(esperado: String, real: BigDecimal) {
        assertEquals(
            "Esperado <$esperado> pero fue <$real>",
            0,
            BigDecimal(esperado).compareTo(real)
        )
    }

    @Test
    fun `1 kg convertido a g da 1000 g`() {
        val r = MotorUnidades.convertir(BigDecimal("1"), Unidad.KILOGRAMO, Unidad.GRAMO)
        assertBigDecimalEquals("1000", r)
    }

    @Test
    fun `500 g convertido a kg da 0 punto 5`() {
        val r = MotorUnidades.convertir(BigDecimal("500"), Unidad.GRAMO, Unidad.KILOGRAMO)
        assertBigDecimalEquals("0.5", r)
    }

    @Test
    fun `1 L convertido a ml da 1000 ml`() {
        val r = MotorUnidades.convertir(BigDecimal("1"), Unidad.LITRO, Unidad.MILILITRO)
        assertBigDecimalEquals("1000", r)
    }

    @Test
    fun `1 docena convertido a piezas da 12`() {
        val r = MotorUnidades.convertir(BigDecimal("1"), Unidad.DOCENA, Unidad.PIEZA)
        assertBigDecimalEquals("12", r)
    }

    @Test
    fun `misma unidad devuelve la misma cantidad sin cambios`() {
        val r = MotorUnidades.convertir(BigDecimal("3.14"), Unidad.KILOGRAMO, Unidad.KILOGRAMO)
        assertBigDecimalEquals("3.14", r)
    }

    @Test(expected = MotorUnidades.ConversionInvalida::class)
    fun `kg a ml lanza ConversionInvalida`() {
        MotorUnidades.convertir(BigDecimal("1"), Unidad.KILOGRAMO, Unidad.MILILITRO)
    }

    @Test(expected = MotorUnidades.ConversionInvalida::class)
    fun `docena a litro lanza ConversionInvalida`() {
        MotorUnidades.convertir(BigDecimal("1"), Unidad.DOCENA, Unidad.LITRO)
    }
}
