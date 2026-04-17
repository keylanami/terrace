package com.group10.terrace.ui.screen.plant

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.group10.terrace.R
import com.group10.terrace.ui.components.ActivePlantCard
import com.group10.terrace.ui.components.BottomNavBar
import com.group10.terrace.ui.components.ExpandableLeaderboard
import com.group10.terrace.ui.theme.*
import com.group10.terrace.viewmodel.HomeViewModel
import com.group10.terrace.viewmodel.LeaderboardViewModel

@Composable
fun PlantControlScreen(
    viewModel: HomeViewModel,
    leaderboardViewModel: LeaderboardViewModel = androidx.lifecycle.viewmodel.compose.viewModel(),
    onNavigateBack: () -> Unit,
    onNavigateToAddPlant: () -> Unit,
    onNavigateToDetail: (String) -> Unit,
    onNavigateToNav: (String) -> Unit
) {
    val activePlants by viewModel.activePlants.collectAsState()
    val user by viewModel.userData.collectAsState()
    val leaderboardData by leaderboardViewModel.leaderboardData.collectAsState()
    val masterPlants by viewModel.masterPlants.collectAsState()

    Scaffold(
        bottomBar = { BottomNavBar(currentRoute = "plant", onNavigate = onNavigateToNav) },

        floatingActionButton = {
            Box(
                modifier = Modifier
                    .padding(bottom = 16.dp)
                    .size(62.dp)
                    .background(color = Green600, shape = RoundedCornerShape(100.dp))
                    .clickable { onNavigateToAddPlant() },
                contentAlignment = Alignment.Center
            ) {

                Icon(
                    painter = painterResource(id = android.R.drawable.ic_input_add),
                    contentDescription = "Add Plant",
                    tint = Neutral50,
                    modifier = Modifier.size(32.dp)
                )
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Neutral50)
                .padding(paddingValues)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    modifier = Modifier.size(24.dp).clickable { onNavigateBack() },
                    tint = Neutral900
                )
                Text(
                    text = "Plant Control",
                    style = Typography.titleLarge.copy(fontWeight = FontWeight.Bold, fontSize = 20.sp),
                    color = Neutral900,
                    modifier = Modifier.weight(1f),
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )
                Spacer(modifier = Modifier.size(24.dp))
            }

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(start = 24.dp, end = 24.dp, top = 8.dp, bottom = 100.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                item {
                    ExpandableLeaderboard(currentUser = user, leaderboardData = leaderboardData)
                }

                item {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Active Plants",
                        style = Typography.titleLarge.copy(fontWeight = FontWeight.Bold, fontSize = 20.sp),
                        color = Neutral900
                    )
                }

                items(activePlants) { plant ->
                    val masterPlantData = masterPlants.find { it.id == plant.plantId }
                    val dynamicDifficulty = masterPlantData?.difficulty ?: "Mudah"
                    val durationText = masterPlantData?.harvest_duration ?: "30 hari"
                    val dynamicEstimationDays = extractMaxDays(durationText)

                    ActivePlantCard(
                        userPlant = plant,
                        estimationDays = dynamicEstimationDays,
                        difficulty = dynamicDifficulty,
                        isPriority = plant == activePlants.first(),
                        onClick = { onNavigateToDetail(plant.userPlantId) }
                    )
                }
            }
        }
    }
}

fun extractMaxDays(duration: String): Int {
    val numbers = Regex("\\d+").findAll(duration).map { it.value.toInt() }.toList()
    return numbers.maxOrNull() ?: 30
}