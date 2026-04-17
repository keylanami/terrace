package com.group10.terrace.screen

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.group10.terrace.R
import com.group10.terrace.ui.components.ColorButtonGreen
import com.group10.terrace.ui.theme.*


@Composable
fun IntroScreen(onNext: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Neutral50),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.5f)
                .background(ColorButtonGreen)
        ) {
            Image(
                painter = painterResource(id = R.drawable.peopleonboarding),
                contentDescription = "Ilustrasi",
                contentScale = ContentScale.FillBounds,
                modifier = Modifier.fillMaxSize()
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        Text(
            text = "Mulai Perjalanan\nBertanam Anda",
            style = Typography.displayLarge.copy(fontSize = 36.sp, fontWeight = FontWeight.ExtraBold),
            color = Color(0xFF1C1B1B),
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Bantu kami mengenal ruang dan ambisi\nAnda. Personalisasi memastikan setiap\nbenih yang Anda tanam tumbuh\nmenjadi hasil panen yang maksimal.",
            style = Typography.bodyLarge.copy(fontSize = 18.sp),
            color = Color(0xFF41493F),
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 32.dp)
        )

        Spacer(modifier = Modifier.weight(1f))

        Button(
            onClick = onNext,
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp)
                .height(50.dp),
            colors = ButtonDefaults.buttonColors(containerColor = ColorButtonGreen),
            shape = RoundedCornerShape(10.dp)
        ) {
            Text("Mulai Personalisasi", style = Typography.titleMedium.copy(fontSize = 16.sp), color = Neutral50)
        }
    }
}