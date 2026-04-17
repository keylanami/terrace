package com.group10.terrace.screen.profile

import androidx.compose.foundation.background
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.group10.terrace.R
import com.group10.terrace.model.CartItem
import com.group10.terrace.ui.theme.*
import com.group10.terrace.viewmodel.HomeViewModel
import com.group10.terrace.viewmodel.MarketplaceViewModel
import androidx.compose.runtime.collectAsState

@Composable
fun AlamatPenerimaScreen(
    viewModel: MarketplaceViewModel,
    homeViewModel: HomeViewModel,
    userId: String,
    onBack: () -> Unit
) {
    LaunchedEffect(userId) {
        viewModel.loadOrderHistory(userId)
    }

    val orders by viewModel.orderHistory.collectAsState()
    val userData by homeViewModel.userData.collectAsState()

    // Flatten semua items dari semua order
    val allItems: List<CartItem> = orders.flatMap { it.items }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Neutral50)
    ) {
        // ── Top Bar ───────────────────────────────────────────────────────
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
                text = "Alamat Penerima",
                style = Typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = Neutral900,
                modifier = Modifier.align(Alignment.Center)
            )
        }

        HorizontalDivider(color = Neutral200)

        if (allItems.isEmpty()) {
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
                verticalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(vertical = 16.dp)
            ) {
                items(allItems) { cartItem ->
                    AlamatItemRow(
                        cartItem = cartItem,
                        // Alamat dari user profile — userData.address + userData.location
                        // Format: "Kota Asal – Kota Tujuan"
                        // Asal = seller, tujuan = userData.location (kota user)
                        address = buildString {
                            val origin = "Terrace Official"
                            val dest = userData?.location?.split(",")?.firstOrNull()?.trim() ?: userData?.location ?: "—"
                            append("$origin – $dest")
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun AlamatItemRow(
    cartItem: CartItem,
    address: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Neutral100, RoundedCornerShape(12.dp))
            .padding(12.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Product image
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
                color = Neutral900
            )
            Text(
                text = "Per ${cartItem.quantity}pcs",
                style = Typography.bodyMedium.copy(fontSize = 12.sp),
                color = Neutral600
            )
            Text(
                text = address,
                style = Typography.bodyMedium.copy(
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 14.sp
                ),
                color = Green600
            )
        }
    }
}