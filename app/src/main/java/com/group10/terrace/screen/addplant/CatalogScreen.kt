package com.group10.terrace.ui.screen.catalog

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.group10.terrace.R
import com.group10.terrace.ui.components.BottomNavBar
import com.group10.terrace.ui.components.PlantRecommendationCard
import com.group10.terrace.ui.theme.*
import com.group10.terrace.viewmodel.HomeViewModel

@Composable
fun CatalogScreen(
    viewModel: HomeViewModel,
    onBack: () -> Unit, // FIX: Menambahkan parameter back
    onNavigateToDetail: (String) -> Unit,
    onNavigateToNav: (String) -> Unit
) {
    val masterPlants by viewModel.masterPlants.collectAsState()
    val user by viewModel.userData.collectAsState()

    var searchQuery by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("Semua") }

    val categories = listOf("Semua", "Sayuran", "Buah", "Hias")

    val filteredPlants by remember {
        derivedStateOf {
            masterPlants.filter { plant ->
                val matchSearch = plant.name.contains(searchQuery, ignoreCase = true)

                // FIX BUG 2: Logika Filter Sayuran vs Sayur & Buah
                val matchCategory = when (selectedCategory) {
                    "Semua" -> true
                    "Sayuran" -> plant.category.contains("Sayur", ignoreCase = true)
                    "Buah" -> plant.category.contains("Buah", ignoreCase = true)
                    else -> plant.category.contains(selectedCategory, ignoreCase = true)
                }

                matchSearch && matchCategory
            }
        }
    }

    Scaffold(
        bottomBar = { BottomNavBar(currentRoute = "plant", onNavigate = onNavigateToNav) }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Neutral50)
                .windowInsetsPadding(WindowInsets.systemBars.only(WindowInsetsSides.Top)) // Anti Mendelep
                .padding(paddingValues)
        ) {

            // ── FIX BUG 4: Menambahkan Topbar Back Navigation ──
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Green700) // Menyatu dengan Header search
                    .padding(horizontal = 20.dp, vertical = 16.dp)
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    modifier = Modifier.align(Alignment.CenterStart).size(24.dp).clickable { onBack() },
                    tint = Neutral50
                )
                Text(
                    text = "Katalog Tanaman",
                    style = Typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = Neutral50,
                    modifier = Modifier.align(Alignment.Center)
                )
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(bottomStart = 30.dp, bottomEnd = 30.dp))
                    .background(Green700)
                    .padding(horizontal = 24.dp, vertical = 16.dp)
            ) {
                Column {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(40.dp)
                            .border(1.dp, Neutral50, RoundedCornerShape(25.dp))
                            .padding(horizontal = 16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        BasicTextField(
                            value = searchQuery,
                            onValueChange = { searchQuery = it },
                            textStyle = TextStyle(color = Neutral50, fontSize = 14.sp),
                            modifier = Modifier.weight(1f),
                            cursorBrush = SolidColor(Neutral50),
                            decorationBox = { inner ->
                                if (searchQuery.isEmpty()) Text("Cari tanaman...", color = Neutral200, fontSize = 14.sp)
                                inner()
                            }
                        )
                        Icon(
                            painter = painterResource(id = R.drawable.search),
                            contentDescription = "Search",
                            tint = Neutral50,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            painter = painterResource(id = R.drawable.clover),
                            contentDescription = "Points",
                            tint = Neutral50,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(text = "${user?.totalPoints ?: 0} Points", style = Typography.labelMedium, color = Neutral50)
                    }
                }
            }

            Column(modifier = Modifier.padding(24.dp)) {
                Text(
                    text = "Kategori",
                    style = Typography.titleLarge.copy(fontWeight = FontWeight.Bold, fontSize = 20.sp),
                    color = Neutral900
                )
                Spacer(modifier = Modifier.height(16.dp))

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    categories.forEach { category ->
                        val isSelected = selectedCategory == category
                        Box(
                            modifier = Modifier
                                .border(1.dp, Green700, RoundedCornerShape(30.dp))
                                .background(if (isSelected) Green700 else Color.Transparent, RoundedCornerShape(30.dp))
                                .clickable { selectedCategory = category }
                                .padding(horizontal = 16.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = category,
                                style = Typography.labelMedium,
                                color = if (isSelected) Neutral50 else Green700
                            )
                        }
                    }
                }
            }

            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                contentPadding = PaddingValues(horizontal = 24.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.weight(1f)
            ) {
                items(filteredPlants) { plant ->
                    PlantRecommendationCard(
                        plant = plant,
                        onClick = { onNavigateToDetail(plant.id) }
                    )
                }
            }
        }
    }
}