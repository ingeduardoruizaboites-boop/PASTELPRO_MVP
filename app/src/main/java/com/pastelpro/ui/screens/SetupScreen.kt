package com.pastelpro.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pastelpro.R
import com.pastelpro.ui.theme.Berry
import com.pastelpro.ui.theme.Cocoa
import com.pastelpro.ui.theme.Cream
import com.pastelpro.ui.theme.Neutral300
import com.pastelpro.ui.theme.Neutral700
import com.pastelpro.ui.theme.White

@Composable
fun SetupScreen(onFinished: () -> Unit) {
    var step by remember { mutableIntStateOf(1) }
    var workProfile by remember { mutableStateOf<String?>(null) }
    var currency by remember { mutableStateOf("MXN") }
    var businessName by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Cream)
            .padding(24.dp)
    ) {
        Text(
            text = stringResource(R.string.setup_step_of, step, 3),
            style = MaterialTheme.typography.labelLarge,
            color = Berry
        )

        Spacer(Modifier.height(24.dp))

        when (step) {
            1 -> StepHowWork(
                selected = workProfile,
                onSelect = { workProfile = it }
            )
            2 -> StepCurrency(
                selected = currency,
                onSelect = { currency = it }
            )
            3 -> StepBusiness(
                value = businessName,
                onChange = { businessName = it }
            )
        }

        Spacer(Modifier.weight(1f))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            if (step > 1) {
                TextButton(
                    onClick = { step-- },
                    modifier = Modifier.weight(1f)
                ) {
                    Text(stringResource(R.string.setup_back), color = Cocoa)
                }
            }

            Button(
                onClick = {
                    if (step < 3) step++ else onFinished()
                },
                modifier = Modifier
                    .weight(2f)
                    .height(56.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Cocoa,
                    contentColor = White
                ),
                enabled = when (step) {
                    1 -> workProfile != null
                    else -> true
                }
            ) {
                Text(
                    text = if (step < 3)
                        stringResource(R.string.setup_next)
                    else
                        stringResource(R.string.setup_finish),
                    style = MaterialTheme.typography.labelLarge
                )
            }
        }
    }
}

@Composable
private fun StepHowWork(selected: String?, onSelect: (String) -> Unit) {
    Column {
        Text(
            text = stringResource(R.string.setup_title_how),
            style = MaterialTheme.typography.headlineMedium,
            color = Cocoa
        )
        Spacer(Modifier.height(24.dp))
        OptionCard(
            text = stringResource(R.string.setup_option_home),
            isSelected = selected == "home",
            onClick = { onSelect("home") }
        )
        Spacer(Modifier.height(12.dp))
        OptionCard(
            text = stringResource(R.string.setup_option_small_business),
            isSelected = selected == "small",
            onClick = { onSelect("small") }
        )
        Spacer(Modifier.height(12.dp))
        OptionCard(
            text = stringResource(R.string.setup_option_occasional),
            isSelected = selected == "occasional",
            onClick = { onSelect("occasional") }
        )
    }
}

@Composable
private fun StepCurrency(selected: String, onSelect: (String) -> Unit) {
    Column {
        Text(
            text = stringResource(R.string.setup_title_currency),
            style = MaterialTheme.typography.headlineMedium,
            color = Cocoa
        )
        Spacer(Modifier.height(24.dp))
        listOf("MXN" to "Peso mexicano", "USD" to "US Dollar", "EUR" to "Euro", "BRL" to "Real brasileiro").forEach { (code, label) ->
            OptionCard(
                text = "$code · $label",
                isSelected = selected == code,
                onClick = { onSelect(code) }
            )
            Spacer(Modifier.height(12.dp))
        }
    }
}

@Composable
private fun StepBusiness(value: String, onChange: (String) -> Unit) {
    Column {
        Text(
            text = stringResource(R.string.setup_title_business),
            style = MaterialTheme.typography.headlineMedium,
            color = Cocoa
        )
        Spacer(Modifier.height(24.dp))
        OutlinedTextField(
            value = value,
            onValueChange = onChange,
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text(stringResource(R.string.setup_business_hint)) },
            singleLine = true,
            shape = RoundedCornerShape(12.dp)
        )
    }
}

@Composable
private fun OptionCard(
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                color = if (isSelected) White else Cream,
                shape = RoundedCornerShape(12.dp)
            )
            .border(
                width = if (isSelected) 2.dp else 1.dp,
                color = if (isSelected) Cocoa else Neutral300,
                shape = RoundedCornerShape(12.dp)
            )
            .clickable(onClick = onClick)
            .padding(horizontal = 20.dp, vertical = 18.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.bodyLarge,
            color = if (isSelected) Cocoa else Neutral700,
            fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal
        )
    }
}
