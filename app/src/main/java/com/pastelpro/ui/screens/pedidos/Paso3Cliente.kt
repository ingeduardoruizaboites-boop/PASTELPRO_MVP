package com.pastelpro.ui.screens.pedidos

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimePicker
import androidx.compose.material3.rememberDatePickerState
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
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Paso3Cliente(vm: NuevoPedidoViewModel) {
    var mostrarDatePicker by remember { mutableStateOf(false) }
    var mostrarTimePicker by remember { mutableStateOf(false) }

    val fechaTexto = vm.fechaEntregaIso?.let { Pedido.fechaAIsoADisplay(it) } ?: ""

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Text(
            text = stringResource(R.string.nuevo_pedido_paso3_titulo),
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.primary,
            fontWeight = FontWeight.SemiBold
        )
        Spacer(Modifier.height(6.dp))
        Text(
            text = stringResource(R.string.nuevo_pedido_paso3_ayuda),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(Modifier.height(20.dp))

        OutlinedTextField(
            value = vm.cliente,
            onValueChange = vm::cambiarCliente,
            modifier = Modifier.fillMaxWidth(),
            label = { Text(stringResource(R.string.nuevo_pedido_cliente)) },
            singleLine = true
        )

        Spacer(Modifier.height(12.dp))

        OutlinedTextField(
            value = vm.telefonoContacto,
            onValueChange = vm::cambiarTelefono,
            modifier = Modifier.fillMaxWidth(),
            label = { Text(stringResource(R.string.nuevo_pedido_telefono)) },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
            singleLine = true
        )

        Spacer(Modifier.height(12.dp))

        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            // Botón de fecha
            OutlinedTextField(
                value = fechaTexto,
                onValueChange = { },
                modifier = Modifier.weight(1f),
                label = { Text(stringResource(R.string.nuevo_pedido_fecha)) },
                readOnly = true,
                singleLine = true,
                trailingIcon = {
                    androidx.compose.material3.IconButton(onClick = { mostrarDatePicker = true }) {
                        Icon(
                            imageVector = Icons.Filled.CalendarMonth,
                            contentDescription = stringResource(R.string.nuevo_pedido_seleccionar_fecha)
                        )
                    }
                }
            )

            // Botón de hora
            OutlinedTextField(
                value = vm.horaEntrega,
                onValueChange = { },
                modifier = Modifier.weight(1f),
                label = { Text(stringResource(R.string.nuevo_pedido_hora)) },
                readOnly = true,
                singleLine = true,
                trailingIcon = {
                    androidx.compose.material3.IconButton(onClick = { mostrarTimePicker = true }) {
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
            value = vm.direccionEntrega,
            onValueChange = vm::cambiarDireccion,
            modifier = Modifier.fillMaxWidth(),
            label = { Text(stringResource(R.string.nuevo_pedido_direccion)) },
            minLines = 2,
            maxLines = 3
        )

        Spacer(Modifier.height(12.dp))

        OutlinedTextField(
            value = vm.notas,
            onValueChange = vm::cambiarNotas,
            modifier = Modifier.fillMaxWidth(),
            label = { Text(stringResource(R.string.nuevo_pedido_notas)) },
            minLines = 3,
            maxLines = 5
        )

        Spacer(Modifier.height(24.dp))
    }

    // DatePickerDialog
    if (mostrarDatePicker) {
        val hoyInicioDia = java.util.Calendar.getInstance().apply {
            set(java.util.Calendar.HOUR_OF_DAY, 0)
            set(java.util.Calendar.MINUTE, 0)
            set(java.util.Calendar.SECOND, 0)
            set(java.util.Calendar.MILLISECOND, 0)
        }.timeInMillis

        val estadoFecha = rememberDatePickerState(
            initialSelectedDateMillis = vm.fechaEntregaIso?.let { iso -> isoAMillis(iso) }
                ?: System.currentTimeMillis(),
            selectableDates = object : androidx.compose.material3.SelectableDates {
                override fun isSelectableDate(utcTimeMillis: Long): Boolean {
                    // No permitir fechas anteriores a hoy
                    return utcTimeMillis >= hoyInicioDia
                }
                override fun isSelectableYear(year: Int): Boolean {
                    return year >= java.util.Calendar.getInstance().get(java.util.Calendar.YEAR)
                }
            }
        )
        DatePickerDialog(
            onDismissRequest = { mostrarDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    estadoFecha.selectedDateMillis?.let { millis ->
                        vm.cambiarFecha(millisAIso(millis))
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

    // TimePickerDialog (custom, Material 3 no trae uno listo)
    if (mostrarTimePicker) {
        val estadoHora = rememberTimePickerState(
            initialHour = parseHora(vm.horaEntrega).first,
            initialMinute = parseHora(vm.horaEntrega).second,
            is24Hour = true
        )
        androidx.compose.material3.AlertDialog(
            onDismissRequest = { mostrarTimePicker = false },
            title = { Text(stringResource(R.string.nuevo_pedido_seleccionar_hora)) },
            text = {
                TimePicker(state = estadoHora)
            },
            confirmButton = {
                TextButton(onClick = {
                    val h = estadoHora.hour.toString().padStart(2, '0')
                    val m = estadoHora.minute.toString().padStart(2, '0')
                    vm.cambiarHora("$h:$m")
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

private fun millisAIso(millis: Long): String {
    val fmt = SimpleDateFormat("yyyy-MM-dd", Locale.US)
    return fmt.format(Date(millis))
}

private fun isoAMillis(iso: String): Long {
    return try {
        val fmt = SimpleDateFormat("yyyy-MM-dd", Locale.US)
        fmt.parse(iso)?.time ?: System.currentTimeMillis()
    } catch (e: Exception) {
        System.currentTimeMillis()
    }
}

private fun parseHora(hora: String): Pair<Int, Int> {
    return try {
        val partes = hora.split(":")
        if (partes.size == 2) {
            partes[0].toInt() to partes[1].toInt()
        } else {
            val cal = Calendar.getInstance()
            cal.get(Calendar.HOUR_OF_DAY) to cal.get(Calendar.MINUTE)
        }
    } catch (e: Exception) {
        val cal = Calendar.getInstance()
        cal.get(Calendar.HOUR_OF_DAY) to cal.get(Calendar.MINUTE)
    }
}
