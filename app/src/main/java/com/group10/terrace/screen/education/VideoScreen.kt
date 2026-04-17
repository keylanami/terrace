package com.group10.terrace.screen.education

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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.group10.terrace.R
import com.group10.terrace.model.EducationItem
import com.group10.terrace.ui.theme.*
import com.group10.terrace.viewmodel.AcademyViewModel

@Composable
fun VideoDetailScreen(
    viewModel: AcademyViewModel,
    video: EducationItem,
    onBack: () -> Unit,
    onRecommendationClick: (item: EducationItem, type: String) -> Unit
) {
    val allVideos  by viewModel.videos.collectAsState()
    val allArticles by viewModel.articles.collectAsState()
    val currentUser by viewModel.currentUser.collectAsState()

    val recommendations: List<Pair<EducationItem, String>> = remember(allVideos, allArticles, video.id) {
        allVideos
            .filter { it.id != video.id }
            .map { it to "Video" } +
                allArticles.map { it to "Article" }
    }

    var likeCount by remember { mutableIntStateOf(0) }
    var isLiked by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Neutral50)
            .verticalScroll(rememberScrollState())
    ) {
        // ── Top Bar ───────────────────────────────────────────────────────
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Outlined.ArrowBack,
                contentDescription = "Back",
                modifier = Modifier
                    .size(24.dp)
                    .clickable { onBack() }
            )
            Text(
                text = "Video",
                style = TextStyle(
                    fontSize = 20.sp,
                    fontFamily = PlusJakartaSans,
                    fontWeight = FontWeight.Bold,
                    color = Neutral900,
                    textAlign = TextAlign.Center,
                    letterSpacing = 0.1.sp
                ),
                modifier = Modifier.weight(1f),
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.size(24.dp))
        }


        AsyncImage(
            model = video.imageUrl,
            contentDescription = video.title,
            contentScale = ContentScale.FillBounds,
            modifier = Modifier
                .fillMaxWidth()
                .height(271.dp),
            error = painterResource(id = R.drawable.fototanaman),
            placeholder = painterResource(id = R.drawable.fototanaman)
        )

        Column(modifier = Modifier.padding(horizontal = 20.dp)) {
            Spacer(modifier = Modifier.height(16.dp))

            // ── Title + Save ──────────────────────────────────────────────
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.Top,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = video.title,
                    style = TextStyle(
                        fontSize = 20.sp,
                        fontFamily = PlusJakartaSans,
                        fontWeight = FontWeight.Bold,
                        color = Neutral900,
                        letterSpacing = 0.1.sp
                    ),
                    modifier = Modifier.weight(1f)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Icon(
                    painter = painterResource(id = R.drawable.download),
                    contentDescription = "Simpan",
                    modifier = Modifier.size(19.dp),
                    tint = Neutral400
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // ── Meta: durasi + kategori ───────────────────────────────────
            Row(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = video.durationOrTime,
                    style = TextStyle(
                        fontSize = 12.sp,
                        fontFamily = PlusJakartaSans,
                        fontWeight = FontWeight.Medium,
                        color = Neutral900,
                        letterSpacing = 0.06.sp
                    )
                )
                if (video.category.isNotBlank()) {
                    Text("•", style = TextStyle(fontSize = 12.sp, color = Neutral400))
                    Text(
                        text = video.category,
                        style = TextStyle(
                            fontSize = 12.sp,
                            fontFamily = PlusJakartaSans,
                            fontWeight = FontWeight.Medium,
                            color = Neutral400,
                            letterSpacing = 0.06.sp
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // ── Action Row (Like / Dislike / Share / Download) ────────────
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(21.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                VideoActionButton(
                    iconRes = R.drawable.like,
                    label = likeCount.toString(),
                    tint = if (isLiked) Green500 else Neutral400,
                    onClick = {
                        isLiked = !isLiked
                        likeCount += if (isLiked) 1 else -1
                    }
                )
                VideoActionButton(
                    iconRes = R.drawable.dislike,
                    label = "0",
                    tint = Neutral400,
                    onClick = {}
                )
                VideoActionButton(
                    iconRes = R.drawable.share,
                    label = "Bagikan",
                    tint = Neutral400,
                    onClick = {}
                )
                VideoActionButton(
                    iconRes = R.drawable.download,
                    label = "Unduh",
                    tint = Neutral400,
                    onClick = {}
                )
            }

            Spacer(modifier = Modifier.height(16.dp))
            HorizontalDivider(color = Neutral200)
            Spacer(modifier = Modifier.height(16.dp))

            // ── Author — selalu "Terrace Official" (tidak ada field authorName di model) ──
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                AsyncImage(
                    model = currentUser?.profileImageUrl,
                    contentDescription = "Avatar",
                    contentScale = ContentScale.FillBounds,
                    modifier = Modifier
                        .size(38.dp)
                        .clip(RoundedCornerShape(9999.dp)),
                    error = painterResource(id = R.drawable.fototanaman),
                    placeholder = painterResource(id = R.drawable.fototanaman)
                )
                Text(
                    text = "Terrace Official",
                    style = TextStyle(
                        fontSize = 16.sp,
                        fontFamily = PlusJakartaSans,
                        fontWeight = FontWeight.Medium,
                        color = Neutral900,
                        letterSpacing = 0.08.sp
                    )
                )
                Icon(
                    painter = painterResource(id = R.drawable.badge_check),
                    contentDescription = "Verified",
                    tint = Green500,
                    modifier = Modifier.size(16.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // ── Description ───────────────────────────────────────────────
            if (video.description.isNotBlank()) {
                Text(
                    text = video.description,
                    style = TextStyle(
                        fontSize = 14.sp,
                        fontFamily = PlusJakartaSans,
                        fontWeight = FontWeight.Normal,
                        color = Neutral800,
                        letterSpacing = 0.07.sp,
                        lineHeight = 22.sp
                    )
                )
                Spacer(modifier = Modifier.height(16.dp))
            }



            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Neutral100, RoundedCornerShape(5.dp))
                    .padding(10.dp)
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Komentar",
                            style = TextStyle(
                                fontSize = 16.sp,
                                fontFamily = PlusJakartaSans,
                                fontWeight = FontWeight.SemiBold,
                                color = Neutral900,
                                letterSpacing = 0.08.sp
                            )
                        )
                        Text(
                            text = "0",
                            style = TextStyle(
                                fontSize = 12.sp,
                                fontFamily = PlusJakartaSans,
                                fontWeight = FontWeight.Medium,
                                color = Neutral900,
                                letterSpacing = 0.06.sp
                            )
                        )
                    }
                    Text(
                        text = "Belum ada komentar. Jadilah yang pertama!",
                        style = TextStyle(
                            fontSize = 12.sp,
                            fontFamily = PlusJakartaSans,
                            fontWeight = FontWeight.Normal,
                            color = Neutral400,
                            letterSpacing = 0.06.sp
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // ── Direkomendasikan untuk Anda ───────────────────────────────
            Text(
                text = "Direkomendasikan untuk anda",
                style = TextStyle(
                    fontSize = 14.sp,
                    fontFamily = PlusJakartaSans,
                    fontWeight = FontWeight.SemiBold,
                    color = Neutral900
                )
            )

            Spacer(modifier = Modifier.height(12.dp))

            LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                items(recommendations) { (item, type) ->
                    Box(modifier = Modifier.width(176.dp)) {
                        EducationCard(
                            item = item,
                            type = type,
                            userPoints = currentUser?.currentPoint ?: 0,
                            onClick = { onRecommendationClick(item, type) }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(80.dp))
        }
    }
}

// ---------------------------------------------------------------------------
// Local action button (like / dislike / share / download)
// ---------------------------------------------------------------------------
@Composable
private fun VideoActionButton(
    iconRes: Int,
    label: String,
    tint: Color,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp),
        modifier = Modifier.clickable { onClick() }
    ) {
        Icon(
            painter = painterResource(id = iconRes),
            contentDescription = label,
            tint = tint,
            modifier = Modifier.size(24.dp)
        )
        Text(
            text = label,
            style = TextStyle(
                fontSize = 10.sp,
                fontFamily = PlusJakartaSans,
                fontWeight = FontWeight.Normal,
                color = Neutral900,
                textAlign = TextAlign.Center,
                letterSpacing = 0.05.sp
            )
        )
    }
}