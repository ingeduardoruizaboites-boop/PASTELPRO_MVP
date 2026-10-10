package com.pastelpro.engine

import com.pastelpro.domain.model.Ingrediente
import com.pastelpro.domain.model.IngredienteDeReceta
import com.pastelpro.domain.model.Receta
import java.math.BigDecimal
import java.math.RoundingMode

/**
 * Calculadora de costo completo de una receta.
 *
 * Aplica en orden:
 *   1. Costo de cada ingrediente (usa MotorCostoIngrediente).
 *   2. Suma de ingredientes.
 *   3. Merma sobre el subtotal.
 *   4. Costo total y costo por porción.
 *
 * NO incluye mano de obra ni energía todavía — esos vienen cuando exista
 * pantalla de configuración. El diseño soporta añadirlos sin refactor.
 *
 * Reglas (§64, §31, §35 del Maestro):
 * - BigDecimal, escala final 2.
 * - Ingredientes no encontrados en inventario se reportan aparte (no se suman al costo).
 * - Receta sin rendimiento válido → costo por porción = 0.
 */
object CalculadoraCostoReceta {

    class CostoRecetaInvalido(mensaje: String) : IllegalArgumentException(mensaje)

    private const val PRECISION_INTERNA = 6
    private const val PRECISION_FINAL = 2

    data class CostoIngrediente(
        val ingredienteId: String,
        val nombre: String,
        val cantidad: BigDecimal,
        val unidad: String,
        val costo: BigDecimal,
        val encontrado: Boolean
    )

    data class Resultado(
        val costoIngredientes: BigDecimal,
        val mermaPorcentaje: BigDecimal,
        val costoMerma: BigDecimal,
        val costoHorneada: BigDecimal,      // Gas/luz (0 si no aplica)
        val costoTotal: BigDecimal,
        val rendimientoCantidad: Int,
        val costoPorPorcion: BigDecimal,
        val desglose: List<CostoIngrediente>,
        val faltantes: List<String>
    )

    /**
     * Calcula el costo total de una receta.
     *
     * @param receta receta con sus ingredientes
     * @param inventario lista de ingredientes del inventario (con sus precios)
     * @param mermaPorcentaje porcentaje de merma a aplicar (default 5%)
     */
    fun calcular(
        receta: Receta,
        inventario: List<Ingrediente>,
        mermaPorcentaje: BigDecimal = BigDecimal("5"),
        costoHorneada: BigDecimal = BigDecimal.ZERO
    ): Resultado {
        if (mermaPorcentaje < BigDecimal.ZERO || mermaPorcentaje >= BigDecimal(100)) {
            throw CostoRecetaInvalido("El porcentaje de merma debe estar entre 0 y <100: $mermaPorcentaje")
        }

        // Indexar inventario por id Y por nombre normalizado para robustez.
        // Motivo: cuando se desinstala/reinstala la app, los ingredientes reciben nuevos UUIDs
        // pero las recetas guardadas apuntan a los IDs viejos. El fallback por nombre permite
        // que las recetas sigan funcionando.
        val porId = inventario.associateBy { it.id }
        val porNombre = inventario.associateBy { it.nombre.trim().lowercase() }

        val desglose = mutableListOf<CostoIngrediente>()
        val faltantes = mutableListOf<String>()
        var subtotal = BigDecimal.ZERO

        receta.ingredientes.forEach { item ->
            // 1) Buscar por id; 2) fallback por nombre (case-insensitive)
            val inventarioItem = porId[item.ingredienteId]
                ?: porNombre[item.nombre.trim().lowercase()]

            if (inventarioItem == null) {
                // Ingrediente no encontrado en inventario
                faltantes += item.nombre
                desglose += CostoIngrediente(
                    ingredienteId = item.ingredienteId,
                    nombre = item.nombre,
                    cantidad = item.cantidad,
                    unidad = item.unidad,
                    costo = BigDecimal.ZERO,
                    encontrado = false
                )
            } else {
                val costo = try {
                    MotorCostoIngrediente.calcular(
                        ingrediente = inventarioItem,
                        cantidadUsada = item.cantidad,
                        unidadUsada = item.unidad
                    )
                } catch (e: MotorUnidades.ConversionInvalida) {
                    // Unidad incompatible: incluir unidades esperadas vs encontradas
                    val unidadReceta = item.unidad
                    val unidadInv = inventarioItem.unidadCompra
                    faltantes += "${item.nombre} · receta: $unidadReceta · inventario: $unidadInv (revisa las unidades)"
                    BigDecimal.ZERO
                } catch (e: MotorCostoIngrediente.CostoInvalido) {
                    faltantes += "${item.nombre} (datos incompletos)"
                    BigDecimal.ZERO
                }

                subtotal = subtotal.add(costo)

                desglose += CostoIngrediente(
                    ingredienteId = item.ingredienteId,
                    nombre = item.nombre,
                    cantidad = item.cantidad,
                    unidad = item.unidad,
                    costo = costo,
                    encontrado = true
                )
            }
        }

        // Aplicar merma sobre el subtotal de ingredientes
        val resultadoMerma = MotorMerma.aplicar(subtotal, mermaPorcentaje)

        // Costo por porción
        val costoPorPorcion = if (receta.rendimientoCantidad > 0) {
            resultadoMerma.costoTotal.divide(
                BigDecimal(receta.rendimientoCantidad),
                PRECISION_FINAL,
                RoundingMode.HALF_UP
            )
        } else {
            BigDecimal.ZERO
        }

        val costoTotalConHorneada = resultadoMerma.costoTotal.add(costoHorneada)
            .setScale(PRECISION_FINAL, RoundingMode.HALF_UP)

        val costoPorcionFinal = if (receta.rendimientoCantidad > 0) {
            costoTotalConHorneada.divide(
                BigDecimal(receta.rendimientoCantidad),
                PRECISION_FINAL,
                RoundingMode.HALF_UP
            )
        } else {
            BigDecimal.ZERO
        }

        return Resultado(
            costoIngredientes = resultadoMerma.costoAntes,
            mermaPorcentaje = mermaPorcentaje,
            costoMerma = resultadoMerma.costoMerma,
            costoHorneada = costoHorneada,
            costoTotal = costoTotalConHorneada,
            rendimientoCantidad = receta.rendimientoCantidad,
            costoPorPorcion = costoPorcionFinal,
            desglose = desglose,
            faltantes = faltantes
        )
    }

    /**
     * Calcula los 3 precios sugeridos a partir de un resultado de costo.
     * Reusa MotorPrecio. Devuelve mínimo/recomendado/premium con ganancia y margen.
     */
    fun calcularPrecios(
        resultado: Resultado,
        margenMinimo: BigDecimal = BigDecimal("20"),
        margenRecomendado: BigDecimal = BigDecimal("40"),
        margenPremium: BigDecimal = BigDecimal("60")
    ): MotorPrecio.TresPrecios {
        return MotorPrecio.calcularTresPrecios(
            costo = resultado.costoTotal,
            margenMinimo = margenMinimo,
            margenRecomendado = margenRecomendado,
            margenPremium = margenPremium
        )
    }
}
