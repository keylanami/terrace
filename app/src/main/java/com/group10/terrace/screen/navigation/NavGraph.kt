package com.group10.terrace.screen.navigation

import androidx.activity.compose.BackHandler
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.*
import androidx.navigation.*
import androidx.navigation.compose.*
import com.group10.terrace.screen.HomeScreen
import com.group10.terrace.screen.SplashScreen
import com.group10.terrace.screen.auth.LoginScreen
import com.group10.terrace.screen.auth.SignUpScreen
import com.group10.terrace.screen.education.ArticleDetailScreen
import com.group10.terrace.screen.education.EducationScreen
import com.group10.terrace.screen.education.VideoDetailScreen
import com.group10.terrace.screen.marketplace.CheckoutScreen
import com.group10.terrace.screen.marketplace.PilihAlamatScreen
import com.group10.terrace.screen.marketplace.CartScreen
import com.group10.terrace.screen.marketplace.MarketplaceScreen
import com.group10.terrace.screen.marketplace.ProductDetailScreen
import com.group10.terrace.screen.onboarding.PersonalizationScreen
import com.group10.terrace.screen.profile.AlamatPenerimaScreen
import com.group10.terrace.screen.profile.PesananScreen
import com.group10.terrace.screen.profile.ProfileScreen
import com.group10.terrace.screen.profile.SettingsScreen
import com.group10.terrace.ui.screen.addplant.PlantDetailScreen
import com.group10.terrace.ui.screen.catalog.CatalogScreen
import com.group10.terrace.ui.screen.plant.PlantControlScreen
import com.group10.terrace.ui.theme.Green600
import com.group10.terrace.ui.theme.Red600
import com.group10.terrace.viewmodel.*

object Routes {
    const val SPLASH          = "splash"
    const val LOGIN           = "login"
    const val SIGNUP          = "signup"
    const val PERSONALIZATION = "personalization"
    const val HOME            = "home"
    const val PLANT_CONTROL   = "plant_control"
    const val PLANT_DETAIL    = "plant_detail/{plantId}"
    const val CATALOG         = "catalog"
    const val MARKETPLACE     = "marketplace"
    const val PRODUCT_DETAIL  = "product_detail/{productId}"
    const val CART            = "cart"
    const val CHECKOUT        = "checkout"
    const val PILIH_ALAMAT    = "pilih_alamat"
    const val ACADEMY         = "academy"
    const val VIDEO_DETAIL    = "video_detail"
    const val ARTICLE_DETAIL  = "article_detail"
    const val PROFILE         = "profile"
    const val SETTINGS        = "settings"
    const val PESANAN         = "pesanan"
    const val ALAMAT_PENERIMA = "alamat_penerima"

    fun plantDetail(plantId: String)    = "plant_detail/$plantId"
    fun productDetail(productId: String) = "product_detail/$productId"
}

