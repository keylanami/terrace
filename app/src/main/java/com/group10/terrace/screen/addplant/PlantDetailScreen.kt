package com.group10.terrace.ui.screen.addplant

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.group10.terrace.R
import com.group10.terrace.model.Mission
import com.group10.terrace.model.TaskItem
import com.group10.terrace.ui.components.DifficultyBadge
import com.group10.terrace.ui.components.calculateDaysPassed
import com.group10.terrace.ui.theme.*
import com.group10.terrace.viewmodel.GamificationEvent
import com.group10.terrace.viewmodel.HomeViewModel
import com.group10.terrace.viewmodel.MissionViewModel

@Composable
fun PlantDetailScreen(
    viewModel: HomeViewModel,
    missionViewModel: MissionViewModel,  // FIX: tambah parameter
    userPlantId: String,
    onBack: () -> Unit
) {
    val activePlants by viewModel.activePlants.collectAsState()
    val masterPlants by viewModel.masterPlants.collectAsState()
    val userData by viewModel.userData.collectAsState()
    val todayMissions by missionViewModel.todayMissions.collectAsState()
    val gamificationEvent by missionViewModel.gamificationEvent.collectAsState()

    val userPlant = activePlants.find { it.userPlantId == userPlantId }
    val masterPlant = masterPlants.find { it.id == userPlant?.plantId }

    // Load missions saat masuk screen
    LaunchedEffect(userPlantId) {
        if (userPlant != null && masterPlant != null) {
            missionViewModel.loadTodayMissions(userPlant, masterPlant)
        }
    }

    // Snackbar feedback poin
    var snackbarMessage by remember { mutableStateOf<String?>(null) }
    LaunchedEffect(gamificationEvent) {
        when (val event = gamificationEvent) {
            is GamificationEvent.Success -> {
                snackbarMessage = "+${event.earnedPoints} Poin! Streak: ${event.newStreak} 🔥"
                viewModel.loadDashboardData()
                missionViewModel.clearEvent()
            }
            is GamificationEvent.Error -> {
                snackbarMessage = event.message
                missionViewModel.clearEvent()
            }
            null -> {}
        }
    }

    if (userPlant == null || masterPlant == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator(color = Green600)
        }
        return
    }

    val currentDay = calculateDaysPassed(userPlant.startDate).toInt()
    val maxDays = extractMaxDays(masterPlant.harvest_duration)

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Neutral50)
                .verticalScroll(rememberScrollState())
        ) {
            // ── Top Bar ───────────────────────────────────────────────────
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    Icons.AutoMirrored.Outlined.ArrowBack,
                    contentDescription = "Back",
                    modifier = Modifier.size(24.dp).clickable { onBack() }
                )
                Text(
                    "Tanaman Mu",
                    style = Typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.size(24.dp))
            }

            // ── Hero Image ────────────────────────────────────────────────
            AsyncImage(
                model = masterPlant.imageUrl.ifBlank { null },
                contentDescription = masterPlant.name,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxWidth().height(198.dp),
                error = painterResource(id = R.drawable.fototanaman),
                placeholder = painterResource(id = R.drawable.fototanaman)
            )

            Column(modifier = Modifier.padding(24.dp)) {
                // ── Category + Difficulty ─────────────────────────────────
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .background(Green700, RoundedCornerShape(10.dp))
                            .padding(horizontal = 10.dp, vertical = 4.dp)
                    ) {
                        Text(
                            masterPlant.category.ifEmpty { "Sayuran & Buah" },
                            style = Typography.labelMedium.copy(fontSize = 10.sp),
                            color = Neutral50
                        )
                    }
                    DifficultyBadge(difficulty = masterPlant.difficulty)
                }

                Spacer(modifier = Modifier.height(12.dp))

                // ── Name + Priority ───────────────────────────────────────
                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (activePlants.firstOrNull()?.userPlantId == userPlantId) {
                        Box(
                            modifier = Modifier
                                .background(Yellow500, RoundedCornerShape(10.dp))
                                .padding(horizontal = 10.dp, vertical = 4.dp)
                        ) {
                            Text("Priority", style = Typography.labelMedium.copy(fontSize = 10.sp), color = Neutral50)
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                    }
                    Text(
                        userPlant.plantName,
                        style = Typography.titleLarge.copy(fontWeight = FontWeight.Bold, fontSize = 20.sp),
                        color = Neutral900,
                        modifier = Modifier.weight(1f)
                    )
                    Icon(
                        painter = painterResource(id = android.R.drawable.ic_menu_edit),
                        contentDescription = "Edit",
                        modifier = Modifier.size(20.dp),
                        tint = Neutral400
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                // ── Day Progress Row ──────────────────────────────────────
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(maxDays) { index ->
                        val dayNum = index + 1
                        val isActive = dayNum <= currentDay
                        Box(
                            modifier = Modifier
                                .background(
                                    if (isActive) Green600 else Green100,
                                    RoundedCornerShape(20.dp)
                                )
                                .padding(horizontal = 12.dp, vertical = 8.dp)
                        ) {
                            Text(
                                "Hari $dayNum",
                                style = Typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                color = if (isActive) Neutral50 else Green700
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))

                // ── Daily Tasks Card ──────────────────────────────────────
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .shadow(
                            elevation = 15.dp,
                            shape = RoundedCornerShape(20.dp),
                            spotColor = Neutral900.copy(alpha = 0.1f)
                        )
                        .background(Neutral50, RoundedCornerShape(20.dp))
                        .padding(24.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            "Daily Tasks",
                            style = Typography.titleLarge.copy(fontWeight = FontWeight.Bold, fontSize = 20.sp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Icon(painter = painterResource(R.drawable.pin), contentDescription = "Pin")
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    if (todayMissions.isEmpty()) {
                        Text(
                            "Tidak ada tugas untuk hari ini. Bersantailah!",
                            style = Typography.bodyMedium,
                            color = Neutral400
                        )
                    } else {
                        // FIX: pakai Mission dari MissionViewModel, bukan TaskItem langsung
                        todayMissions.forEach { mission ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 8.dp)
                                    // FIX: panggil onTaskChecked saat centang
                                    .clickable(enabled = !mission.isCompleted) {
                                        val userId = userData?.uid ?: return@clickable
                                        missionViewModel.onTaskChecked(userId, userPlantId, mission)
                                    },
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    painter = painterResource(id = R.drawable.checklist),
                                    contentDescription = null,
                                    tint = if (mission.isCompleted) Green600 else Neutral400,
                                    modifier = Modifier.size(24.dp)
                                )
                                Spacer(modifier = Modifier.width(12.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        mission.name,
                                        style = Typography.bodyMedium.copy(fontWeight = FontWeight.Medium),
                                        color = if (mission.isCompleted) Neutral400 else Neutral900
                                    )
                                    Text(
                                        "+${mission.points} poin",
                                        style = Typography.labelMedium.copy(fontSize = 10.sp),
                                        color = if (mission.isCompleted) Neutral400 else Green500
                                    )
                                }
                                if (mission.isMilestone) {
                                    Box(
                                        modifier = Modifier
                                            .background(Yellow200, RoundedCornerShape(4.dp))
                                            .padding(horizontal = 6.dp, vertical = 2.dp)
                                    ) {
                                        Text(
                                            "Milestone",
                                            style = Typography.labelMedium.copy(fontSize = 9.sp),
                                            color = Yellow800
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(40.dp))
            }
        }

        // ── Snackbar poin feedback ────────────────────────────────────────
        snackbarMessage?.let { msg ->
            Box(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(24.dp)
                    .background(Green700, RoundedCornerShape(12.dp))
                    .padding(horizontal = 20.dp, vertical = 12.dp)
            ) {
                Text(msg, style = Typography.bodyMedium, color = Neutral50)
            }
            LaunchedEffect(msg) {
                kotlinx.coroutines.delay(3000)
                snackbarMessage = null
            }
        }
    }
}

fun extractMaxDays(harvestDuration: String): Int {
    return try {
        val numbers = harvestDuration.replace("+", "").split("-", " ")
            .mapNotNull { it.trim().toIntOrNull() }
        numbers.maxOrNull() ?: 30
    } catch (e: Exception) { 30 }
}