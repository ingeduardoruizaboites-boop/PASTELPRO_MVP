package com.pastelpro.ui.screens.mas

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.pastelpro.data.repository.RepositorioProvider
import com.pastelpro.domain.repository.PedidoRepository
import com.pastelpro.notificaciones.NotificacionScheduler
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class NotificacionesViewModel(
    private val application: android.app.Application,
    private val pedidoRepository: PedidoRepository
) : ViewModel() {

    private val configRepo = RepositorioProvider.configuracionRepository

    val habilitadas: StateFlow<Boolean> = configRepo.notificacionesHabilitadas
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), true)

    val diasAntes: StateFlow<Int> = configRepo.diasAntes
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), 1)

    val horaNotificacion: StateFlow<String> = configRepo.horaNotificacion
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), "09:00")

    fun setHabilitadas(v: Boolean) {
        viewModelScope.launch { configRepo.setNotificacionesHabilitadas(v) }
    }

    fun setDiasAntes(d: Int) {
        viewModelScope.launch { configRepo.setDiasAntes(d) }
    }

    fun setHora(h: String) {
        viewModelScope.launch { configRepo.setHoraNotificacion(h) }
    }

    companion object {
        fun factory(application: android.app.Application): ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return NotificacionesViewModel(
                        application = application,
                        pedidoRepository = RepositorioProvider.pedidoRepository
                    ) as T
                }
            }
    }
}
