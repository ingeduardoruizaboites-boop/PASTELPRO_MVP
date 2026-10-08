package com.pastelpro.ui.screens.recetas

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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.pastelpro.R
import com.pastelpro.domain.model.IngredienteDeReceta
import com.pastelpro.domain.model.Receta

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecetaDetalleScreen(
    recetaId: String,
    onBack: () -> Unit
) {
    val vm: RecetaDetalleViewModel = viewModel(
        factory = RecetaDetalleViewModel.factory(recetaId),
        key = recetaId
    )
    val state by vm.uiState.collectAsState()
    val disponibles by vm.ingredientesDisponibles.collectAsState()
    var mostrarAgregar by remember { mutableStateOf(false) }
    var mostrarEscalar by remember { mutableStateOf(false) }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = when (val s = state) {
                            is RecetaDetalleUiState.ConDatos -> s.receta.nombre
                            else -> stringResource(R.string.receta_detalle_titulo)
                        },
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
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            when (val s = state) {
                RecetaDetalleUiState.Cargando -> {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text(stringResource(R.string.estado_cargando))
                    }
                }
                RecetaDetalleUiState.NoEncontrada -> {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text(stringResource(R.string.receta_no_encontrada))
                    }
                }
                is RecetaDetalleUiState.ConDatos -> {
                    ContenidoReceta(
                        receta = s.receta,
                        onEliminar = vm::eliminarIngrediente,
                        onAgregarClick = { mostrarAgregar = true },
                        onEscalarClick = { mostrarEscalar = true }
                    )
                }
            }
        }
    }

    if (mostrarAgregar) {
        AgregarIngredienteSheet(
            disponibles = disponibles,
            onDismiss = { mostrarAgregar = false },
            onAgregar = { ingrediente, cantidad, unidad ->
                vm.agregarIngrediente(ingrediente, cantidad, unidad)
                mostrarAgregar = false
            }
        )
    }

    val recetaActual = (state as? RecetaDetalleUiState.ConDatos)?.receta
    if (mostrarEscalar && recetaActual != null) {
        EscalarRecetaSheet(
            receta = recetaActual,
            onDismiss = { mostrarEscalar = false }
        )
    }
}

@Composable
private fun ContenidoReceta(
    receta: Receta,
    onEliminar: (String) -> Unit,
    onAgregarClick: () -> Unit,
    onEscalarClick: () -> Unit
) {
    Column(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.padding(horizontal = 24.dp, vertical = 8.dp)) {
            Text(
                text = receta.tipo,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = receta.rendimientoTexto,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(Modifier.height(16.dp))
            Text(
                text = stringResource(R.string.receta_ingredientes_titulo),
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onBackground,
                fontWeight = FontWeight.SemiBold
            )
        }

        if (receta.ingredientes.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(32.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = stringResource(R.string.receta_sin_ingredientes),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(bottom = 16.dp)
            ) {
                items(receta.ingredientes, key = { it.ingredienteId }) { ing ->
                    IngredienteDeRecetaCard(ing, onEliminar = { onEliminar(ing.ingredienteId) })
                }
            }
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            OutlinedButton(
                onClick = onAgregarClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(Icons.Filled.Add, contentDescription = null)
                Spacer(Modifier.padding(horizontal = 4.dp))
                Text(
                    text = stringResource(R.string.receta_agregar_ingrediente),
                    fontWeight = FontWeight.SemiBold
                )
            }
            Button(
                onClick = onEscalarClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                ),
                enabled = receta.ingredientes.isNotEmpty()
            ) {
                Icon(Icons.Filled.TrendingUp, contentDescription = null)
                Spacer(Modifier.padding(horizontal = 4.dp))
                Text(
                    text = stringResource(R.string.receta_escalar),
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}

@Composable
private fun IngredienteDeRecetaCard(
    item: IngredienteDeReceta,
    onEliminar: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        color = MaterialTheme.colorScheme.surface,
        shadowElevation = 1.dp
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = item.nombre,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(Modifier.height(2.dp))
                Text(
                    text = "${item.cantidad.stripTrailingZeros().toPlainString()} ${item.unidad}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.primary
                )
            }
            IconButton(onClick = onEliminar) {
                Icon(
                    imageVector = Icons.Filled.Delete,
                    contentDescription = stringResource(R.string.accion_eliminar),
                    tint = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}
