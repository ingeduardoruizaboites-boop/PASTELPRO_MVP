package com.pastelpro.ui.screens.recetas

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.pastelpro.data.repository.RepositorioProvider
import com.pastelpro.domain.model.Receta
import com.pastelpro.domain.repository.RecetaRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

sealed interface RecetasUiState {
    data object Cargando : RecetasUiState
    data object Vacio : RecetasUiState
    data class ConDatos(val recetas: List<Receta>) : RecetasUiState
}

class RecetasViewModel(
    private val repository: RecetaRepository
) : ViewModel() {

    val uiState: StateFlow<RecetasUiState> =
        repository.observarTodas()
            .map { lista ->
                if (lista.isEmpty()) RecetasUiState.Vacio
                else RecetasUiState.ConDatos(lista)
            }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = RecetasUiState.Cargando
            )

    fun agregar(receta: Receta) {
        viewModelScope.launch { repository.agregar(receta) }
    }

    fun eliminar(id: String) {
        viewModelScope.launch { repository.eliminar(id) }
    }

    companion object {
        val Factory: ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return RecetasViewModel(
                    RepositorioProvider.recetaRepository
                ) as T
            }
        }
    }
}
