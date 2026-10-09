package com.pastelpro.ui.screens

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pastelpro.R
import com.pastelpro.ui.theme.Cocoa
import com.pastelpro.ui.theme.Cream
import com.pastelpro.ui.theme.Neutral700
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(onFinished: () -> Unit) {
    val logoAlpha = remember { Animatable(0f) }
    val logoScale = remember { Animatable(0.5f) }
    val nombreAlpha = remember { Animatable(0f) }
    val taglineAlpha = remember { Animatable(0f) }

    // Animar la escala del logo continuamente (900ms)
    LaunchedEffect(Unit) {
        logoScale.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = 900, easing = FastOutSlowInEasing)
        )
    }

    LaunchedEffect(Unit) {
        // 1. Logo aparece (700ms)
        logoAlpha.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = 700, easing = FastOutSlowInEasing)
        )
        delay(200)

        // 2. Nombre "PastelPro" (400ms)
        nombreAlpha.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = 400, easing = FastOutSlowInEasing)
        )
        delay(150)

        // 3. Tagline (400ms)
        taglineAlpha.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = 400, easing = FastOutSlowInEasing)
        )

        // 4. Pausa final para que se vea completo (~900ms)
        delay(900)

        onFinished()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Cream),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.padding(24.dp)
        ) {
            Image(
                painter = painterResource(id = R.drawable.logo_pastelpro),
                contentDescription = stringResource(R.string.app_name),
                modifier = Modifier
                    .size(240.dp)
                    .alpha(logoAlpha.value)
                    .scale(logoScale.value)
            )

            Spacer(Modifier.height(32.dp))

            Text(
                text = stringResource(R.string.app_name),
                fontSize = 44.sp,
                fontWeight = FontWeight.Bold,
                color = Cocoa,
                textAlign = TextAlign.Center,
                modifier = Modifier.alpha(nombreAlpha.value)
            )

            Spacer(Modifier.height(12.dp))

            Text(
                text = stringResource(R.string.app_tagline),
                style = MaterialTheme.typography.bodyLarge,
                color = Neutral700,
                textAlign = TextAlign.Center,
                modifier = Modifier.alpha(taglineAlpha.value)
            )
        }
    }
}
