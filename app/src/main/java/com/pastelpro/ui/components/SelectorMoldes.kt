package com.pastelpro.ui.components

import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Image
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.pastelpro.R
import com.pastelpro.engine.FormaMolde
import com.pastelpro.engine.MotorMoldes

/** Límites de validación para medidas manuales. */
private const val ANCHO_MIN = 5
private const val ANCHO_MAX = 60
private const val ALTO_MIN = 3
private const val ALTO_MAX = 30
private const val LARGO_MIN = 5
private const val LARGO_MAX = 60

/** Estado interno del selector (compartido con el sheet). */
data class EstadoMolde(
    val forma: FormaMolde? = null,
    val anchoCm: Int? = null,
    val largoCm: Int? = null,
    val altoCm: Int? = null,
    val esPersonalizado: Boolean = false,
    val descripcionEstandar: String? = null
)

/**
 * Sección completa de selección de molde.
 *
 * Flujo:
 * 1. Usuario elige forma (dropdown)
 * 2. Se muestra imagen de referencia
 * 3. Lista de tamaños estándar según forma
 * 4. Opción "Otro tamaño" activa inputs manuales
 */
@Composable
fun SelectorMoldes(
    estado: EstadoMolde,
    onEstadoChange: (EstadoMolde) -> Unit,
    cm3PorPorcion: Int,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxWidth()) {

        // ── Imagen dinámica según forma ──
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(140.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(MaterialTheme.colorScheme.surfaceVariant),
            contentAlignment = Alignment.Center
        ) {
            if (estado.forma == null) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    // Logo de PastelPro como guía visual
                    Image(
                        painter = painterResource(id = R.drawable.logo_pastelpro),
                        contentDescription = stringResource(R.string.app_name),
                        modifier = Modifier.size(72.dp)
                    )
                    Spacer(Modifier.height(8.dp))
                    Text(
                        text = stringResource(R.string.receta_molde_selecciona_forma),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontWeight = FontWeight.Medium
                    )
                }
            } else {
                Crossfade(
                    targetState = estado.forma,
                    animationSpec = tween(300),
                    label = "molde_img"
                ) { forma ->
                    val drawableId = when (forma) {
                        FormaMolde.REDONDO -> R.drawable.molde_redondo
                        FormaMolde.CUADRADO -> R.drawable.molde_cuadrado
                        FormaMolde.RECTANGULAR -> R.drawable.molde_rectangular
                        FormaMolde.CORAZON -> R.drawable.molde_corazon
                        null -> R.drawable.molde_redondo
                    }
                    Image(
                        painter = painterResource(id = drawableId),
                        contentDescription = forma.etiqueta,
                        modifier = Modifier
                            .size(140.dp)
                            .padding(8.dp)
                    )
                }
            }
        }

        Spacer(Modifier.height(16.dp))

        // ── Dropdown Forma ──
        var formaDropdown by remember { mutableStateOf(false) }

        Box {
            OutlinedTextField(
                value = estado.forma?.etiqueta ?: "",
                onValueChange = { },
                modifier = Modifier.fillMaxWidth(),
                label = { Text(stringResource(R.string.receta_molde_forma)) },
                readOnly = true,
                trailingIcon = {
                    IconButton(onClick = { formaDropdown = true }) {
                        Icon(Icons.Filled.ArrowDropDown, contentDescription = null)
                    }
                }
            )
            DropdownMenu(
                expanded = formaDropdown,
                onDismissRequest = { formaDropdown = false }
            ) {
                FormaMolde.values().forEach { f ->
                    DropdownMenuItem(
                        text = { Text(f.etiqueta) },
                        onClick = {
                            // Al cambiar forma, resetear medidas
                            onEstadoChange(
                                EstadoMolde(forma = f)
                            )
                            formaDropdown = false
                        }
                    )
                }
            }
        }

        // ── Lista de tamaños según forma ──
        estado.forma?.let { forma ->
            Spacer(Modifier.height(16.dp))

            val opciones = when (forma) {
                FormaMolde.REDONDO -> MotorMoldes.moldesRedondosEstandar()
                FormaMolde.CUADRADO -> MotorMoldes.moldesCuadradosEstandar()
                FormaMolde.RECTANGULAR -> MotorMoldes.moldesRectangularesEstandar()
                FormaMolde.CORAZON -> MotorMoldes.moldesCorazonEstandar()
            }

            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                opciones.forEach { molde ->
                    val seleccionado = !estado.esPersonalizado &&
                            estado.descripcionEstandar == molde.descripcion

                    TamañoItem(
                        titulo = molde.descripcion,
                        subtitulo = "${molde.rangoPorciones} porciones",
                        seleccionado = seleccionado,
                        onClick = {
                            val dim = molde.dimensiones
                            onEstadoChange(
                                EstadoMolde(
                                    forma = dim.forma,
                                    anchoCm = dim.anchoCm,
                                    largoCm = dim.largoCm,
                                    altoCm = dim.altoCm,
                                    esPersonalizado = false,
                                    descripcionEstandar = molde.descripcion
                                )
                            )
                        }
                    )
                }

                // ── Opción "Otro tamaño" ──
                TamañoItem(
                    titulo = stringResource(R.string.receta_molde_otro_tamano),
                    subtitulo = stringResource(R.string.receta_molde_otro_sub),
                    seleccionado = estado.esPersonalizado,
                    onClick = {
                        onEstadoChange(
                            EstadoMolde(
                                forma = forma,
                                esPersonalizado = true
                            )
                        )
                    }
                )
            }

            // ── Inputs manuales si es personalizado ──
            if (estado.esPersonalizado) {
                Spacer(Modifier.height(12.dp))
                InputsPersonalizados(estado, onEstadoChange)
            }

            // ── Preview ──
            val dimensiones = construirDimensiones(estado)
            if (dimensiones != null) {
                val porciones = MotorMoldes.porcionesDesdeMolde(
                    dimensiones,
                    java.math.BigDecimal(cm3PorPorcion)
                )
                Spacer(Modifier.height(16.dp))
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = MaterialTheme.colorScheme.primaryContainer,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = stringResource(
                            R.string.receta_molde_porciones_sugeridas,
                            porciones
                        ),
                        modifier = Modifier.padding(12.dp),
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}

