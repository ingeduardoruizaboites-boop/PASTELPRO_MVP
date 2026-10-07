package com.pastelpro.data.repository

import com.pastelpro.data.local.RecetaDao
import com.pastelpro.data.local.mapper.RecetaMapper
import com.pastelpro.domain.model.Receta
import com.pastelpro.domain.repository.RecetaRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class RecetaRepositoryRoom(
    private val dao: RecetaDao
) : RecetaRepository {

    override fun observarTodas(): Flow<List<Receta>> =
        dao.observarTodas().map { entities ->
            entities.map { e ->
                val ings = dao.ingredientesDe(e.id)
                RecetaMapper.aDominio(e, ings)
            }
        }

    override suspend fun obtener(id: String): Receta? {
        val e = dao.obtener(id) ?: return null
        return RecetaMapper.aDominio(e, dao.ingredientesDe(id))
    }

    override suspend fun agregar(receta: Receta) {
        dao.insertarConIngredientes(
            RecetaMapper.aEntity(receta),
            RecetaMapper.ingredientesAEntities(receta.id, receta.ingredientes)
        )
    }

    override suspend fun eliminar(id: String) {
        dao.eliminarCompleta(id)
    }
}
