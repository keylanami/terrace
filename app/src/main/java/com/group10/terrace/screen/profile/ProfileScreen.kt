package com.group10.terrace.screen.profile

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
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
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.group10.terrace.R
import com.group10.terrace.ui.components.ActivePlantCard
import com.group10.terrace.ui.components.BottomNavBar
import com.group10.terrace.ui.theme.*
import com.group10.terrace.viewmodel.HomeViewModel
import androidx.compose.runtime.collectAsState

@Composable
fun ProfileScreen(
    viewModel: HomeViewModel,
    onNavigate: (String) -> Unit,
    onSettingsClick: () -> Unit
) {
    val userData by viewModel.userData.collectAsState()
    val activePlants by viewModel.activePlants.collectAsState()
    val masterPlants by viewModel.masterPlants.collectAsState()

    Box(modifier = Modifier.fillMaxSize().background(Neutral100)) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            // ── Green Header ──────────────────────────────────────────────
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(brush = PrimaryGradient)
                    .padding(horizontal = 20.dp, vertical = 24.dp)
            ) {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.putih_full),
                                contentDescription = "Logo",
                                modifier = Modifier.size(24.dp)
                            )
                            Text(
                                text = "errace",
                                style = Typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                color = Neutral50
                            )
                        }
                        Icon(
                            painter = painterResource(id = R.drawable.settings),
                            contentDescription = "Settings",
                            tint = Neutral50,
                            modifier = Modifier
                                .size(24.dp)
                                .clickable { onSettingsClick() }
                        )
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    // User info row
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        // Rank badge + avatar
                        Box(contentAlignment = Alignment.BottomStart) {
                            AsyncImage(
                                model = userData?.profileImageUrl,
                                contentDescription = "Avatar",
                                contentScale = ContentScale.Crop,
                                modifier = Modifier
                                    .size(72.dp)
                                    .clip(CircleShape),
                                error = painterResource(id = R.drawable.fototanaman),
                                placeholder = painterResource(id = R.drawable.fototanaman)
                            )
                            // Rank circle
                            Box(
                                modifier = Modifier
                                    .size(24.dp)
                                    .background(Neutral900, CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "2", // TODO: hitung rank dari leaderboard
                                    style = Typography.labelMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 10.sp
                                    ),
                                    color = Neutral50
                                )
                            }
                        }

                        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Text(
                                text = userData?.name ?: "—",
                                style = Typography.titleLarge.copy(
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 22.sp
                                ),
                                color = Neutral50
                            )
                            Text(
                                text = userData?.experience.orEmpty().ifBlank { "Urban Farmer" },
                                style = Typography.bodyMedium,
                                color = Neutral50.copy(alpha = 0.8f)
                            )


                            Box(
                                modifier = Modifier
                                    .background(Yellow400, RoundedCornerShape(9999.dp))
                                    .padding(horizontal = 12.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    text = "${userData?.totalPoints ?: 0} Points",
                                    style = Typography.labelMedium.copy(
                                        fontWeight = FontWeight.SemiBold,
                                        fontSize = 11.sp
                                    ),
                                    color = Yellow900
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Location
                    if (!userData?.location.isNullOrBlank()) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.map),
                                contentDescription = null,
                                tint = Neutral50.copy(alpha = 0.8f),
                                modifier = Modifier.size(14.dp)
                            )
                            Text(
                                text = userData?.location ?: "",
                                style = Typography.bodyMedium.copy(fontSize = 12.sp),
                                color = Neutral50.copy(alpha = 0.8f)
                            )
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                    }

                    // Stats row: Rank | Plants | Active Days
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(24.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        StatItem(value = "#2", label = "Rank") // TODO: dari leaderboard
                        StatDivider()
                        StatItem(value = "${activePlants.size}", label = "Plants")
                        StatDivider()
                        StatItem(value = "${userData?.currentStreak ?: 0}", label = "Active Days")
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    // Badges row
                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        val badgeIcons = listOf(
                            R.drawable.badge_plant,
                            R.drawable.badge_timer,
                            R.drawable.badge_lightning,
                            R.drawable.badge_sun
                        ) // TODO: map dari userData.badges ke drawable
                        badgeIcons.forEach { iconRes ->
                            Box(
                                modifier = Modifier
                                    .size(48.dp)
                                    .background(Neutral50.copy(alpha = 0.15f), RoundedCornerShape(8.dp)),
                                contentAlignment = Alignment.Center
                            ) {
                                Image(
                                    painter = painterResource(id = iconRes),
                                    contentDescription = null,
                                    modifier = Modifier.size(28.dp)
                                )
                            }
                        }
                    }
                }
            }

            // ── Progress Card ─────────────────────────────────────────────
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
                    .offset(y = (-20).dp)
                    .shadow(10.dp, RoundedCornerShape(20.dp))
                    .background(Neutral50, RoundedCornerShape(20.dp))
                    .padding(horizontal = 20.dp, vertical = 16.dp)
            ) {
                Column {
                    // Drag handle
                    Box(
                        modifier = Modifier
                            .width(40.dp)
                            .height(4.dp)
                            .background(Neutral300, RoundedCornerShape(2.dp))
                            .align(Alignment.CenterHorizontally)
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "Progress",
                        style = Typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = Neutral900,
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    // Active plant list — max 2 di profile, sisanya di PlantProgressScreen
                    val plantsToShow = activePlants.take(2)
                    if (plantsToShow.isEmpty()) {
                        Text(
                            text = "Belum ada tanaman aktif.",
                            style = Typography.bodyMedium,
                            color = Neutral400,
                            modifier = Modifier.fillMaxWidth(),
                            textAlign = TextAlign.Center
                        )
                    } else {
                        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                            plantsToShow.forEachIndexed { index, userPlant ->
                                val master = masterPlants.find { it.id == userPlant.plantId }
                                ActivePlantCard(
                                    userPlant = userPlant,
                                    estimationDays = master?.estimationDays ?: 0,
                                    difficulty = master?.difficulty ?: "Easy",
                                    isPriority = index == 0,
                                    onClick = {}
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(80.dp))
        }

        // ── Bottom Nav ────────────────────────────────────────────────────
        BottomNavBar(
            currentRoute = "academy",
            onNavigate = onNavigate,
            modifier = Modifier.align(Alignment.BottomCenter)
        )
    }
}

@Composable
private fun StatItem(value: String, label: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = value,
            style = Typography.titleLarge.copy(fontWeight = FontWeight.Bold, fontSize = 20.sp),
            color = Neutral50
        )
        Text(
            text = label,
            style = Typography.bodyMedium.copy(fontSize = 12.sp),
            color = Neutral50.copy(alpha = 0.8f)
        )
    }
}

@Composable
private fun StatDivider() {
    Box(
        modifier = Modifier
            .width(1.dp)
            .height(32.dp)
            .background(Neutral50.copy(alpha = 0.4f))
    )
}
