package com.pastelpro.domain.repository

import com.pastelpro.domain.model.Receta
import kotlinx.coroutines.flow.Flow

interface RecetaRepository {
    fun observarTodas(): Flow<List<Receta>>
    suspend fun obtener(id: String): Receta?
    suspend fun agregar(receta: Receta)
    suspend fun eliminar(id: String)
}
