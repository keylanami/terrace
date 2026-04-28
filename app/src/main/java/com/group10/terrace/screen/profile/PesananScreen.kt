package com.group10.terrace.screen.profile

import androidx.compose.foundation.background
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
import coil.compose.AsyncImage
import com.group10.terrace.R
import com.group10.terrace.model.CartItem
import com.group10.terrace.model.Order
import com.group10.terrace.ui.theme.*
import com.group10.terrace.viewmodel.MarketplaceViewModel
import java.text.NumberFormat
import java.util.Locale

@Composable
fun PesananScreen(
    viewModel: MarketplaceViewModel,
    userId: String,
    onBack: () -> Unit
) {
    LaunchedEffect(userId) {
        viewModel.loadOrderHistory(userId)
    }

    val orders by viewModel.orderHistory.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Neutral50)
            .windowInsetsPadding(WindowInsets.systemBars.only(WindowInsetsSides.Top))
    ) {
        // ── Top Bar ───────────────────────────────────────────────────────
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 16.dp)
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Outlined.ArrowBack,
                contentDescription = "Back",
                modifier = Modifier
                    .align(Alignment.CenterStart)
                    .size(24.dp)
                    .clickable { onBack() },
                tint = Neutral900
            )
            Text(
                text = "Pesanan",
                style = Typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = Neutral900,
                modifier = Modifier.align(Alignment.Center)
            )
        }

        HorizontalDivider(color = Neutral200)

        if (orders.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(
                    text = "Belum ada pesanan.",
                    style = Typography.bodyMedium,
                    color = Neutral400,
                    textAlign = TextAlign.Center
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                contentPadding = PaddingValues(vertical = 16.dp, horizontal = 16.dp)
            ) {
                items(orders) { order ->
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Neutral100, RoundedCornerShape(12.dp))
                            .padding(12.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {

                        order.items.forEach { cartItem ->
                            OrderItemRow(cartItem = cartItem, status = order.status)
                        }

                        HorizontalDivider(color = Neutral200)

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            val formatRupiah = NumberFormat.getNumberInstance(Locale("id", "ID"))
                            Column {
                                Text("Total Pesanan", style = Typography.labelMedium, color = Neutral600)
                                Text("Rp ${formatRupiah.format(order.totalAmount)}", style = Typography.bodyMedium.copy(fontWeight = FontWeight.Bold), color = Green700)
                            }

                            if (order.status.equals("Dikirim", ignoreCase = true)) {
                                Button(
                                    onClick = { viewModel.markOrderAsCompleted(userId, order.orderId) },
                                    colors = ButtonDefaults.buttonColors(containerColor = Green600),
                                    shape = RoundedCornerShape(8.dp),
                                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                                    modifier = Modifier.height(36.dp)
                                ) {
                                    Text("Selesaikan Pesanan", style = Typography.labelMedium.copy(fontWeight = FontWeight.Bold), color = Neutral50)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun OrderItemRow(cartItem: CartItem, status: String) {
    val formatRupiah = NumberFormat.getNumberInstance(Locale("id", "ID"))

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {

        AsyncImage(
            model = cartItem.imageUrl,
            contentDescription = cartItem.productName,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .size(72.dp)
                .clip(RoundedCornerShape(8.dp)),
            error = painterResource(id = R.drawable.fototanaman),
            placeholder = painterResource(id = R.drawable.fototanaman)
        )

        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = cartItem.productName,
                style = Typography.bodyLarge.copy(fontWeight = FontWeight.Bold, fontSize = 16.sp),
                color = Neutral900,
                maxLines = 1
            )
            Text(
                text = "Per ${cartItem.quantity}pcs",
                style = Typography.bodyMedium.copy(fontSize = 12.sp),
                color = Neutral600
            )
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "Rp ${formatRupiah.format(cartItem.price * cartItem.quantity)}",
                    style = Typography.bodyMedium.copy(
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 14.sp
                    ),
                    color = Green600
                )

                Box(
                    modifier = Modifier
                        .background(
                            color = when (status.lowercase()) {
                                "dikemas" -> Color(0xFF2196F3)
                                "dikirim" -> Yellow400
                                "selesai" -> Green500
                                else -> Neutral300
                            }.let { it },
                            shape = RoundedCornerShape(9999.dp)
                        )
                        .padding(horizontal = 8.dp, vertical = 3.dp)
                ) {
                    Text(
                        text = status,
                        style = Typography.labelMedium.copy(
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 10.sp
                        ),
                        color = Neutral50
                    )
                }
            }
        }
    }
}