@Composable
private fun TamañoItem(
    titulo: String,
    subtitulo: String,
    seleccionado: Boolean,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(10.dp),
        color = if (seleccionado)
            MaterialTheme.colorScheme.primaryContainer
        else
            MaterialTheme.colorScheme.surface,
        border = androidx.compose.foundation.BorderStroke(
            width = if (seleccionado) 1.5.dp else 1.dp,
            color = if (seleccionado)
                MaterialTheme.colorScheme.primary
            else
                MaterialTheme.colorScheme.outlineVariant
        )
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = titulo,
                    style = MaterialTheme.typography.bodyLarge,
                    color = if (seleccionado)
                        MaterialTheme.colorScheme.onPrimaryContainer
                    else
                        MaterialTheme.colorScheme.onSurface,
                    fontWeight = if (seleccionado) FontWeight.SemiBold else FontWeight.Medium
                )
                Text(
                    text = subtitulo,
                    style = MaterialTheme.typography.bodyMedium,
                    color = if (seleccionado)
                        MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                    else
                        MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            if (seleccionado) {
                Icon(
                    imageVector = Icons.Filled.Check,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}

@Composable
private fun InputsPersonalizados(
    estado: EstadoMolde,
    onEstadoChange: (EstadoMolde) -> Unit
) {
    var anchoTxt by remember(estado) {
        mutableStateOf(estado.anchoCm?.toString() ?: "")
    }
    var largoTxt by remember(estado) {
        mutableStateOf(estado.largoCm?.toString() ?: "")
    }
    var altoTxt by remember(estado) {
        mutableStateOf(estado.altoCm?.toString() ?: "")
    }

    fun actualizar() {
        val ancho = anchoTxt.toIntOrNull()
        val largo = largoTxt.toIntOrNull()
        val alto = altoTxt.toIntOrNull()

        val anchoValido = ancho != null && ancho in ANCHO_MIN..ANCHO_MAX
        val altoValido = alto != null && alto in ALTO_MIN..ALTO_MAX
        val largoValido = if (estado.forma == FormaMolde.RECTANGULAR) {
            largo != null && largo in LARGO_MIN..LARGO_MAX
        } else true

        onEstadoChange(
            estado.copy(
                anchoCm = if (anchoValido) ancho else null,
                largoCm = if (largoValido) largo else null,
                altoCm = if (altoValido) alto else null
            )
        )
    }

    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            OutlinedTextField(
                value = anchoTxt,
                onValueChange = {
                    anchoTxt = it.filter { c -> c.isDigit() }
                    actualizar()
                },
                modifier = Modifier.weight(1f),
                label = { Text(stringResource(R.string.receta_molde_ancho)) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                singleLine = true,
                isError = anchoTxt.isNotEmpty() &&
                        (anchoTxt.toIntOrNull()?.let { it !in ANCHO_MIN..ANCHO_MAX } == true),
                supportingText = {
                    if (anchoTxt.isNotEmpty() &&
                        anchoTxt.toIntOrNull()?.let { it !in ANCHO_MIN..ANCHO_MAX } == true) {
                        Text("$ANCHO_MIN a $ANCHO_MAX cm")
                    }
                }
            )
            if (estado.forma == FormaMolde.RECTANGULAR) {
                OutlinedTextField(
                    value = largoTxt,
                    onValueChange = {
                        largoTxt = it.filter { c -> c.isDigit() }
                        actualizar()
                    },
                    modifier = Modifier.weight(1f),
                    label = { Text(stringResource(R.string.receta_molde_largo)) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    isError = largoTxt.isNotEmpty() &&
                            (largoTxt.toIntOrNull()?.let { it !in LARGO_MIN..LARGO_MAX } == true)
                )
            }
            OutlinedTextField(
                value = altoTxt,
                onValueChange = {
                    altoTxt = it.filter { c -> c.isDigit() }
                    actualizar()
                },
                modifier = Modifier.weight(1f),
                label = { Text(stringResource(R.string.receta_molde_alto)) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                singleLine = true,
                isError = altoTxt.isNotEmpty() &&
                        (altoTxt.toIntOrNull()?.let { it !in ALTO_MIN..ALTO_MAX } == true),
                supportingText = {
                    if (altoTxt.isNotEmpty() &&
                        altoTxt.toIntOrNull()?.let { it !in ALTO_MIN..ALTO_MAX } == true) {
                        Text("$ALTO_MIN a $ALTO_MAX cm")
                    }
                }
            )
        }
    }
}

/** Intenta construir Dimensiones válidas a partir del estado. */
private fun construirDimensiones(estado: EstadoMolde): MotorMoldes.Dimensiones? {
    val forma = estado.forma ?: return null
    val ancho = estado.anchoCm ?: return null
    val alto = estado.altoCm ?: return null
    val largo = estado.largoCm

    return try {
        MotorMoldes.Dimensiones(forma, ancho, largo, alto)
    } catch (e: Exception) {
        null
    }
}
