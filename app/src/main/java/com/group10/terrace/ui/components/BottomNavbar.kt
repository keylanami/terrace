package com.group10.terrace.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.group10.terrace.R
import com.group10.terrace.ui.theme.Neutral50

@Composable
fun BottomNavBar(
    currentRoute: String = "home",
    onNavigate: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(79.dp)
            .shadow(
                elevation = 20.dp,
                shape = RoundedCornerShape(topStart = 10.dp, topEnd = 10.dp)
            )
            .background(
                color = Neutral50,
                shape = RoundedCornerShape(topStart = 10.dp, topEnd = 10.dp)
            )
            .padding(horizontal = 32.dp, vertical = 20.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            painter = painterResource(id = if (currentRoute == "home") R.drawable.dashboard_on else R.drawable.dashboard_off),
            contentDescription = "Home",
            modifier = Modifier.size(28.dp).clickable { onNavigate("home") }
        )
        Image(
            painter = painterResource(id = if (currentRoute == "plant") R.drawable.plant_on else R.drawable.plant_off),
            contentDescription = "Pantau Tanaman",
            modifier = Modifier.size(28.dp).clickable { onNavigate("plant") }
        )
        Image(
            painter = painterResource(id = if (currentRoute == "market") R.drawable.marketplace_on else R.drawable.marketplace_off),
            contentDescription = "Marketplace",
            modifier = Modifier.size(28.dp).clickable { onNavigate("market") }
        )
        Image(
            painter = painterResource(id = if (currentRoute == "academy") R.drawable.profile_on else R.drawable.profile_off),
            contentDescription = "Academy",
            modifier = Modifier.size(28.dp).clickable { onNavigate("academy") }
        )
    }
}