package com.group10.terrace.screen.navigation

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
import com.group10.terrace.screen.market.CheckoutScreen
import com.group10.terrace.screen.market.PilihAlamatScreen
import com.group10.terrace.screen.marketplace.CartScreen
import com.group10.terrace.screen.marketplace.MarketplaceScreen
import com.group10.terrace.screen.marketplace.ProductDetailScreen
import com.group10.terrace.screen.onboarding.PersonalizationScreen
import com.group10.terrace.screen.plant.PlantProgressScreen
import com.group10.terrace.screen.profile.AlamatPenerimaScreen
import com.group10.terrace.screen.profile.PesananScreen
import com.group10.terrace.screen.profile.ProfileScreen
import com.group10.terrace.screen.profile.SettingsScreen
import com.group10.terrace.ui.screen.addplant.PlantDetailScreen
import com.group10.terrace.ui.screen.catalog.CatalogScreen
import com.group10.terrace.viewmodel.*

object Routes {
    const val SPLASH             = "splash"
    const val LOGIN              = "login"
    const val SIGNUP             = "signup"
    const val PERSONALIZATION    = "personalization"
    const val HOME               = "home"
    const val PLANT_PROGRESS     = "plant_progress"
    const val PLANT_DETAIL       = "plant_detail/{userPlantId}"
    const val CATALOG            = "catalog"
    const val MARKETPLACE        = "marketplace"
    const val PRODUCT_DETAIL     = "product_detail/{productId}"
    const val CART               = "cart"
    const val CHECKOUT           = "checkout"
    const val PILIH_ALAMAT       = "pilih_alamat"
    const val ACADEMY            = "academy"
    const val VIDEO_DETAIL       = "video_detail"
    const val ARTICLE_DETAIL     = "article_detail"
    const val PROFILE            = "profile"
    const val SETTINGS           = "settings"
    const val PESANAN            = "pesanan"
    const val ALAMAT_PENERIMA    = "alamat_penerima"

    fun plantDetail(userPlantId: String) = "plant_detail/$userPlantId"
    fun productDetail(productId: String) = "product_detail/$productId"
}

