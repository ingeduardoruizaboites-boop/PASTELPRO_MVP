package com.pastelpro.domain

import com.pastelpro.domain.model.EstadoPedido
import com.pastelpro.domain.model.Pedido
import org.junit.Assert.assertEquals
import org.junit.Test
import java.math.BigDecimal

class PedidoTest {

    private fun assertBD(esperado: String, real: BigDecimal) {
        assertEquals("Esperado <$esperado> pero fue <$real>", 0, BigDecimal(esperado).compareTo(real))
    }

    private fun pedido(
        costo: String = "239.40",
        precio: String = "399.00"
    ) = Pedido(
        recetaId = "r1",
        recetaNombre = "Pastel de naranja",
        porciones = 50,
        costoTotal = BigDecimal(costo),
        precioAcordado = BigDecimal(precio)
    )

    @Test
    fun `ganancia es precio menos costo`() {
        assertBD("159.60", pedido().ganancia)
    }

    @Test
    fun `margen real es correcto para 239 punto 40 y 399`() {
        // (399 - 239.40) / 399 * 100 = 40.00%
        assertBD("40.00", pedido().margenReal)
    }

    @Test
    fun `pedido a perdida da ganancia negativa`() {
        val p = pedido(costo = "500.00", precio = "400.00")
        assertBD("-100.00", p.ganancia)
        assertBD("-25.00", p.margenReal)
    }

    @Test
    fun `precio cero da margen cero sin division por cero`() {
        val p = pedido(precio = "0.00")
        assertBD("0.00", p.margenReal)
    }

    @Test
    fun `estado por defecto es pendiente`() {
        assertEquals(EstadoPedido.PENDIENTE, pedido().estado)
    }

    @Test
    fun `etiqueta de estado es legible`() {
        assertEquals("Pendiente", EstadoPedido.PENDIENTE.etiqueta)
        assertEquals("Entregado", EstadoPedido.ENTREGADO.etiqueta)
    }
}
