package com.pastelpro.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

@Database(
    entities = [
        IngredienteEntity::class,
        RecetaEntity::class,
        RecetaIngredienteEntity::class
    ],
    version = 3,
    exportSchema = false
)
abstract class PastelProDatabase : RoomDatabase() {

    abstract fun ingredienteDao(): IngredienteDao
    abstract fun recetaDao(): RecetaDao

    companion object {
        private const val NOMBRE = "pastelpro.db"

        @Volatile private var INSTANCIA: PastelProDatabase? = null

        /**
         * Migración v2 → v3 con recreate pattern.
         *
         * Motivo: SQLite < 3.35 no soporta DROP COLUMN.
         * El schema viejo tiene `rendimiento: String`.
         * El nuevo reemplaza esa columna por `rendimientoCantidad: Int` + `rendimientoUnidad: String`.
         *
         * Pasos:
         *   1. Crear tabla nueva con schema correcto.
         *   2. Copiar datos parseando el string viejo.
         *   3. Borrar tabla vieja.
         *   4. Renombrar nueva.
         *
         * FKs: receta_ingredientes apunta a recetas(id). Al recrear la tabla, los IDs se conservan,
         * por lo que la FK sigue válida. Room ejecuta la migración en una transacción.
         */
        private val MIGRACION_2_3 = object : Migration(2, 3) {
            override fun migrate(db: SupportSQLiteDatabase) {
                // 1. Crear tabla nueva
                db.execSQL("""
                    CREATE TABLE IF NOT EXISTS recetas_new (
                        id TEXT NOT NULL PRIMARY KEY,
                        nombre TEXT NOT NULL,
                        tipo TEXT NOT NULL,
                        rendimientoCantidad INTEGER NOT NULL,
                        rendimientoUnidad TEXT NOT NULL,
                        notas TEXT
                    )
                """)

                // 2. Copiar datos con parseo del string viejo
                //    "20"           → cantidad=20, unidad="porciones"
                //    "20 porciones" → cantidad=20, unidad="porciones"
                //    "8 personas"   → cantidad=8,  unidad="personas"
                //    "" o null      → cantidad=0,  unidad="porciones"
                db.execSQL("""
                    INSERT INTO recetas_new (id, nombre, tipo, rendimientoCantidad, rendimientoUnidad, notas)
                    SELECT
                        id,
                        nombre,
                        tipo,
                        COALESCE(
                            CAST(
                                REPLACE(
                                    REPLACE(
                                        SUBSTR(
                                            rendimiento,
                                            1,
                                            INSTR(rendimiento || ' ', ' ') - 1
                                        ),
                                        ',', '.'
                                    ),
                                    ' ', ''
                                ) AS INTEGER
                            ),
                            0
                        ),
                        CASE
                            WHEN rendimiento IS NULL OR TRIM(rendimiento) = '' THEN 'porciones'
                            WHEN INSTR(rendimiento, ' ') > 0
                                THEN TRIM(SUBSTR(rendimiento, INSTR(rendimiento, ' ') + 1))
                            ELSE 'porciones'
                        END,
                        notas
                    FROM recetas
                """)

                // 3. Borrar tabla vieja
                db.execSQL("DROP TABLE recetas")

                // 4. Renombrar nueva
                db.execSQL("ALTER TABLE recetas_new RENAME TO recetas")
            }
        }

        fun obtener(context: Context): PastelProDatabase =
            INSTANCIA ?: synchronized(this) {
                INSTANCIA ?: Room.databaseBuilder(
                    context.applicationContext,
                    PastelProDatabase::class.java,
                    NOMBRE
                )
                .addMigrations(MIGRACION_2_3)
                .build()
                .also { INSTANCIA = it }
            }
    }
}
