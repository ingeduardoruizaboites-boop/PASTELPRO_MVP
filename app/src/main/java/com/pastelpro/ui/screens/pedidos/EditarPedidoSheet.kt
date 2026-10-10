package com.pastelpro.ui.screens.pedidos

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SelectableDates
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimePicker
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.material3.rememberTimePickerState
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
import com.pastelpro.domain.model.Pedido
import java.math.BigDecimal
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditarPedidoSheet(
    pedido: Pedido,
    onDismiss: () -> Unit,
    onGuardar: (Pedido) -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    var cliente by remember { mutableStateOf(pedido.cliente ?: "") }
    var telefono by remember { mutableStateOf(pedido.telefonoContacto ?: "") }
    var porcionesTxt by remember { mutableStateOf(pedido.porciones.toString()) }
    var precioTxt by remember {
        mutableStateOf(pedido.precioAcordado.stripTrailingZeros().toPlainString())
    }
    var fechaIso by remember { mutableStateOf(pedido.fechaEntrega) }
    var hora by remember { mutableStateOf(pedido.horaEntrega ?: "") }
    var direccion by remember { mutableStateOf(pedido.direccionEntrega ?: "") }
    var notas by remember { mutableStateOf(pedido.notas ?: "") }

    var mostrarDatePicker by remember { mutableStateOf(false) }
    var mostrarTimePicker by remember { mutableStateOf(false) }

    val fechaTexto = fechaIso?.let { Pedido.fechaAIsoADisplay(it) } ?: ""

    val puedeGuardar = porcionesTxt.toIntOrNull()?.let { it > 0 } == true
            && precioTxt.toBigDecimalOrNull()?.let { it > BigDecimal.ZERO } == true

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
                text = stringResource(R.string.pedido_editar_titulo),
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(Modifier.height(4.dp))
            Text(
                text = pedido.recetaNombre,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(Modifier.height(20.dp))

            OutlinedTextField(
                value = cliente,
                onValueChange = { cliente = it },
                modifier = Modifier.fillMaxWidth(),
                label = { Text(stringResource(R.string.nuevo_pedido_cliente)) },
                singleLine = true
            )

            Spacer(Modifier.height(12.dp))

            OutlinedTextField(
                value = telefono,
                onValueChange = { telefono = it },
                modifier = Modifier.fillMaxWidth(),
                label = { Text(stringResource(R.string.nuevo_pedido_telefono)) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                singleLine = true
            )

            Spacer(Modifier.height(12.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = porcionesTxt,
                    onValueChange = { porcionesTxt = it.filter { c -> c.isDigit() } },
                    modifier = Modifier.weight(1f),
                    label = { Text(stringResource(R.string.pedido_porciones)) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true
                )
                OutlinedTextField(
                    value = precioTxt,
                    onValueChange = { precioTxt = it },
                    modifier = Modifier.weight(1f),
                    label = { Text(stringResource(R.string.nuevo_pedido_precio_input_label)) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    singleLine = true
                )
            }

            Spacer(Modifier.height(12.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = fechaTexto,
                    onValueChange = { },
                    modifier = Modifier.weight(1f),
                    label = { Text(stringResource(R.string.nuevo_pedido_fecha)) },
                    readOnly = true,
                    singleLine = true,
                    trailingIcon = {
                        IconButton(onClick = { mostrarDatePicker = true }) {
                            Icon(
                                imageVector = Icons.Filled.CalendarMonth,
                                contentDescription = stringResource(R.string.nuevo_pedido_seleccionar_fecha)
                            )
                        }
                    }
                )
                OutlinedTextField(
                    value = hora,
                    onValueChange = { },
                    modifier = Modifier.weight(1f),
                    label = { Text(stringResource(R.string.nuevo_pedido_hora)) },
                    readOnly = true,
                    singleLine = true,
                    trailingIcon = {
                        IconButton(onClick = { mostrarTimePicker = true }) {
                            Icon(
                                imageVector = Icons.Filled.Schedule,
                                contentDescription = stringResource(R.string.nuevo_pedido_seleccionar_hora)
                            )
                        }
                    }
                )
            }

            Spacer(Modifier.height(12.dp))

            OutlinedTextField(
                value = direccion,
                onValueChange = { direccion = it },
                modifier = Modifier.fillMaxWidth(),
                label = { Text(stringResource(R.string.nuevo_pedido_direccion)) },
                minLines = 2,
                maxLines = 3
            )

            Spacer(Modifier.height(12.dp))

            OutlinedTextField(
                value = notas,
                onValueChange = { notas = it },
                modifier = Modifier.fillMaxWidth(),
                label = { Text(stringResource(R.string.nuevo_pedido_notas)) },
                minLines = 3,
                maxLines = 5
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
                        val porcionesNuevas = porcionesTxt.toIntOrNull() ?: pedido.porciones
                        val precioNuevo = precioTxt.toBigDecimalOrNull() ?: pedido.precioAcordado

                        val actualizado = pedido.copy(
                            cliente = cliente.trim().ifBlank { null },
                            telefonoContacto = telefono.trim().ifBlank { null },
                            porciones = porcionesNuevas,
                            precioAcordado = precioNuevo,
                            fechaEntrega = fechaIso,
                            horaEntrega = hora.trim().ifBlank { null },
                            direccionEntrega = direccion.trim().ifBlank { null },
                            notas = notas.trim().ifBlank { null }
                        )
                        onGuardar(actualizado)
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
                        text = stringResource(R.string.accion_guardar_cambios),
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            Spacer(Modifier.height(24.dp))
        }
    }

    // ─── DatePicker ───
    if (mostrarDatePicker) {
        val hoyInicioDia = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }.timeInMillis

        val estadoFecha = rememberDatePickerState(
            initialSelectedDateMillis = fechaIso?.let { isoAMillisEditar(it) } ?: System.currentTimeMillis(),
            selectableDates = object : SelectableDates {
                override fun isSelectableDate(utcTimeMillis: Long): Boolean {
                    return utcTimeMillis >= hoyInicioDia
                }
                override fun isSelectableYear(year: Int): Boolean {
                    return year >= Calendar.getInstance().get(Calendar.YEAR)
                }
            }
        )
        DatePickerDialog(
            onDismissRequest = { mostrarDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    estadoFecha.selectedDateMillis?.let { millis ->
                        fechaIso = millisAIsoEditar(millis)
                    }
                    mostrarDatePicker = false
                }) {
                    Text(stringResource(R.string.accion_confirmar))
                }
            },
            dismissButton = {
                TextButton(onClick = { mostrarDatePicker = false }) {
                    Text(stringResource(R.string.accion_cancelar))
                }
            }
        ) {
            DatePicker(state = estadoFecha)
        }
    }

    // ─── TimePicker ───
    if (mostrarTimePicker) {
        val partes = hora.split(":")
        val estadoHora = rememberTimePickerState(
            initialHour = partes.getOrNull(0)?.toIntOrNull() ?: 9,
            initialMinute = partes.getOrNull(1)?.toIntOrNull() ?: 0,
            is24Hour = true
        )
        AlertDialog(
            onDismissRequest = { mostrarTimePicker = false },
            title = { Text(stringResource(R.string.nuevo_pedido_seleccionar_hora)) },
            text = { TimePicker(state = estadoHora) },
            confirmButton = {
                TextButton(onClick = {
                    val h = estadoHora.hour.toString().padStart(2, '0')
                    val m = estadoHora.minute.toString().padStart(2, '0')
                    hora = "$h:$m"
                    mostrarTimePicker = false
                }) {
                    Text(stringResource(R.string.accion_confirmar))
                }
            },
            dismissButton = {
                TextButton(onClick = { mostrarTimePicker = false }) {
                    Text(stringResource(R.string.accion_cancelar))
                }
            }
        )
    }
}

private fun millisAIsoEditar(millis: Long): String {
    return SimpleDateFormat("yyyy-MM-dd", Locale.US).format(Date(millis))
}

private fun isoAMillisEditar(iso: String): Long {
    return try {
        SimpleDateFormat("yyyy-MM-dd", Locale.US).parse(iso)?.time ?: System.currentTimeMillis()
    } catch (e: Exception) {
        System.currentTimeMillis()
    }
}
