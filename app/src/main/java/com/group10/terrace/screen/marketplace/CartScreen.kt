package com.group10.terrace.screen.marketplace

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.group10.terrace.R
import com.group10.terrace.model.CartItem
import com.group10.terrace.ui.components.BottomNavBar
import com.group10.terrace.ui.theme.*
import com.group10.terrace.viewmodel.MarketplaceViewModel
import java.text.NumberFormat
import java.util.Locale

@Composable
fun CartScreen(
    viewModel: MarketplaceViewModel,
    userId: String,
    onBack: () -> Unit,
    onNavigate: (String) -> Unit,
    onNavigateToCheckout: () -> Unit
) {
    LaunchedEffect(Unit) { viewModel.loadCart(userId) }

    val cartItems by viewModel.cartItems.collectAsState()
    val products by viewModel.products.collectAsState()
    val formatRupiah = NumberFormat.getNumberInstance(Locale("id", "ID"))

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Neutral50)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {

            // --- Top Bar (Mengikuti gaya Checkout) ---
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 16.dp)
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    modifier = Modifier
                        .align(Alignment.CenterStart)
                        .size(24.dp)
                        .clickable { onBack() },
                    tint = Neutral900
                )
                Text(
                    text = "Keranjang",
                    style = Typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = Neutral900,
                    modifier = Modifier.align(Alignment.Center)
                )
            }

            if (cartItems.isEmpty()) {
                Box(modifier = Modifier.weight(1f).fillMaxWidth(), contentAlignment = Alignment.Center) {
                    Text("Keranjangmu masih kosong.", color = Neutral400)
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    contentPadding = PaddingValues(bottom = 100.dp) // Beri space agar tidak tertutup button/navbar
                ) {
                    items(cartItems) { item ->
                        val originalProduct = products.find { it.id == item.productId }

                        // --- Item Card (Mengikuti gaya CheckoutCartItemRow) ---
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Neutral50, RoundedCornerShape(12.dp))
                                .border(1.dp, Neutral200, RoundedCornerShape(12.dp))
                                .padding(12.dp),
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            AsyncImage(
                                model = item.imageUrl,
                                contentDescription = item.productName,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier
                                    .size(64.dp)
                                    .clip(RoundedCornerShape(8.dp)),
                                error = painterResource(id = R.drawable.fototanaman),
                                placeholder = painterResource(id = R.drawable.fototanaman)
                            )

                            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                Text(
                                    text = item.productName,
                                    style = Typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                                    color = Neutral900
                                )
                                Text(
                                    text = "Satuan",
                                    style = Typography.labelMedium.copy(fontSize = 11.sp),
                                    color = Neutral600
                                )
                                Text(
                                    text = "Rp ${formatRupiah.format(item.price)}",
                                    style = Typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                                    color = Green600
                                )
                            }

                            // --- Stepper Quantity (Gaya Kotak Abu-abu seperti Checkout) ---
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(24.dp)
                                        .background(Neutral200, RoundedCornerShape(4.dp))
                                        .clickable {
                                            if (item.quantity > 1 && originalProduct != null) {
                                                viewModel.addToCart(userId, originalProduct, item.quantity - 1)
                                            } else {
                                                viewModel.removeFromCart(userId, item.cartItemId)
                                            }
                                        },
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text("−", style = Typography.bodyLarge, color = Neutral900)
                                }

                                Text(
                                    text = "${item.quantity}",
                                    style = Typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                                    color = Neutral900,
                                    modifier = Modifier.widthIn(min = 20.dp),
                                    textAlign = TextAlign.Center
                                )

                                Box(
                                    modifier = Modifier
                                        .size(24.dp)
                                        .background(Neutral200, RoundedCornerShape(4.dp))
                                        .clickable {
                                            if (originalProduct != null) {
                                                viewModel.addToCart(userId, originalProduct, item.quantity + 1)
                                            }
                                        },
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text("+", style = Typography.bodyLarge, color = Neutral900)
                                }
                            }
                        }
                    }
                }
            }

            if (cartItems.isNotEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                        .background(Green700, RoundedCornerShape(12.dp))
                        .clickable { onNavigateToCheckout() }
                        .padding(vertical = 16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Checkout Sekarang",
                        style = Typography.bodyLarge.copy(fontWeight = FontWeight.Bold),
                        color = Neutral50
                    )
                }
            }

            Spacer(modifier = Modifier.height(70.dp))
        }

        BottomNavBar(
            currentRoute = "market",
            onNavigate = onNavigate,
            modifier = Modifier.align(Alignment.BottomCenter)
        )
    }
}