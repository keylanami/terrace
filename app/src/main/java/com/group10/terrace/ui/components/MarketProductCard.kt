package com.group10.terrace.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ShoppingCart
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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
fun MarketProductCard(
    product: Product,
    onClick: () -> Unit,
    onAddToCartClick: () -> Unit = {} // Tambahan fungsi aksi cepat
) {
    val fmt = NumberFormat.getNumberInstance(Locale("id", "ID"))

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(Neutral50)
            .clickable { onClick() }
            .padding(12.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        AsyncImage(
            model = product.imageUrl,
            contentDescription = product.name,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxWidth().aspectRatio(1f).clip(RoundedCornerShape(8.dp)),
            placeholder = painterResource(id = R.drawable.fototanaman),
            error = painterResource(id = R.drawable.fototanaman)
        )
        Text(
            text = product.name,
            style = Typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
            color = Neutral900,
            maxLines = 2
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Rp ${fmt.format(product.price)}",
                style = Typography.bodyLarge.copy(fontWeight = FontWeight.Bold),
                color = Green600
            )
            // Aksi Cepat Tambah ke Keranjang
            Box(
                modifier = Modifier
                    .size(28.dp)
                    .background(Green100, RoundedCornerShape(8.dp))
                    .clickable { onAddToCartClick() },
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Outlined.ShoppingCart, contentDescription = "Add to Cart", tint = Green600, modifier = Modifier.size(16.dp))
            }
        }
    }
}