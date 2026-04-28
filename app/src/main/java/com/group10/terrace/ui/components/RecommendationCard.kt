package com.group10.terrace.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import coil.compose.AsyncImage
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
            .shadow(
                elevation = 12.dp,
                shape = RoundedCornerShape(16.dp),
                spotColor = Neutral900.copy(alpha = 0.08f)
            )
            .clip(RoundedCornerShape(16.dp))
            .background(color = Neutral50)
            .clickable { onClick() }
            .padding(12.dp)
    ) {

        AsyncImage(
            model = plant.imageUrl,
            contentDescription = plant.name,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxWidth()
                .height(96.dp)
                .clip(RoundedCornerShape(10.dp)),
            placeholder = painterResource(id = R.drawable.fototanaman),
            error = painterResource(id = R.drawable.fototanaman)
        )

        Spacer(modifier = Modifier.height(10.dp))

        Text(
            text = plant.name,
            style = Typography.labelLarge.copy(fontWeight = FontWeight.Bold),
            color = Neutral900,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )

        Spacer(modifier = Modifier.height(8.dp))

        DifficultyBadge(difficulty = plant.difficulty)
    }
}