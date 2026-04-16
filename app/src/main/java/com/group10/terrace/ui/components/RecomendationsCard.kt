package com.group10.terrace.ui.components

import androidx.compose.foundation.Image
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.group10.terrace.R
import com.group10.terrace.model.UserPlant
import com.group10.terrace.ui.theme.*

@Composable
fun PriorityPlantCard(
    userPlant: UserPlant,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(
                elevation = 20.dp,
                shape = RoundedCornerShape(20.dp),
                spotColor = Neutral900.copy(alpha = 0.05f)
            )
            .background(color = Neutral50, shape = RoundedCornerShape(20.dp))
            .clickable { onClick() }
            .padding(start = 14.dp, top = 15.dp, end = 24.dp, bottom = 15.dp),
        horizontalArrangement = Arrangement.spacedBy(17.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {

        Image(
            painter = painterResource(id = R.drawable.fototanaman),
            contentDescription = userPlant.plantName,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .size(width = 106.dp, height = 94.dp)
                .clip(RoundedCornerShape(12.dp))
        )

        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {

            Text(
                text = userPlant.plantName,
                style = Typography.bodyLarge.copy(fontWeight = FontWeight.SemiBold, fontSize = 14.sp),
                color = Neutral900,
                maxLines = 1
            )


            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(10.dp)
                        .background(color = Neutral200, shape = RoundedCornerShape(20.dp))
                ) {

                    Box(
                        modifier = Modifier
                            .fillMaxWidth(fraction = userPlant.progress / 100f)
                            .height(10.dp)
                            .background(color = Green600, shape = RoundedCornerShape(20.dp))
                    )
                }

                Text(
                    text = "${userPlant.progress}%",
                    style = Typography.labelMedium.copy(fontWeight = FontWeight.Medium, fontSize = 10.sp),
                    color = Neutral900
                )
            }
        }
    }
}