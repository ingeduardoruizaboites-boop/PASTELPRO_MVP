package com.pastelpro.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Grain
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.ViewInAr
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.pastelpro.R
import com.pastelpro.ui.theme.White

@Composable
fun HomeScreen(
    onNuevoPastel: () -> Unit = {},
    onVerRecetas: () -> Unit = {},
    onVerIngredientes: () -> Unit = {}
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 24.dp, vertical = 16.dp)
    ) {
        Spacer(Modifier.height(16.dp))

        Text(
            text = stringResource(R.string.home_greeting),
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = stringResource(R.string.home_question),
            style = MaterialTheme.typography.headlineLarge,
            color = MaterialTheme.colorScheme.primary,
            fontWeight = FontWeight.SemiBold
        )

        Spacer(Modifier.height(28.dp))

        // ── CTA principal: Nuevo pastel (placeholder hasta Bloque 5) ──
        Button(
            onClick = onNuevoPastel,
            modifier = Modifier
                .fillMaxWidth()
                .height(64.dp),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = White
            )
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Icon(
                    imageVector = Icons.Filled.Add,
                    contentDescription = null,
                    tint = White
                )
                Spacer(Modifier.padding(horizontal = 4.dp))
                Text(
                    text = stringResource(R.string.home_new_cake),
                    style = MaterialTheme.typography.titleMedium,
                    color = White
                )
            }
        }

        Spacer(Modifier.height(20.dp))

        Text(
            text = stringResource(R.string.home_accesos_rapidos),
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            fontWeight = FontWeight.SemiBold
        )

        Spacer(Modifier.height(12.dp))

        // ── Grid 2x2 de accesos rápidos ──
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            AccesoRapido(
                icon = Icons.AutoMirrored.Filled.MenuBook,
                label = stringResource(R.string.home_acceso_recetas),
                onClick = onVerRecetas,
                modifier = Modifier.weight(1f)
            )
            AccesoRapido(
                icon = Icons.Filled.Grain,
                label = stringResource(R.string.home_acceso_ingredientes),
                onClick = onVerIngredientes,
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(Modifier.height(12.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            AccesoRapido(
                icon = Icons.Filled.ShoppingCart,
                label = stringResource(R.string.home_acceso_compras),
                onClick = { /* Bloque futuro */ },
                modifier = Modifier.weight(1f),
                habilitado = false
            )
            AccesoRapido(
                icon = Icons.Filled.ViewInAr,
                label = stringResource(R.string.home_acceso_moldes),
                onClick = { /* Bloque futuro */ },
                modifier = Modifier.weight(1f),
                habilitado = false
            )
        }

        Spacer(Modifier.weight(1f))
    }
}

@Composable
private fun AccesoRapido(
    icon: ImageVector,
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    habilitado: Boolean = true
) {
    Surface(
        modifier = modifier
            .height(96.dp)
            .clickable(enabled = habilitado, onClick = onClick),
        shape = RoundedCornerShape(14.dp),
        color = MaterialTheme.colorScheme.surface,
        shadowElevation = if (habilitado) 1.dp else 0.dp
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = if (habilitado)
                    MaterialTheme.colorScheme.primary
                else
                    MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f),
                modifier = Modifier.size(28.dp)
            )
            Spacer(Modifier.height(6.dp))
            Text(
                text = label,
                style = MaterialTheme.typography.bodyMedium,
                color = if (habilitado)
                    MaterialTheme.colorScheme.onSurface
                else
                    MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                fontWeight = FontWeight.Medium
            )
        }
    }
}
