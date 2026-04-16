package com.group10.terrace.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.group10.terrace.R
import com.group10.terrace.ui.theme.Green200
import com.group10.terrace.ui.theme.Green700
import com.group10.terrace.ui.theme.Neutral50
import com.group10.terrace.ui.theme.Red400
import com.group10.terrace.ui.theme.Typography
import com.group10.terrace.ui.theme.Yellow200
import com.group10.terrace.ui.theme.Yellow800


@Composable
fun DifficultyBadge(difficulty: String) {
    val (bgColor, textColor, iconRes) = when (difficulty.lowercase()) {
        "mudah", "easy" -> Triple(Green200, Green700, R.drawable.stars)
        "sedang", "medium" -> Triple(Yellow200, Yellow800, R.drawable.stars_shiny)
        "sulit", "hard" -> Triple(Red400, Neutral50, R.drawable.lightning)
        else -> Triple(Green200, Green700, R.drawable.stars)
    }

    Row(
        modifier = Modifier
            .background(color = bgColor, shape = RoundedCornerShape(4.dp))
            .padding(horizontal = 6.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            painter = painterResource(id = iconRes),
            contentDescription = null,
            modifier = Modifier.size(12.dp)
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(
            text = difficulty.capitalize(),
            style = Typography.labelMedium.copy(fontSize = 10.sp, fontWeight = FontWeight.SemiBold),
            color = textColor
        )
    }
}