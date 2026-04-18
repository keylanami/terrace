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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.group10.terrace.R
import com.group10.terrace.model.Product
import com.group10.terrace.ui.theme.*
import java.text.NumberFormat
import java.util.Locale

@Composable
fun MarketProductCard(product: Product, onClick: () -> Unit) {
    val formatRupiah = NumberFormat.getNumberInstance(Locale("id", "ID"))

    Column(
        modifier = Modifier
            .width(176.dp)
            .shadow(elevation = 12.dp, shape = RoundedCornerShape(12.dp), spotColor = Neutral900.copy(alpha = 0.08f))
            .clip(RoundedCornerShape(12.dp))
            .background(Neutral50)
            .clickable { onClick() }
            .padding(12.dp)
    ) {
        // Badge Kategori
        Box(
            modifier = Modifier
                .background(Green600, RoundedCornerShape(8.dp))
                .padding(horizontal = 8.dp, vertical = 4.dp)
        ) {
            Text(
                text = product.category,
                style = Typography.labelSmall.copy(fontSize = 10.sp, fontWeight = FontWeight.SemiBold),
                color = Neutral50
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        // UI Fix: Gabungkan Image & AsyncImage jadi satu
        AsyncImage(
            model = product.imageUrl,
            contentDescription = product.name,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxWidth()
                .height(96.dp)
                .clip(RoundedCornerShape(8.dp)),
            placeholder = painterResource(id = R.drawable.fototanaman),
            error = painterResource(id = R.drawable.fototanaman)
        )

        Spacer(modifier = Modifier.height(10.dp))

        Text(
            text = product.name,
            style = Typography.labelLarge.copy(fontWeight = FontWeight.Bold, fontSize = 13.sp),
            color = Neutral900,
            maxLines = 1
        )

        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (product.category.contains("Bibit") || product.category.contains("Sayuran")) {
                DifficultyBadge(difficulty = "mudah")
            } else {
                Spacer(modifier = Modifier.width(1.dp))
            }

            Text(
                text = "Rp${formatRupiah.format(product.price)}",
                style = Typography.labelMedium.copy(fontSize = 11.sp, fontWeight = FontWeight.ExtraBold),
                color = Green700
            )
        }
    }
}