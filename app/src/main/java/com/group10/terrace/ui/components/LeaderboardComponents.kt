package com.group10.terrace.ui.components

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.group10.terrace.R
import com.group10.terrace.model.User
import com.group10.terrace.ui.theme.*

@Composable
fun ExpandableLeaderboard(
    currentUser: User?,
    leaderboardData: List<User>
) {
    var isExpanded by remember { mutableStateOf(false) }

    val users = if (leaderboardData.isNotEmpty()) leaderboardData else listOf(
        User(name = "User One", totalPoints = 3500),
        User(name = currentUser?.name ?: "User (You)", totalPoints = currentUser?.totalPoints ?: 3000),
        User(name = "User Three", totalPoints = 2800),
        User(name = "User Four", totalPoints = 2500),
        User(name = "User Five", totalPoints = 2000)
    )

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(brush = SecondaryGradient)
            .animateContentSize(animationSpec = tween(500))
            .padding(bottom = 16.dp)
    ) {

        Row(
            modifier = Modifier.padding(start = 24.dp, top = 20.dp, bottom = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Image(painter = painterResource(id = R.drawable.crown), contentDescription = "Crown", modifier = Modifier.size(24.dp))
            Text("Leaderboard", style = Typography.titleLarge.copy(fontWeight = FontWeight.Bold, fontSize = 20.sp), color = Yellow900)
        }

        val itemsToShow = if (isExpanded) users.take(5) else users.take(2)

        itemsToShow.forEachIndexed { index, user ->
            val isMe = user.name == (currentUser?.name ?: "User (You)")
            val rank = index + 1

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(if (isMe) Yellow900.copy(alpha = 0.8f) else Color.Transparent)
                    .padding(horizontal = 24.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "#$rank",
                    style = Typography.titleLarge.copy(fontWeight = FontWeight.Bold, fontSize = 24.sp),
                    color = if (isMe) Neutral50 else Yellow900,
                    modifier = Modifier.width(38.dp),
                    textAlign = TextAlign.End
                )
                Spacer(modifier = Modifier.width(16.dp))

                Box(
                    modifier = Modifier.size(36.dp).background(color = if(isMe) Neutral50 else Yellow200, shape = RoundedCornerShape(100.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Image(painter = painterResource(id = R.drawable.profile_coklat), contentDescription = null, modifier = Modifier.size(16.dp))
                }

                Spacer(modifier = Modifier.width(12.dp))

                Text(
                    text = user.name,
                    style = Typography.titleMedium.copy(fontWeight = FontWeight.SemiBold, fontSize = 16.sp),
                    color = if (isMe) Neutral50 else Yellow900,
                    modifier = Modifier.weight(1f)
                )

                Text(
                    text = "${user.totalPoints} EXP",
                    style = Typography.labelMedium.copy(fontWeight = FontWeight.Medium, fontSize = 12.sp),
                    color = if (isMe) Neutral50 else Yellow900
                )
            }
        }

        if (!isExpanded) {
            Box(
                modifier = Modifier
                    .padding(top = 16.dp)
                    .align(Alignment.CenterHorizontally)
                    .clip(RoundedCornerShape(5.dp))
                    .background(Yellow200)
                    .clickable { isExpanded = true }
                    .padding(horizontal = 16.dp, vertical = 6.dp)
            ) {
                Text("See full leaderboard", style = Typography.labelMedium.copy(fontWeight = FontWeight.SemiBold, fontSize = 12.sp), color = Yellow900)
            }
        }
    }
}