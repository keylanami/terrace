package com.group10.terrace.ui.screen.plant

import androidx.compose.foundation.Image
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.group10.terrace.R
import com.group10.terrace.ui.components.DifficultyBadge
import com.group10.terrace.ui.components.calculateDaysPassed
import com.group10.terrace.ui.theme.*
import com.group10.terrace.viewmodel.HomeViewModel




@Composable
fun PlantDetailScreen(
    viewModel: HomeViewModel,
    userPlantId: String,
    onBack: () -> Unit
) {
    val activePlants by viewModel.activePlants.collectAsState()
    val masterPlants by viewModel.masterPlants.collectAsState()
    val user by viewModel.userData.collectAsState()

    val userPlant = activePlants.find { it.userPlantId == userPlantId }
    val masterPlant = masterPlants.find { it.id == userPlant?.plantId }

    if (userPlant == null || masterPlant == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator(color = Green600)
        }
        return
    }

    val currentDay = calculateDaysPassed(userPlant.startDate).toInt()
    val maxDays = extractMaxDays(masterPlant.harvest_duration)
    val progressFraction = (currentDay.toFloat() / maxDays.toFloat()).coerceIn(0f, 1f)
    val progressPercentage = (progressFraction * 100).toInt()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Neutral50)
            .verticalScroll(rememberScrollState())
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                Icons.AutoMirrored.Outlined.ArrowBack,
                contentDescription = "Back",
                modifier = Modifier
                    .size(24.dp)
                    .clickable { onBack() }
            )
            Text(
                "Tanaman Mu",
                style = Typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                modifier = Modifier.weight(1f),
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.size(24.dp))
        }

        Image(
            painter = painterResource(id = R.drawable.fototanaman),
            contentDescription = "Foto Tanaman",
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxWidth()
                .height(198.dp)
        )

        Column(modifier = Modifier.padding(24.dp)) {

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
                        masterPlant.type.ifEmpty { "Sayuran" },
                        style = Typography.labelMedium.copy(fontSize = 10.sp),
                        color = Neutral50
                    )
                }
                DifficultyBadge(difficulty = masterPlant.difficulty)
            }

            Spacer(modifier = Modifier.height(12.dp))

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

            // --- PROGRESS CARD ---
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(elevation = 30.dp, shape = RoundedCornerShape(20.dp), spotColor = Neutral900.copy(alpha = 0.1f))
                    .background(Neutral50, RoundedCornerShape(20.dp))
                    .padding(horizontal = 17.dp, vertical = 30.dp)
            ) {
                Text("Progress", style = Typography.titleLarge.copy(fontWeight = FontWeight.Bold, fontSize = 20.sp), color = Yellow800)

                Spacer(modifier = Modifier.height(16.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(modifier = Modifier.weight(1f).height(19.dp).background(Neutral300, RoundedCornerShape(20.dp))) {
                        Box(modifier = Modifier.fillMaxWidth(fraction = progressFraction).height(19.dp).background(Yellow300, RoundedCornerShape(20.dp)))
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Text("$progressPercentage%", style = Typography.labelMedium.copy(fontWeight = FontWeight.Medium, fontSize = 10.sp), color = Neutral900)
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text("$currentDay/$maxDays Hari Tumbuh", style = Typography.labelMedium.copy(fontWeight = FontWeight.SemiBold, fontSize = 10.sp), color = Yellow800)
            }

            Spacer(modifier = Modifier.height(24.dp))

            // --- SCROLLABLE DAYS BADGE ---
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(maxDays) { index ->
                    val dayNum = index + 1
                    val (bgColor, textColor) = when {
                        dayNum < currentDay -> Green600 to Neutral50
                        dayNum == currentDay -> Yellow400 to Neutral900
                        else -> Neutral200 to Neutral600
                    }
                    Box(
                        modifier = Modifier
                            .background(color = bgColor, shape = RoundedCornerShape(20.dp))
                            .padding(horizontal = 16.dp, vertical = 10.dp)
                    ) {
                        Text("Hari $dayNum", style = Typography.labelMedium.copy(fontWeight = FontWeight.Bold, fontSize = 12.sp), color = textColor)
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // --- DAILY TASKS CARD ---
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(elevation = 30.dp, shape = RoundedCornerShape(20.dp), spotColor = Neutral900.copy(alpha = 0.1f))
                    .background(Neutral50, RoundedCornerShape(20.dp))
            ) {
                Column(modifier = Modifier.padding(start = 17.dp, top = 31.dp, end = 16.dp, bottom = 40.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("Daily Tasks", style = Typography.titleLarge.copy(fontWeight = FontWeight.Bold, fontSize = 20.sp))
                            Spacer(modifier = Modifier.width(8.dp))
                            // Ganti dengan pin dari resource kamu jika ada
                            Icon(painter = painterResource(id = android.R.drawable.ic_menu_add), contentDescription = null, modifier = Modifier.size(20.dp), tint = Neutral900)
                        }

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            // Ganti dengan streak icon kamu
                            Icon(painter = painterResource(id = android.R.drawable.star_on), contentDescription = "Streak", modifier = Modifier.size(24.dp), tint = Yellow500)
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("${user?.currentStreak ?: 0}", style = Typography.titleLarge.copy(fontWeight = FontWeight.Bold, fontSize = 20.sp), color = Yellow500)
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Mengambil data menggunakan interface TaskItem
                    val todaysRecurringTasks = masterPlant.tasks_logic?.recurringTask
                        ?.filter { currentDay % it.frequency_days == 0 } ?: emptyList() // Perhatikan camelCase
                    val todaysMilestones = masterPlant.tasks_logic?.milestoneTask
                        ?.filter { it.day == currentDay } ?: emptyList()

                    val allTodaysTasks: List<TaskItem> = todaysRecurringTasks + todaysMilestones

                    if (allTodaysTasks.isEmpty()) {
                        Text("Semua tugas selesai. Bersantailah!", style = Typography.bodyMedium, color = Neutral400)
                    } else {
                        allTodaysTasks.forEach { task ->
                            var isChecked by remember { mutableStateOf(false) }
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 8.dp)
                                    .clickable { isChecked = !isChecked },
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                // Ganti R.drawable.checklist dengan asset aslimu
                                Icon(
                                    painter = painterResource(id = android.R.drawable.checkbox_on_background),
                                    contentDescription = null,
                                    tint = if (isChecked) Green600 else Neutral200,
                                    modifier = Modifier.size(24.dp)
                                )
                                Spacer(modifier = Modifier.width(12.dp))
                                Text(
                                    task.taskName, // Sekarang aman dipanggil!
                                    style = Typography.bodyMedium.copy(fontWeight = FontWeight.Medium),
                                    color = Neutral900
                                )
                            }
                        }
                    }
                }

                // Tombol Plus (+)
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(bottom = 20.dp, end = 20.dp)
                        .size(62.dp)
                        .background(color = Green600, shape = RoundedCornerShape(100.dp))
                        .clickable { /* TODO */ },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(painter = painterResource(id = android.R.drawable.ic_input_add), contentDescription = "Add", tint = Neutral50, modifier = Modifier.size(32.dp))
                }
            }

            Spacer(modifier = Modifier.height(100.dp))
        }
    }
}