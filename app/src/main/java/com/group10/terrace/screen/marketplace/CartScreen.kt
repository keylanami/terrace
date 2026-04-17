package com.group10.terrace.screen.marketplace

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
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
import com.group10.terrace.R
import com.group10.terrace.ui.theme.*
import com.group10.terrace.viewmodel.MarketplaceViewModel
import java.text.NumberFormat
import java.util.Locale

@Composable
fun CartScreen(
    viewModel: MarketplaceViewModel,
    userId: String,
    onBack: () -> Unit,
    onNavigateToCheckout: () -> Unit
) {
    // Pastikan loadCart() dipanggil saat masuk screen ini (idealnya di NavHost atau via efek)
    LaunchedEffect(Unit) { viewModel.loadCart(userId) }

    val cartItems by viewModel.cartItems.collectAsState()
    val products by viewModel.products.collectAsState()
    val formatRupiah = NumberFormat.getNumberInstance(Locale("id", "ID"))

    Scaffold(
        bottomBar = {
            if (cartItems.isNotEmpty()) {
                Box(modifier = Modifier.fillMaxWidth().background(Neutral50).padding(24.dp)) {
                    Button(
                        onClick = onNavigateToCheckout,
                        modifier = Modifier.fillMaxWidth().height(50.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Green600),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text("Checkout Sekarang", style = Typography.titleMedium.copy(fontSize = 16.sp), color = Neutral50)
                    }
                }
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier.fillMaxSize().background(Neutral50).padding(paddingValues)
        ) {

            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp, vertical = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.AutoMirrored.Outlined.ArrowBack, contentDescription = "Back", modifier = Modifier.size(24.dp).clickable { onBack() })
                Text("Keranjang", style = Typography.titleLarge.copy(fontWeight = FontWeight.Bold, fontSize = 20.sp), modifier = Modifier.weight(1f), textAlign = TextAlign.Center)
                Spacer(modifier = Modifier.size(24.dp))
            }

            if (cartItems.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Keranjangmu masih kosong.", color = Neutral400)
                }
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(24.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(cartItems) { item ->
                        val originalProduct = products.find { it.id == item.productId }

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Color(0xFFF4F4F4), RoundedCornerShape(20.dp))
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {

                            Image(
                                painter = painterResource(id = R.drawable.tomat), // TODO: Coil
                                contentDescription = item.productName,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.size(96.dp).clip(RoundedCornerShape(10.dp))
                            )
                            Spacer(modifier = Modifier.width(16.dp))


                            Column(modifier = Modifier.weight(1f)) {
                                Text(item.productName, style = Typography.titleMedium.copy(fontWeight = FontWeight.Bold, fontSize = 18.sp), color = Neutral900)
                                Text("Satuan", style = Typography.labelMedium.copy(fontWeight = FontWeight.Medium, fontSize = 12.sp), color = Neutral900)
                                Spacer(modifier = Modifier.height(8.dp))
                                Text("Rp ${formatRupiah.format(item.price)}", style = Typography.titleMedium.copy(fontWeight = FontWeight.Bold, fontSize = 16.sp), color = Green600)

                                Spacer(modifier = Modifier.height(12.dp))


                                Row(
                                    modifier = Modifier
                                        .border(1.dp, Color(0x33C0C9BC), RoundedCornerShape(9999.dp))
                                        .background(Neutral50, RoundedCornerShape(9999.dp))
                                        .padding(horizontal = 8.dp, vertical = 4.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                                ) {
                                    Icon(
                                        painter = painterResource(id = android.R.drawable.ic_media_previous),
                                        contentDescription = "Min",
                                        modifier = Modifier.size(24.dp).clickable {
                                            if (item.quantity > 1 && originalProduct != null) {
                                                viewModel.addToCart(userId, originalProduct, item.quantity - 1)
                                            } else {
                                                viewModel.removeFromCart(userId, item.cartItemId)
                                            }
                                        }
                                    )
                                    Text("${item.quantity}", style = Typography.titleMedium.copy(fontWeight = FontWeight.Bold))
                                    Icon(
                                        painter = painterResource(id = android.R.drawable.ic_input_add),
                                        contentDescription = "Add",
                                        modifier = Modifier.size(24.dp).clickable {
                                            if (originalProduct != null) {
                                                viewModel.addToCart(userId, originalProduct, item.quantity + 1)
                                            }
                                        }
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}