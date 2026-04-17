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
import com.group10.terrace.model.TaskItem
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

        AsyncImage(
            model = masterPlant.imageUrl.ifBlank { null },
            contentDescription = masterPlant.name,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxWidth().height(198.dp),
            error = painterResource(id = R.drawable.fototanaman),
            placeholder = painterResource(id = R.drawable.fototanaman)
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
                    // FIX: category (bukan type — type tidak ada di plants.json)
                    Text(masterPlant.category.ifEmpty { "Sayuran & Buah" },
                        style = Typography.labelMedium.copy(fontSize = 10.sp), color = Neutral50)
                }
                DifficultyBadge(difficulty = masterPlant.difficulty)
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                if (activePlants.firstOrNull()?.userPlantId == userPlantId) {
                    Box(modifier = Modifier.background(Yellow500, RoundedCornerShape(10.dp)).padding(horizontal = 10.dp, vertical = 4.dp)) {
                        Text("Priority", style = Typography.labelMedium.copy(fontSize = 10.sp), color = Neutral50)
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                }
                Text(userPlant.plantName,
                    style = Typography.titleLarge.copy(fontWeight = FontWeight.Bold, fontSize = 20.sp),
                    color = Neutral900, modifier = Modifier.weight(1f))
                Icon(painter = painterResource(id = android.R.drawable.ic_menu_edit),
                    contentDescription = "Edit", modifier = Modifier.size(20.dp), tint = Neutral400)
            }

            Spacer(modifier = Modifier.height(24.dp))

            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(maxDays) { index ->
                    val dayNum = index + 1
                    val isActive = dayNum <= currentDay
                    Box(
                        modifier = Modifier
                            .background(if (isActive) Green600 else Green100, RoundedCornerShape(20.dp))
                            .padding(horizontal = 12.dp, vertical = 8.dp)
                    ) {
                        Text("Hari $dayNum",
                            style = Typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                            color = if (isActive) Neutral50 else Green700)
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(elevation = 15.dp, shape = RoundedCornerShape(20.dp), spotColor = Neutral900.copy(alpha = 0.1f))
                    .background(Neutral50, RoundedCornerShape(20.dp))
                    .padding(24.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("Daily Tasks", style = Typography.titleLarge.copy(fontWeight = FontWeight.Bold, fontSize = 20.sp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Icon(painter = painterResource(R.drawable.pin), contentDescription = "Pin")
                }

                Spacer(modifier = Modifier.height(16.dp))

                val todaysRecurring: List<TaskItem> = masterPlant.tasks_logic
                    ?.recurringTask?.filter { currentDay % it.frequency_days == 0 } ?: emptyList()
                val todaysMilestones: List<TaskItem> = masterPlant.tasks_logic
                    ?.milestoneTask?.filter { it.day == currentDay } ?: emptyList()
                val allTodaysTasks: List<TaskItem> = todaysRecurring + todaysMilestones

                if (allTodaysTasks.isEmpty()) {
                    Text("Tidak ada tugas untuk hari ini. Bersantailah!", style = Typography.bodyMedium, color = Neutral400)
                } else {
                    allTodaysTasks.forEach { task ->
                        var isChecked by remember { mutableStateOf(false) }
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp).clickable { isChecked = !isChecked },
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.checklist),
                                contentDescription = null,
                                tint = if (isChecked) Green600 else Neutral400,
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(task.task_name,
                                style = Typography.bodyMedium.copy(fontWeight = FontWeight.Medium),
                                color = if (isChecked) Neutral400 else Neutral900)
                        }
                    }
                }
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