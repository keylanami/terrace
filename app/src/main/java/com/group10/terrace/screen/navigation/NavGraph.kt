package com.group10.terrace.screen.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.group10.terrace.screen.SplashScreen
import com.group10.terrace.screen.auth.LoginScreen
import com.group10.terrace.screen.auth.SignUpScreen
import com.group10.terrace.screen.education.ArticleDetailScreen
import com.group10.terrace.screen.education.EducationScreen
import com.group10.terrace.screen.education.VideoDetailScreen
import com.group10.terrace.screen.market.CheckoutScreen
import com.group10.terrace.screen.market.PilihAlamatScreen
import com.group10.terrace.screen.marketplace.MarketplaceScreen
import com.group10.terrace.screen.plant.PlantProgressScreen
import com.group10.terrace.screen.profile.AlamatPenerimaScreen
import com.group10.terrace.screen.profile.PesananScreen
import com.group10.terrace.screen.profile.ProfileScreen
import com.group10.terrace.screen.profile.SettingsScreen
import com.group10.terrace.screen.HomeScreen
import com.group10.terrace.screen.onboarding.PersonalizationScreen
import com.group10.terrace.ui.screen.addplant.PlantDetailScreen
import com.group10.terrace.viewmodel.AcademyViewModel
import com.group10.terrace.viewmodel.AuthViewModel
import com.group10.terrace.viewmodel.HomeViewModel
import com.group10.terrace.viewmodel.MarketplaceViewModel

// ── Route constants ───────────────────────────────────────────────────────────
object Routes {
    const val SPLASH          = "splash"
    const val LOGIN           = "login"
    const val SIGNUP          = "signup"
    const val PERSONALIZATION = "personalization"

    const val HOME            = "home"
    const val PLANT_PROGRESS  = "plant_progress"
    const val PLANT_DETAIL    = "plant_detail/{userPlantId}"
    const val MARKETPLACE     = "marketplace"
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

    fun plantDetail(userPlantId: String) = "plant_detail/$userPlantId"
}

// ── Shared ViewModels holder — buat di MainActivity lalu pass ke sini ─────────
// Semua shared di sini supaya state tidak reset saat navigate back

