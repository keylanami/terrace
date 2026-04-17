package com.group10.terrace.screen.market

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
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
import com.group10.terrace.model.UserAddress
import com.group10.terrace.ui.components.BottomNavBar
import com.group10.terrace.ui.theme.*
import com.group10.terrace.viewmodel.CheckoutState
import com.group10.terrace.viewmodel.MarketplaceViewModel
import com.group10.terrace.viewmodel.PaymentMethod
import java.text.NumberFormat
import java.util.Locale

@Composable
fun CheckoutScreen(
    viewModel: MarketplaceViewModel,
    userId: String,
    onBack: () -> Unit,
    onNavigate: (String) -> Unit,
    onPilihAlamat: () -> Unit,
    onCheckoutSuccess: () -> Unit
) {
    val cartItems by viewModel.cartItems.collectAsState()
    val selectedAddress by viewModel.selectedAddress.collectAsState()
    val usePoints by viewModel.usePoints.collectAsState()
    val userPoints by viewModel.userCurrentPoints.collectAsState()
    val paymentMethod by viewModel.paymentMethod.collectAsState()
    val checkoutState by viewModel.checkoutState.collectAsState()

    val fmt = NumberFormat.getNumberInstance(Locale("id", "ID"))

    val subtotal = viewModel.subtotal
    val ongkir = MarketplaceViewModel.ONGKOS_KIRIM
    val potongan = viewModel.potonganPoin(userPoints)
    val total = viewModel.totalPembayaran(userPoints)


    LaunchedEffect(checkoutState) {
        if (checkoutState is CheckoutState.Success) {
            onCheckoutSuccess()
            viewModel.resetCheckoutState()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Neutral50)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {

            // ── Top Bar ───────────────────────────────────────────────────
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
                    text = "Checkout",
                    style = Typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = Neutral900,
                    modifier = Modifier.align(Alignment.Center)
                )
            }

            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(bottom = 16.dp)
            ) {

                // ── Pilih Alamat row ──────────────────────────────────────
                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Neutral50, RoundedCornerShape(12.dp))
                            .border(1.dp, Neutral200, RoundedCornerShape(12.dp))
                            .clickable { onPilihAlamat() }
                            .padding(horizontal = 16.dp, vertical = 14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.map),
                                contentDescription = null,
                                tint = Green600,
                                modifier = Modifier.size(20.dp)
                            )
                            if (selectedAddress != null) {
                                Column {
                                    Text(
                                        text = "${selectedAddress!!.name} (${selectedAddress!!.phone})",
                                        style = Typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                                        color = Neutral900
                                    )
                                    Text(
                                        text = selectedAddress!!.address,
                                        style = Typography.bodyMedium.copy(fontSize = 12.sp),
                                        color = Neutral600,
                                        maxLines = 1
                                    )
                                }
                            } else {
                                Text(
                                    text = "Pilih Alamat",
                                    style = Typography.bodyMedium,
                                    color = Neutral600
                                )
                            }
                        }
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                            contentDescription = null,
                            tint = Neutral400,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }

                // ── Cart Items ────────────────────────────────────────────
                items(cartItems) { cartItem ->
                    CheckoutCartItemRow(
                        cartItem = cartItem,
                        userId = userId,
                        onQuantityChange = { newQty ->
                            viewModel.updateQuantity(userId, cartItem, newQty)
                        }
                    )
                }

                // ── Gunakan Poin ──────────────────────────────────────────
                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                color = if (usePoints) Green100 else Neutral50,
                                shape = RoundedCornerShape(12.dp)
                            )
                            .border(
                                width = 1.dp,
                                color = if (usePoints) Green400 else Neutral200,
                                shape = RoundedCornerShape(12.dp)
                            )
                            .padding(horizontal = 16.dp, vertical = 14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.clover),
                                contentDescription = null,
                                tint = Green600,
                                modifier = Modifier.size(20.dp)
                            )
                            Column {
                                Text(
                                    text = "Gunakan Point",
                                    style = Typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                                    color = Neutral900
                                )
                                Text(
                                    text = "Tersedia: ${fmt.format(userPoints)} Point",
                                    style = Typography.labelMedium.copy(fontSize = 11.sp),
                                    color = Neutral600
                                )
                            }
                        }
                        Switch(
                            checked = usePoints,
                            onCheckedChange = { viewModel.toggleUsePoints() },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = Neutral50,
                                checkedTrackColor = Green500,
                                uncheckedThumbColor = Neutral300,
                                uncheckedTrackColor = Neutral200
                            )
                        )
                    }
                }

                // ── Price Summary ─────────────────────────────────────────
                item {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        PriceLine(label = "Subtotal", value = "Rp ${fmt.format(subtotal)}")
                        PriceLine(label = "Ongkos Kirim", value = "Rp ${fmt.format(ongkir)}")
                        if (usePoints && potongan > 0) {
                            PriceLine(
                                label = "Potongan Poin",
                                value = "-Rp ${fmt.format(potongan)}",
                                valueColor = Green600
                            )
                        }
                        HorizontalDivider(color = Neutral200, thickness = 1.dp)
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "TOTAL PEMBAYARAN",
                                style = Typography.labelMedium.copy(
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 11.sp
                                ),
                                color = Neutral600
                            )
                        }
                        Text(
                            text = "Rp ${fmt.format(total)}",
                            style = Typography.titleLarge.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 24.sp
                            ),
                            color = Neutral900
                        )
                    }
                }

                // ── Metode Pembayaran ─────────────────────────────────────
                item {
                    Text(
                        text = "Metode Pembayaran",
                        style = Typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = Neutral900
                    )
                }

                item {
                    PaymentMethodRow(
                        iconRes = R.drawable.qrcode,
                        label = "QRIS",
                        selected = paymentMethod == PaymentMethod.QRIS,
                        onClick = { viewModel.selectPaymentMethod(PaymentMethod.QRIS) }
                    )
                }

                item {
                    PaymentMethodRow(
                        iconRes = R.drawable.bankva,
                        label = "Bank Virtual Account",
                        selected = paymentMethod == PaymentMethod.BANK_VIRTUAL_ACCOUNT,
                        onClick = { viewModel.selectPaymentMethod(PaymentMethod.BANK_VIRTUAL_ACCOUNT) }
                    )
                }

                item { Spacer(modifier = Modifier.height(16.dp)) }
            }

            // ── Bayar Sekarang Button ─────────────────────────────────────
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp)
                    .background(Green700, RoundedCornerShape(12.dp))
                    .clickable(enabled = checkoutState !is CheckoutState.Loading) {
                        viewModel.processCheckout(userId)
                    }
                    .padding(vertical = 18.dp),
                contentAlignment = Alignment.Center
            ) {
                if (checkoutState is CheckoutState.Loading) {
                    CircularProgressIndicator(
                        color = Neutral50,
                        strokeWidth = 2.dp,
                        modifier = Modifier.size(22.dp)
                    )
                } else {
                    Text(
                        text = "Bayar Sekarang",
                        style = Typography.bodyLarge.copy(fontWeight = FontWeight.Bold),
                        color = Neutral50
                    )
                }
            }
        }

        // ── Error snackbar ────────────────────────────────────────────────
        if (checkoutState is CheckoutState.Error) {
            Box(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(16.dp)
                    .background(Red600, RoundedCornerShape(8.dp))
                    .padding(horizontal = 16.dp, vertical = 10.dp)
            ) {
                Text(
                    text = (checkoutState as CheckoutState.Error).message,
                    style = Typography.bodyMedium,
                    color = Neutral50
                )
            }
        }

        BottomNavBar(
            currentRoute = "market",
            onNavigate = onNavigate,
            modifier = Modifier.align(Alignment.BottomCenter)
        )
    }
}

