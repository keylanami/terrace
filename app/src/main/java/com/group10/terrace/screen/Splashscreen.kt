package com.group10.terrace.screen

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.group10.terrace.R
import com.group10.terrace.ui.theme.Neutral50
import com.group10.terrace.ui.theme.PrimaryGradient
import kotlinx.coroutines.delay

@Composable
fun SplashScreen() {
    var isGrowing by remember { mutableStateOf(false) }
    var isShrinking by remember { mutableStateOf(false) }

    var isFinalStage by remember { mutableStateOf(false) }

    val scale = animateFloatAsState(
        targetValue = when {
            isFinalStage -> 1f
            isShrinking -> 1f
            isGrowing -> 2f
            else -> 1f
        },
        animationSpec = tween(durationMillis = 600, easing = FastOutSlowInEasing)
    )

    val backgroundAlpha = animateFloatAsState(
        targetValue = if (isFinalStage) 1f else 0f,
        animationSpec = tween(durationMillis = 500)
    )

    LaunchedEffect(Unit) {
        delay(300)
        isGrowing = true

        delay(600)
        isGrowing = false
        isShrinking = true

        delay(600)
        isShrinking = false
        isFinalStage = true

        delay(1200)
    }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        if (isFinalStage) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .alpha(backgroundAlpha.value)
                    .background(brush = PrimaryGradient)
            )
        } else {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Neutral50)
            )
        }

        Image(
            painter = painterResource(
                id = if (isFinalStage) R.drawable.putih_full else R.drawable.hijau
            ),
            contentDescription = "Logo",
            modifier = Modifier
                .size(100.dp)
                .scale(scale.value)
        )
    }
}