@Composable
fun TerracNavGraph(
    navController: NavHostController,
    authViewModel: AuthViewModel,
    homeViewModel: HomeViewModel,
    marketplaceViewModel: MarketplaceViewModel,
    academyViewModel: AcademyViewModel
) {
    // ── Shared selected education item (passed across video/article detail) ──
    // Karena NavGraph Compose tidak bisa pass complex object via NavArg langsung
    // tanpa serialization, kita simpan di ViewModel scope sebagai state sementara.
    // Alternatif: pakai SharedViewModel atau SavedStateHandle.
    // Untuk MVP, kita pakai remember di NavGraph level via ViewModels.

    val userData by authViewModel.userData.collectAsState()

    NavHost(
        navController = navController,
        startDestination = Routes.SPLASH
    ) {

        // ── SPLASH ────────────────────────────────────────────────────────────
        composable(Routes.SPLASH) {
            SplashScreen(
                onNavigateToLogin = {
                    navController.navigate(Routes.LOGIN) {
                        popUpTo(Routes.SPLASH) { inclusive = true }
                    }
                },
                onNavigateToHome = {
                    navController.navigate(Routes.HOME) {
                        popUpTo(Routes.SPLASH) { inclusive = true }
                    }
                }
            )
        }

        // ── AUTH ──────────────────────────────────────────────────────────────
        composable(Routes.LOGIN) {
            LoginScreen(
                viewModel = authViewModel,
                onNavigateToSignUp = { navController.navigate(Routes.SIGNUP) },
                onLoginSuccess = {
                    navController.navigate(Routes.HOME) {
                        popUpTo(Routes.LOGIN) { inclusive = true }
                    }
                }
            )
        }

        composable(Routes.SIGNUP) {
            SignUpScreen(
                viewModel = authViewModel,
                onNavigateToLogin = { navController.popBackStack() },
                onSignUpSuccess = {
                    // Setelah register → Personalization (bukan Home langsung)
                    navController.navigate(Routes.PERSONALIZATION) {
                        popUpTo(Routes.SIGNUP) { inclusive = true }
                    }
                }
            )
        }

        // ── ONBOARDING / PERSONALIZATION ──────────────────────────────────────
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

        // ── MAIN NAV ──────────────────────────────────────────────────────────
        composable(Routes.HOME) {
            HomeScreen(
                viewModel = homeViewModel,
                onNavigateToAddPlant = { navController.navigate(Routes.PLANT_PROGRESS) },
                onNavigateToNav = { route ->
                    navController.navigate(route) {
                        popUpTo(Routes.HOME) { saveState = true }
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            )
        }

        // ── PLANT ─────────────────────────────────────────────────────────────
        composable(Routes.PLANT_PROGRESS) {
            PlantProgressScreen(
                viewModel = homeViewModel,
                onNavigate = { route ->
                    navController.navigate(route) {
                        popUpTo(Routes.HOME) { saveState = true }
                        launchSingleTop = true
                        restoreState = true
                    }
                },
                onPlantClick = { userPlantId ->
                    navController.navigate(Routes.plantDetail(userPlantId))
                },
                onAddPlant = {
                    // TODO: navigate ke layar pilih tanaman
                    // navController.navigate(Routes.SELECT_PLANT)
                }
            )
        }

        composable(
            route = Routes.PLANT_DETAIL,
            arguments = listOf(navArgument("userPlantId") { type = NavType.StringType })
        ) { backStackEntry ->
            val userPlantId = backStackEntry.arguments?.getString("userPlantId") ?: return@composable
            PlantDetailScreen(
                viewModel = homeViewModel,
                userPlantId = userPlantId,
                onBack = { navController.popBackStack() }
            )
        }

        // ── MARKETPLACE ───────────────────────────────────────────────────────
        composable(Routes.MARKETPLACE) {
            val userId = userData?.uid ?: ""
            // Load cart saat masuk marketplace
            LaunchedEffect(userId) {
                if (userId.isNotBlank()) {
                    marketplaceViewModel.loadCart(userId)
                    marketplaceViewModel.loadUserPoints(userId)
                }
            }
            MarketplaceScreen(
                marketplaceViewModel = marketplaceViewModel,
                authViewModel = authViewModel,
                onNavigateToDetail = { /* TODO: Product detail */ },
                onNavigateToCart = { navController.navigate(Routes.CHECKOUT) },
                onNavigateToNav = { route ->
                    navController.navigate(route) {
                        popUpTo(Routes.HOME) { saveState = true }
                        launchSingleTop = true
                        restoreState = true
                    }
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
                onNavigate = { route ->
                    navController.navigate(route) {
                        popUpTo(Routes.HOME) { saveState = true }
                        launchSingleTop = true
                        restoreState = true
                    }
                },
                onPilihAlamat = { navController.navigate(Routes.PILIH_ALAMAT) },
                onCheckoutSuccess = {
                    navController.navigate(Routes.HOME) {
                        popUpTo(Routes.CHECKOUT) { inclusive = true }
                    }
                }
            )
        }

        composable(Routes.PILIH_ALAMAT) {
            val userId = userData?.uid ?: ""
            PilihAlamatScreen(
                viewModel = marketplaceViewModel,
                userId = userId,
                onBack = { navController.popBackStack() },
                onNavigate = { route ->
                    navController.navigate(route) {
                        popUpTo(Routes.HOME) { saveState = true }
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            )
        }

        // ── ACADEMY / EDUCATION ───────────────────────────────────────────────
        composable(Routes.ACADEMY) {
            EducationScreen(
                viewModel = academyViewModel,
                onItemClick = { item, type ->
                    // Simpan item ke AcademyViewModel sebagai selected item
                    academyViewModel.setSelectedItem(item, type)
                    if (type == "Video") {
                        navController.navigate(Routes.VIDEO_DETAIL)
                    } else {
                        navController.navigate(Routes.ARTICLE_DETAIL)
                    }
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
                        if (type == "Video") {
                            navController.navigate(Routes.VIDEO_DETAIL) {
                                launchSingleTop = true
                            }
                        } else {
                            navController.navigate(Routes.ARTICLE_DETAIL)
                        }
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

        // ── PROFILE ───────────────────────────────────────────────────────────
        composable(Routes.PROFILE) {
            ProfileScreen(
                viewModel = homeViewModel,
                onNavigate = { route ->
                    navController.navigate(route) {
                        popUpTo(Routes.HOME) { saveState = true }
                        launchSingleTop = true
                        restoreState = true
                    }
                },
                onSettingsClick = { navController.navigate(Routes.SETTINGS) }
            )
        }

        composable(Routes.SETTINGS) {
            SettingsScreen(
                viewModel = homeViewModel,
                onBack = { navController.popBackStack() },
                onEditProfile = { /* TODO: EditProfileScreen */ },
                onPesananSaya = { navController.navigate(Routes.PESANAN) },
                onAlamatPenerima = { navController.navigate(Routes.ALAMAT_PENERIMA) },
                onLogoutConfirmed = {
                    authViewModel.logout()
                    navController.navigate(Routes.LOGIN) {
                        popUpTo(0) { inclusive = true }
                    }
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