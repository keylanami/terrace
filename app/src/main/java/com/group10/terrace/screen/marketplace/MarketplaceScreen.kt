package com.group10.terrace.screen.marketplace

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.group10.terrace.R
import com.group10.terrace.ui.components.BottomNavBar
import com.group10.terrace.ui.components.MarketProductCard
import com.group10.terrace.ui.theme.*
import com.group10.terrace.viewmodel.AuthViewModel
import com.group10.terrace.viewmodel.MarketplaceViewModel
import androidx.compose.material.icons.outlined.ShoppingCart

@Composable
fun MarketplaceScreen(
    marketplaceViewModel: MarketplaceViewModel,
    authViewModel: AuthViewModel,
    onNavigateToDetail: (String) -> Unit,
    onNavigateToCart: () -> Unit,
    onNavigateToNav: (String) -> Unit
) {
    val products by marketplaceViewModel.products.collectAsState()
    val user by authViewModel.userData.collectAsState()

    var searchQuery by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("Semua") }
    val categories = listOf("Semua", "Sayuran", "Pot", "Pupuk", "Alat Berkebun")

    val filteredProducts by remember {
        derivedStateOf {
            products.filter { p ->
                val matchSearch = p.name.contains(searchQuery, ignoreCase = true)
                val matchCategory = if (selectedCategory == "Semua") true else p.category.contains(selectedCategory, ignoreCase = true)
                matchSearch && matchCategory
            }
        }
    }

    Scaffold(
        bottomBar = { BottomNavBar(currentRoute = "market", onNavigate = onNavigateToNav,) },
        floatingActionButton = {
            Box(
                modifier = Modifier
                    .padding(bottom = 16.dp, end = 16.dp)
                    .size(62.dp)
                    .background(Green700, RoundedCornerShape(100.dp))
                    .clickable { onNavigateToCart() },
                contentAlignment = Alignment.Center
            ) {
                Icon(imageVector = Icons.Outlined.ShoppingCart, contentDescription = "Cart", tint = Neutral50)
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier.fillMaxSize().background(Neutral50).padding(paddingValues)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(bottomStart = 30.dp, bottomEnd = 30.dp))
                    .background(Green700)
                    .padding(horizontal = 24.dp, vertical = 24.dp)
            ) {
                Column(modifier = Modifier.padding(top = 16.dp)) {

                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        placeholder = { Text("Cari produk...", color = Neutral200, fontSize = 14.sp) },
                        leadingIcon = { Icon(painter = painterResource(id = R.drawable.search), contentDescription = null, tint = Neutral50) },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Neutral50,
                            unfocusedBorderColor = Neutral50,
                            focusedTextColor = Neutral50,
                            unfocusedTextColor = Neutral50
                        ),
                        modifier = Modifier.fillMaxWidth().height(50.dp),
                        shape = RoundedCornerShape(25.dp)
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(painter = painterResource(id = R.drawable.clover), contentDescription = null, tint = Neutral50, modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(text = "${user?.totalPoints ?: 0} EXP", style = Typography.labelMedium.copy(fontSize = 12.sp), color = Neutral50)
                    }
                }
            }

            LazyRow(
                contentPadding = PaddingValues(horizontal = 24.dp, vertical = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(categories) { category ->
                    val isSelected = selectedCategory == category
                    Box(
                        modifier = Modifier
                            .background(if (isSelected) Green600 else Neutral200, RoundedCornerShape(9999.dp))
                            .clickable { selectedCategory = category }
                            .padding(horizontal = 24.dp, vertical = 8.dp)
                    ) {
                        Text(
                            text = category,
                            style = Typography.labelMedium.copy(fontWeight = FontWeight.Medium),
                            color = if (isSelected) Neutral50 else Green600
                        )
                    }
                }
            }

            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                contentPadding = PaddingValues(start = 24.dp, end = 24.dp, bottom = 100.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.weight(1f)
            ) {
                items(filteredProducts) { product ->
                    MarketProductCard(
                        product = product,
                        onClick = { onNavigateToDetail(product.id) },
                        onAddToCartClick = {
                            val userId = user?.uid ?: return@MarketProductCard
                            marketplaceViewModel.addToCart(userId, product, 1)
                        }
                    )
                }
            }
        }
    }
}