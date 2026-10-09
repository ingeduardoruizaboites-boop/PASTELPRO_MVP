package com.pastelpro.ui.screens.pedidos

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.pastelpro.R
import com.pastelpro.domain.model.Receta
import com.pastelpro.engine.MotorPrecio
import java.math.BigDecimal
import java.math.RoundingMode
import java.text.NumberFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NuevoPedidoScreen(
    onBack: () -> Unit,
    onGuardado: () -> Unit
) {
    val vm: NuevoPedidoViewModel = viewModel(factory = NuevoPedidoViewModel.Factory)
    val recetas by vm.recetas.collectAsState()
    val resultado by vm.resultado.collectAsState()
    val precios by vm.precios.collectAsState()

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(R.string.nuevo_pedido_titulo),
                        fontWeight = FontWeight.SemiBold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.accion_volver)
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    titleContentColor = MaterialTheme.colorScheme.primary,
                    navigationIconContentColor = MaterialTheme.colorScheme.primary
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // Barra de progreso
            Column(modifier = Modifier.padding(horizontal = 24.dp)) {
                Text(
                    text = stringResource(R.string.nuevo_pedido_paso_de, vm.paso, 4),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(Modifier.height(8.dp))
                LinearProgressIndicator(
                    progress = { vm.paso / 4f },
                    modifier = Modifier.fillMaxWidth(),
                    color = MaterialTheme.colorScheme.primary,
                    trackColor = MaterialTheme.colorScheme.surfaceVariant
                )
            }

            Spacer(Modifier.height(20.dp))

            // Contenido del paso actual
            Box(modifier = Modifier.weight(1f)) {
                when (vm.paso) {
                    1 -> Paso1SeleccionReceta(
                        recetas = recetas,
                        recetaSeleccionada = vm.recetaSeleccionada,
                        onSeleccionar = vm::seleccionarReceta
                    )
                    2 -> Paso2Porciones(vm)
                    3 -> Paso3Cliente(vm)
                    4 -> Paso4Resumen(vm, resultado, precios)
                }
            }

            // Botones de navegación
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                if (vm.paso > 1) {
                    TextButton(
                        onClick = vm::anterior,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            text = stringResource(R.string.setup_back),
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Button(
                    onClick = {
                        when (vm.paso) {
                            4 -> vm.guardar(onExito = onGuardado)
                            else -> vm.siguiente()
                        }
                    },
                    modifier = Modifier
                        .weight(2f)
                        .height(52.dp),
                    shape = RoundedCornerShape(12.dp),
                    enabled = when (vm.paso) {
                        1 -> vm.puedeAvanzarPaso1
                        2 -> vm.puedeAvanzarPaso2
                        3 -> vm.puedeAvanzarPaso3
                        4 -> vm.puedeGuardar
                        else -> false
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary
                    )
                ) {
                    Text(
                        text = if (vm.paso == 4)
                            stringResource(R.string.nuevo_pedido_guardar)
                        else
                            stringResource(R.string.setup_next),
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }
    }
}

// ═══════════════════════════════════════════════════════════════
// PASO 1
// ═══════════════════════════════════════════════════════════════
@Composable
private fun Paso1SeleccionReceta(
    recetas: List<Receta>,
    recetaSeleccionada: Receta?,
    onSeleccionar: (Receta) -> Unit
) {
    Column(modifier = Modifier.fillMaxSize()) {
        Text(
            text = stringResource(R.string.nuevo_pedido_paso1_titulo),
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.primary,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(horizontal = 24.dp)
        )

        Spacer(Modifier.height(16.dp))

        if (recetas.isEmpty()) {
            Box(
                Modifier
                    .fillMaxSize()
                    .padding(32.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = stringResource(R.string.nuevo_pedido_sin_recetas),
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center
                )
            }
        } else {
            LazyColumn(
                contentPadding = PaddingValues(horizontal = 24.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(recetas, key = { it.id }) { receta ->
                    val activa = recetaSeleccionada?.id == receta.id
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onSeleccionar(receta) },
                        shape = RoundedCornerShape(12.dp),
                        color = if (activa)
                            MaterialTheme.colorScheme.primaryContainer
                        else
                            MaterialTheme.colorScheme.surface,
                        shadowElevation = 1.dp
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = receta.nombre,
                                    style = MaterialTheme.typography.titleMedium,
                                    color = if (activa)
                                        MaterialTheme.colorScheme.onPrimaryContainer
                                    else
                                        MaterialTheme.colorScheme.onSurface,
                                    fontWeight = FontWeight.SemiBold
                                )
                                Text(
                                    text = "${receta.tipo} · ${receta.rendimientoTexto}",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = if (activa)
                                        MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                                    else
                                        MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            if (activa) {
                                Icon(
                                    imageVector = Icons.Filled.Check,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

// ═══════════════════════════════════════════════════════════════
// PASO 2
// ═══════════════════════════════════════════════════════════════
@Composable
private fun Paso2Porciones(vm: NuevoPedidoViewModel) {
    val receta = vm.recetaSeleccionada ?: return

    Column(modifier = Modifier.fillMaxSize().padding(horizontal = 24.dp)) {
        Text(
            text = stringResource(R.string.nuevo_pedido_paso2_titulo),
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.primary,
            fontWeight = FontWeight.SemiBold
        )
        Spacer(Modifier.height(6.dp))
        Text(
            text = stringResource(R.string.nuevo_pedido_base, receta.rendimientoTexto),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(Modifier.height(20.dp))

        OutlinedTextField(
            value = vm.porcionesTxt,
            onValueChange = vm::cambiarPorciones,
            modifier = Modifier.fillMaxWidth(),
            label = { Text(stringResource(R.string.escalar_input_label, receta.rendimientoUnidad)) },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            singleLine = true
        )

        Spacer(Modifier.height(12.dp))

        Text(
            text = stringResource(R.string.escalar_rapidos),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(Modifier.height(8.dp))

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            AssistChip(
                onClick = { vm.setPorciones(receta.rendimientoCantidad * 2) },
                label = { Text("×2") },
                colors = AssistChipDefaults.assistChipColors(
                    labelColor = MaterialTheme.colorScheme.primary
                )
            )
            AssistChip(
                onClick = { vm.setPorciones(receta.rendimientoCantidad * 3) },
                label = { Text("×3") },
                colors = AssistChipDefaults.assistChipColors(
                    labelColor = MaterialTheme.colorScheme.primary
                )
            )
            AssistChip(
                onClick = { vm.setPorciones(receta.rendimientoCantidad * 10) },
                label = { Text("×10") },
                colors = AssistChipDefaults.assistChipColors(
                    labelColor = MaterialTheme.colorScheme.primary
                )
            )
            AssistChip(
                onClick = { vm.setPorciones(100) },
                label = { Text("100") },
                colors = AssistChipDefaults.assistChipColors(
                    labelColor = MaterialTheme.colorScheme.primary
                )
            )
        }
    }
}

// ═══════════════════════════════════════════════════════════════
// PASO 4
// ═══════════════════════════════════════════════════════════════
@Composable
private fun Paso4Resumen(
    vm: NuevoPedidoViewModel,
    resultado: com.pastelpro.engine.CalculadoraCostoReceta.Resultado?,
    precios: MotorPrecio.TresPrecios?
) {
    if (resultado == null || precios == null) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text(stringResource(R.string.nuevo_pedido_calculando))
        }
        return
    }

    val formatoMoneda = NumberFormat.getCurrencyInstance(Locale("es", "MX"))

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(horizontal = 24.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Text(
                text = stringResource(R.string.nuevo_pedido_paso4_titulo),
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(Modifier.height(4.dp))
            Text(
                text = stringResource(R.string.nuevo_pedido_resumen, vm.porcionesNuevas, vm.recetaSeleccionada?.rendimientoUnidad ?: ""),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        if (resultado.faltantes.isNotEmpty()) {
            item {
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.errorContainer,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Filled.Warning,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.error,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(Modifier.padding(horizontal = 4.dp))
                            Text(
                                text = stringResource(R.string.costo_precio_faltantes, resultado.faltantes.size),
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onErrorContainer,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                        resultado.faltantes.forEach { nombre ->
                            Text(
                                text = "· $nombre",
                                modifier = Modifier.padding(start = 24.dp, top = 2.dp),
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onErrorContainer
                            )
                        }
                    }
                }
            }
        }

        item {
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = stringResource(R.string.costo_precio_costo_total),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.85f)
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(
                        text = formatoMoneda.format(resultado.costoTotal.setScale(2, RoundingMode.HALF_UP)),
                        fontSize = 36.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                }
            }
        }

        item {
            Text(
                text = stringResource(R.string.costo_precio_precios_titulo),
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onBackground,
                fontWeight = FontWeight.SemiBold
            )
        }

        item {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                PrecioMiniCard(
                    etiqueta = stringResource(R.string.costo_precio_minimo),
                    precio = precios.minimo.precio,
                    activo = false,
                    formatoMoneda = formatoMoneda,
                    modifier = Modifier.weight(1f)
                )
                PrecioMiniCard(
                    etiqueta = stringResource(R.string.costo_precio_recomendado),
                    precio = precios.recomendado.precio,
                    activo = true,
                    formatoMoneda = formatoMoneda,
                    modifier = Modifier.weight(1f)
                )
                PrecioMiniCard(
                    etiqueta = stringResource(R.string.costo_precio_premium),
                    precio = precios.premium.precio,
                    activo = false,
                    formatoMoneda = formatoMoneda,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        item {
            Text(
                text = stringResource(R.string.nuevo_pedido_precio_acordado),
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onBackground,
                fontWeight = FontWeight.SemiBold
            )
        }

        item {
            OutlinedTextField(
                value = vm.precioAcordadoTxt,
                onValueChange = vm::cambiarPrecioAcordado,
                modifier = Modifier.fillMaxWidth(),
                label = { Text(stringResource(R.string.nuevo_pedido_precio_input_label)) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                singleLine = true
            )
        }

        item { Spacer(Modifier.height(16.dp)) }
    }
}

@Composable
private fun PrecioMiniCard(
    etiqueta: String,
    precio: BigDecimal,
    activo: Boolean,
    formatoMoneda: NumberFormat,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        color = if (activo)
            MaterialTheme.colorScheme.primary
        else
            MaterialTheme.colorScheme.surface,
        shadowElevation = 1.dp
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = etiqueta,
                style = MaterialTheme.typography.bodyMedium,
                color = if (activo)
                    MaterialTheme.colorScheme.onPrimary
                else
                    MaterialTheme.colorScheme.onSurfaceVariant,
                fontWeight = FontWeight.Medium
            )
            Spacer(Modifier.height(4.dp))
            Text(
                text = formatoMoneda.format(precio.setScale(2, RoundingMode.HALF_UP)),
                style = MaterialTheme.typography.titleMedium,
                color = if (activo)
                    MaterialTheme.colorScheme.onPrimary
                else
                    MaterialTheme.colorScheme.onSurface,
                fontWeight = FontWeight.Bold
            )
        }
    }
}
