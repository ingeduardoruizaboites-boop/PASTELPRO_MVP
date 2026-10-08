package com.pastelpro.ui.screens.recetas

import android.content.Intent
import android.util.Log
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
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.FileProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import com.pastelpro.R
import com.pastelpro.engine.CalculadoraCostoReceta
import com.pastelpro.diagnostico.DiagnosticLogger
import com.pastelpro.engine.MotorPrecio
import java.math.BigDecimal
import java.math.RoundingMode
import java.text.NumberFormat
import java.util.Locale

private const val TAG = "PastelProCostoScreen"

/** Formato monetario defensivo: escala a 2 decimales antes de formatear. */
private fun formatearMoneda(valor: BigDecimal, formato: NumberFormat): String {
    return try {
        formato.format(valor.setScale(2, RoundingMode.HALF_UP))
    } catch (e: Exception) {
        Log.e(TAG, "Error formateando moneda: $valor", e)
        "—"
    }
}

/** Formato porcentaje defensivo. */
private fun formatearPct(valor: BigDecimal, formato: NumberFormat): String {
    return try {
        formato.format(valor.setScale(2, RoundingMode.HALF_UP)) + "%"
    } catch (e: Exception) {
        Log.e(TAG, "Error formateando pct: $valor", e)
        "—"
    }
}

