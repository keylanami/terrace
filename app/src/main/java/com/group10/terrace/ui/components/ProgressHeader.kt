package com.group10.terrace.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.group10.terrace.ui.theme.*


val ColorDarkGreenText = Color(0xFF024B18)
val ColorButtonGreen = Color(0xFF08712D)
val ColorCardBg = Color(0xFFF6F2F2)
val ColorOptionBg = Color(0xFFF0EDEC)


@Composable
fun ProgressHeader(step: Int, percentageText: String, progress: Float) {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Bottom
        ) {
            Text("Step $step of 2", style = Typography.titleMedium.copy(fontWeight = FontWeight.Medium), color = Color(0xFF41493F))
            Text(percentageText, style = Typography.titleMedium.copy(fontWeight = FontWeight.Bold), color = ColorDarkGreenText)
        }
        Spacer(modifier = Modifier.height(8.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp)
                .background(ColorCardBg, RoundedCornerShape(20.dp))
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(fraction = progress)
                    .height(8.dp)
                    .background(ColorDarkGreenText, RoundedCornerShape(20.dp))
            )
        }
    }
}