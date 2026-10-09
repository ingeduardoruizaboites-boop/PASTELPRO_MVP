package com.pastelpro.ui.screens.pedidos

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.pastelpro.data.repository.RepositorioProvider
import com.pastelpro.domain.model.EstadoPedido
import com.pastelpro.domain.model.Pedido
import com.pastelpro.domain.repository.PedidoRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

sealed interface PedidosUiState {
    data object Cargando : PedidosUiState
    data object Vacio : PedidosUiState
    data class ConDatos(val pedidos: List<Pedido>) : PedidosUiState
}

class PedidosViewModel(
    private val repository: PedidoRepository
) : ViewModel() {

    val uiState: StateFlow<PedidosUiState> =
        repository.observarTodos()
            .map { lista ->
                if (lista.isEmpty()) PedidosUiState.Vacio
                else PedidosUiState.ConDatos(lista)
            }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = PedidosUiState.Cargando
            )

    fun eliminar(id: String) {
        viewModelScope.launch { repository.eliminar(id) }
    }

    fun cambiarEstado(id: String, nuevoEstado: EstadoPedido) {
        viewModelScope.launch { repository.cambiarEstado(id, nuevoEstado) }
    }

    companion object {
        val Factory: ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return PedidosViewModel(RepositorioProvider.pedidoRepository) as T
            }
        }
    }
}
