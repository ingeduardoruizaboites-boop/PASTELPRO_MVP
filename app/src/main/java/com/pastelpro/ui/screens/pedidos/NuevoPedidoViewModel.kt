package com.pastelpro.ui.screens.pedidos

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.pastelpro.data.repository.RepositorioProvider
import com.pastelpro.diagnostico.DiagnosticLogger
import com.pastelpro.domain.model.Ingrediente
import com.pastelpro.domain.model.Pedido
import com.pastelpro.domain.model.Receta
import com.pastelpro.domain.repository.IngredienteRepository
import com.pastelpro.domain.repository.PedidoRepository
import com.pastelpro.domain.repository.RecetaRepository
import com.pastelpro.engine.CalculadoraCostoReceta
import com.pastelpro.engine.MotorEscalado
import com.pastelpro.engine.MotorPrecio
import com.pastelpro.engine.Unidad
import com.pastelpro.notificaciones.NotificacionScheduler
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.math.BigDecimal
import java.math.RoundingMode

private const val TAG = "PastelProWizard"

class NuevoPedidoViewModel(
    private val application: Application,
    private val recetaRepository: RecetaRepository,
    private val ingredienteRepository: IngredienteRepository,
    private val pedidoRepository: PedidoRepository
) : ViewModel() {

    var paso by mutableIntStateOf(1)
        private set

    var recetaSeleccionada by mutableStateOf<Receta?>(null)
        private set

    var porcionesTxt by mutableStateOf("")
        private set

    var cliente by mutableStateOf("")
        private set

    var telefonoContacto by mutableStateOf("")
        private set

    var fechaEntregaIso by mutableStateOf<String?>(null)
        private set

    var horaEntrega by mutableStateOf("")
        private set

    var direccionEntrega by mutableStateOf("")
        private set

    var notas by mutableStateOf("")
        private set

    var precioAcordadoTxt by mutableStateOf("")
        private set

    /** Marca si el usuario tocó el precio manualmente. Si es false, se auto-actualiza. */
    private var precioEditadoManualmente = false

    val recetas: StateFlow<List<Receta>> =
        recetaRepository.observarTodas()
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    val inventario: StateFlow<List<Ingrediente>> =
        ingredienteRepository.observarTodos()
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    private val _resultado = MutableStateFlow<CalculadoraCostoReceta.Resultado?>(null)
    val resultado: StateFlow<CalculadoraCostoReceta.Resultado?> = _resultado.asStateFlow()

    private val _precios = MutableStateFlow<MotorPrecio.TresPrecios?>(null)
    val precios: StateFlow<MotorPrecio.TresPrecios?> = _precios.asStateFlow()

    val porcionesNuevas: Int
        get() = porcionesTxt.toIntOrNull() ?: 0

    val puedeAvanzarPaso1: Boolean
        get() = recetaSeleccionada != null

    val puedeAvanzarPaso2: Boolean
        get() = porcionesNuevas > 0 && recetaSeleccionada != null

    val puedeAvanzarPaso3: Boolean
        get() = true

    val puedeGuardar: Boolean
        get() = (precioAcordadoTxt.toBigDecimalOrNull() ?: BigDecimal.ZERO) > BigDecimal.ZERO

    init {
        // Cuando el inventario cambie (Room emite), recalcular si hay receta.
        // Resuelve el bug de timing: al abrir el wizard, el inventario aún no está cargado.
        viewModelScope.launch {
            inventario.collect { inv ->
                DiagnosticLogger.log(TAG, "Inventario emitido: ${inv.size} items")
                if (recetaSeleccionada != null && porcionesNuevas > 0) {
                    recalcularInterno(inv)
                }
            }
        }
    }

    fun seleccionarReceta(receta: Receta) {
        DiagnosticLogger.log(TAG, "Receta seleccionada: ${receta.nombre}")
        recetaSeleccionada = receta
        // Reset precio y flag cuando cambia la receta
        precioAcordadoTxt = ""
        precioEditadoManualmente = false
        recalcularInterno(inventario.value)
    }

    fun cambiarPorciones(txt: String) {
        porcionesTxt = txt.filter { it.isDigit() }
        recalcularInterno(inventario.value)
    }

    fun setPorciones(n: Int) {
        porcionesTxt = n.toString()
        recalcularInterno(inventario.value)
    }

    fun cambiarCliente(txt: String) { cliente = txt }
    fun cambiarTelefono(txt: String) { telefonoContacto = txt }
    fun cambiarFecha(iso: String?) { fechaEntregaIso = iso }
    fun cambiarHora(txt: String) { horaEntrega = txt }
    fun cambiarDireccion(txt: String) { direccionEntrega = txt }
    fun cambiarNotas(txt: String) { notas = txt }

    fun cambiarPrecioAcordado(txt: String) {
        precioAcordadoTxt = txt.filter { it.isDigit() || it == '.' || it == ',' }
        precioEditadoManualmente = true
    }

    fun siguiente() {
        if (paso < 4) paso++
    }

    fun anterior() {
        if (paso > 1) paso--
    }

    fun guardar(onExito: () -> Unit) {
        val receta = recetaSeleccionada ?: return
        val costo = _resultado.value?.costoTotal ?: return
        val precio = precioAcordadoTxt.replace(',', '.').toBigDecimalOrNull() ?: return

        val pedido = Pedido(
            recetaId = receta.id,
            recetaNombre = receta.nombre,
            cliente = cliente.trim().ifBlank { null },
            telefonoContacto = telefonoContacto.trim().ifBlank { null },
            porciones = porcionesNuevas,
            costoTotal = costo,
            precioAcordado = precio,
            fechaEntrega = fechaEntregaIso,
            horaEntrega = horaEntrega.trim().ifBlank { null },
            direccionEntrega = direccionEntrega.trim().ifBlank { null },
            notas = notas.trim().ifBlank { null }
        )

        DiagnosticLogger.log(TAG, "Guardando pedido: costo=$costo, precio=$precio")

        viewModelScope.launch {
            pedidoRepository.agregar(pedido)

            // Programar recordatorios
            try {
                val configRepo = com.pastelpro.data.repository.RepositorioProvider.configuracionRepository
                val habilitadas = configRepo.notificacionesHabilitadas.first()
                val diasAntes = configRepo.diasAntes.first()
                val hora = configRepo.horaNotificacion.first()

                NotificacionScheduler.programarRecordatorios(
                    context = application,
                    pedido = pedido,
                    diasAntes = diasAntes,
                    horaNotificacion = hora,
                    habilitadas = habilitadas
                )
            } catch (e: Exception) {
                DiagnosticLogger.logError(TAG, "Error programando recordatorios", e)
            }

            onExito()
        }
    }

    /**
     * Recalcula el resultado con el inventario provisto.
     * IMPORTANTE: recibe el inventario como parámetro para no depender del timing del StateFlow.
     */
    private fun recalcularInterno(inventarioActual: List<Ingrediente>) {
        val receta = recetaSeleccionada ?: return
        val porciones = porcionesNuevas

        if (porciones <= 0) {
            _resultado.value = null
            _precios.value = null
            return
        }

        DiagnosticLogger.log(TAG, "Recalculando: receta=${receta.nombre}, porciones=$porciones, inventario=${inventarioActual.size}")

        // Si el inventario aún no llegó, no calculamos (esperamos al collect del init).
        if (inventarioActual.isEmpty()) {
            DiagnosticLogger.log(TAG, "Inventario vacío, esperando emisión de Room...")
            return
        }

        val factor = MotorEscalado.factorPorPersonas(receta.rendimientoCantidad, porciones)
        val ingredientesEscalados = receta.ingredientes.map { item ->
            val cantEscalada = MotorEscalado.escalar(item.cantidad, factor)
            val unidad = Unidad.desdeSimbolo(item.unidad)
            val cantRedondeada = if (unidad != null)
                MotorEscalado.redondearUsable(cantEscalada, unidad)
            else cantEscalada
            item.copy(cantidad = cantRedondeada)
        }

        val recetaEscalada = receta.copy(
            rendimientoCantidad = porciones,
            ingredientes = ingredientesEscalados
        )

        val r = CalculadoraCostoReceta.calcular(recetaEscalada, inventarioActual)
        val p = CalculadoraCostoReceta.calcularPrecios(r)

        DiagnosticLogger.log(TAG, "Costo total=${r.costoTotal}, faltantes=${r.faltantes.size}")
        DiagnosticLogger.log(TAG, "Precios: min=${p.minimo.precio}, rec=${p.recomendado.precio}, prem=${p.premium.precio}")

        _resultado.value = r
        _precios.value = p

        // Auto-set precio solo si el usuario no lo ha editado manualmente.
        if (!precioEditadoManualmente) {
            precioAcordadoTxt = p.recomendado.precio
                .setScale(2, RoundingMode.HALF_UP)
                .toPlainString()
        }
    }

    companion object {
        fun factory(application: Application): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return NuevoPedidoViewModel(
                    application = application,
                    recetaRepository = RepositorioProvider.recetaRepository,
                    ingredienteRepository = RepositorioProvider.ingredienteRepository,
                    pedidoRepository = RepositorioProvider.pedidoRepository
                ) as T
            }
        }
    }
}
