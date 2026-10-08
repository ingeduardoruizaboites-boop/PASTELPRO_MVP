package com.pastelpro.ui.screens.recetas

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.pastelpro.data.repository.RepositorioProvider
import com.pastelpro.domain.model.IngredienteDeReceta
import com.pastelpro.domain.model.Ingrediente
import com.pastelpro.domain.model.Receta
import com.pastelpro.domain.repository.IngredienteRepository
import com.pastelpro.domain.repository.RecetaRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

sealed interface RecetaDetalleUiState {
    data object Cargando : RecetaDetalleUiState
    data object NoEncontrada : RecetaDetalleUiState
    data class ConDatos(val receta: Receta) : RecetaDetalleUiState
}

class RecetaDetalleViewModel(
    private val recetaId: String,
    private val recetaRepository: RecetaRepository,
    private val ingredienteRepository: IngredienteRepository
) : ViewModel() {

    private val _recetaActual = MutableStateFlow<Receta?>(null)

    val uiState: StateFlow<RecetaDetalleUiState> = _recetaActual
        .map { receta ->
            when {
                receta == null -> RecetaDetalleUiState.NoEncontrada
                else -> RecetaDetalleUiState.ConDatos(receta)
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = RecetaDetalleUiState.Cargando
        )

    /** Lista de ingredientes de inventario disponibles para agregar. */
    val ingredientesDisponibles: StateFlow<List<Ingrediente>> =
        ingredienteRepository.observarTodos()
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = emptyList()
            )

    init {
        cargar()
    }

    private fun cargar() {
        viewModelScope.launch {
            _recetaActual.value = recetaRepository.obtener(recetaId)
        }
    }

    fun agregarIngrediente(ingrediente: Ingrediente, cantidad: java.math.BigDecimal, unidad: String) {
        val actual = _recetaActual.value ?: return
        val nuevo = IngredienteDeReceta(
            ingredienteId = ingrediente.id,
            nombre = ingrediente.nombre,
            cantidad = cantidad,
            unidad = unidad
        )
        // Evitar duplicados por ingredienteId
        val filtrados = actual.ingredientes.filterNot { it.ingredienteId == ingrediente.id }
        val actualizada = actual.copy(ingredientes = filtrados + nuevo)
        persistir(actualizada)
    }

    fun eliminarIngrediente(ingredienteId: String) {
        val actual = _recetaActual.value ?: return
        val actualizada = actual.copy(
            ingredientes = actual.ingredientes.filterNot { it.ingredienteId == ingredienteId }
        )
        persistir(actualizada)
    }

    private fun persistir(receta: Receta) {
        viewModelScope.launch {
            recetaRepository.actualizar(receta)
            _recetaActual.value = receta
        }
    }

    companion object {
        fun factory(recetaId: String): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return RecetaDetalleViewModel(
                    recetaId = recetaId,
                    recetaRepository = RepositorioProvider.recetaRepository,
                    ingredienteRepository = RepositorioProvider.ingredienteRepository
                ) as T
            }
        }
    }
}
