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
        RecetaIngredienteEntity::class,
        PedidoEntity::class
    ],
    version = 5,
    exportSchema = false
)
abstract class PastelProDatabase : RoomDatabase() {

    abstract fun ingredienteDao(): IngredienteDao
    abstract fun recetaDao(): RecetaDao
    abstract fun pedidoDao(): PedidoDao

    companion object {
        private const val NOMBRE = "pastelpro.db"

        @Volatile private var INSTANCIA: PastelProDatabase? = null

        /** Migración v2 → v3: recreate de recetas (ver sesión 4). */
        private val MIGRACION_2_3 = object : Migration(2, 3) {
            override fun migrate(db: SupportSQLiteDatabase) {
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
                db.execSQL("""
                    INSERT INTO recetas_new (id, nombre, tipo, rendimientoCantidad, rendimientoUnidad, notas)
                    SELECT
                        id, nombre, tipo,
                        COALESCE(CAST(REPLACE(REPLACE(SUBSTR(rendimiento, 1, INSTR(rendimiento || ' ', ' ') - 1), ',', '.'), ' ', '') AS INTEGER), 0),
                        CASE
                            WHEN rendimiento IS NULL OR TRIM(rendimiento) = '' THEN 'porciones'
                            WHEN INSTR(rendimiento, ' ') > 0 THEN TRIM(SUBSTR(rendimiento, INSTR(rendimiento, ' ') + 1))
                            ELSE 'porciones'
                        END,
                        notas
                    FROM recetas
                """)
                db.execSQL("DROP TABLE recetas")
                db.execSQL("ALTER TABLE recetas_new RENAME TO recetas")
            }
        }

        /** Migración v3 → v4: añade tabla pedidos. Puramente aditiva. */
        private val MIGRACION_3_4 = object : Migration(3, 4) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("""
                    CREATE TABLE IF NOT EXISTS pedidos (
                        id TEXT NOT NULL PRIMARY KEY,
                        recetaId TEXT NOT NULL,
                        recetaNombre TEXT NOT NULL,
                        cliente TEXT,
                        porciones INTEGER NOT NULL,
                        costoTotal TEXT NOT NULL,
                        precioAcordado TEXT NOT NULL,
                        fechaEntrega TEXT,
                        notas TEXT,
                        estado TEXT NOT NULL,
                        creadoEn INTEGER NOT NULL
                    )
                """)
                db.execSQL("CREATE INDEX IF NOT EXISTS index_pedidos_recetaId ON pedidos(recetaId)")
            }
        }

        /** Migración v4 → v5: añade campos opcionales al pedido. Puramente aditiva. */
        private val MIGRACION_4_5 = object : Migration(4, 5) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE pedidos ADD COLUMN telefonoContacto TEXT")
                db.execSQL("ALTER TABLE pedidos ADD COLUMN horaEntrega TEXT")
                db.execSQL("ALTER TABLE pedidos ADD COLUMN direccionEntrega TEXT")
            }
        }

        fun obtener(context: Context): PastelProDatabase =
            INSTANCIA ?: synchronized(this) {
                INSTANCIA ?: Room.databaseBuilder(
                    context.applicationContext,
                    PastelProDatabase::class.java,
                    NOMBRE
                )
                .addMigrations(MIGRACION_2_3, MIGRACION_3_4, MIGRACION_4_5)
                .build()
                .also { INSTANCIA = it }
            }
    }
}
