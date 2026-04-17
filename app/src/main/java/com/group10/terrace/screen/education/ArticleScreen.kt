package com.group10.terrace.ui.screen.education

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.group10.terrace.R
import com.group10.terrace.model.EducationItem
import com.group10.terrace.ui.theme.*

@Composable
fun ArticleDetailScreen(
    // Artikel dikirim via NavArg / parameter dari EducationScreen
    article: EducationItem,
    onBack: () -> Unit
) {
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
                text = "Artikel",
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

        Column(modifier = Modifier.padding(horizontal = 20.dp)) {

            // ── Article Title ─────────────────────────────────────────────
            Text(
                text = article.title,
                style = TextStyle(
                    fontSize = 24.sp,
                    fontFamily = PlusJakartaSans,
                    fontWeight = FontWeight.Bold,
                    color = Neutral900,
                    letterSpacing = 0.12.sp
                )
            )

            Spacer(modifier = Modifier.height(6.dp))

            // ── Author — tidak ada field authorName di model, statis "Terrace Official" ──
            Text(
                text = "by Terrace Official",
                style = TextStyle(
                    fontSize = 14.sp,
                    fontFamily = PlusJakartaSans,
                    fontWeight = FontWeight.Medium,
                    color = Neutral900,
                    letterSpacing = 0.07.sp
                )
            )

            // ── Category + Read Time ──────────────────────────────────────
            if (article.category.isNotBlank() || article.durationOrTime.isNotBlank()) {
                Spacer(modifier = Modifier.height(6.dp))
                Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (article.category.isNotBlank()) {
                        Box(
                            modifier = Modifier
                                .background(Green100, RoundedCornerShape(6.dp))
                                .padding(horizontal = 8.dp, vertical = 3.dp)
                        ) {
                            Text(
                                text = article.category,
                                style = TextStyle(
                                    fontSize = 10.sp,
                                    fontFamily = PlusJakartaSans,
                                    fontWeight = FontWeight.SemiBold,
                                    color = Green700
                                )
                            )
                        }
                    }
                    if (article.durationOrTime.isNotBlank()) {
                        Text(
                            text = article.durationOrTime,
                            style = TextStyle(
                                fontSize = 12.sp,
                                fontFamily = PlusJakartaSans,
                                fontWeight = FontWeight.Normal,
                                color = Neutral400
                            )
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // ── Cover Image ───────────────────────────────────────────────
            AsyncImage(
                model = article.imageUrl,
                contentDescription = article.title,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .clip(RoundedCornerShape(10.dp)),
                error = painterResource(id = R.drawable.fototanaman),
                placeholder = painterResource(id = R.drawable.fototanaman)
            )

            Spacer(modifier = Modifier.height(20.dp))

            // ── Article Body ──────────────────────────────────────────────
            // Render content dari field article.content
            // Kata pertama dari setiap paragraf di-bold sesuai desain Figma
            if (!article.content.isNullOrBlank()) {
                ArticleBodyText(content = article.content)
            } else {
                Text(
                    text = article.description,
                    style = TextStyle(
                        fontSize = 14.sp,
                        fontFamily = PlusJakartaSans,
                        fontWeight = FontWeight.Normal,
                        color = Neutral900,
                        letterSpacing = 0.07.sp,
                        lineHeight = 22.sp
                    )
                )
            }

            Spacer(modifier = Modifier.height(80.dp))
        }
    }
}

// ---------------------------------------------------------------------------
// Render content string — kata pertama tiap paragraf di-bold (sesuai desain)
// ---------------------------------------------------------------------------
@Composable
private fun ArticleBodyText(content: String) {
    val bodyStyle = TextStyle(
        fontSize = 14.sp,
        fontFamily = PlusJakartaSans,
        fontWeight = FontWeight.Normal,
        color = Neutral900,
        letterSpacing = 0.07.sp,
        lineHeight = 22.sp
    )

    // Pisah per paragraf berdasarkan "\n\n" atau "\n"
    val paragraphs = content
        .split("\n\n")
        .map { it.trim() }
        .filter { it.isNotBlank() }

    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        paragraphs.forEach { paragraph ->
            // Deteksi section header: dimulai dengan angka + titik (contoh: "1.", "2.")
            val isSectionHeader = paragraph.matches(Regex("^\\d+\\..*"))

            if (isSectionHeader) {
                Text(
                    text = paragraph,
                    style = bodyStyle.copy(fontWeight = FontWeight.Bold)
                )
            } else {
                // Bold kata pertama dari paragraf biasa
                val firstSpaceIndex = paragraph.indexOf(' ')
                val annotated = buildAnnotatedString {
                    if (firstSpaceIndex != -1) {
                        withStyle(SpanStyle(fontWeight = FontWeight.Bold)) {
                            append(paragraph.substring(0, firstSpaceIndex))
                        }
                        append(paragraph.substring(firstSpaceIndex))
                    } else {
                        withStyle(SpanStyle(fontWeight = FontWeight.Bold)) {
                            append(paragraph)
                        }
                    }
                }
                Text(text = annotated, style = bodyStyle)
            }
        }
    }
}