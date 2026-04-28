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
import java.util.concurrent.TimeUnit

@Composable
fun ActivePlantCard(
    userPlant: UserPlant,
    estimationDays: Int,
    difficulty: String,
    isPriority: Boolean = false,
    masterPlant: Plant,
    onClick: () -> Unit
) {
    val currentDay =
        calculateDaysPassedActive(userPlant.startDate).coerceAtMost(estimationDays.toLong())

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
            .height(124.dp)
            .shadow(
                elevation = 20.dp,
                shape = RoundedCornerShape(size = 20.dp),
                spotColor = Color(0x0F000000),
                ambientColor = Color(0x0F000000)
            )
            .background(color = Color(0xFFFFFFFF), shape = RoundedCornerShape(size = 20.dp))
            .clickable { onClick() }
            .padding(start = 14.dp, top = 15.dp, end = 14.dp, bottom = 15.dp),
        horizontalArrangement = Arrangement.spacedBy(15.dp, Alignment.Start),
        verticalAlignment = Alignment.CenterVertically
    ) {
        AsyncImage(
            model = masterPlant.imageUrl,
            contentDescription = userPlant.plantName,
            contentScale = ContentScale.FillBounds,
            modifier = Modifier
                .width(106.dp)
                .height(94.dp)
                .clip(RoundedCornerShape(12.dp)),
            placeholder = painterResource(id = R.drawable.fototanaman),
            error = painterResource(id = R.drawable.fototanaman)
        )

        Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                if (isPriority) {
                    Box(
                        modifier = Modifier
                            .background(
                                color = Yellow500,
                                shape = RoundedCornerShape(10.dp)
                            )
                            .padding(horizontal = 10.dp, vertical = 4.dp)
                    ) {
                        Text(
                            "Priority",
                            style = Typography.labelMedium.copy(
                                fontSize = 10.sp,
                                fontWeight = FontWeight.SemiBold
                            ),
                            color = Neutral50
                        )
                    }
                }

                // --- BADGE KESEHATAN DISINI ---
                Box(
                    modifier = Modifier
                        .background(healthColor, RoundedCornerShape(10.dp))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        userPlant.healthStatus,
                        style = Typography.labelMedium.copy(
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        ),
                        color = Neutral50
                    )
                }

                Text(
                    text = userPlant.plantName,
                    style = Typography.bodyLarge.copy(
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 14.sp
                    ),
                    color = Neutral900,
                    maxLines = 1
                )
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(10.dp)
                        .background(color = Neutral300, shape = RoundedCornerShape(20.dp))
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(
                                fraction = (userPlant.progress / 100f).coerceIn(
                                    0f,
                                    1f
                                )
                            )
                            .height(10.dp)
                            .background(color = Green600, shape = RoundedCornerShape(20.dp))
                    )
                }
                Text(
                    text = "${userPlant.progress}%",
                    style = Typography.labelMedium.copy(
                        fontWeight = FontWeight.Medium,
                        fontSize = 10.sp
                    ),
                    color = Neutral900
                )
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                DifficultyBadge(difficulty = difficulty)
                Text(
                    text = "$currentDay/$estimationDays Hari Tumbuh",
                    style = Typography.labelMedium.copy(
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 10.sp
                    ),
                    color = Green600
                )
            }
        }
    }
}

private fun calculateDaysPassedActive(startDateMillis: Long): Long {
    val diff = System.currentTimeMillis() - startDateMillis
    return TimeUnit.MILLISECONDS.toDays(diff).coerceAtLeast(1L)
}