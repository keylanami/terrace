package com.group10.terrace.screen.navigation

import androidx.compose.runtime.*
import androidx.navigation.*
import androidx.navigation.compose.*
import kotlinx.serialization.Serializable
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

@Serializable object Splash
@Serializable object Login
@Serializable object SignUp
@Serializable object Personalization
@Serializable object Home
@Serializable object PlantProgress
@Serializable object Catalog
@Serializable object Marketplace
@Serializable object Cart
@Serializable object Checkout
@Serializable object PilihAlamat
@Serializable object Academy
@Serializable object VideoDetail
@Serializable object ArticleDetail
@Serializable object Profile
@Serializable object Settings
@Serializable object Pesanan
@Serializable object AlamatPenerima
@Serializable data class PlantDetail(val userPlantId: String)
@Serializable data class ProductDetail(val productId: String)

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
        popUpTo<Home> { saveState = true }
        launchSingleTop = true
        restoreState = true
    }

    // Helper: map string route dari BottomNavBar ke typed route
    fun navigateBottomNav(route: String) {
        when (route) {
            "home"    -> navController.navigate(Home) { bottomNav() }
            "plant"   -> navController.navigate(PlantProgress) { bottomNav() }
            "market"  -> navController.navigate(Marketplace) { bottomNav() }
            "academy" -> navController.navigate(Academy) { bottomNav() }
            "profile" -> navController.navigate(Profile) { bottomNav() }
        }
    }

    NavHost(navController = navController, startDestination = Splash) {

        composable<Splash> {
            SplashScreen(
                onNavigateToLogin = { navController.navigate(Login) { popUpTo<Splash> { inclusive = true } } },
                onNavigateToHome = { navController.navigate(Home) { popUpTo<Splash> { inclusive = true } } }
            )
        }

        composable<Login> {
            LoginScreen(
                viewModel = authViewModel,
                onNavigateToSignUp = { navController.navigate(SignUp) },
                onLoginSuccess = { navController.navigate(Home) { popUpTo<Login> { inclusive = true } } }
            )
        }

        composable<SignUp> {
            SignUpScreen(
                viewModel = authViewModel,
                onNavigateToLogin = { navController.popBackStack() },
                onSignUpSuccess = { navController.navigate(Personalization) { popUpTo<SignUp> { inclusive = true } } }
            )
        }

        composable<Personalization> {
            PersonalizationScreen(
                onFinishPersonalization = { landSize, location, experience ->
                    val uid = userData?.uid ?: return@PersonalizationScreen
                    authViewModel.updatePersonalizationData(uid, landSize, location, experience) { success ->
                        if (success) {
                            homeViewModel.loadDashboardData()
                            navController.navigate(Home) { popUpTo<Personalization> { inclusive = true } }
                        }
                    }
                }
            )
        }

        composable<Home> {
            HomeScreen(
                viewModel = homeViewModel,
                onNavigateToAddPlant = { navController.navigate(Catalog) },
                // FIX: wire kedua parameter baru
                onNavigateToPlantDetail = { userPlantId -> navController.navigate(PlantDetail(userPlantId)) },
                onNavigateToProfile = { navController.navigate(Profile) },
                onNavigateToNav = { route -> navigateBottomNav(route) }
            )
        }

        composable<PlantProgress> {
            PlantProgressScreen(
                viewModel = homeViewModel,
                onNavigate = { route -> navigateBottomNav(route) },
                onPlantClick = { userPlantId -> navController.navigate(PlantDetail(userPlantId)) },
                onAddPlant = { navController.navigate(Catalog) }
            )
        }

        composable<PlantDetail> { backStack ->
            val route = backStack.toRoute<PlantDetail>()
            // FIX: pass missionViewModel
            PlantDetailScreen(
                viewModel = homeViewModel,
                missionViewModel = missionViewModel,
                userPlantId = route.userPlantId,
                onBack = { navController.popBackStack() }
            )
        }

        composable<Catalog> {
            CatalogScreen(
                viewModel = homeViewModel,
                onNavigateToDetail = { plantId ->
                    val plant = homeViewModel.masterPlants.value.find { it.id == plantId }
                    if (plant != null) {
                        homeViewModel.addNewPlant(plant)
                        navController.navigate(PlantProgress) { popUpTo<Catalog> { inclusive = true } }
                    }
                },
                onNavigateToNav = { route -> navigateBottomNav(route) }
            )
        }

        composable<Marketplace> {
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
                onNavigateToDetail = { productId -> navController.navigate(ProductDetail(productId)) },
                onNavigateToCart = { navController.navigate(Cart) },
                onNavigateToNav = { route -> navigateBottomNav(route) }
            )
        }

        composable<ProductDetail> { backStack ->
            val route = backStack.toRoute<ProductDetail>()
            ProductDetailScreen(
                viewModel = marketplaceViewModel,
                productId = route.productId,
                userId = userData?.uid ?: "",
                onBack = { navController.popBackStack() }
            )
        }

        composable<Cart> {
            val userId = userData?.uid ?: ""
            CartScreen(
                viewModel = marketplaceViewModel,
                userId = userId,
                onBack = { navController.popBackStack() },
                onNavigateToCheckout = {
                    marketplaceViewModel.loadUserAddresses(userId)
                    marketplaceViewModel.loadUserPoints(userId)
                    navController.navigate(Checkout)
                }
            )
        }

        composable<Checkout> {
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
                onNavigate = { route -> navigateBottomNav(route) },
                onPilihAlamat = { navController.navigate(PilihAlamat) },
                onCheckoutSuccess = { navController.navigate(Home) { popUpTo<Checkout> { inclusive = true } } }
            )
        }

        composable<PilihAlamat> {
            val userId = userData?.uid ?: ""
            PilihAlamatScreen(
                viewModel = marketplaceViewModel,
                userId = userId,
                onBack = { navController.popBackStack() },
                onNavigate = { route -> navigateBottomNav(route) }
            )
        }

        composable<Academy> {
            EducationScreen(
                viewModel = academyViewModel,
                onItemClick = { item, type ->
                    academyViewModel.setSelectedItem(item, type)
                    if (type == "Video") navController.navigate(VideoDetail)
                    else navController.navigate(ArticleDetail)
                }
            )
        }

        composable<VideoDetail> {
            val selectedVideo by academyViewModel.selectedVideo.collectAsState()
            selectedVideo?.let { video ->
                VideoDetailScreen(
                    viewModel = academyViewModel,
                    video = video,
                    onBack = { navController.popBackStack() },
                    onRecommendationClick = { item, type ->
                        academyViewModel.setSelectedItem(item, type)
                        if (type == "Video") navController.navigate(VideoDetail) { launchSingleTop = true }
                        else navController.navigate(ArticleDetail)
                    }
                )
            }
        }

        composable<ArticleDetail> {
            val selectedArticle by academyViewModel.selectedArticle.collectAsState()
            selectedArticle?.let { article ->
                ArticleDetailScreen(article = article, onBack = { navController.popBackStack() })
            }
        }

        composable<Profile> {
            ProfileScreen(
                viewModel = homeViewModel,
                onNavigate = { route -> navigateBottomNav(route) },
                onSettingsClick = { navController.navigate(Settings) }
            )
        }

        composable<Settings> {
            SettingsScreen(
                viewModel = homeViewModel,
                onBack = { navController.popBackStack() },
                onEditProfile = { /* TODO */ },
                onPesananSaya = { navController.navigate(Pesanan) },
                onAlamatPenerima = { navController.navigate(AlamatPenerima) },
                onLogoutConfirmed = {
                    authViewModel.logout()
                    navController.navigate(Login) { popUpTo(0) { inclusive = true } }
                }
            )
        }

        composable<Pesanan> {
            PesananScreen(
                viewModel = marketplaceViewModel,
                userId = userData?.uid ?: "",
                onBack = { navController.popBackStack() }
            )
        }

        composable<AlamatPenerima> {
            AlamatPenerimaScreen(
                viewModel = marketplaceViewModel,
                homeViewModel = homeViewModel,
                userId = userData?.uid ?: "",
                onBack = { navController.popBackStack() }
            )
        }
    }
}