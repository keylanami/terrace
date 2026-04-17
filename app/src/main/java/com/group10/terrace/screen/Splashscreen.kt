package com.group10.terrace.screen

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.group10.terrace.R
import com.group10.terrace.ui.theme.Neutral50
import com.group10.terrace.ui.theme.PrimaryGradient
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(
    onNavigateToLogin: () -> Unit,
    onNavigateToHome: () -> Unit
) {
    var isGrowing by remember { mutableStateOf(false) }
    var isShrinking by remember { mutableStateOf(false) }
    var isFinalStage by remember { mutableStateOf(false) }

    val scale by animateFloatAsState(
        targetValue = when {
            isGrowing -> 2f
            isShrinking -> 1f
            isFinalStage -> 1f
            else -> 1f
        },
        animationSpec = tween(durationMillis = 600, easing = FastOutSlowInEasing),
        label = "scale"
    )

    val backgroundAlpha by animateFloatAsState(
        targetValue = if (isFinalStage) 1f else 0f,
        animationSpec = tween(durationMillis = 500),
        label = "bgAlpha"
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

        val currentUser = Firebase.auth.currentUser
        if (currentUser != null) {
            onNavigateToHome()
        } else {
            onNavigateToLogin()
        }
    }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Neutral50)
        )
        if (isFinalStage) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .alpha(backgroundAlpha)
                    .background(brush = PrimaryGradient)
            )
        }

        Image(
            painter = painterResource(
                id = if (isFinalStage) R.drawable.putih_full else R.drawable.hijau
            ),
            contentDescription = "Logo Terrace",
            modifier = Modifier
                .size(100.dp)
                .scale(scale)
        )
    }
}