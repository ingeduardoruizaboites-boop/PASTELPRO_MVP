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
    private val application: android.app.Application,
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

    /**
     * Actualiza un pedido existente.
     * Si la fecha u hora cambiaron, reprograma las notificaciones.
     */
    fun actualizar(pedido: Pedido, diasAntes: Int, horaNotificacion: String, notifHabilitadas: Boolean) {
        viewModelScope.launch {
            repository.actualizar(pedido)

            // Si sigue PENDIENTE, reprogramar notificaciones
            if (pedido.estado == EstadoPedido.PENDIENTE) {
                try {
                    com.pastelpro.notificaciones.NotificacionScheduler.reprogramarRecordatorios(
                        context = application,
                        pedido = pedido,
                        diasAntes = diasAntes,
                        horaNotificacion = horaNotificacion,
                        habilitadas = notifHabilitadas
                    )
                } catch (e: Exception) {
                    com.pastelpro.diagnostico.DiagnosticLogger.logError(
                        "PedidosVM", "Error reprogramando notificaciones", e
                    )
                }
            }
        }
    }

    fun cambiarEstado(id: String, nuevoEstado: EstadoPedido) {
        viewModelScope.launch {
            repository.cambiarEstado(id, nuevoEstado)
            // Si pasa a ENTREGADO, cancelar recordatorios
            if (nuevoEstado == EstadoPedido.ENTREGADO) {
                com.pastelpro.notificaciones.NotificacionScheduler.cancelarRecordatorios(application, id)
            }
        }
    }

    companion object {
        fun factory(application: android.app.Application): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return PedidosViewModel(application, RepositorioProvider.pedidoRepository) as T
            }
        }
    }
}
