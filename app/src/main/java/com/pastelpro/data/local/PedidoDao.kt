package com.pastelpro.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface PedidoDao {

    @Query("SELECT * FROM pedidos ORDER BY creadoEn DESC")
    fun observarTodos(): Flow<List<PedidoEntity>>

    @Query("SELECT * FROM pedidos WHERE id = :id LIMIT 1")
    suspend fun obtener(id: String): PedidoEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertar(pedido: PedidoEntity)

    @Query("DELETE FROM pedidos WHERE id = :id")
    suspend fun eliminar(id: String)

    @Query("UPDATE pedidos SET estado = :nuevoEstado WHERE id = :id")
    suspend fun cambiarEstado(id: String, nuevoEstado: String)
}