@Composable
fun TerracNavGraph(
    navController: NavHostController,
    authViewModel: AuthViewModel,
    homeViewModel: HomeViewModel,
    marketplaceViewModel: MarketplaceViewModel,
    academyViewModel: AcademyViewModel,
    missionViewModel: MissionViewModel
) {
    val userData by authViewModel.userData.collectAsState()

    fun NavOptionsBuilder.bottomNav() {
        popUpTo(Routes.HOME) { saveState = true }
        launchSingleTop = true
        restoreState = true
    }

    NavHost(navController = navController, startDestination = Routes.SPLASH) {

        // ── SPLASH ────────────────────────────────────────────────────────
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

        // ── AUTH ──────────────────────────────────────────────────────────
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

        // ── PERSONALIZATION ───────────────────────────────────────────────
        composable(Routes.PERSONALIZATION) {
            PersonalizationScreen(
                onFinishPersonalization = { landSize, location, experience ->
                    val uid = userData?.uid ?: return@PersonalizationScreen
                    authViewModel.updatePersonalizationData(uid, landSize, location, experience) { success ->
                        if (success) {
                            homeViewModel.loadDashboardData()
                            navController.navigate(Routes.HOME) {
                                popUpTo(Routes.PERSONALIZATION) { inclusive = true }
                            }
                        }
                    }
                }
            )
        }

        // ── HOME ──────────────────────────────────────────────────────────
        composable(Routes.HOME) {
            // HomeScreen yang ada di codebase mu (versi original tanpa missionViewModel)
            // Kalau ingin wire gamification, gunakan HomeScreen dari output gw sebelumnya
            HomeScreen(
                viewModel = homeViewModel,
                onNavigateToAddPlant = { navController.navigate(Routes.CATALOG) },
                onNavigateToNav = { route -> navController.navigate(route) { bottomNav() } }
            )
        }

        // ── PLANT ─────────────────────────────────────────────────────────
        composable(Routes.PLANT_PROGRESS) {
            PlantProgressScreen(
                viewModel = homeViewModel,
                onNavigate = { route -> navController.navigate(route) { bottomNav() } },
                onPlantClick = { userPlantId -> navController.navigate(Routes.plantDetail(userPlantId)) },
                onAddPlant = { navController.navigate(Routes.CATALOG) }
            )
        }

        composable(
            route = Routes.PLANT_DETAIL,
            arguments = listOf(navArgument("userPlantId") { type = NavType.StringType })
        ) { backStack ->
            val userPlantId = backStack.arguments?.getString("userPlantId") ?: return@composable
            PlantDetailScreen(
                viewModel = homeViewModel,
                userPlantId = userPlantId,
                onBack = { navController.popBackStack() }
            )
        }

        composable(Routes.CATALOG) {
            CatalogScreen(
                viewModel = homeViewModel,
                onNavigateToDetail = { plantId ->
                    // Tambah tanaman langsung dari catalog
                    val plant = homeViewModel.masterPlants.value.find { it.id == plantId }
                    if (plant != null) {
                        homeViewModel.addNewPlant(plant)
                        navController.navigate(Routes.PLANT_PROGRESS) {
                            popUpTo(Routes.CATALOG) { inclusive = true }
                        }
                    }
                },
                onNavigateToNav = { route -> navController.navigate(route) { bottomNav() } }
            )
        }

        // ── MARKETPLACE ───────────────────────────────────────────────────
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
                onNavigateToNav = { route -> navController.navigate(route) { bottomNav() } }
            )
        }

        composable(
            route = Routes.PRODUCT_DETAIL,
            arguments = listOf(navArgument("productId") { type = NavType.StringType })
        ) { backStack ->
            val productId = backStack.arguments?.getString("productId") ?: return@composable
            val userId = userData?.uid ?: ""
            ProductDetailScreen(
                viewModel = marketplaceViewModel,
                productId = productId,
                userId = userId,
                onBack = { navController.popBackStack() }
            )
        }

        composable(Routes.CART) {
            val userId = userData?.uid ?: ""
            CartScreen(
                viewModel = marketplaceViewModel,
                userId = userId,
                onBack = { navController.popBackStack() },
                onNavigateToCheckout = {
                    marketplaceViewModel.loadUserAddresses(userId)
                    marketplaceViewModel.loadUserPoints(userId)
                    navController.navigate(Routes.CHECKOUT)
                }
            )
        }

        composable(Routes.CHECKOUT) {
            val userId = userData?.uid ?: ""
            LaunchedEffect(userId) {
                if (userId.isNotBlank()) {
                    marketplaceViewModel.loadUserAddresses(userId)
                    marketplaceViewModel.loadUserPoints(userId)
                }
            }
            CheckoutScreen(
                viewModel = marketplaceViewModel,
                userId = userId,
                onBack = { navController.popBackStack() },
                onNavigate = { route -> navController.navigate(route) { bottomNav() } },
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
                onNavigate = { route -> navController.navigate(route) { bottomNav() } }
            )
        }

        // ── ACADEMY ───────────────────────────────────────────────────────
        composable(Routes.ACADEMY) {
            EducationScreen(
                viewModel = academyViewModel,
                onItemClick = { item, type ->
                    academyViewModel.setSelectedItem(item, type)
                    if (type == "Video") navController.navigate(Routes.VIDEO_DETAIL)
                    else navController.navigate(Routes.ARTICLE_DETAIL)
                }
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

        // ── PROFILE ───────────────────────────────────────────────────────
        composable(Routes.PROFILE) {
            ProfileScreen(
                viewModel = homeViewModel,
                onNavigate = { route -> navController.navigate(route) { bottomNav() } },
                onSettingsClick = { navController.navigate(Routes.SETTINGS) }
            )
        }

        composable(Routes.SETTINGS) {
            SettingsScreen(
                viewModel = homeViewModel,
                onBack = { navController.popBackStack() },
                onEditProfile = { /* TODO */ },
                onPesananSaya = { navController.navigate(Routes.PESANAN) },
                onAlamatPenerima = { navController.navigate(Routes.ALAMAT_PENERIMA) },
                onLogoutConfirmed = {
                    authViewModel.logout()
                    navController.navigate(Routes.LOGIN) { popUpTo(0) { inclusive = true } }
                }
            )
        }

        composable(Routes.PESANAN) {
            val userId = userData?.uid ?: ""
            PesananScreen(
                viewModel = marketplaceViewModel,
                userId = userId,
                onBack = { navController.popBackStack() }
            )
        }

        composable(Routes.ALAMAT_PENERIMA) {
            val userId = userData?.uid ?: ""
            AlamatPenerimaScreen(
                viewModel = marketplaceViewModel,
                homeViewModel = homeViewModel,
                userId = userId,
                onBack = { navController.popBackStack() }
            )
        }
    }
}