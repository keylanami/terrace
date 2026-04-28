package com.group10.terrace.screen.marketplace

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.outlined.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.group10.terrace.R
import com.group10.terrace.ui.theme.*
import com.group10.terrace.viewmodel.MarketplaceViewModel
import java.text.NumberFormat
import java.util.Locale

@Composable
fun ProductDetailScreen(
    viewModel: MarketplaceViewModel,
    productId: String,
    userId: String,
    onBack: () -> Unit,
    onNavigateToCart: () -> Unit
) {
    val products by viewModel.products.collectAsState()
    val product = products.find { it.id == productId }

    if (product == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { CircularProgressIndicator() }
        return
    }

    val formatRupiah = NumberFormat.getNumberInstance(Locale("id", "ID"))
    val hargaAsli = product.price.toInt()
    val hargaCoret = hargaAsli + 10000

    Box(modifier = Modifier.fillMaxSize().background(Neutral50)) {

        AsyncImage(
            model = product.imageUrl,
            contentDescription = product.name,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxWidth().height(453.dp),
            placeholder = painterResource(id = R.drawable.fototanaman),
            error = painterResource(id = R.drawable.fototanaman)
        )

        Icon(
            Icons.AutoMirrored.Outlined.ArrowBack,
            contentDescription = "Back",
            modifier = Modifier.padding(24.dp).size(32.dp).clickable { onBack() },
            tint = Neutral50
        )

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.65f)
                .align(Alignment.BottomCenter)
                .clip(RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp))
                .background(Neutral50)
                .padding(24.dp)
                .verticalScroll(rememberScrollState())
        ) {

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Box(modifier = Modifier.background(Green600, RoundedCornerShape(10.dp)).padding(horizontal = 10.dp, vertical = 4.dp)) {
                        Text(product.category, style = Typography.labelSmall.copy(fontSize = 10.sp), color = Neutral50)
                    }
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(painter = painterResource(id = android.R.drawable.star_on), contentDescription = "Star", tint = Green600, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("${product.rating}", style = Typography.titleMedium.copy(fontWeight = FontWeight.Bold, fontSize = 14.sp), color = Green600)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(product.name, style = Typography.displaySmall.copy(fontWeight = FontWeight.ExtraBold, fontSize = 30.sp), color = Neutral900)
            Spacer(modifier = Modifier.height(8.dp))
            Row(verticalAlignment = Alignment.Bottom, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Text("Rp. ${formatRupiah.format(hargaAsli)}", style = Typography.headlineSmall.copy(fontWeight = FontWeight.ExtraBold, fontSize = 24.sp), color = Green600)
                Text("Rp. ${formatRupiah.format(hargaCoret)}", style = Typography.bodyMedium.copy(color = Color(0x996D5F62), textDecoration = TextDecoration.LineThrough))
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text("Deskripsi", style = Typography.titleLarge.copy(fontWeight = FontWeight.Bold, fontSize = 20.sp), color = Neutral900)
            Spacer(modifier = Modifier.height(8.dp))
            Text(product.description, style = Typography.bodyLarge.copy(fontSize = 15.sp), color = Color(0xFF41493F), textAlign = TextAlign.Justify)

            Spacer(modifier = Modifier.height(40.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(50.dp)
                        .background(Neutral50, RoundedCornerShape(10.dp))
                        .border(1.dp, Green600, RoundedCornerShape(10.dp))
                        .clickable {
                            viewModel.addToCart(userId, product, 1)
                            onBack()
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Outlined.ShoppingCart, contentDescription = "Add to Cart", tint = Green600)
                }

                Button(
                    onClick = {
                        viewModel.addToCart(userId, product, 1)
                        onNavigateToCart()
                    },
                    modifier = Modifier.weight(1f).height(50.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Green600),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text("Beli Sekarang", style = Typography.titleMedium.copy(fontSize = 16.sp), color = Neutral50)
                }
            }
        }
    }
}