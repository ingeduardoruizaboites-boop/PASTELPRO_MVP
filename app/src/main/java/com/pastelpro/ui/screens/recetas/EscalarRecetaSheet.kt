package com.pastelpro.ui.screens.recetas

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
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
import com.pastelpro.engine.MotorEscalado
import com.pastelpro.engine.Unidad
import java.math.BigDecimal
import java.math.RoundingMode

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EscalarRecetaSheet(
    receta: Receta,
    onDismiss: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var cantidadTxt by remember { mutableStateOf("") }
    val cantidadNueva = cantidadTxt.toIntOrNull()

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
                text = stringResource(R.string.escalar_titulo),
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.SemiBold
            )

            Spacer(Modifier.height(6.dp))

            Text(
                text = stringResource(R.string.escalar_base, receta.rendimientoTexto),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(Modifier.height(20.dp))

            OutlinedTextField(
                value = cantidadTxt,
                onValueChange = { cantidadTxt = it.filter { c -> c.isDigit() } },
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
                    onClick = { cantidadTxt = (receta.rendimientoCantidad * 2).toString() },
                    label = { Text("×2") },
                    colors = AssistChipDefaults.assistChipColors(
                        labelColor = MaterialTheme.colorScheme.primary
                    )
                )
                AssistChip(
                    onClick = { cantidadTxt = (receta.rendimientoCantidad * 3).toString() },
                    label = { Text("×3") },
                    colors = AssistChipDefaults.assistChipColors(
                        labelColor = MaterialTheme.colorScheme.primary
                    )
                )
                AssistChip(
                    onClick = { cantidadTxt = (receta.rendimientoCantidad * 10).toString() },
                    label = { Text("×10") },
                    colors = AssistChipDefaults.assistChipColors(
                        labelColor = MaterialTheme.colorScheme.primary
                    )
                )
                AssistChip(
                    onClick = { cantidadTxt = "100" },
                    label = { Text("100") },
                    colors = AssistChipDefaults.assistChipColors(
                        labelColor = MaterialTheme.colorScheme.primary
                    )
                )
            }

            if (cantidadNueva != null && cantidadNueva > 0) {
                Spacer(Modifier.height(20.dp))
                HorizontalDivider()
                Spacer(Modifier.height(16.dp))

                Text(
                    text = stringResource(R.string.escalar_resultado_titulo),
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onBackground,
                    fontWeight = FontWeight.SemiBold
                )

                Spacer(Modifier.height(4.dp))

                val factor = try {
                    MotorEscalado.factorPorPersonas(receta.rendimientoCantidad, cantidadNueva)
                } catch (e: Exception) {
                    null
                }

                if (factor != null) {
                    Text(
                        text = stringResource(
                            R.string.escalar_factor,
                            factor.setScale(2, RoundingMode.HALF_UP).toPlainString()
                        ),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.primary
                    )

                    Spacer(Modifier.height(12.dp))

                    LazyColumn(
                        modifier = Modifier.height(280.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(receta.ingredientes, key = { it.ingredienteId }) { ing ->
                            IngredienteEscaladoCard(
                                nombre = ing.nombre,
                                cantidadOriginal = ing.cantidad,
                                unidad = ing.unidad,
                                factor = factor
                            )
                        }
                    }
                }
            }

            Spacer(Modifier.height(24.dp))

            TextButton(
                onClick = onDismiss,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = stringResource(R.string.accion_cerrar),
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(Modifier.height(16.dp))
        }
    }
}

@Composable
private fun IngredienteEscaladoCard(
    nombre: String,
    cantidadOriginal: BigDecimal,
    unidad: String,
    factor: BigDecimal
) {
    val unidadEnum = Unidad.desdeSimbolo(unidad)

    // Escalar
    val escalado = MotorEscalado.escalar(cantidadOriginal, factor)

    // Redondear según tipo de unidad
    val redondeado = if (unidadEnum != null) {
        MotorEscalado.redondearUsable(escalado, unidadEnum)
    } else {
        escalado.setScale(2, RoundingMode.HALF_UP)
    }

    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(10.dp),
        color = MaterialTheme.colorScheme.surfaceVariant,
        shadowElevation = 0.dp
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = nombre,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontWeight = FontWeight.Medium
                )
                Spacer(Modifier.size(2.dp))
                Text(
                    text = "Original: ${cantidadOriginal.stripTrailingZeros().toPlainString()} $unidad",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Text(
                text = "${redondeado.stripTrailingZeros().toPlainString()} $unidad",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}
