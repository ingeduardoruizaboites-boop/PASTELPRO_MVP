package com.pastelpro.data.repository

import android.content.Context
import com.pastelpro.data.local.PastelProDatabase
import com.pastelpro.domain.repository.IngredienteRepository

/**
 * Service Locator simple.
 * En V2 se puede migrar a Hilt si el proyecto lo justifica.
 *
 * REQUIERE inicialización con Context desde Application o MainActivity.
 * Ver PastelProApp.onCreate o MainActivity.onCreate.
 */
object RepositorioProvider {

    private var _ingredienteRepository: IngredienteRepository? = null

    val ingredienteRepository: IngredienteRepository
        get() = _ingredienteRepository
            ?: error("RepositorioProvider no ha sido inicializado. Llama a init(context) primero.")

    fun init(context: Context) {
        if (_ingredienteRepository == null) {
            val db = PastelProDatabase.obtener(context)
            _ingredienteRepository = IngredienteRepositoryRoom(db.ingredienteDao())
        }
    }
}
