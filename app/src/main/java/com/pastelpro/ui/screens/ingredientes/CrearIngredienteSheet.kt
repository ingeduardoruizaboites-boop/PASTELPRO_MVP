package com.pastelpro.ui.screens.ingredientes

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
import com.pastelpro.domain.model.Ingrediente
import java.math.BigDecimal

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CrearIngredienteSheet(
    onDismiss: () -> Unit,
    onGuardar: (Ingrediente) -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    var nombre by remember { mutableStateOf("") }
    var categoria by remember { mutableStateOf("Harinas") }
    var presentacion by remember { mutableStateOf("1 kg") }
    var cantidadTxt by remember { mutableStateOf("1") }
    var unidad by remember { mutableStateOf("kg") }
    var precioTxt by remember { mutableStateOf("") }
    var proveedor by remember { mutableStateOf("") }

    val puedeGuardar = nombre.isNotBlank() &&
            cantidadTxt.toBigDecimalOrNull() != null &&
            precioTxt.toBigDecimalOrNull() != null

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
                text = stringResource(R.string.ingredientes_nuevo_titulo),
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
                value = categoria,
                onValueChange = { categoria = it },
                modifier = Modifier.fillMaxWidth(),
                label = { Text(stringResource(R.string.campo_categoria)) },
                singleLine = true
            )

            Spacer(Modifier.height(12.dp))

            OutlinedTextField(
                value = presentacion,
                onValueChange = { presentacion = it },
                modifier = Modifier.fillMaxWidth(),
                label = { Text(stringResource(R.string.campo_presentacion)) },
                singleLine = true
            )

            Spacer(Modifier.height(12.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = cantidadTxt,
                    onValueChange = { cantidadTxt = it },
                    modifier = Modifier.weight(1f),
                    label = { Text(stringResource(R.string.campo_cantidad)) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    singleLine = true
                )
                OutlinedTextField(
                    value = unidad,
                    onValueChange = { unidad = it },
                    modifier = Modifier.weight(1f),
                    label = { Text(stringResource(R.string.campo_unidad)) },
                    singleLine = true
                )
            }

            Spacer(Modifier.height(12.dp))

            OutlinedTextField(
                value = precioTxt,
                onValueChange = { precioTxt = it },
                modifier = Modifier.fillMaxWidth(),
                label = { Text(stringResource(R.string.campo_precio)) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                singleLine = true
            )

            Spacer(Modifier.height(12.dp))

            OutlinedTextField(
                value = proveedor,
                onValueChange = { proveedor = it },
                modifier = Modifier.fillMaxWidth(),
                label = { Text(stringResource(R.string.campo_proveedor)) },
                singleLine = true
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
                        val nuevo = Ingrediente(
                            nombre = nombre.trim(),
                            categoria = categoria.trim(),
                            presentacionCompra = presentacion.trim(),
                            cantidadCompra = cantidadTxt.toBigDecimalOrNull() ?: BigDecimal.ZERO,
                            unidadCompra = unidad.trim(),
                            precioCompra = precioTxt.toBigDecimalOrNull() ?: BigDecimal.ZERO,
                            proveedor = proveedor.trim().ifBlank { null }
                        )
                        onGuardar(nuevo)
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
