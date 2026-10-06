package com.pastelpro.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pastelpro.R
import com.pastelpro.ui.theme.Cocoa
import com.pastelpro.ui.theme.Cream
import com.pastelpro.ui.theme.Neutral700
import com.pastelpro.ui.theme.White

@Composable
fun HomeScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Cream)
            .padding(24.dp)
    ) {
        Spacer(Modifier.height(16.dp))
        Text(
            text = stringResource(R.string.home_greeting),
            style = MaterialTheme.typography.bodyLarge,
            color = Neutral700
        )
        Text(
            text = stringResource(R.string.home_question),
            style = MaterialTheme.typography.headlineLarge,
            color = Cocoa,
            fontWeight = FontWeight.SemiBold
        )
        Spacer(Modifier.height(32.dp))
        Button(
            onClick = { /* Bloque 2 */ },
            modifier = Modifier
                .fillMaxSize()
                .height(64.dp),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Cocoa,
                contentColor = White
            )
        ) {
            Text(
                text = "+  " + stringResource(R.string.home_new_cake),
                style = MaterialTheme.typography.titleMedium
            )
        }
    }
}

@Composable
fun RecipesScreen() = PlaceholderScreen(title = stringResource(R.string.tab_recipes))

@Composable
fun OrdersScreen() = PlaceholderScreen(title = stringResource(R.string.tab_orders))

@Composable
fun ShoppingScreen() = PlaceholderScreen(title = stringResource(R.string.tab_shopping))

@Composable
fun MoreScreen() = PlaceholderScreen(title = stringResource(R.string.tab_more))

@Composable
private fun PlaceholderScreen(title: String) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Cream),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.padding(32.dp)
        ) {
            Text(
                text = title,
                fontSize = 32.sp,
                fontWeight = FontWeight.SemiBold,
                color = Cocoa
            )
            Spacer(Modifier.height(16.dp))
            Text(
                text = stringResource(R.string.placeholder_coming_soon),
                style = MaterialTheme.typography.titleLarge,
                color = Neutral700
            )
            Spacer(Modifier.height(8.dp))
            Text(
                text = stringResource(R.string.placeholder_coming_soon_detail),
                style = MaterialTheme.typography.bodyMedium,
                color = Neutral700,
                textAlign = TextAlign.Center
            )
        }
    }
}
