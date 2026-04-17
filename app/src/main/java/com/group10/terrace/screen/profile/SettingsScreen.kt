package com.group10.terrace.screen.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import coil.compose.AsyncImage
import com.group10.terrace.R
import com.group10.terrace.ui.theme.*
import com.group10.terrace.viewmodel.HomeViewModel
import androidx.compose.runtime.collectAsState

@Composable
fun SettingsScreen(
    viewModel: HomeViewModel,
    onBack: () -> Unit,
    onEditProfile: () -> Unit,
    onPesananSaya: () -> Unit,
    onAlamatPenerima: () -> Unit,
    onLogoutConfirmed: () -> Unit
) {
    val userData by viewModel.userData.collectAsState()

    var showLogoutDialog by remember { mutableStateOf(false) }
    var isLoggingOut by remember { mutableStateOf(false) }
    var notifEnabled by remember { mutableStateOf(true) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Neutral50)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            // ── Top Bar ───────────────────────────────────────────────────
            Box(modifier = Modifier.fillMaxWidth()) {
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
                    text = "Settings",
                    style = Typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = Neutral900,
                    modifier = Modifier.align(Alignment.Center)
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            // ── Avatar + edit button ──────────────────────────────────────
            Box(contentAlignment = Alignment.BottomEnd) {
                AsyncImage(
                    model = userData?.profileImageUrl,
                    contentDescription = "Avatar",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(100.dp)
                        .clip(CircleShape),
                    error = painterResource(id = R.drawable.fototanaman),
                    placeholder = painterResource(id = R.drawable.fototanaman)
                )
                Box(
                    modifier = Modifier
                        .size(28.dp)
                        .background(Green500, CircleShape)
                        .clickable { onEditProfile() },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.plus),
                        contentDescription = "Edit photo",
                        tint = Neutral50,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // ── Menu Buttons ──────────────────────────────────────────────
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                SettingsMenuButton(
                    iconRes = R.drawable.ic_person,
                    label = "Edit Profile",
                    onClick = onEditProfile
                )
                SettingsMenuButton(
                    iconRes = R.drawable.order,
                    label = "Pesana Saya",
                    onClick = onPesananSaya
                )
                SettingsMenuButton(
                    iconRes = R.drawable.map,
                    label = "Alamat Penerima",
                    onClick = onAlamatPenerima
                )

                // Notifications toggle
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Green700, RoundedCornerShape(12.dp))
                        .padding(horizontal = 20.dp, vertical = 16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.notif),
                            contentDescription = null,
                            tint = Neutral50,
                            modifier = Modifier.size(20.dp)
                        )
                        Text(
                            text = "Notifications",
                            style = Typography.bodyLarge.copy(fontWeight = FontWeight.Medium),
                            color = Neutral50
                        )
                    }
                    Switch(
                        checked = notifEnabled,
                        onCheckedChange = { notifEnabled = it },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Neutral50,
                            checkedTrackColor = Green400,
                            uncheckedThumbColor = Neutral300,
                            uncheckedTrackColor = Green900
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            // ── Log Out Button ────────────────────────────────────────────
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Red600, RoundedCornerShape(12.dp))
                    .clickable { showLogoutDialog = true }
                    .padding(vertical = 18.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Log Out",
                    style = Typography.bodyLarge.copy(fontWeight = FontWeight.Bold),
                    color = Neutral50
                )
            }

            Spacer(modifier = Modifier.height(32.dp))
        }

        // ── Logout Confirmation Dialog ────────────────────────────────────
        if (showLogoutDialog) {
            Dialog(onDismissRequest = { showLogoutDialog = false }) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Neutral50, RoundedCornerShape(20.dp))
                        .padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.logout),
                            contentDescription = null,
                            tint = Green600,
                            modifier = Modifier.size(40.dp)
                        )
                        Text(
                            text = "Yakin Ingin Log Out",
                            style = Typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
                            color = Neutral900,
                            textAlign = TextAlign.Center
                        )
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            // Ya
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .background(Neutral50, RoundedCornerShape(8.dp))
                                    .shadow(2.dp, RoundedCornerShape(8.dp))
                                    .clickable {
                                        showLogoutDialog = false
                                        isLoggingOut = true
                                        onLogoutConfirmed()
                                    }
                                    .padding(vertical = 12.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "Ya",
                                    style = Typography.bodyLarge.copy(fontWeight = FontWeight.Medium),
                                    color = Neutral900
                                )
                            }
                            // Tidak
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .background(Neutral50, RoundedCornerShape(8.dp))
                                    .shadow(2.dp, RoundedCornerShape(8.dp))
                                    .clickable { showLogoutDialog = false }
                                    .padding(vertical = 12.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "Tidak",
                                    style = Typography.bodyLarge.copy(fontWeight = FontWeight.Medium),
                                    color = Neutral900
                                )
                            }
                        }
                    }
                }
            }
        }

        // ── Loading Overlay saat logout ───────────────────────────────────
        if (isLoggingOut) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 40.dp)
                    .align(Alignment.Center)
                    .background(Neutral50, RoundedCornerShape(20.dp))
                    .padding(40.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    CircularProgressIndicator(
                        color = Green500,
                        strokeWidth = 4.dp,
                        modifier = Modifier.size(64.dp)
                    )
                    Text(
                        text = "Loading",
                        style = Typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = Green600
                    )
                }
            }
        }
    }
}

@Composable
private fun SettingsMenuButton(
    iconRes: Int,
    label: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Green700, RoundedCornerShape(12.dp))
            .clickable { onClick() }
            .padding(horizontal = 20.dp, vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Icon(
            painter = painterResource(id = iconRes),
            contentDescription = label,
            tint = Neutral50,
            modifier = Modifier.size(20.dp)
        )
        Text(
            text = label,
            style = Typography.bodyLarge.copy(fontWeight = FontWeight.Medium),
            color = Neutral50
        )
    }
}
