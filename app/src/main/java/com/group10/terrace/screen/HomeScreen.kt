package com.group10.terrace.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.group10.terrace.R
import com.group10.terrace.model.Plant
import com.group10.terrace.ui.components.BottomNavBar
import com.group10.terrace.ui.components.PlantRecommendationCard
import com.group10.terrace.ui.components.PriorityPlantCard
import com.group10.terrace.ui.theme.*
import com.group10.terrace.viewmodel.HomeViewModel



@Composable
fun HomeScreen(
    viewModel: HomeViewModel,
    onNavigateToAddPlant: () -> Unit,
    onNavigateToNav: (String) -> Unit
) {

    val user by viewModel.userData.collectAsState()
    val activePlants by viewModel.activePlants.collectAsState()
    val recommendations by viewModel.recommendations.collectAsState()

    Scaffold(
        bottomBar = {
            BottomNavBar(currentRoute = "home", onNavigate = onNavigateToNav)
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Neutral50)
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
        ) {

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(bottomStart = 32.dp, bottomEnd = 32.dp))
                    .background(brush = PrimaryGradient)
                    .padding(start = 24.dp, end = 24.dp, top = 48.dp, bottom = 48.dp)
            ) {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.putih_full),
                            contentDescription = "Logo",
                            modifier = Modifier.height(38.dp)
                        )
                        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                            Icon(
                                painter = painterResource(id = R.drawable.notif),
                                contentDescription = "Notif",
                                tint = Neutral50,
                                modifier = Modifier
                                    .size(24.dp))

                            Icon(painter = painterResource(id = R.drawable.ic_person),
                                contentDescription = "Profile",
                                tint = Neutral50,
                                modifier = Modifier
                                    .size(24.dp))
                        }
                    }

                    Spacer(modifier = Modifier.height(32.dp))


                    Text(
                        text = "Hi, ${user?.name ?: "User"}!",
                        style = Typography.titleLarge.copy(fontSize = 24.sp, fontWeight = FontWeight.Bold),
                        color = Neutral50
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                painter = painterResource(id = R.drawable.fire),
                                contentDescription = "EXP",
                                tint = Neutral50,
                                modifier = Modifier.size(16.dp))


                            Spacer(modifier = Modifier.width(4.dp))


                            Text(text = "${user?.totalPoints ?: 0} EXP",
                                style = Typography.labelMedium,
                                color = Neutral50)
                        }

                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Text(text = "${activePlants.size} Active Plants",
                                style = Typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                color = Neutral50)


                            Text(text = "${user?.totalHarvested ?: 0} Plants Done",
                                style = Typography.labelMedium,
                                color = Neutral50)
                        }
                    }
                }
            }

            Box(modifier = Modifier.offset(y = (-30).dp).padding(horizontal = 20.dp)) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .shadow(elevation = 12.dp, shape = RoundedCornerShape(20.dp), spotColor = Green700.copy(alpha = 0.3f))
                        .background(color = Green800, shape = RoundedCornerShape(20.dp))
                        .padding(20.dp)
                ) {

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row {
                            Text("You're an ", style = Typography.bodyMedium, color = Neutral50)
                            Text("Amateur!", style = Typography.bodyMedium.copy(fontWeight = FontWeight.Bold), color = Yellow400)
                        }
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Image(painter = painterResource(id = R.drawable.fire), contentDescription = "Streak", modifier = Modifier.size(24.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("${user?.currentStreak ?: 0}", style = Typography.titleMedium.copy(fontWeight = FontWeight.Bold), color = Yellow400)
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                    Text("Daily Tasks", style = Typography.titleMedium.copy(fontWeight = FontWeight.Bold), color = Neutral50)
                    Spacer(modifier = Modifier.height(8.dp))

                    repeat(3) {
                        Row(
                            modifier = Modifier.padding(vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(painter = painterResource(id = R.drawable.checklist), contentDescription = "Check", tint = Neutral50, modifier = Modifier.size(20.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Water the plant", style = Typography.bodyMedium, color = Neutral50)
                        }
                    }
                }
            }



            Column(modifier = Modifier.padding(horizontal = 24.dp)) {
                Text(
                    text = "Priority Plant",
                    style = Typography.titleLarge.copy(fontSize = 20.sp, fontWeight = FontWeight.Bold),
                    color = Neutral900
                )
                Spacer(modifier = Modifier.height(16.dp))

                if (activePlants.isNotEmpty()) {
                    PriorityPlantCard(
                        userPlant = activePlants.first(),
                        onClick = { /* TODO: Navigasi ke Detail Tanaman */ }
                    )
                } else {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .shadow(elevation = 10.dp, shape = RoundedCornerShape(20.dp), spotColor = Neutral900.copy(alpha = 0.05f))
                            .background(color = Neutral50, shape = RoundedCornerShape(20.dp))
                            .clickable { onNavigateToAddPlant() }
                            .padding(vertical = 40.dp),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(42.dp)
                                .background(color = Neutral300, shape = RoundedCornerShape(100.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("+", fontSize = 24.sp, color = Neutral50)
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(text = "Add Plant", style = Typography.labelLarge, color = Neutral400)
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))


            Column(modifier = Modifier.padding(start = 24.dp)) {
                Text(
                    text = "Recommendations",
                    style = Typography.titleLarge.copy(fontSize = 20.sp, fontWeight = FontWeight.SemiBold),
                    color = Neutral900
                )
                Spacer(modifier = Modifier.height(16.dp))


                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    contentPadding = PaddingValues(end = 24.dp)
                ) {

                    val listToShow = if (recommendations.isNotEmpty()) recommendations else listOf(
                        Plant(name = "Kangkung Hidroponik", difficulty = "Mudah"),
                        Plant(name = "Monstera Variegata", difficulty = "Sulit")
                    )

                    items(listToShow) { plant ->
                        PlantRecommendationCard(plant = plant, onClick = { /* TODO Navigate to Detail */ })
                    }
                }
            }

            Spacer(modifier = Modifier.height(40.dp))
        }
    }
}