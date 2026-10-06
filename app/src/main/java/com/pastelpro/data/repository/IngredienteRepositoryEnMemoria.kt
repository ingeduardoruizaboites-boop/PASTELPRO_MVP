package com.pastelpro.data.repository

import com.pastelpro.domain.model.Ingrediente
import com.pastelpro.domain.repository.IngredienteRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import java.math.BigDecimal

/**
 * Implementación en memoria — SOLO para validar la arquitectura del Bloque 2A.
 * En Bloque 2B se reemplaza por IngredienteRepositoryRoom SIN tocar UI ni ViewModel.
 *
 * Los datos NO persisten al cerrar la app (intencional).
 */
class IngredienteRepositoryEnMemoria : IngredienteRepository {

    private val _ingredientes = MutableStateFlow<List<Ingrediente>>(datosDeEjemplo())

    override fun observarTodos(): Flow<List<Ingrediente>> = _ingredientes.asStateFlow()

    override suspend fun obtener(id: String): Ingrediente? =
        _ingredientes.value.firstOrNull { it.id == id }

    override suspend fun agregar(ingrediente: Ingrediente) {
        _ingredientes.update { lista -> lista + ingrediente }
    }

    override suspend fun actualizar(ingrediente: Ingrediente) {
        _ingredientes.update { lista ->
            lista.map { if (it.id == ingrediente.id) ingrediente else it }
        }
    }

    override suspend fun eliminar(id: String) {
        _ingredientes.update { lista -> lista.filterNot { it.id == id } }
    }

    private fun datosDeEjemplo(): List<Ingrediente> = listOf(
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
