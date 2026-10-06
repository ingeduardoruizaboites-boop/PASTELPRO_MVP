package com.pastelpro.domain.repository

import com.pastelpro.domain.model.Ingrediente
import kotlinx.coroutines.flow.Flow

/**
 * Contrato del repositorio de ingredientes.
 * La UI y el ViewModel dependen SOLO de esta interfaz, nunca de la implementación.
 * Esto permite cambiar de memoria → Room sin tocar UI ni ViewModel.
 */
interface IngredienteRepository {
    fun observarTodos(): Flow<List<Ingrediente>>
    suspend fun obtener(id: String): Ingrediente?
    suspend fun agregar(ingrediente: Ingrediente)
    suspend fun actualizar(ingrediente: Ingrediente)
    suspend fun eliminar(id: String)
    suspend fun eliminarTodos()

    /**
     * Reinicia el estado de ejemplos.
     * Borra todos los ingredientes y vuelve a cargar los 3 de demostración.
     * Útil al inicio para limpiar datos de prueba.
     */
    suspend fun reiniciarEjemplos()
}
