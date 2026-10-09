package com.pastelpro.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.pastelpro.R

/**
 * Unidades disponibles para ingredientes.
 * Ordenadas por categoría: masa, volumen, conteo.
 */
val UNIDADES_DISPONIBLES = listOf(
    "kg", "g",
    "L", "ml",
    "pieza", "docena", "paquete", "caja", "unidad"
)

@Composable
fun DropdownUnidad(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true
) {
    var expandido by remember { mutableStateOf(false) }

    Box(modifier = modifier) {
        OutlinedTextField(
            value = value,
            onValueChange = { },
            modifier = Modifier.fillMaxWidth(),
            enabled = enabled,
            label = { Text(label) },
            readOnly = true,
            trailingIcon = {
                androidx.compose.material3.IconButton(
                    onClick = { expandido = true },
                    enabled = enabled
                ) {
                    Icon(
                        imageVector = Icons.Filled.ArrowDropDown,
                        contentDescription = stringResource(R.string.campo_unidad)
                    )
                }
            }
        )

        DropdownMenu(
            expanded = expandido,
            onDismissRequest = { expandido = false }
        ) {
            UNIDADES_DISPONIBLES.forEach { unidad ->
                DropdownMenuItem(
                    text = { Text(unidad) },
                    onClick = {
                        onValueChange(unidad)
                        expandido = false
                    }
                )
            }
        }
    }
}
