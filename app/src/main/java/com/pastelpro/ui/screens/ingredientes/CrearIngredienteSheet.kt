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
import androidx.compose.runtime.LaunchedEffect
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
import com.pastelpro.ui.components.DropdownUnidad
import java.math.BigDecimal

/**
 * Bottom Sheet para crear o editar un ingrediente.
 * Si [ingredienteExistente] es null → modo creación.
 * Si [ingredienteExistente] no es null → modo edición.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CrearIngredienteSheet(
    onDismiss: () -> Unit,
    onGuardar: (Ingrediente) -> Unit,
    ingredienteExistente: Ingrediente? = null
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    val esEdicion = ingredienteExistente != null

    var nombre by remember { mutableStateOf("") }
    var categoria by remember { mutableStateOf("Harinas") }
    var presentacion by remember { mutableStateOf("") }
    var cantidadTxt by remember { mutableStateOf("") }
    var unidad by remember { mutableStateOf("kg") }
    var precioTxt by remember { mutableStateOf("") }
    var proveedor by remember { mutableStateOf("") }

    // Si estamos en edición, precargar valores
    LaunchedEffect(ingredienteExistente) {
        ingredienteExistente?.let { ing ->
            nombre = ing.nombre
            categoria = ing.categoria
            presentacion = ing.presentacionCompra
            cantidadTxt = ing.cantidadCompra.stripTrailingZeros().toPlainString()
            unidad = ing.unidadCompra
            precioTxt = ing.precioCompra.stripTrailingZeros().toPlainString()
            proveedor = ing.proveedor ?: ""
        }
    }

    val puedeGuardar = nombre.isNotBlank()
            && cantidadTxt.toBigDecimalOrNull() != null
            && precioTxt.toBigDecimalOrNull() != null

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
                text = stringResource(
                    if (esEdicion) R.string.ingrediente_editar_titulo
                    else R.string.ingredientes_nuevo_titulo
                ),
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

            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = cantidadTxt,
                    onValueChange = { cantidadTxt = it },
                    modifier = Modifier.weight(1f),
                    label = { Text(stringResource(R.string.campo_cantidad)) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    singleLine = true
                )
                DropdownUnidad(
                    label = stringResource(R.string.campo_unidad),
                    value = unidad,
                    onValueChange = { unidad = it },
                    modifier = Modifier.weight(1f)
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
                        val cantidad = cantidadTxt.toBigDecimalOrNull() ?: BigDecimal.ZERO
                        val precio = precioTxt.toBigDecimalOrNull() ?: BigDecimal.ZERO

                        val nuevo = Ingrediente(
                            id = ingredienteExistente?.id ?: java.util.UUID.randomUUID().toString(),
                            nombre = nombre.trim(),
                            categoria = categoria.trim(),
                            presentacionCompra = "${cantidad.stripTrailingZeros().toPlainString()} $unidad",
                            cantidadCompra = cantidad,
                            unidadCompra = unidad,
                            precioCompra = precio,
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
                        text = stringResource(
                            if (esEdicion) R.string.accion_guardar_cambios
                            else R.string.accion_guardar
                        ),
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            Spacer(Modifier.height(24.dp))
        }
    }
}
