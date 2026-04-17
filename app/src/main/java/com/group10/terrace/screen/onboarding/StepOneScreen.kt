package com.group10.terrace.screen.onboarding

import android.R
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.group10.terrace.ui.components.ColorButtonGreen
import com.group10.terrace.ui.components.ColorCardBg
import com.group10.terrace.ui.components.ColorDarkGreenText
import com.group10.terrace.ui.components.ProgressHeader
import com.group10.terrace.ui.theme.*



@Composable
fun StepOneScreen(
    landSize: String, onLandSizeChange: (String) -> Unit,
    location: String, onLocationChange: (String) -> Unit,
    onNext: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Neutral50)
            .padding(24.dp)
    ) {
        // Top Progress
        ProgressHeader(step = 1, percentageText = "50%", progress = 0.5f)

        Spacer(modifier = Modifier.height(32.dp))

        Text("Informasi Lahan Anda", style = Typography.titleLarge.copy(fontWeight = FontWeight.ExtraBold), color = Color(0xFF1C1B1B))
        Spacer(modifier = Modifier.height(8.dp))
        Text("Bantu kami menyesuaikan saran tanaman\nyang paling cocok untuk ruang hijau Anda.", style = Typography.bodyLarge, color = Color(0xFF41493F))

        Spacer(modifier = Modifier.height(24.dp))

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(ColorCardBg, RoundedCornerShape(32.dp))
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {

            Column {
                Text("Luas Lahan", style = Typography.titleMedium.copy(fontWeight = FontWeight.SemiBold), color = ColorDarkGreenText)
                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(60.dp)
                        .background(Neutral50, RoundedCornerShape(48.dp))
                        .padding(horizontal = 24.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    BasicTextField(
                        value = landSize,
                        onValueChange = onLandSizeChange,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        textStyle = TextStyle(fontSize = 18.sp, color = Neutral900),
                        modifier = Modifier.weight(1f),
                        decorationBox = { innerTextField ->
                            if (landSize.isEmpty()) Text("Contoh: 2.5", color = Neutral400, fontSize = 18.sp)
                            innerTextField()
                        },
                        cursorBrush = SolidColor(ColorButtonGreen)
                    )
                    Text("m²", style = Typography.titleMedium.copy(fontWeight = FontWeight.Bold), color = ColorDarkGreenText)
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text("Estimasi area yang tersedia untuk bercocok tanam.", style = Typography.labelSmall, color = Color(0xFF504346))
            }


            Column {
                Text("Lokasi", style = Typography.titleMedium.copy(fontWeight = FontWeight.SemiBold), color = ColorDarkGreenText)
                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                        .background(Neutral50, RoundedCornerShape(48.dp))
                        .padding(horizontal = 20.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(painter = painterResource(id = R.drawable.ic_dialog_map), contentDescription = "Lokasi", tint = Color(0xFF717A6E), modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(12.dp))
                    BasicTextField(
                        value = location,
                        onValueChange = onLocationChange,
                        textStyle = TextStyle(fontSize = 16.sp, color = Neutral900),
                        modifier = Modifier.weight(1f),
                        decorationBox = { innerTextField ->
                            if (location.isEmpty()) Text("Masukkan kota, kecamatan...", color = Neutral400, fontSize = 16.sp)
                            innerTextField()
                        },
                        cursorBrush = SolidColor(ColorButtonGreen)
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text("Digunakan untuk menganalisis cuaca dan kelembapan setempat.", style = Typography.labelSmall, color = Color(0xFF504346))
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        Button(
            onClick = onNext,
            modifier = Modifier.fillMaxWidth().height(50.dp),
            colors = ButtonDefaults.buttonColors(containerColor = ColorButtonGreen),
            shape = RoundedCornerShape(10.dp)
        ) {
            Text("Lanjut", style = Typography.titleMedium.copy(fontSize = 16.sp), color = Neutral50)
        }
    }
}