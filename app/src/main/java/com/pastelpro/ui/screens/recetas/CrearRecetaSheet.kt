package com.pastelpro.ui.screens.recetas

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.pastelpro.R
import com.pastelpro.domain.model.Receta

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CrearRecetaSheet(
    onDismiss: () -> Unit,
    onGuardar: (Receta) -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    var nombre by remember { mutableStateOf("") }
    var tipo by remember { mutableStateOf("") }
    var rendimientoCantidadTxt by remember { mutableStateOf("") }
    var rendimientoUnidad by remember { mutableStateOf("porciones") }

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
                OutlinedTextField(
                    value = rendimientoUnidad,
                    onValueChange = { rendimientoUnidad = it },
                    modifier = Modifier.weight(1f),
                    label = { Text(stringResource(R.string.campo_rendimiento_unidad)) },
                    singleLine = true
                )
            }

            Spacer(Modifier.height(8.dp))
            Text(
                text = stringResource(R.string.recetas_rendimiento_hint),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

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
                                rendimientoUnidad = rendimientoUnidad.trim()
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
