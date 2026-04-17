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
            .shadow(elevation = 15.dp, shape = RoundedCornerShape(10.dp), spotColor = Neutral900.copy(alpha = 0.4f))
            .background(Neutral50, RoundedCornerShape(10.dp))
            .clickable { onClick() }
            .padding(15.dp)
    ) {
        // Badge Kategori
        Box(
            modifier = Modifier
                .background(Green600, RoundedCornerShape(10.dp))
                .padding(horizontal = 8.dp, vertical = 4.dp)
        ) {
            Text(
                text = product.category,
                style = Typography.labelSmall.copy(fontSize = 10.sp, fontWeight = FontWeight.SemiBold),
                color = Neutral50
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Gambar
        Image(
            painter = painterResource(id = R.drawable.fototanaman), // Sesuaikan dengan placeholder/Coil
            contentDescription = product.name,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxWidth()
                .height(94.dp)
                .clip(RoundedCornerShape(8.dp))
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Judul
        Text(
            text = product.name,
            style = Typography.labelLarge.copy(fontWeight = FontWeight.SemiBold, fontSize = 12.sp),
            color = Neutral900,
            maxLines = 1
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Baris Bawah: Difficulty & Harga
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Difficulty Badge (Muncul kalau ada hubungannya dengan tanaman/bibit)
            if (product.category.contains("Bibit") || product.category.contains("Sayuran")) {
                DifficultyBadge(difficulty = "mudah") // Bisa dihubungkan ke data master tanaman jika ada relasinya nanti
            } else {
                Spacer(modifier = Modifier.width(1.dp))
            }

            Text(
                text = "Rp${formatRupiah.format(product.price)}",
                style = Typography.labelMedium.copy(fontSize = 10.sp, fontWeight = FontWeight.Medium),
                color = Neutral900
            )
        }
    }
}