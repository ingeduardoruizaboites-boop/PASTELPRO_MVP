package com.pastelpro.data.repository

import com.pastelpro.data.local.IngredienteDao
import com.pastelpro.data.local.mapper.IngredienteMapper
import com.pastelpro.domain.model.Ingrediente
import com.pastelpro.domain.repository.IngredienteRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.math.BigDecimal

/**
 * Implementación real con Room.
 * La UI y el ViewModel NO saben que esto existe — solo ven IngredienteRepository.
 */
class IngredienteRepositoryRoom(
    private val dao: IngredienteDao
) : IngredienteRepository {

    override fun observarTodos(): Flow<List<Ingrediente>> =
        dao.observarTodos().map { entities ->
            entities.map(IngredienteMapper::aDominio)
        }

    override suspend fun obtener(id: String): Ingrediente? =
        dao.obtener(id)?.let(IngredienteMapper::aDominio)

    override suspend fun agregar(ingrediente: Ingrediente) {
        dao.insertar(IngredienteMapper.aEntity(ingrediente))
    }

    override suspend fun actualizar(ingrediente: Ingrediente) {
        dao.actualizar(IngredienteMapper.aEntity(ingrediente))
    }

    override suspend fun eliminar(id: String) {
        val actual = dao.obtener(id) ?: return
        dao.eliminar(actual)
    }

    override suspend fun eliminarTodos() {
        dao.eliminarTodos()
    }

    override suspend fun reiniciarEjemplos() {
        dao.eliminarTodos()
        dao.insertarVarios(ejemplos().map(IngredienteMapper::aEntity))
    }

    private fun ejemplos(): List<Ingrediente> = listOf(
        Ingrediente(
            nombre = "Harina de trigo",
            categoria = "Harinas",
            presentacionCompra = "1 kg",
            cantidadCompra = BigDecimal("1"),
            unidadCompra = "kg",
            precioCompra = BigDecimal("28.00"),
            proveedor = "Proveedor habitual"
        ),
        Ingrediente(
            nombre = "Huevo",
            categoria = "Huevos",
            presentacionCompra = "12 piezas",
            cantidadCompra = BigDecimal("12"),
            unidadCompra = "pieza",
            precioCompra = BigDecimal("48.00")
        ),
        Ingrediente(
            nombre = "Leche entera",
            categoria = "Lácteos",
            presentacionCompra = "1 L",
            cantidadCompra = BigDecimal("1"),
            unidadCompra = "L",
            precioCompra = BigDecimal("26.50")
        )
    )
}
