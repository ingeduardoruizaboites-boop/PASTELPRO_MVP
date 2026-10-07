package com.pastelpro.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [
        IngredienteEntity::class,
        RecetaEntity::class,
        RecetaIngredienteEntity::class
    ],
    version = 2,
    exportSchema = false
)
abstract class PastelProDatabase : RoomDatabase() {

    abstract fun ingredienteDao(): IngredienteDao
    abstract fun recetaDao(): RecetaDao

    companion object {
        private const val NOMBRE = "pastelpro.db"

        @Volatile private var INSTANCIA: PastelProDatabase? = null

        fun obtener(context: Context): PastelProDatabase =
            INSTANCIA ?: synchronized(this) {
                INSTANCIA ?: Room.databaseBuilder(
                    context.applicationContext,
                    PastelProDatabase::class.java,
                    NOMBRE
                )
                // Destructivo: MVP sin usuarios reales aún. En V1.1 se agregan migraciones.
                .fallbackToDestructiveMigration()
                .build()
                .also { INSTANCIA = it }
            }
    }
}
