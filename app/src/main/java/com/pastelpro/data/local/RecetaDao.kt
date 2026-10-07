package com.pastelpro.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import kotlinx.coroutines.flow.Flow

@Dao
interface RecetaDao {

    @Query("SELECT * FROM recetas ORDER BY nombre COLLATE NOCASE ASC")
    fun observarTodas(): Flow<List<RecetaEntity>>

    @Query("SELECT * FROM recetas WHERE id = :id LIMIT 1")
    suspend fun obtener(id: String): RecetaEntity?

    @Query("SELECT * FROM receta_ingredientes WHERE recetaId = :recetaId")
    suspend fun ingredientesDe(recetaId: String): List<RecetaIngredienteEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertarReceta(receta: RecetaEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertarIngredientes(ingredientes: List<RecetaIngredienteEntity>)

    @Query("DELETE FROM recetas WHERE id = :id")
    suspend fun eliminarReceta(id: String)

    @Query("DELETE FROM receta_ingredientes WHERE recetaId = :recetaId")
    suspend fun eliminarIngredientesDe(recetaId: String)

    @Transaction
    suspend fun insertarConIngredientes(
        receta: RecetaEntity,
        ingredientes: List<RecetaIngredienteEntity>
    ) {
        insertarReceta(receta)
        if (ingredientes.isNotEmpty()) {
            insertarIngredientes(ingredientes)
        }
    }

    @Transaction
    suspend fun eliminarCompleta(id: String) {
        eliminarIngredientesDe(id)
        eliminarReceta(id)
    }
}
