package com.group10.terrace

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.group10.terrace.screen.navigation.TerracNavGraph
import com.group10.terrace.ui.theme.Neutral50
import com.group10.terrace.ui.theme.TerraceTheme
import com.group10.terrace.viewmodel.AcademyViewModel
import com.group10.terrace.viewmodel.AuthViewModel
import com.group10.terrace.viewmodel.HomeViewModel
import com.group10.terrace.viewmodel.LeaderboardViewModel
import com.group10.terrace.viewmodel.MarketplaceViewModel
import com.group10.terrace.viewmodel.MissionViewModel

class MainActivity : ComponentActivity() {

    private val authViewModel: AuthViewModel by viewModels()
    private val homeViewModel: HomeViewModel by viewModels()
    private val marketplaceViewModel: MarketplaceViewModel by viewModels()
    private val academyViewModel: AcademyViewModel by viewModels()
    private val missionViewModel: MissionViewModel by viewModels()
    private val leaderboardViewModel: LeaderboardViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            TerraceTheme {
                Surface(modifier = Modifier.fillMaxSize(), color = Neutral50) {
                    TerracNavGraph(
                        navController = rememberNavController(),
                        authViewModel = authViewModel,
                        homeViewModel = homeViewModel,
                        marketplaceViewModel = marketplaceViewModel,
                        academyViewModel = academyViewModel,
                        missionViewModel = missionViewModel,
                        leaderboardViewModel = leaderboardViewModel
                    )
                }
            }
        }
    }
}