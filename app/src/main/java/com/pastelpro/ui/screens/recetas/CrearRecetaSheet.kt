package com.pastelpro.ui.screens.recetas

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.pastelpro.R
import com.pastelpro.domain.model.Receta
import com.pastelpro.engine.MotorMoldes
import com.pastelpro.ui.components.EstadoMolde
import com.pastelpro.ui.components.SelectorMoldes
import java.math.BigDecimal

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CrearRecetaSheet(
    onDismiss: () -> Unit,
    onGuardar: (Receta) -> Unit,
    cm3PorPorcion: Int = 125
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    // ── Campos base ──
    var nombre by remember { mutableStateOf("") }
    var tipo by remember { mutableStateOf("") }
    var rendimientoCantidadTxt by remember { mutableStateOf("") }
    var rendimientoUnidad by remember { mutableStateOf("porciones") }
    var unidadDropdown by remember { mutableStateOf(false) }

    // ── Campos molde (nuevo sistema) ──
    var mostrarMolde by remember { mutableStateOf(false) }
    var estadoMolde by remember { mutableStateOf(EstadoMolde()) }

    val cantidadInt = rendimientoCantidadTxt.toIntOrNull()
    val puedeGuardar = nombre.isNotBlank()
            && tipo.isNotBlank()
            && cantidadInt != null && cantidadInt > 0
            && rendimientoUnidad.isNotBlank()

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp, vertical = 8.dp)
        ) {
            Text(
                text = stringResource(R.string.recetas_nueva_titulo),
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.SemiBold
            )

            Spacer(Modifier.height(20.dp))

            OutlinedTextField(
                value = nombre,
                onValueChange = { nombre = it },
                modifier = Modifier.fillMaxWidth(),
                label = { Text(stringResource(R.string.campo_nombre)) },
                singleLine = true
            )

            Spacer(Modifier.height(12.dp))

            OutlinedTextField(
                value = tipo,
                onValueChange = { tipo = it },
                modifier = Modifier.fillMaxWidth(),
                label = { Text(stringResource(R.string.campo_tipo)) },
                singleLine = true
            )

            Spacer(Modifier.height(12.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = rendimientoCantidadTxt,
                    onValueChange = { rendimientoCantidadTxt = it.filter { c -> c.isDigit() } },
                    modifier = Modifier.weight(1f),
                    label = { Text(stringResource(R.string.campo_cantidad)) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true
                )

                // Dropdown inline para unidad de rendimiento
                Box(modifier = Modifier.weight(1f)) {
                    OutlinedTextField(
                        value = rendimientoUnidad,
                        onValueChange = { },
                        modifier = Modifier.fillMaxWidth(),
                        readOnly = true,
                        label = { Text(stringResource(R.string.campo_rendimiento_unidad)) },
                        trailingIcon = {
                            IconButton(onClick = { unidadDropdown = true }) {
                                Icon(Icons.Filled.ArrowDropDown, contentDescription = null)
                            }
                        }
                    )
                    DropdownMenu(
                        expanded = unidadDropdown,
                        onDismissRequest = { unidadDropdown = false }
                    ) {
                        listOf("porciones", "personas", "piezas", "unidades").forEach { opcion ->
                            DropdownMenuItem(
                                text = { Text(opcion) },
                                onClick = {
                                    rendimientoUnidad = opcion
                                    unidadDropdown = false
                                }
                            )
                        }
                    }
                }
            }

            Spacer(Modifier.height(20.dp))

            // ═══ SECCIÓN MOLDE ═══
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = MaterialTheme.colorScheme.surface,
                modifier = Modifier.fillMaxWidth(),
                shadowElevation = 1.dp
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { mostrarMolde = !mostrarMolde }
                    ) {
                        Text(
                            text = stringResource(R.string.receta_molde_seccion),
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.SemiBold,
                            modifier = Modifier.weight(1f)
                        )
                        Text(
                            text = if (mostrarMolde)
                                stringResource(R.string.accion_ocultar)
                            else
                                stringResource(R.string.accion_agregar),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }

                    if (mostrarMolde) {
                        Spacer(Modifier.height(16.dp))
                        SelectorMoldes(
                            estado = estadoMolde,
                            onEstadoChange = { estadoMolde = it },
                            cm3PorPorcion = cm3PorPorcion
                        )
                    }
                }
            }

            Spacer(Modifier.height(24.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                TextButton(
                    onClick = onDismiss,
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = stringResource(R.string.accion_cancelar),
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Button(
                    onClick = {
                        onGuardar(
                            Receta(
                                nombre = nombre.trim(),
                                tipo = tipo.trim(),
                                rendimientoCantidad = cantidadInt ?: 0,
                                rendimientoUnidad = rendimientoUnidad.trim(),
                                moldeForma = estadoMolde.forma?.etiqueta,
                                moldeAnchoCm = estadoMolde.anchoCm,
                                moldeLargoCm = estadoMolde.largoCm,
                                moldeAltoCm = estadoMolde.altoCm,
                                porcionesPorMolde = construirPorciones(estadoMolde, cm3PorPorcion)
                            )
                        )
                    },
                    modifier = Modifier
                        .weight(2f)
                        .height(52.dp),
                    enabled = puedeGuardar,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary
                    )
                ) {
                    Text(
                        text = stringResource(R.string.accion_guardar),
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            Spacer(Modifier.height(24.dp))
        }
    }
}

/** Helper para calcular porciones desde el estado del molde. */
private fun construirPorciones(estado: EstadoMolde, cm3: Int): Int? {
    val forma = estado.forma ?: return null
    val ancho = estado.anchoCm ?: return null
    val alto = estado.altoCm ?: return null
    val largo = estado.largoCm

    return try {
        val dim = MotorMoldes.Dimensiones(forma, ancho, largo, alto)
        MotorMoldes.porcionesDesdeMolde(dim, BigDecimal(cm3))
    } catch (e: Exception) {
        null
    }
}