/** Formato cantidad defensivo. */
private fun formatearCantidad(valor: BigDecimal): String {
    return try {
        valor.stripTrailingZeros().toPlainString()
    } catch (e: Exception) {
        Log.e(TAG, "Error formateando cantidad: $valor", e)
        "—"
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CostoPrecioScreen(
    recetaId: String,
    onBack: () -> Unit
) {
    val vm: CostoPrecioViewModel = viewModel(
        factory = CostoPrecioViewModel.factory(recetaId),
        key = "costo_precio_$recetaId"
    )
    val state by vm.uiState.collectAsState()

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(R.string.costo_precio_titulo),
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
                actions = {
                    val context = LocalContext.current
                    IconButton(onClick = {
                        compartirDiagnostico(context)
                    }) {
                        Icon(
                            imageVector = Icons.Filled.Share,
                            contentDescription = "Compartir diagnóstico"
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
        Box(modifier = Modifier.fillMaxSize().padding(padding)) {
            when (val s = state) {
                CostoPrecioUiState.Cargando -> {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text(stringResource(R.string.estado_cargando))
                    }
                }
                CostoPrecioUiState.NoEncontrada -> {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text(stringResource(R.string.receta_no_encontrada))
                    }
                }
                is CostoPrecioUiState.ConDatos -> {
                    ContenidoCostoPrecio(s.resultado, s.precios, s.numInventario)
                }
            }
        }
    }
}

@Composable
private fun ContenidoCostoPrecio(
    resultado: CalculadoraCostoReceta.Resultado,
    precios: MotorPrecio.TresPrecios,
    numInventario: Int
) {
    val formatoMoneda = NumberFormat.getCurrencyInstance(Locale("es", "MX"))
    val formatoPct = NumberFormat.getNumberInstance(Locale("es", "MX")).apply {
        maximumFractionDigits = 2
    }

    DiagnosticLogger.seccion("ContenidoCostoPrecio · render")
    DiagnosticLogger.log("UI", "desglose=${resultado.desglose.size} faltantes=${resultado.faltantes.size}")
    DiagnosticLogger.log("UI", "costoTotal=${resultado.costoTotal} costoPorPorcion=${resultado.costoPorPorcion}")

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(horizontal = 20.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        if (resultado.faltantes.isNotEmpty()) {
            item {
                DiagnosticLogger.log("UI", "Render: card de faltantes")
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.errorContainer,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Filled.Warning,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.error
                            )
                            Spacer(Modifier.padding(horizontal = 4.dp))
                            Text(
                                text = stringResource(
                                    R.string.costo_precio_faltantes,
                                    resultado.faltantes.size
                                ),
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onErrorContainer,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                        resultado.faltantes.forEach { nombre ->
                            Text(
                                text = "· $nombre",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onErrorContainer
                            )
                        }
                    }
                }
            }
        }

        item {
            DiagnosticLogger.log("UI", "Render: card de costo total")
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = stringResource(R.string.costo_precio_costo_total),
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.85f)
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(
                        text = formatearMoneda(resultado.costoTotal, formatoMoneda),
                        fontSize = 44.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                    Spacer(Modifier.height(8.dp))
                    Text(
                        text = stringResource(
                            R.string.costo_precio_costo_porcion,
                            formatearMoneda(resultado.costoPorPorcion, formatoMoneda),
                            resultado.rendimientoCantidad
                        ),
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.85f)
                    )
                }
            }
        }

        item {
            Text(
                text = stringResource(R.string.costo_precio_precios_titulo),
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onBackground,
                fontWeight = FontWeight.SemiBold
            )
        }

        item {
            DiagnosticLogger.log("UI", "Render: PrecioCard Mínimo")
            PrecioCard(
                etiqueta = stringResource(R.string.costo_precio_minimo),
                precio = precios.minimo.precio,
                ganancia = precios.minimo.ganancia,
                margen = precios.minimo.margen,
                destacado = false,
                formatoMoneda = formatoMoneda,
                formatoPct = formatoPct
            )
        }
        item {
            DiagnosticLogger.log("UI", "Render: PrecioCard Recomendado")
            PrecioCard(
                etiqueta = stringResource(R.string.costo_precio_recomendado),
                precio = precios.recomendado.precio,
                ganancia = precios.recomendado.ganancia,
                margen = precios.recomendado.margen,
                destacado = true,
                formatoMoneda = formatoMoneda,
                formatoPct = formatoPct
            )
        }
        item {
            DiagnosticLogger.log("UI", "Render: PrecioCard Premium")
            PrecioCard(
                etiqueta = stringResource(R.string.costo_precio_premium),
                precio = precios.premium.precio,
                ganancia = precios.premium.ganancia,
                margen = precios.premium.margen,
                destacado = false,
                formatoMoneda = formatoMoneda,
                formatoPct = formatoPct
            )
        }

        item {
            Spacer(Modifier.height(8.dp))
            Text(
                text = stringResource(R.string.costo_precio_desglose_titulo),
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onBackground,
                fontWeight = FontWeight.SemiBold
            )
        }

        itemsIndexed(resultado.desglose) { index, item ->
            DiagnosticLogger.log("UI", "Render: desglose[$index] ${item.nombre} costo=${item.costo}")
            Column {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = item.nombre,
                            style = MaterialTheme.typography.bodyLarge,
                            color = if (item.encontrado)
                                MaterialTheme.colorScheme.onSurface
                            else
                                MaterialTheme.colorScheme.error,
                            fontWeight = FontWeight.Medium
                        )
                        Text(
                            text = "${formatearCantidad(item.cantidad)} ${item.unidad}",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Text(
                        text = if (item.encontrado)
                            formatearMoneda(item.costo, formatoMoneda)
                        else
                            stringResource(R.string.costo_precio_no_encontrado),
                        style = MaterialTheme.typography.bodyLarge,
                        color = if (item.encontrado)
                            MaterialTheme.colorScheme.onSurface
                        else
                            MaterialTheme.colorScheme.error,
                        fontWeight = FontWeight.SemiBold
                    )
                }
                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
            }
        }

        item {
            DiagnosticLogger.log("UI", "Render: fila resumen")
            Spacer(Modifier.height(4.dp))
            FilaResumen(
                stringResource(R.string.costo_precio_subtotal),
                formatearMoneda(resultado.costoIngredientes, formatoMoneda),
                false
            )
            FilaResumen(
                stringResource(
                    R.string.costo_precio_merma,
                    formatearCantidad(resultado.mermaPorcentaje)
                ),
                formatearMoneda(resultado.costoMerma, formatoMoneda),
                false
            )
            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
            FilaResumen(
                stringResource(R.string.costo_precio_total),
                formatearMoneda(resultado.costoTotal, formatoMoneda),
                true
            )
            Spacer(Modifier.height(24.dp))
        }
    }
}

