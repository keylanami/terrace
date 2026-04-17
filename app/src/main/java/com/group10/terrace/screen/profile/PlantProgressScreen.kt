package com.group10.terrace.screen.plant

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.group10.terrace.R
import com.group10.terrace.ui.components.ActivePlantCard
import com.group10.terrace.ui.components.BottomNavBar
import com.group10.terrace.ui.theme.*
import com.group10.terrace.viewmodel.HomeViewModel
import androidx.compose.runtime.collectAsState

@Composable
fun PlantProgressScreen(
    viewModel: HomeViewModel,
    onNavigate: (String) -> Unit,
    onPlantClick: (userPlantId: String) -> Unit,
    onAddPlant: () -> Unit
) {
    val activePlants by viewModel.activePlants.collectAsState()
    val masterPlants by viewModel.masterPlants.collectAsState()

    Box(modifier = Modifier.fillMaxSize().background(Neutral100)) {
        Column(modifier = Modifier.fillMaxSize()) {

            // ── Green Header ──────────────────────────────────────────────
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(brush = PrimaryGradient)
                    .padding(horizontal = 20.dp, vertical = 20.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.putih),
                            contentDescription = "Logo",
                            tint = Neutral50,
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
                        modifier = Modifier.size(24.dp)
                    )
                }
            }

            // ── Progress Card ─────────────────────────────────────────────
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(horizontal = 16.dp)
                    .offset(y = (-12).dp)
                    .shadow(10.dp, RoundedCornerShape(20.dp))
                    .background(Neutral50, RoundedCornerShape(20.dp))
            ) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Spacer(modifier = Modifier.height(12.dp))

                    // Drag handle
                    Box(
                        modifier = Modifier
                            .width(40.dp)
                            .height(4.dp)
                            .background(Neutral300, RoundedCornerShape(2.dp))
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = "Progress",
                        style = Typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = Neutral900
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    if (activePlants.isEmpty()) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(1f),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "Belum ada tanaman aktif.\nTekan + untuk mulai menanam!",
                                style = Typography.bodyMedium,
                                color = Neutral400,
                                textAlign = TextAlign.Center
                            )
                        }
                    } else {
                        LazyColumn(
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(1f)
                                .padding(horizontal = 16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp),
                            contentPadding = PaddingValues(bottom = 80.dp)
                        ) {
                            itemsIndexed(activePlants) { index, userPlant ->
                                val master = masterPlants.find { it.id == userPlant.plantId }
                                ActivePlantCard(
                                    userPlant = userPlant,
                                    estimationDays = master?.estimationDays ?: 0,
                                    difficulty = master?.difficulty ?: "Easy",
                                    isPriority = index == 0,
                                    onClick = { onPlantClick(userPlant.userPlantId) }
                                )
                            }
                        }
                    }
                }

                // ── FAB ───────────────────────────────────────────────────
                FloatingActionButton(
                    onClick = onAddPlant,
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(bottom = 80.dp),
                    containerColor = Green600,
                    shape = CircleShape,
                    elevation = FloatingActionButtonDefaults.elevation(6.dp)
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.plus),
                        contentDescription = "Tambah Tanaman",
                        tint = Neutral50,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
        }

        // ── Bottom Nav ────────────────────────────────────────────────────
        BottomNavBar(
            currentRoute = "plant",
            onNavigate = onNavigate,
            modifier = Modifier.align(Alignment.BottomCenter)
        )
    }
}
