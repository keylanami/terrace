package com.group10.terrace.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.group10.terrace.R
import com.group10.terrace.model.Plant
import com.group10.terrace.model.UserPlant
import com.group10.terrace.ui.theme.*

@Composable
fun PriorityPlantCard(
    userPlant: UserPlant,
    masterPlant: Plant,
    onClick: () -> Unit
) {
    // Penentuan Warna Badge Kesehatan
    val healthColor = when (userPlant.healthStatus) {
        "Subur" -> Green600
        "Kering" -> Color(0xFFFFA000) // Orange
        "Layu" -> Red600
        else -> Green600
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(elevation = 12.dp, shape = RoundedCornerShape(16.dp), spotColor = Neutral900.copy(alpha = 0.08f))
            .clip(RoundedCornerShape(16.dp))
            .background(color = Neutral50)
            .clickable { onClick() }
            .padding(14.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        AsyncImage(
            model = masterPlant.imageUrl,
            contentDescription = userPlant.plantName,
            contentScale = ContentScale.Crop,
            modifier = Modifier.size(width = 96.dp, height = 96.dp).clip(RoundedCornerShape(12.dp)),
            placeholder = painterResource(id = R.drawable.fototanaman),
            error = painterResource(id = R.drawable.fototanaman)
        )

        Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(12.dp)) {

            // Baris Nama & Badge Kesehatan
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Box(modifier = Modifier.background(healthColor, RoundedCornerShape(6.dp)).padding(horizontal = 6.dp, vertical = 2.dp)) {
                    Text(userPlant.healthStatus, style = Typography.labelSmall.copy(fontSize = 9.sp, fontWeight = FontWeight.Bold), color = Neutral50)
                }
                Text(text = userPlant.plantName, style = Typography.titleMedium.copy(fontWeight = FontWeight.Bold, fontSize = 16.sp), color = Neutral900, maxLines = 1, modifier = Modifier.weight(1f))
            }

            // Progress Bar
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                Box(modifier = Modifier.weight(1f).height(8.dp).background(color = Neutral200, shape = RoundedCornerShape(100.dp))) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(fraction = (userPlant.progress / 100f).coerceIn(0f, 1f))
                            .height(8.dp)
                            .background(color = Green600, shape = RoundedCornerShape(100.dp))
                    )
                }
                Text(text = "${userPlant.progress}%", style = Typography.labelMedium.copy(fontWeight = FontWeight.Bold, fontSize = 11.sp), color = Green700)
            }
        }
    }
}