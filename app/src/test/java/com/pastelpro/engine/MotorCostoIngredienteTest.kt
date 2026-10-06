package com.pastelpro.engine

import com.pastelpro.domain.model.Ingrediente
import org.junit.Assert.assertEquals
import org.junit.Test
import java.math.BigDecimal

class MotorCostoIngredienteTest {

    private fun assertBigDecimalEquals(esperado: String, real: BigDecimal) {
        assertEquals(
            "Esperado <$esperado> pero fue <$real>",
            0,
            BigDecimal(esperado).compareTo(real)
        )
    }

    private fun harina() = Ingrediente(
        nombre = "Harina de trigo",
        categoria = "Harinas",
        presentacionCompra = "1 kg",
        cantidadCompra = BigDecimal("1"),
        unidadCompra = "kg",
        precioCompra = BigDecimal("28.00")
    )

    private fun leche() = Ingrediente(
        nombre = "Leche",
        categoria = "Lácteos",
        presentacionCompra = "1 L",
        cantidadCompra = BigDecimal("1"),
        unidadCompra = "L",
        precioCompra = BigDecimal("26.50")
    )

    private fun huevo() = Ingrediente(
        nombre = "Huevo",
        categoria = "Huevos",
        presentacionCompra = "12 piezas",
        cantidadCompra = BigDecimal("12"),
        unidadCompra = "pieza",
        precioCompra = BigDecimal("48.00")
    )

    // ══════════════════════════════════════════════
    // Casos documentados en el Maestro (§18, §34)
    // ══════════════════════════════════════════════

    @Test
    fun `harina 1kg a 28 pesos usar 250 g da 7 pesos`() {
        val r = MotorCostoIngrediente.calcular(harina(), BigDecimal("250"), "g")
        assertBigDecimalEquals("7.00", r)
    }

    @Test
    fun `leche 1L a 26 pesos 50 usar 500 ml da 13 pesos 25`() {
        val r = MotorCostoIngrediente.calcular(leche(), BigDecimal("500"), "ml")
        assertBigDecimalEquals("13.25", r)
    }

    @Test
    fun `huevo 12 piezas por 48 pesos usar 3 piezas da 12 pesos`() {
        val r = MotorCostoIngrediente.calcular(huevo(), BigDecimal("3"), "pieza")
        assertBigDecimalEquals("12.00", r)
    }

    @Test
    fun `usar la presentacion completa devuelve el precio completo`() {
        val r = MotorCostoIngrediente.calcular(harina(), BigDecimal("1"), "kg")
        assertBigDecimalEquals("28.00", r)
    }

    // ══════════════════════════════════════════════
    // Errores controlados
    // ══════════════════════════════════════════════

    @Test(expected = MotorCostoIngrediente.CostoInvalido::class)
    fun `cantidad usada negativa lanza CostoInvalido`() {
        MotorCostoIngrediente.calcular(harina(), BigDecimal("-1"), "g")
    }

    @Test(expected = MotorCostoIngrediente.CostoInvalido::class)
    fun `unidad de uso desconocida lanza CostoInvalido`() {
        MotorCostoIngrediente.calcular(harina(), BigDecimal("100"), "cucharada")
    }

    @Test(expected = MotorUnidades.ConversionInvalida::class)
    fun `unidad de uso incompatible lanza ConversionInvalida`() {
        // Harina (kg) con cantidad en ml → incompatible
        MotorCostoIngrediente.calcular(harina(), BigDecimal("100"), "ml")
    }
}
