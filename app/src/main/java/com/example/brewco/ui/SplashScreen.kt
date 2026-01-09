package com.example.brewco.ui

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.EaseOutBack
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.brewco.R
import com.example.brewco.ui.theme.BrewCoTheme
import com.example.brewco.ui.theme.HighlandRed
import com.example.brewco.ui.theme.HighlandWhite
import kotlinx.coroutines.delay

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun SplashScreen(onSplashFinished: () -> Unit = {}) {
    LaunchedEffect(Unit) {
        delay(1800)
        onSplashFinished()
    }

    val titleAlpha by animateFloatAsState(
        targetValue = 1f,
        animationSpec = tween(durationMillis = 800, easing = FastOutSlowInEasing),
        label = "title_alpha"
    )

    val taglineAlpha by animateFloatAsState(
        targetValue = 1f,
        animationSpec = tween(durationMillis = 800, delayMillis = 200, easing = FastOutSlowInEasing),
        label = "tagline_alpha"
    )

    val logoScale by animateFloatAsState(
        targetValue = 1f,
        animationSpec = tween(durationMillis = 800, easing = EaseOutBack),
        label = "logo_scale"
    )

    val contentAlpha by animateFloatAsState(
        targetValue = 1f,
        animationSpec = tween(durationMillis = 600, easing = LinearEasing),
        label = "content_alpha"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(HighlandRed)
            .alpha(contentAlpha),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.scale(logoScale)
        ) {
            Text(
                text = "Brew Co",
                color = HighlandWhite,
                fontSize = 56.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily(Font(R.font.agbalumo_regular)),
                textAlign = TextAlign.Center,
                modifier = Modifier.alpha(titleAlpha)
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Khơi nguồn đam mê cà phê",
                color = HighlandWhite,
                fontSize = 16.sp,
                fontWeight = FontWeight.Normal,
                textAlign = TextAlign.Center,
                modifier = Modifier.alpha(taglineAlpha)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun SplashScreenPreview() {
    BrewCoTheme {
        SplashScreen()
    }
}
