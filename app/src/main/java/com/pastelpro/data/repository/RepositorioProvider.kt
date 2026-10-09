package com.pastelpro.data.repository

import android.content.Context
import com.pastelpro.data.local.PastelProDatabase
import com.pastelpro.data.preferences.ConfiguracionRepository
import com.pastelpro.domain.repository.IngredienteRepository
import com.pastelpro.domain.repository.PedidoRepository
import com.pastelpro.domain.repository.RecetaRepository

object RepositorioProvider {

    private var _ingredienteRepository: IngredienteRepository? = null
    private var _recetaRepository: RecetaRepository? = null
    private var _pedidoRepository: PedidoRepository? = null
    private var _configuracionRepository: ConfiguracionRepository? = null

    val ingredienteRepository: IngredienteRepository
        get() = _ingredienteRepository
            ?: error("RepositorioProvider no ha sido inicializado. Llama a init(context) primero.")

    val recetaRepository: RecetaRepository
        get() = _recetaRepository
            ?: error("RepositorioProvider no ha sido inicializado. Llama a init(context) primero.")

    val pedidoRepository: PedidoRepository
        get() = _pedidoRepository
            ?: error("RepositorioProvider no ha sido inicializado. Llama a init(context) primero.")

    val configuracionRepository: ConfiguracionRepository
        get() = _configuracionRepository
            ?: error("RepositorioProvider no ha sido inicializado. Llama a init(context) primero.")

    fun init(context: Context) {
        if (_ingredienteRepository == null || _recetaRepository == null || _pedidoRepository == null) {
            val db = PastelProDatabase.obtener(context)
            _ingredienteRepository = IngredienteRepositoryRoom(db.ingredienteDao())
            _recetaRepository = RecetaRepositoryRoom(db.recetaDao())
            _pedidoRepository = PedidoRepositoryRoom(db.pedidoDao())
            _configuracionRepository = ConfiguracionRepository(context.applicationContext)
        }
    }
}
