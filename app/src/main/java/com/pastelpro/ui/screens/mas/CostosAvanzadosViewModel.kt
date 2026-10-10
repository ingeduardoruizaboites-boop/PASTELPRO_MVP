package com.pastelpro.ui.screens.mas

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.pastelpro.data.repository.RepositorioProvider
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class CostosAvanzadosViewModel : ViewModel() {

    private val configRepo = RepositorioProvider.configuracionRepository

    val costoHorneadaActivo: StateFlow<Boolean> = configRepo.costoHorneadaActivo
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), false)

    val costoHorneadaMonto: StateFlow<Int> = configRepo.costoHorneadaMonto
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), 10)

    fun setActivo(v: Boolean) {
        viewModelScope.launch { configRepo.setCostoHorneadaActivo(v) }
    }

    fun setMonto(m: Int) {
        viewModelScope.launch { configRepo.setCostoHorneadaMonto(m) }
    }

    companion object {
        val Factory: ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return CostosAvanzadosViewModel() as T
            }
        }
    }
}
