package com.pastelpro.engine

import com.pastelpro.domain.model.Ingrediente
import com.pastelpro.domain.model.IngredienteDeReceta
import com.pastelpro.domain.model.Receta
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.math.BigDecimal

class CalculadoraCostoRecetaTest {

    private fun bd(s: String) = BigDecimal(s)

    private fun assertBD(esperado: String, real: BigDecimal) {
        assertEquals("Esperado <$esperado> pero fue <$real>", 0, BigDecimal(esperado).compareTo(real))
    }

    private fun assertBDConTolerancia(esperado: String, real: BigDecimal, tolerancia: String) {
        val diff = BigDecimal(esperado).subtract(real).abs()
        assertTrue(
            "Esperado <$esperado> ± <$tolerancia> pero fue <$real> (dif: $diff)",
            diff <= BigDecimal(tolerancia)
        )
    }

    // ══════════════════════════════════════════════
    // Helpers de datos
    // ══════════════════════════════════════════════

    private fun harina() = Ingrediente(
        id = "harina-id",
        nombre = "Harina",
        categoria = "Harinas",
        presentacionCompra = "1 kg",
        cantidadCompra = bd("1"),
        unidadCompra = "kg",
        precioCompra = bd("28")
    )

    private fun leche() = Ingrediente(
        id = "leche-id",
        nombre = "Leche",
        categoria = "Lácteos",
        presentacionCompra = "1 L",
        cantidadCompra = bd("1"),
        unidadCompra = "L",
        precioCompra = bd("26")
    )

    private fun huevo() = Ingrediente(
        id = "huevo-id",
        nombre = "Huevo",
        categoria = "Huevos",
        presentacionCompra = "12 piezas",
        cantidadCompra = bd("12"),
        unidadCompra = "pieza",
        precioCompra = bd("48")
    )

    private fun receta(vararg items: Pair<String, Pair<BigDecimal, String>>) = Receta(
        nombre = "Test",
        tipo = "Test",
        rendimientoCantidad = 10,
        rendimientoUnidad = "porciones",
        ingredientes = items.map { (id, cantUnidad) ->
            IngredienteDeReceta(
                ingredienteId = id,
                nombre = id,
                cantidad = cantUnidad.first,
                unidad = cantUnidad.second
            )
        }
    )

    // ══════════════════════════════════════════════
    // Casos básicos
    // ══════════════════════════════════════════════

    @Test
    fun `receta con 1 ingrediente calcula costo correctamente`() {
        // 500g harina a $28/kg = $14
        // + merma 5% = $14.70
        val r = CalculadoraCostoReceta.calcular(
            receta = receta("harina-id" to (bd("500") to "g")),
            inventario = listOf(harina())
        )
        assertBD("14.00", r.costoIngredientes)
        assertBD("0.70", r.costoMerma)
        assertBD("14.70", r.costoTotal)
        assertBD("1.47", r.costoPorPorcion) // 14.70 / 10 porciones
    }

    @Test
    fun `receta con 3 ingredientes suma correctamente`() {
        // Harina 500g -> $14.00
        // Leche 500ml -> $13.00
        // Huevo 3 pzas -> $12.00
        // Total ingredientes: $39.00
        // + merma 5% = $40.95
        val r = CalculadoraCostoReceta.calcular(
            receta = receta(
                "harina-id" to (bd("500") to "g"),
                "leche-id" to (bd("500") to "ml"),
                "huevo-id" to (bd("3") to "pieza")
            ),
            inventario = listOf(harina(), leche(), huevo())
        )
        assertBD("39.00", r.costoIngredientes)
        assertBD("1.95", r.costoMerma)
        assertBD("40.95", r.costoTotal)
        assertBD("4.10", r.costoPorPorcion)
    }

    @Test
    fun `merma cero devuelve el mismo costo`() {
        val r = CalculadoraCostoReceta.calcular(
            receta = receta("harina-id" to (bd("500") to "g")),
            inventario = listOf(harina()),
            mermaPorcentaje = BigDecimal.ZERO
        )
        assertBD("14.00", r.costoIngredientes)
        assertBD("0.00", r.costoMerma)
        assertBD("14.00", r.costoTotal)
    }

    // ══════════════════════════════════════════════
    // Faltantes
    // ══════════════════════════════════════════════

    @Test
    fun `ingrediente no encontrado se marca como faltante`() {
        val r = CalculadoraCostoReceta.calcular(
            receta = receta(
                "harina-id" to (bd("500") to "g"),
                "falta-id" to (bd("100") to "g")
            ),
            inventario = listOf(harina())
        )
        assertBD("14.00", r.costoIngredientes) // solo harina
        assertEquals(1, r.faltantes.size)
        assertEquals("falta-id", r.faltantes[0])
        assertEquals(2, r.desglose.size)
        assertEquals(false, r.desglose[1].encontrado)
    }

