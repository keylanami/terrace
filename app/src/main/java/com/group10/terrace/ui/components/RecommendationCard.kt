package com.group10.terrace.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.group10.terrace.R
import com.group10.terrace.model.Plant
import com.group10.terrace.ui.theme.Neutral50
import com.group10.terrace.ui.theme.Neutral900
import com.group10.terrace.ui.theme.Typography

@Composable
fun PlantRecommendationCard(plant: Plant, onClick: () -> Unit) {
    Column(
        modifier = Modifier
            .width(160.dp)
            .shadow(elevation = 10.dp, shape = RoundedCornerShape(15.dp), spotColor = Neutral900.copy(alpha = 0.1f))
            .background(color = Neutral50, shape = RoundedCornerShape(15.dp))
            .padding(12.dp)
    ) {
        Image(
            painter = painterResource(id = R.drawable.fototanaman),
            contentDescription = plant.name,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxWidth()
                .height(94.dp)
                .clip(RoundedCornerShape(8.dp))
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = plant.name,
            style = Typography.labelLarge.copy(fontWeight = FontWeight.SemiBold),
            color = Neutral900,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )

        Spacer(modifier = Modifier.height(6.dp))

        DifficultyBadge(difficulty = plant.difficulty)
    }
}