package com.pastelpro.data.repository

import com.pastelpro.domain.repository.IngredienteRepository

/**
 * Service Locator simple.
 * En Bloque 2B se reemplaza por Room aquí adentro SIN tocar el resto del código.
 * En V2 se puede migrar a Hilt si el proyecto lo justifica.
 */
object RepositorioProvider {
    val ingredienteRepository: IngredienteRepository by lazy {
        IngredienteRepositoryEnMemoria()
    }
}