@Composable
private fun PrecioCard(
    etiqueta: String,
    precio: BigDecimal,
    ganancia: BigDecimal,
    margen: BigDecimal,
    destacado: Boolean,
    formatoMoneda: NumberFormat,
    formatoPct: NumberFormat
) {
    val contenedor = if (destacado) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface
    val textoPrincipal = if (destacado) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface
    val textoSecundario = if (destacado)
        MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.85f)
    else
        MaterialTheme.colorScheme.onSurfaceVariant

    Surface(
        shape = RoundedCornerShape(16.dp),
        color = contenedor,
        shadowElevation = if (destacado) 3.dp else 1.dp,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = etiqueta,
                    style = MaterialTheme.typography.titleMedium,
                    color = textoPrincipal,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.weight(1f)
                )
                if (destacado) {
                    Text(text = "★", color = textoPrincipal, fontWeight = FontWeight.Bold)
                }
            }
            Spacer(Modifier.height(8.dp))
            Text(
                text = formatearMoneda(precio, formatoMoneda),
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                color = textoPrincipal
            )
            Spacer(Modifier.height(6.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                Text(
                    text = stringResource(R.string.costo_precio_ganancia, formatearMoneda(ganancia, formatoMoneda)),
                    style = MaterialTheme.typography.bodyMedium,
                    color = textoSecundario
                )
                Text(
                    text = stringResource(R.string.costo_precio_margen, formatearPct(margen, formatoPct)),
                    style = MaterialTheme.typography.bodyMedium,
                    color = textoSecundario
                )
            }
        }
    }
}

@Composable
private fun FilaResumen(etiqueta: String, valor: String, destacado: Boolean) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = etiqueta,
            style = if (destacado) MaterialTheme.typography.titleMedium else MaterialTheme.typography.bodyLarge,
            color = if (destacado) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
            fontWeight = if (destacado) FontWeight.SemiBold else FontWeight.Normal,
            modifier = Modifier.weight(1f)
        )
        Text(
            text = valor,
            style = if (destacado) MaterialTheme.typography.titleMedium else MaterialTheme.typography.bodyLarge,
            color = if (destacado) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface,
            fontWeight = if (destacado) FontWeight.Bold else FontWeight.SemiBold
        )
    }
}

/**
 * Comparte el archivo diagnostico_pastelpro.txt via Intent.ACTION_SEND.
 * Útil para reproducir bugs sin ADB ni Logcat Reader.
 */
private fun compartirDiagnostico(context: android.content.Context) {
    try {
        val archivo = DiagnosticLogger.obtenerArchivo()
        if (archivo == null || !archivo.exists()) {
            android.widget.Toast.makeText(
                context,
                "Sin archivo de diagnóstico aún. Provoca el bug primero.",
                android.widget.Toast.LENGTH_LONG
            ).show()
            return
        }

        val uri = FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            archivo
        )

        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_STREAM, uri)
            putExtra(Intent.EXTRA_SUBJECT, "PastelPro · Diagnóstico")
            putExtra(Intent.EXTRA_TEXT, "Diagnóstico generado en: ${DiagnosticLogger.rutaArchivo()}")
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }

        context.startActivity(
            Intent.createChooser(intent, "Compartir diagnóstico")
        )
    } catch (e: Exception) {
        DiagnosticLogger.logError("UI", "Error compartiendo diagnóstico", e)
        android.widget.Toast.makeText(
            context,
            "Error al compartir: ${e.message}",
            android.widget.Toast.LENGTH_LONG
        ).show()
    }
}
