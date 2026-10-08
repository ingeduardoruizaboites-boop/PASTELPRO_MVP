package com.pastelpro.ui.screens.recetas

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.pastelpro.data.repository.RepositorioProvider
import com.pastelpro.diagnostico.DiagnosticLogger
import com.pastelpro.domain.model.Ingrediente
import com.pastelpro.domain.model.Receta
import com.pastelpro.domain.repository.IngredienteRepository
import com.pastelpro.domain.repository.RecetaRepository
import com.pastelpro.engine.CalculadoraCostoReceta
import com.pastelpro.engine.MotorPrecio
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

private const val TAG = "PastelProCosto"

sealed interface CostoPrecioUiState {
    data object Cargando : CostoPrecioUiState
    data object NoEncontrada : CostoPrecioUiState
    data class ConDatos(
        val receta: Receta,
        val resultado: CalculadoraCostoReceta.Resultado,
        val precios: MotorPrecio.TresPrecios,
        val numInventario: Int
    ) : CostoPrecioUiState
}

class CostoPrecioViewModel(
    private val recetaId: String,
    private val recetaRepository: RecetaRepository,
    private val ingredienteRepository: IngredienteRepository
) : ViewModel() {

    private val _state = MutableStateFlow<CostoPrecioUiState>(CostoPrecioUiState.Cargando)
    val uiState: StateFlow<CostoPrecioUiState> = _state.asStateFlow()

    init {
        observar()
    }

    /**
     * Combina el flujo del inventario con la carga puntual de la receta.
     * Cuando AMBOS están disponibles, recalcula.
     */
    private fun observar() {
        viewModelScope.launch {
            DiagnosticLogger.seccion("CostoPrecioViewModel.observar()")
            DiagnosticLogger.log("VM", "Cargando receta id=$recetaId")

            val receta = recetaRepository.obtener(recetaId)

            if (receta == null) {
                DiagnosticLogger.logError("VM", "Receta no encontrada: $recetaId")
                _state.value = CostoPrecioUiState.NoEncontrada
                return@launch
            }

            DiagnosticLogger.log("VM", "Receta: ${receta.nombre}")
            DiagnosticLogger.log("VM", "  · ingredientes: ${receta.ingredientes.size}")
            DiagnosticLogger.log("VM", "  · rendimiento: ${receta.rendimientoCantidad} ${receta.rendimientoUnidad}")

            ingredienteRepository.observarTodos().collect { inventario ->
                DiagnosticLogger.seccion("Inventario emitido")
                DiagnosticLogger.log("VM", "Inventario: ${inventario.size} ingredientes")
                inventario.forEach { ing ->
                    DiagnosticLogger.log("VM", "  · [id=${ing.id}] ${ing.nombre} precio=${ing.precioCompra} cantidad=${ing.cantidadCompra} ${ing.unidadCompra}")
                }
                receta.ingredientes.forEach { r ->
                    DiagnosticLogger.log("VM", "  · receta pide: [id=${r.ingredienteId}] ${r.nombre} ${r.cantidad} ${r.unidad}")
                }

                try {
                    DiagnosticLogger.log("VM", "Calculando resultado...")
                    val resultado = CalculadoraCostoReceta.calcular(receta, inventario)
                    DiagnosticLogger.log("VM", "Resultado: costoTotal=${resultado.costoTotal}, faltantes=${resultado.faltantes.size}, desglose=${resultado.desglose.size}")

                    DiagnosticLogger.log("VM", "Calculando precios...")
                    val precios = CalculadoraCostoReceta.calcularPrecios(resultado)
                    DiagnosticLogger.log("VM", "Precios: min=${precios.minimo.precio}, rec=${precios.recomendado.precio}, prem=${precios.premium.precio}")

                    DiagnosticLogger.log("VM", "Emitiendo estado ConDatos...")
                    _state.value = CostoPrecioUiState.ConDatos(
                        receta = receta,
                        resultado = resultado,
                        precios = precios,
                        numInventario = inventario.size
                    )
                    DiagnosticLogger.log("VM", "Estado emitido OK")
                } catch (e: Exception) {
                    DiagnosticLogger.logError("VM", "Error en cálculo", e)
                }
            }
        }
    }

    companion object {
        fun factory(recetaId: String): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return CostoPrecioViewModel(
                    recetaId = recetaId,
                    recetaRepository = RepositorioProvider.recetaRepository,
                    ingredienteRepository = RepositorioProvider.ingredienteRepository
                ) as T
            }
        }
    }
}
