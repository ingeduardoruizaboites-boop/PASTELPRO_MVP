package com.pastelpro.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [IngredienteEntity::class],
    version = 1,
    exportSchema = false
)
abstract class PastelProDatabase : RoomDatabase() {

    abstract fun ingredienteDao(): IngredienteDao

    companion object {
        private const val NOMBRE = "pastelpro.db"

        @Volatile private var INSTANCIA: PastelProDatabase? = null

        fun obtener(context: Context): PastelProDatabase =
            INSTANCIA ?: synchronized(this) {
                INSTANCIA ?: Room.databaseBuilder(
                    context.applicationContext,
                    PastelProDatabase::class.java,
                    NOMBRE
                ).build().also { INSTANCIA = it }
            }
    }
}
