package com.pastelpro.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface IngredienteDao {

    @Query("SELECT * FROM ingredientes ORDER BY nombre COLLATE NOCASE ASC")
    fun observarTodos(): Flow<List<IngredienteEntity>>

    @Query("SELECT * FROM ingredientes WHERE id = :id LIMIT 1")
    suspend fun obtener(id: String): IngredienteEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertar(entity: IngredienteEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertarVarios(entities: List<IngredienteEntity>)

    @Update
    suspend fun actualizar(entity: IngredienteEntity)

    @Delete
    suspend fun eliminar(entity: IngredienteEntity)

    @Query("DELETE FROM ingredientes")
    suspend fun eliminarTodos()
}
