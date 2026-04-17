package com.group10.terrace.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.group10.terrace.ui.components.ColorButtonGreen
import com.group10.terrace.ui.components.ColorDarkGreenText
import com.group10.terrace.ui.components.ColorOptionBg
import com.group10.terrace.ui.components.ProgressHeader
import com.group10.terrace.ui.theme.*



@Composable
fun StepTwoScreen(
    selectedExperience: String,
    onExperienceChange: (String) -> Unit,
    onSubmit: () -> Unit
) {
    val options = listOf(
        "Pemula (Newbie)" to "Baru mulai belajar bertanam.",
        "Menengah (Amateur)" to "Sudah pernah mencoba beberapa kali.",
        "Ahli (Expert)" to "Sudah mahir dan punya banyak tanaman."
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Neutral50)
            .padding(24.dp)
    ) {
        ProgressHeader(step = 2, percentageText = "100%", progress = 1f)

        Spacer(modifier = Modifier.height(32.dp))

        Text("Seberapa Pengalaman Anda", style = Typography.titleLarge.copy(fontWeight = FontWeight.ExtraBold), color = Color(0xFF1C1B1B))
        Spacer(modifier = Modifier.height(8.dp))
        Text("Kami akan menyesuaikan rekomendasi tanaman dan panduan perawatan berdasarkan keahlian Anda", style = Typography.bodyLarge, color = Color(0xFF41493F))

        Spacer(modifier = Modifier.height(24.dp))

        Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
            options.forEach { (title, desc) ->
                val isSelected = selectedExperience == title

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .shadow(if (isSelected) 6.dp else 0.dp, RoundedCornerShape(32.dp))
                        .clip(RoundedCornerShape(32.dp))
                        .background(if (isSelected) Neutral50 else ColorOptionBg)
                        .border(
                            width = if (isSelected) 2.dp else 0.dp,
                            color = if (isSelected) ColorDarkGreenText else Color.Transparent,
                            shape = RoundedCornerShape(32.dp)
                        )
                        .clickable { onExperienceChange(title) }
                        .padding(24.dp)
                ) {
                    Text(
                        text = title,
                        style = Typography.titleLarge.copy(fontSize = 20.sp, fontWeight = FontWeight.Bold),
                        color = if (isSelected) ColorDarkGreenText else Color(0xFF1C1B1B)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = desc,
                        style = Typography.bodyMedium.copy(fontSize = 16.sp),
                        color = Color(0xFF41493F)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        Button(
            onClick = onSubmit,
            modifier = Modifier.fillMaxWidth().height(50.dp),
            colors = ButtonDefaults.buttonColors(containerColor = ColorButtonGreen),
            shape = RoundedCornerShape(10.dp)
        ) {
            Text("Selesai & Lihat Rekomendasi", style = Typography.titleMedium.copy(fontSize = 16.sp), color = Neutral50)
        }
    }
}