@Composable
fun TerracNavGraph(
    navController: NavHostController,
    authViewModel: AuthViewModel,
    homeViewModel: HomeViewModel,
    marketplaceViewModel: MarketplaceViewModel,
    academyViewModel: AcademyViewModel,
    missionViewModel: MissionViewModel,
    leaderboardViewModel: LeaderboardViewModel
) {
    val userData by authViewModel.userData.collectAsState()
    val masterPlants by homeViewModel.masterPlants.collectAsState()

    fun NavOptionsBuilder.bottomNav() {
        popUpTo(Routes.HOME) { saveState = true }
        launchSingleTop = true
        restoreState = true
    }

    fun navigateBottomNav(route: String) {
        when (route) {
            "home"    -> navController.navigate(Routes.HOME) { bottomNav() }
            "plant"   -> navController.navigate(Routes.PLANT_CONTROL) { bottomNav() }
            "market"  -> navController.navigate(Routes.MARKETPLACE) { bottomNav() }
            "academy" -> navController.navigate(Routes.ACADEMY) { bottomNav() }
            "profile" -> navController.navigate(Routes.PROFILE) { bottomNav() }
        }
    }

    NavHost(navController = navController, startDestination = Routes.SPLASH) {

        composable(Routes.SPLASH) {
            SplashScreen(
                onNavigateToLogin = {
                    navController.navigate(Routes.LOGIN) { popUpTo(Routes.SPLASH) { inclusive = true } }
                },
                onNavigateToHome = {
                    navController.navigate(Routes.HOME) { popUpTo(Routes.SPLASH) { inclusive = true } }
                }
            )
        }

        composable(Routes.LOGIN) {
            LoginScreen(
                viewModel = authViewModel,
                onNavigateToSignUp = { navController.navigate(Routes.SIGNUP) },
                onLoginSuccess = {
                    navController.navigate(Routes.HOME) { popUpTo(Routes.LOGIN) { inclusive = true } }
                }
            )
        }

        composable(Routes.SIGNUP) {
            SignUpScreen(
                viewModel = authViewModel,
                onNavigateToLogin = { navController.popBackStack() },
                onSignUpSuccess = {
                    navController.navigate(Routes.PERSONALIZATION) { popUpTo(Routes.SIGNUP) { inclusive = true } }
                }
            )
        }

        composable(Routes.PERSONALIZATION) {
            PersonalizationScreen(
                onFinishPersonalization = { landSize, location, experience ->

                    val uid = userData?.uid
                    if (uid != null) {
                        authViewModel.updatePersonalizationData(uid, landSize, location, experience) { success ->
                            if (success) {
                                homeViewModel.loadDashboardData()
                                navController.navigate(Routes.HOME) {
                                    popUpTo(Routes.PERSONALIZATION) { inclusive = true }
                                }
                            }
                        }
                    }
                }
            )
        }

        composable(Routes.HOME) {
            HomeScreen(
                viewModel = homeViewModel,
                onNavigateToAddPlant = { navController.navigate(Routes.CATALOG) },
                onNavigateToPlantDetail = { plantId ->
                    navController.navigate(Routes.plantDetail(plantId))
                },
                onNavigateToProfile = { navController.navigate(Routes.PROFILE) },
                onNavigateToNav = { route -> navigateBottomNav(route) }
            )
        }

        composable(Routes.PLANT_CONTROL) {
            PlantControlScreen(
                viewModel = homeViewModel,
                onNavigateBack = { navController.popBackStack() },
                onNavigateToAddPlant = { navController.navigate(Routes.CATALOG) },
                onNavigateToDetail = { userPlantId ->
                    val activePlant = homeViewModel.activePlants.value.find { it.userPlantId == userPlantId }
                    val plantId = activePlant?.plantId ?: userPlantId
                    navController.navigate(Routes.plantDetail(plantId))
                },
                onNavigateToNav = { route -> navigateBottomNav(route) }
            )
        }

        composable(
            route = Routes.PLANT_DETAIL,
            arguments = listOf(navArgument("plantId") { type = NavType.StringType })
        ) { backStack ->
            val plantId = backStack.arguments?.getString("plantId")
            if (plantId != null) {
                PlantDetailScreen(
                    viewModel = homeViewModel,
                    missionViewModel = missionViewModel,
                    plantId = plantId,
                    onBack = { navController.popBackStack() },
                    onAddPlant = {
                        val plant = masterPlants.find { it.id == plantId }
                        if (plant != null) {
                            homeViewModel.addNewPlant(plant)
                            navController.navigate(Routes.PLANT_CONTROL) {
                                popUpTo(Routes.CATALOG) { inclusive = true }
                            }
                        }
                    }
                )
            }
        }

        composable(Routes.CATALOG) {
            CatalogScreen(
                viewModel = homeViewModel,
                onNavigateToDetail = { plantId ->
                    navController.navigate(Routes.plantDetail(plantId))
                },
                onNavigateToNav = { route -> navigateBottomNav(route) }
            )
        }

        composable(Routes.MARKETPLACE) {
            val userId = userData?.uid ?: ""
            LaunchedEffect(userId) {
                if (userId.isNotBlank()) {
                    marketplaceViewModel.loadCart(userId)
                    marketplaceViewModel.loadUserPoints(userId)
                }
            }
            MarketplaceScreen(
                marketplaceViewModel = marketplaceViewModel,
                authViewModel = authViewModel,
                onNavigateToDetail = { productId -> navController.navigate(Routes.productDetail(productId)) },
                onNavigateToCart = { navController.navigate(Routes.CART) },
                onNavigateToNav = { route -> navigateBottomNav(route) }
            )
        }

        composable(
            route = Routes.PRODUCT_DETAIL,
            arguments = listOf(navArgument("productId") { type = NavType.StringType })
        ) { backStack ->
            val productId = backStack.arguments?.getString("productId")
            if (productId != null) {
                ProductDetailScreen(
                    viewModel = marketplaceViewModel,
                    productId = productId,
                    userId = userData?.uid ?: "",
                    onBack = { navController.popBackStack() },
                    onNavigateToCart = { navController.navigate(Routes.CART) }
                )
            }
        }

        composable(Routes.CART) {
            val userId = userData?.uid ?: ""
            CartScreen(
                viewModel = marketplaceViewModel,
                userId = userId,
                onBack = { navController.popBackStack() },
                onNavigate = { route -> navigateBottomNav(route) },
                onNavigateToCheckout = {
                    marketplaceViewModel.loadUserAddresses(userId)
                    marketplaceViewModel.loadUserPoints(userId)
                    navController.navigate(Routes.CHECKOUT)
                }
            )
        }

        composable(Routes.CHECKOUT) {
            val userId = userData?.uid ?: ""
            var showCancelDialog by remember { mutableStateOf(false) }

            LaunchedEffect(userId) {
                if (userId.isNotBlank()) {
                    marketplaceViewModel.loadUserAddresses(userId)
                    marketplaceViewModel.loadUserPoints(userId)
                }
            }

            BackHandler { showCancelDialog = true }

            if (showCancelDialog) {
                AlertDialog(
                    onDismissRequest = { showCancelDialog = false },
                    title = { Text("Batalkan Checkout?") },
                    text = { Text("Pesanan belum selesai. Yakin ingin keluar?") },
                    confirmButton = {
                        TextButton(onClick = {
                            showCancelDialog = false
                            navController.popBackStack()
                        }) { Text("Ya, Keluar", color = Red600) }
                    },
                    dismissButton = {
                        TextButton(onClick = { showCancelDialog = false }) {
                            Text("Lanjut Bayar", color = Green600)
                        }
                    }
                )
            }

            CheckoutScreen(
                viewModel = marketplaceViewModel,
                userId = userId,
                onBack = { showCancelDialog = true },
                onNavigate = { route -> navigateBottomNav(route) },
                onPilihAlamat = { navController.navigate(Routes.PILIH_ALAMAT) },
                onCheckoutSuccess = {
                    navController.navigate(Routes.HOME) { popUpTo(Routes.CHECKOUT) { inclusive = true } }
                }
            )
        }

        composable(Routes.PILIH_ALAMAT) {
            val userId = userData?.uid ?: ""
            PilihAlamatScreen(
                viewModel = marketplaceViewModel,
                userId = userId,
                onBack = { navController.popBackStack() },
                onNavigate = { route -> navigateBottomNav(route) }
            )
        }

        composable(Routes.ACADEMY) {
            EducationScreen(
                viewModel = academyViewModel,
                onItemClick = { item, type ->
                    academyViewModel.setSelectedItem(item, type)
                    if (type == "Video") navController.navigate(Routes.VIDEO_DETAIL)
                    else navController.navigate(Routes.ARTICLE_DETAIL)
                },
                onNavigateToNav = { route -> navigateBottomNav(route) }
            )
        }

        composable(Routes.VIDEO_DETAIL) {
            val selectedVideo by academyViewModel.selectedVideo.collectAsState()
            selectedVideo?.let { video ->
                VideoDetailScreen(
                    viewModel = academyViewModel,
                    video = video,
                    onBack = { navController.popBackStack() },
                    onRecommendationClick = { item, type ->
                        academyViewModel.setSelectedItem(item, type)
                        if (type == "Video") navController.navigate(Routes.VIDEO_DETAIL) { launchSingleTop = true }
                        else navController.navigate(Routes.ARTICLE_DETAIL)
                    }
                )
            }
        }

        composable(Routes.ARTICLE_DETAIL) {
            val selectedArticle by academyViewModel.selectedArticle.collectAsState()
            selectedArticle?.let { article ->
                ArticleDetailScreen(
                    article = article,
                    onBack = { navController.popBackStack() }
                )
            }
        }

        composable(Routes.PROFILE) {
            ProfileScreen(
                viewModel = homeViewModel,
                leaderboardViewModel = leaderboardViewModel,
                onNavigate = { route -> navigateBottomNav(route) },
                onBack = { navController.popBackStack() },
                onSettingsClick = { navController.navigate(Routes.SETTINGS) }
            )
        }

        composable(Routes.SETTINGS) {
            SettingsScreen(
                viewModel = homeViewModel,
                onBack = { navController.popBackStack() },
                onEditProfile = { },
                onPesananSaya = { navController.navigate(Routes.PESANAN) },
                onAlamatPenerima = { navController.navigate(Routes.ALAMAT_PENERIMA) },
                onLogoutConfirmed = {
                    authViewModel.logout()
                    navController.navigate(Routes.LOGIN) { popUpTo(0) { inclusive = true } }
                }
            )
        }

        composable(Routes.PESANAN) {
            PesananScreen(
                viewModel = marketplaceViewModel,
                userId = userData?.uid ?: "",
                onBack = { navController.popBackStack() }
            )
        }

        composable(Routes.ALAMAT_PENERIMA) {
            AlamatPenerimaScreen(
                viewModel = marketplaceViewModel,
                homeViewModel = homeViewModel,
                userId = userData?.uid ?: "",
                onBack = { navController.popBackStack() }
            )
        }
    }
}