    @Test
    fun `receta con todos los ingredientes faltantes da costo cero`() {
        val r = CalculadoraCostoReceta.calcular(
            receta = receta(
                "x-id" to (bd("100") to "g"),
                "y-id" to (bd("100") to "g")
            ),
            inventario = listOf(harina())
        )
        assertBD("0.00", r.costoTotal)
        assertEquals(2, r.faltantes.size)
    }

    // ══════════════════════════════════════════════
    // Casos borde
    // ══════════════════════════════════════════════

    @Test
    fun `receta sin ingredientes da costo cero`() {
        val r = CalculadoraCostoReceta.calcular(
            receta = receta(),
            inventario = listOf(harina())
        )
        assertBD("0.00", r.costoTotal)
        assertEquals(0, r.desglose.size)
        assertEquals(0, r.faltantes.size)
    }

    @Test
    fun `receta con rendimiento cero da costo por porcion cero`() {
        val recetaSinRendimiento = Receta(
            nombre = "Test",
            tipo = "Test",
            rendimientoCantidad = 0,
            rendimientoUnidad = "porciones",
            ingredientes = listOf(
                IngredienteDeReceta("harina-id", "harina", bd("500"), "g")
            )
        )
        val r = CalculadoraCostoReceta.calcular(
            receta = recetaSinRendimiento,
            inventario = listOf(harina())
        )
        assertBD("14.70", r.costoTotal)
        assertBD("0.00", r.costoPorPorcion)
    }

    // ══════════════════════════════════════════════
    // Validaciones
    // ══════════════════════════════════════════════

    @Test(expected = CalculadoraCostoReceta.CostoRecetaInvalido::class)
    fun `merma negativa lanza error`() {
        CalculadoraCostoReceta.calcular(
            receta = receta("harina-id" to (bd("500") to "g")),
            inventario = listOf(harina()),
            mermaPorcentaje = bd("-5")
        )
    }

    @Test(expected = CalculadoraCostoReceta.CostoRecetaInvalido::class)
    fun `merma mayor o igual a 100 lanza error`() {
        CalculadoraCostoReceta.calcular(
            receta = receta("harina-id" to (bd("500") to "g")),
            inventario = listOf(harina()),
            mermaPorcentaje = bd("100")
        )
    }

    // ══════════════════════════════════════════════
    // Cálculo de precios
    // ══════════════════════════════════════════════

    @Test
    fun `precios sugeridos sobre costo total 14 punto 70 dan minimo recomendado y premium`() {
        val r = CalculadoraCostoReceta.calcular(
            receta = receta("harina-id" to (bd("500") to "g")),
            inventario = listOf(harina())
        )
        val precios = CalculadoraCostoReceta.calcularPrecios(r)
        // 14.70 / (1-0.20) = 18.375 -> 18.38
        // 14.70 / (1-0.40) = 24.50
        // 14.70 / (1-0.60) = 36.75
        assertBD("18.38", precios.minimo.precio)
        assertBD("24.50", precios.recomendado.precio)
        assertBD("36.75", precios.premium.precio)

        // Verificar ganancias
        assertBD("3.68", precios.minimo.ganancia)
        assertBD("9.80", precios.recomendado.ganancia)
        assertBD("22.05", precios.premium.ganancia)

        // Verificar márgenes con tolerancia ±0.02.
        // Nota: el precio se redondea a 2 decimales ANTES de recalcular el margen real,
        // por lo que el margen puede diferir en centésimas del valor nominal.
        // Esto es matemáticamente correcto y esperado.
        assertBDConTolerancia("20.00", precios.minimo.margen, "0.02")
        assertBDConTolerancia("40.00", precios.recomendado.margen, "0.02")
        assertBDConTolerancia("60.00", precios.premium.margen, "0.02")
    }

    @Test
    fun `costo total cero da precios cero sin errores`() {
        val r = CalculadoraCostoReceta.calcular(
            receta = receta(),
            inventario = listOf(harina())
        )
        val precios = CalculadoraCostoReceta.calcularPrecios(r)
        assertBD("0.00", precios.minimo.precio)
        assertBD("0.00", precios.recomendado.precio)
        assertBD("0.00", precios.premium.precio)
    }

    @Test
    fun `desglose preserva orden y cantidad de ingredientes`() {
        val r = CalculadoraCostoReceta.calcular(
            receta = receta(
                "harina-id" to (bd("500") to "g"),
                "leche-id" to (bd("500") to "ml"),
                "huevo-id" to (bd("3") to "pieza")
            ),
            inventario = listOf(harina(), leche(), huevo())
        )
        assertEquals(3, r.desglose.size)
        assertEquals("harina-id", r.desglose[0].ingredienteId)
        assertEquals("leche-id", r.desglose[1].ingredienteId)
        assertEquals("huevo-id", r.desglose[2].ingredienteId)
        assertTrue(r.desglose.all { it.encontrado })
    }
}
