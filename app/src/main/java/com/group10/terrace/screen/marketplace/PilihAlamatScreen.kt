package com.group10.terrace.screen.marketplace

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.group10.terrace.R
import com.group10.terrace.model.UserAddress
import com.group10.terrace.ui.components.BottomNavBar
import com.group10.terrace.ui.theme.*
import com.group10.terrace.viewmodel.MarketplaceViewModel

@Composable
fun PilihAlamatScreen(
    viewModel: MarketplaceViewModel,
    userId: String,
    onBack: () -> Unit,
    onNavigate: (String) -> Unit
) {
    val addresses by viewModel.userAddresses.collectAsState()
    val selectedAddress by viewModel.selectedAddress.collectAsState()

    var showAddForm by remember { mutableStateOf(false) }

    LaunchedEffect(userId) {
        viewModel.loadUserAddresses(userId)
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
                    text = "Pilih Alamat",
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
                verticalArrangement = Arrangement.spacedBy(10.dp),
                contentPadding = PaddingValues(bottom = 80.dp)
            ) {

                // ── Tambah Alamat button ──────────────────────────────────
                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Neutral100, RoundedCornerShape(12.dp))
                            .border(1.dp, Neutral200, RoundedCornerShape(12.dp))
                            .clickable { showAddForm = true }
                            .padding(horizontal = 16.dp, vertical = 14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.map),
                                contentDescription = null,
                                tint = Green600,
                                modifier = Modifier.size(20.dp)
                            )
                            Text(
                                text = "Tambah Alamat",
                                style = Typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                                color = Neutral900
                            )
                        }
                        Icon(
                            painter = painterResource(id = R.drawable.plus),
                            contentDescription = "Tambah",
                            tint = Neutral600,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }

                // ── Daftar Alamat ─────────────────────────────────────────
                items(addresses) { address ->
                    val isSelected = selectedAddress?.addressId == address.addressId

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Neutral50, RoundedCornerShape(12.dp))
                            .border(
                                width = if (isSelected) 2.dp else 1.dp,
                                color = if (isSelected) Green500 else Neutral200,
                                shape = RoundedCornerShape(12.dp)
                            )
                            .clickable {
                                viewModel.selectAddress(address)
                                onBack() // kembali ke checkout setelah pilih
                            }
                            .padding(horizontal = 16.dp, vertical = 14.dp),
                        verticalAlignment = Alignment.Top,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.map),
                            contentDescription = null,
                            tint = if (isSelected) Green500 else Neutral400,
                            modifier = Modifier
                                .size(20.dp)
                                .padding(top = 2.dp)
                        )

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "${address.name} (${address.phone})",
                                style = Typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                                color = Neutral900
                            )
                            Text(
                                text = address.address,
                                style = Typography.bodyMedium.copy(fontSize = 12.sp),
                                color = Neutral600
                            )
                        }

                        Text(
                            text = "Edit",
                            style = Typography.labelMedium.copy(fontWeight = FontWeight.SemiBold),
                            color = Green600,
                            modifier = Modifier.clickable {
                                // TODO: navigate ke edit alamat screen
                            }
                        )
                    }
                }
            }
        }

        BottomNavBar(
            currentRoute = "market",
            onNavigate = onNavigate,
            modifier = Modifier.align(Alignment.BottomCenter)
        )

        // ── Tambah Alamat Dialog ──────────────────────────────────────────
        if (showAddForm) {
            TambahAlamatDialog(
                onDismiss = { showAddForm = false },
                onSubmit = { newAddress ->
                    viewModel.addAddress(userId, newAddress) { success ->
                        if (success) showAddForm = false
                    }
                }
            )
        }
    }
}

// ── Tambah Alamat Dialog ──────────────────────────────────────────────────

@Composable
fun TambahAlamatDialog(
    onDismiss: () -> Unit,
    onSubmit: (UserAddress) -> Unit
) {
    var nama by remember { mutableStateOf("") }
    var noTelp by remember { mutableStateOf("") }
    var alamat by remember { mutableStateOf("") }
    var kodePos by remember { mutableStateOf("") }

    val isFormValid = nama.isNotBlank() && noTelp.isNotBlank() && alamat.isNotBlank() && kodePos.isNotBlank()

    Dialog(onDismissRequest = onDismiss) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(Neutral50, RoundedCornerShape(16.dp))
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "Tambah Alamat",
                style = Typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = Neutral900
            )

            AlamatTextField(
                value = nama,
                onValueChange = { nama = it },
                label = "Nama Penerima",
                placeholder = "Nama Lengkap"
            )

            AlamatTextField(
                value = noTelp,
                onValueChange = { noTelp = it },
                label = "No. Telp",
                placeholder = "080000000000",
                keyboardType = KeyboardType.Phone
            )

            AlamatTextField(
                value = alamat,
                onValueChange = { alamat = it },
                label = "Alamat",
                placeholder = "Jl. blablablablablablabla"
            )

            AlamatTextField(
                value = kodePos,
                onValueChange = { kodePos = it },
                label = "Kode Pos",
                placeholder = "11111",
                keyboardType = KeyboardType.Number
            )

            Spacer(modifier = Modifier.height(4.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        color = if (isFormValid) Green700 else Neutral300,
                        shape = RoundedCornerShape(8.dp)
                    )
                    .clickable(enabled = isFormValid) {
                        onSubmit(
                            UserAddress(
                                name = nama.trim(),
                                phone = noTelp.trim(),
                                address = alamat.trim(),
                                postalCode = kodePos.trim()
                            )
                        )
                    }
                    .padding(vertical = 14.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Tambah",
                    style = Typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                    color = Neutral50
                )
            }
        }
    }
}

@Composable
private fun AlamatTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    placeholder: String,
    keyboardType: KeyboardType = KeyboardType.Text
) {
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Text(
            text = label,
            style = Typography.labelMedium.copy(fontWeight = FontWeight.Medium, fontSize = 12.sp),
            color = Neutral800
        )
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            placeholder = {
                Text(
                    text = placeholder,
                    style = Typography.bodyMedium.copy(fontSize = 13.sp),
                    color = Neutral400
                )
            },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(8.dp),
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Green500,
                unfocusedBorderColor = Neutral200,
                focusedContainerColor = Neutral50,
                unfocusedContainerColor = Neutral100,
                cursorColor = Green500
            ),
            textStyle = Typography.bodyMedium.copy(fontSize = 13.sp, color = Neutral900)
        )
    }
}