// ── Helper composables ────────────────────────────────────────────────────

@Composable
private fun CheckoutCartItemRow(
    cartItem: CartItem,
    userId: String,
    onQuantityChange: (Int) -> Unit
) {
    val fmt = NumberFormat.getNumberInstance(Locale("id", "ID"))

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
            model = cartItem.imageUrl,
            contentDescription = cartItem.productName,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .size(64.dp)
                .clip(RoundedCornerShape(8.dp)),
            error = painterResource(id = R.drawable.fototanaman),
            placeholder = painterResource(id = R.drawable.fototanaman)
        )

        Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(
                text = cartItem.productName,
                style = Typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                color = Neutral900
            )
            Text(
                text = "Per 1pcs",
                style = Typography.labelMedium.copy(fontSize = 11.sp),
                color = Neutral600
            )
            Text(
                text = "Rp ${fmt.format(cartItem.price)}",
                style = Typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                color = Green600
            )
        }

        // Quantity stepper
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(24.dp)
                    .background(Neutral200, RoundedCornerShape(4.dp))
                    .clickable { onQuantityChange(cartItem.quantity - 1) },
                contentAlignment = Alignment.Center
            ) {
                Text("−", style = Typography.bodyLarge, color = Neutral900)
            }
            Text(
                text = "${cartItem.quantity}",
                style = Typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                color = Neutral900,
                modifier = Modifier.widthIn(min = 20.dp),
                textAlign = TextAlign.Center
            )
            Box(
                modifier = Modifier
                    .size(24.dp)
                    .background(Neutral200, RoundedCornerShape(4.dp))
                    .clickable { onQuantityChange(cartItem.quantity + 1) },
                contentAlignment = Alignment.Center
            ) {
                Text("+", style = Typography.bodyLarge, color = Neutral900)
            }
        }
    }
}

@Composable
private fun PriceLine(
    label: String,
    value: String,
    valueColor: androidx.compose.ui.graphics.Color = Neutral900
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label, style = Typography.bodyMedium, color = Neutral600)
        Text(text = value, style = Typography.bodyMedium.copy(fontWeight = FontWeight.Medium), color = valueColor)
    }
}

@Composable
private fun PaymentMethodRow(
    iconRes: Int,
    label: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Neutral50, RoundedCornerShape(12.dp))
            .border(
                width = if (selected) 2.dp else 1.dp,
                color = if (selected) Green500 else Neutral200,
                shape = RoundedCornerShape(12.dp)
            )
            .clickable { onClick() }
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Icon(
                painter = painterResource(id = iconRes),
                contentDescription = label,
                tint = Color.Unspecified,
                modifier = Modifier.size(24.dp)
            )
            Text(
                text = label,
                style = Typography.bodyMedium.copy(fontWeight = FontWeight.Medium),
                color = Neutral900
            )
        }
        RadioButton(
            selected = selected,
            onClick = null,
            colors = RadioButtonDefaults.colors(
                selectedColor = Green500,
                unselectedColor = Neutral300
            )
        )
    }
}