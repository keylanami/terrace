package com.group10.terrace.ui.screen.addplant

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.group10.terrace.R
import com.group10.terrace.model.Mission
import com.group10.terrace.ui.components.DifficultyBadge
import com.group10.terrace.ui.components.calculateDaysPassed
import com.group10.terrace.ui.theme.*
import com.group10.terrace.viewmodel.GamificationEvent
import com.group10.terrace.viewmodel.HomeViewModel
import com.group10.terrace.viewmodel.MissionViewModel

fun extractMaxDays(harvestDuration: String): Int {
    return try {
        val numbers = harvestDuration.replace("+", "").split("-", " ").mapNotNull { it.trim().toIntOrNull() }
        (numbers.maxOrNull() ?: 30).coerceAtMost(30)
    } catch (e: Exception) { 30 }
}

@Composable
fun PlantDetailScreen(
    viewModel: HomeViewModel,
    missionViewModel: MissionViewModel,
    plantId: String,
    onBack: () -> Unit,
    onAddPlant: () -> Unit
) {
    val activePlants by viewModel.activePlants.collectAsState()
    val masterPlants by viewModel.masterPlants.collectAsState()
    val userData by viewModel.userData.collectAsState()
    val todayMissions by missionViewModel.todayMissions.collectAsState()
    val gamificationEvent by missionViewModel.gamificationEvent.collectAsState()

    val masterPlant = masterPlants.find { it.id == plantId }
    val userPlant = activePlants.find { it.plantId == plantId }
    val isActive = userPlant != null

    if (masterPlant == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { CircularProgressIndicator(color = Green600) }
        return
    }

    val realCurrentDay = if (isActive) calculateDaysPassed(userPlant!!.startDate).toInt() else 0
    val maxDays = extractMaxDays(masterPlant.harvest_duration)

    var selectedPreviewDay by remember { mutableIntStateOf(if (isActive) realCurrentDay.coerceAtMost(maxDays) else 1) }

    val previewTasks = remember(selectedPreviewDay) {
        val recurring = masterPlant.tasks_logic?.recurringTask
            ?.filter { selectedPreviewDay % it.frequency_days == 0 }
            ?.map { Mission(name = it.task_name, points = it.points, isMilestone = false) } ?: emptyList()

        val milestones = masterPlant.tasks_logic?.milestoneTask
            ?.filter { it.day == selectedPreviewDay }
            ?.map { Mission(name = it.task_name, points = it.points, isMilestone = true) } ?: emptyList()

        recurring + milestones
    }

    LaunchedEffect(plantId, isActive, realCurrentDay) {
        if (isActive) missionViewModel.loadTodayMissions(userPlant!!, masterPlant)
    }

    val finalProgress = userPlant?.progress ?: 0
    val progressFraction = (finalProgress / 100f).coerceIn(0f, 1f)

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

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier.fillMaxSize().background(Neutral50).verticalScroll(rememberScrollState())
        ) {
            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.AutoMirrored.Outlined.ArrowBack, contentDescription = "Back", modifier = Modifier.size(24.dp).clickable { onBack() })
                Text("Tanaman Mu", style = Typography.titleLarge.copy(fontWeight = FontWeight.Bold), modifier = Modifier.weight(1f), textAlign = TextAlign.Center)
                Spacer(modifier = Modifier.size(24.dp))
            }

            AsyncImage(
                model = masterPlant.imageUrl.ifBlank { null },
                contentDescription = masterPlant.name,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxWidth().height(198.dp),
                error = painterResource(id = R.drawable.fototanaman),
                placeholder = painterResource(id = R.drawable.fototanaman)
            )

            Column(modifier = Modifier.padding(24.dp)) {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                    Box(modifier = Modifier.background(Green700, RoundedCornerShape(10.dp)).padding(horizontal = 10.dp, vertical = 4.dp)) {
                        Text(masterPlant.category.ifEmpty { "Sayuran & Buah" }, style = Typography.labelMedium.copy(fontSize = 10.sp), color = Neutral50)
                    }
                    DifficultyBadge(difficulty = masterPlant.difficulty)
                }

                Spacer(modifier = Modifier.height(12.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (isActive && activePlants.firstOrNull()?.plantId == plantId) {
                        Box(modifier = Modifier.background(Yellow500, RoundedCornerShape(10.dp)).padding(horizontal = 10.dp, vertical = 4.dp)) {
                            Text("Priority", style = Typography.labelMedium.copy(fontSize = 10.sp), color = Neutral50)
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                    }

                    if (isActive) {
                        val healthColor = when (userPlant?.healthStatus) {
                            "Subur" -> Green600
                            "Kering" -> Color(0xFFFFA000) // Orange
                            "Layu" -> Red600
                            else -> Green600
                        }
                        Box(modifier = Modifier.background(healthColor, RoundedCornerShape(10.dp)).padding(horizontal = 10.dp, vertical = 4.dp)) {
                            Text(userPlant?.healthStatus ?: "Subur", style = Typography.labelMedium.copy(fontSize = 10.sp), color = Neutral50)
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                    }

                    Text(masterPlant.name, style = Typography.titleLarge.copy(fontWeight = FontWeight.Bold, fontSize = 20.sp), color = Neutral900, modifier = Modifier.weight(1f))
                }

                Spacer(modifier = Modifier.height(24.dp))

                Column(
                    modifier = Modifier.fillMaxWidth().shadow(elevation = 30.dp, shape = RoundedCornerShape(20.dp), spotColor = Neutral900.copy(alpha = 0.1f)).background(Neutral50, RoundedCornerShape(20.dp)).padding(horizontal = 17.dp, vertical = 30.dp)
                ) {
                    Text("Progress", style = Typography.titleLarge.copy(fontWeight = FontWeight.Bold, fontSize = 20.sp), color = Yellow800)
                    Spacer(modifier = Modifier.height(16.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(modifier = Modifier.weight(1f).height(19.dp).background(Neutral300, RoundedCornerShape(20.dp))) {
                            Box(modifier = Modifier.fillMaxWidth(fraction = progressFraction).height(19.dp).background(Yellow300, RoundedCornerShape(20.dp)))
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Text("$finalProgress%", style = Typography.labelMedium.copy(fontWeight = FontWeight.Medium, fontSize = 10.sp), color = Neutral900)
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("$realCurrentDay/$maxDays Hari Tumbuh", style = Typography.labelMedium.copy(fontWeight = FontWeight.SemiBold, fontSize = 10.sp), color = Yellow800)
                }

                Spacer(modifier = Modifier.height(24.dp))

                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(maxDays) { index ->
                        val dayNum = index + 1
                        val isSelected = dayNum == selectedPreviewDay

                        // --- LOGIC BUG 3: WARNA HISTORY HARI ---
                        val isCompleted = userPlant?.taskHistory?.contains(dayNum) == true
                        val (bgColor, textColor) = when {
                            isSelected -> Yellow400 to Neutral900
                            isActive && isCompleted -> Green600 to Neutral50 // Selesai -> Hijau
                            isActive && dayNum == realCurrentDay -> Yellow200 to Yellow800 // Hari ini -> Kuning
                            isActive && dayNum < realCurrentDay -> Color(0xFFFFEBEE) to Color(0xFFE57373) // Bolos (Merah Pudar)
                            else -> Neutral200 to Neutral600 // Belum lewat -> Abu-abu
                        }

                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(20.dp))
                                .background(color = bgColor)
                                .clickable { selectedPreviewDay = dayNum }
                                .padding(horizontal = 16.dp, vertical = 10.dp)
                        ) {
                            Text("Hari $dayNum", style = Typography.labelMedium.copy(fontWeight = FontWeight.Bold, fontSize = 12.sp), color = textColor)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))

                Column(
                    modifier = Modifier.fillMaxWidth().shadow(elevation = 15.dp, shape = RoundedCornerShape(20.dp), spotColor = Neutral900.copy(alpha = 0.1f)).background(Neutral50, RoundedCornerShape(20.dp)).padding(24.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(if (selectedPreviewDay == realCurrentDay) "Daily Tasks" else "Preview Hari $selectedPreviewDay", style = Typography.titleLarge.copy(fontWeight = FontWeight.Bold, fontSize = 20.sp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Icon(painter = painterResource(R.drawable.pin), contentDescription = "Pin")
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    val tasksToDisplay = if (isActive && selectedPreviewDay == realCurrentDay) todayMissions else previewTasks

                    if (tasksToDisplay.isEmpty()) {
                        Text("Tidak ada tugas untuk hari ini. Bersantailah!", style = Typography.bodyMedium, color = Neutral400)
                    } else {
                        tasksToDisplay.forEach { mission ->
                            val canComplete = isActive && selectedPreviewDay == realCurrentDay && !mission.isCompleted

                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 8.dp)
                                    .clickable(enabled = canComplete) {
                                        val userId = userData?.uid ?: return@clickable
                                        missionViewModel.onTaskChecked(userId, userPlant!!.userPlantId, mission, masterPlant)
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
                                    Text(mission.name, style = Typography.bodyMedium.copy(fontWeight = FontWeight.Medium), color = if (mission.isCompleted) Neutral400 else Neutral900)
                                    Text("+${mission.points} poin", style = Typography.labelMedium.copy(fontSize = 10.sp), color = if (mission.isCompleted) Neutral400 else Green500)
                                }
                                if (mission.isMilestone) {
                                    Box(modifier = Modifier.background(Yellow200, RoundedCornerShape(4.dp)).padding(horizontal = 6.dp, vertical = 2.dp)) {
                                        Text("Milestone", style = Typography.labelMedium.copy(fontSize = 9.sp), color = Yellow800)
                                    }
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                if (!isActive) {
                    Button(
                        onClick = onAddPlant,
                        modifier = Modifier.fillMaxWidth().height(52.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Green700),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Tambahkan Tanaman", style = Typography.titleMedium.copy(fontWeight = FontWeight.Bold, fontSize = 16.sp), color = Neutral50)
                    }
                }

                Spacer(modifier = Modifier.height(40.dp))
            }
        }

        snackbarMessage?.let { msg ->
            Box(modifier = Modifier.align(Alignment.BottomCenter).padding(24.dp).background(Green700, RoundedCornerShape(12.dp)).padding(horizontal = 20.dp, vertical = 12.dp)) {
                Text(msg, style = Typography.bodyMedium, color = Neutral50)
            }
            LaunchedEffect(msg) { kotlinx.coroutines.delay(3000); snackbarMessage = null }
        }
    }
}