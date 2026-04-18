package com.group10.terrace.screen.education

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.group10.terrace.R
import com.group10.terrace.model.EducationItem
import com.group10.terrace.ui.components.BottomNavBar
import com.group10.terrace.ui.theme.*
import com.group10.terrace.viewmodel.AcademyViewModel

enum class EducationFilter { SEMUA, ARTICLE, VIDEO }

@Composable
fun EducationScreen(
    viewModel: AcademyViewModel,
    onItemClick: (item: EducationItem, type: String) -> Unit,
    onNavigateToNav: (String) -> Unit   // FIX: tambah parameter untuk BottomNavBar
) {
    val articles by viewModel.articles.collectAsState()
    val videos by viewModel.videos.collectAsState()
    val currentUser by viewModel.currentUser.collectAsState()

    var query by remember { mutableStateOf("") }
    var activeFilter by remember { mutableStateOf(EducationFilter.SEMUA) }

    val allItems: List<Pair<EducationItem, String>> = remember(articles, videos) {
        articles.map { it to "Article" } + videos.map { it to "Video" }
    }

    val filteredItems = allItems.filter { (item, type) ->
        val matchesFilter = when (activeFilter) {
            EducationFilter.SEMUA   -> true
            EducationFilter.ARTICLE -> type == "Article"
            EducationFilter.VIDEO   -> type == "Video"
        }
        val matchesQuery = query.isBlank() || item.title.contains(query, ignoreCase = true)
        matchesFilter && matchesQuery
    }

    // FIX: wrap dengan Scaffold agar BottomNavBar muncul
    Scaffold(
        bottomBar = {
            BottomNavBar(currentRoute = "academy", onNavigate = onNavigateToNav)
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Neutral50)
                .padding(paddingValues)
        ) {
            Text(
                text = "Edukasi",
                style = Typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                color = Neutral900,
                modifier = Modifier.padding(start = 20.dp, top = 20.dp, bottom = 12.dp)
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
                    .background(Neutral100, RoundedCornerShape(12.dp))
                    .padding(horizontal = 16.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                BasicTextField(
                    value = query,
                    onValueChange = { query = it },
                    modifier = Modifier.weight(1f),
                    singleLine = true,
                    textStyle = TextStyle(
                        fontSize = 12.sp,
                        fontFamily = PlusJakartaSans,
                        fontWeight = FontWeight.Medium,
                        color = Neutral900,
                        letterSpacing = 0.06.sp
                    ),
                    decorationBox = { inner ->
                        if (query.isEmpty()) {
                            Text(
                                "Pencarian",
                                style = TextStyle(
                                    fontSize = 12.sp,
                                    fontFamily = PlusJakartaSans,
                                    fontWeight = FontWeight.Medium,
                                    color = Neutral400,
                                    letterSpacing = 0.06.sp
                                )
                            )
                        }
                        inner()
                    }
                )
                Icon(
                    painter = painterResource(id = R.drawable.search),
                    contentDescription = "Search",
                    modifier = Modifier.size(24.dp),
                    tint = Neutral400
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.padding(horizontal = 20.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                EducationFilter.entries.forEach { filter ->
                    val isActive = activeFilter == filter
                    Box(
                        modifier = Modifier
                            .background(
                                color = if (isActive) Green500 else Color(0xFFF1F1F1),
                                shape = RoundedCornerShape(9999.dp)
                            )
                            .clickable { activeFilter = filter }
                            .padding(horizontal = 24.dp, vertical = 8.dp)
                    ) {
                        Text(
                            text = when (filter) {
                                EducationFilter.SEMUA   -> "Semua"
                                EducationFilter.ARTICLE -> "Article"
                                EducationFilter.VIDEO   -> "Video"
                            },
                            style = TextStyle(
                                fontSize = 14.sp,
                                lineHeight = 20.sp,
                                fontFamily = PlusJakartaSans,
                                fontWeight = FontWeight.Medium,
                                color = if (isActive) Neutral50 else Green500
                            )
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (filteredItems.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = Green500)
                }
            } else {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    contentPadding = PaddingValues(bottom = 80.dp)
                ) {
                    items(filteredItems) { (item, type) ->
                        EducationCard(
                            item = item,
                            type = type,
                            userPoints = currentUser?.currentPoint ?: 0,
                            onClick = { onItemClick(item, type) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun EducationCard(
    item: EducationItem,
    type: String,
    userPoints: Int,
    onClick: () -> Unit
) {
    val isArticle = type == "Article"
    val isLocked  = item.isPremium && userPoints < item.minPoints

    Column(
        modifier = Modifier
            .shadow(elevation = 8.dp, shape = RoundedCornerShape(10.dp),
                spotColor = Color(0x40000000), ambientColor = Color(0x40000000))
            .background(Neutral50, RoundedCornerShape(10.dp))
            .clickable(enabled = !isLocked) { onClick() }
            .padding(15.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Box(
            modifier = Modifier
                .background(
                    color = if (isArticle) Yellow300 else Green700,
                    shape = RoundedCornerShape(10.dp)
                )
                .padding(horizontal = 8.dp, vertical = 4.dp)
        ) {
            Text(
                text = type,
                style = TextStyle(
                    fontSize = 10.sp, fontFamily = PlusJakartaSans, fontWeight = FontWeight.SemiBold,
                    color = if (isArticle) Color(0xFF73571F) else Neutral50, letterSpacing = 0.05.sp
                )
            )
        }

        Box(
            modifier = Modifier.fillMaxWidth().height(94.dp).clip(RoundedCornerShape(6.dp))
        ) {
            AsyncImage(
                model = item.imageUrl,
                contentDescription = item.title,
                contentScale = ContentScale.FillBounds,
                modifier = Modifier.fillMaxSize(),
                error = painterResource(id = R.drawable.fototanaman),
                placeholder = painterResource(id = R.drawable.fototanaman)
            )
            if (isLocked) {
                Box(
                    modifier = Modifier.fillMaxSize().background(Color(0x99000000)),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_lock),
                            contentDescription = "Konten Premium",
                            tint = Yellow300,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "${item.minPoints} poin",
                            style = TextStyle(fontSize = 10.sp, fontFamily = PlusJakartaSans,
                                fontWeight = FontWeight.SemiBold, color = Yellow300)
                        )
                    }
                }
            }
        }

        Text(
            text = item.title,
            style = TextStyle(
                fontSize = 12.sp, fontFamily = PlusJakartaSans, fontWeight = FontWeight.SemiBold,
                color = if (isLocked) Neutral400 else Neutral900, letterSpacing = 0.06.sp
            ),
            maxLines = 2,
            overflow = TextOverflow.Ellipsis
        )

        if (item.durationOrTime.isNotBlank()) {
            Text(
                text = item.durationOrTime,
                style = TextStyle(fontSize = 10.sp, fontFamily = PlusJakartaSans,
                    fontWeight = FontWeight.Normal, color = Neutral400, letterSpacing = 0.05.sp)
            )
        }
    }
}