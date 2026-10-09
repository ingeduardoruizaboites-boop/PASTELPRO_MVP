package com.pastelpro.ui.screens.ingredientes

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.pastelpro.data.repository.RepositorioProvider
import com.pastelpro.domain.model.Ingrediente
import com.pastelpro.domain.repository.IngredienteRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

sealed interface IngredientesUiState {
    data object Cargando : IngredientesUiState
    data object Vacio : IngredientesUiState
    data class ConDatos(val ingredientes: List<Ingrediente>) : IngredientesUiState
}

class IngredientesViewModel(
    private val repository: IngredienteRepository
) : ViewModel() {

    val uiState: StateFlow<IngredientesUiState> =
        repository.observarTodos()
            .map { lista ->
                if (lista.isEmpty()) IngredientesUiState.Vacio
                else IngredientesUiState.ConDatos(lista)
            }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = IngredientesUiState.Cargando
            )

    fun agregar(ingrediente: Ingrediente) {
        viewModelScope.launch { repository.agregar(ingrediente) }
    }

    fun eliminar(id: String) {
        viewModelScope.launch { repository.eliminar(id) }
    }

    fun reiniciarEjemplos() {
        viewModelScope.launch { repository.reiniciarEjemplos() }
    }

    fun actualizar(ingrediente: Ingrediente) {
        viewModelScope.launch { repository.actualizar(ingrediente) }
    }

    // ─── Factory manual (sin Hilt en MVP) ───
    companion object {
        val Factory: ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return IngredientesViewModel(
                    RepositorioProvider.ingredienteRepository
                ) as T
            }
        }
    }
}
