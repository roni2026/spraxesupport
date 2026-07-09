package com.spraxe.support

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.spraxe.support.data.model.Profile
import com.spraxe.support.ui.navigation.Destination
import com.spraxe.support.ui.navigation.SupportAppScaffold
import com.spraxe.support.ui.screens.auth.AuthUiState
import com.spraxe.support.ui.screens.auth.AuthViewModel
import com.spraxe.support.ui.screens.auth.LoginScreen
import com.spraxe.support.ui.screens.categories.CategoriesScreen
import com.spraxe.support.ui.screens.chat.ChatListScreen
import com.spraxe.support.ui.screens.chat.ChatThreadScreen
import com.spraxe.support.ui.screens.content.FeatureCardsScreen
import com.spraxe.support.ui.screens.content.FeaturedImagesScreen
import com.spraxe.support.ui.screens.customers.CustomerDetailScreen
import com.spraxe.support.ui.screens.customers.CustomersScreen
import com.spraxe.support.ui.screens.dashboard.DashboardScreen
import com.spraxe.support.ui.screens.discounts.DiscountCodesScreen
import com.spraxe.support.ui.screens.invoices.InvoicesScreen
import com.spraxe.support.ui.screens.orders.OrderDetailScreen
import com.spraxe.support.ui.screens.orders.OrdersScreen
import com.spraxe.support.ui.screens.products.ProductEditScreen
import com.spraxe.support.ui.screens.products.ProductsScreen
import com.spraxe.support.ui.screens.profile.ProfileScreen
import com.spraxe.support.ui.screens.sellers.SellerApplicationsScreen
import com.spraxe.support.ui.screens.settings.SiteSettingsScreen
import com.spraxe.support.ui.screens.splash.SplashScreen
import com.spraxe.support.ui.theme.SpraxeSupportTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SpraxeSupportTheme {
                SpraxeSupportApp()
            }
        }
    }
}

@Composable
private fun SpraxeSupportApp() {
    val authViewModel: AuthViewModel = androidx.lifecycle.viewmodel.compose.viewModel()

    LaunchedEffect(Unit) { authViewModel.restoreSession() }

    Box(modifier = Modifier.fillMaxSize()) {
        when (val state = authViewModel.uiState) {
            is AuthUiState.CheckingSession -> SplashScreen()
            is AuthUiState.SignedOut -> LoginScreen(viewModel = authViewModel)
            is AuthUiState.SignedIn -> StaffShell(profile = state.profile, onSignOut = { authViewModel.signOut() })
        }
    }
}

@Composable
private fun StaffShell(profile: Profile, onSignOut: () -> Unit) {
    val navController = rememberNavController()
    val roleLabel = if (profile.role == "admin") "Administrator" else "Moderator"

    SupportAppScaffold(navController = navController, roleLabel = roleLabel, onSignOut = onSignOut) { padding ->
        NavHost(
            navController = navController,
            startDestination = Destination.Dashboard.route,
            modifier = padding
        ) {
            composable(Destination.Dashboard.route) {
                DashboardScreen(onOpenOrder = { orderId -> navController.navigate(Destination.OrderDetail.build(orderId)) })
            }
            composable(Destination.Orders.route) {
                OrdersScreen(onOpenOrder = { orderId -> navController.navigate(Destination.OrderDetail.build(orderId)) })
            }
            composable(
                Destination.OrderDetail.route,
                arguments = listOf(navArgument("orderId") { type = NavType.StringType })
            ) { backStackEntry ->
                val orderId = backStackEntry.arguments?.getString("orderId").orEmpty()
                OrderDetailScreen(orderId = orderId)
            }
            composable(Destination.Products.route) {
                ProductsScreen(onEditProduct = { productId -> navController.navigate(Destination.ProductEdit.build(productId)) })
            }
            composable(
                Destination.ProductEdit.route,
                arguments = listOf(navArgument("productId") { type = NavType.StringType; nullable = true; defaultValue = null })
            ) { backStackEntry ->
                val productId = backStackEntry.arguments?.getString("productId")
                ProductEditScreen(productId = productId, onSaved = { navController.popBackStack() })
            }
            composable(Destination.Categories.route) { CategoriesScreen() }
            composable(Destination.Customers.route) {
                CustomersScreen(onOpenCustomer = { customerId -> navController.navigate(Destination.CustomerDetail.build(customerId)) })
            }
            composable(
                Destination.CustomerDetail.route,
                arguments = listOf(navArgument("customerId") { type = NavType.StringType })
            ) { backStackEntry ->
                val customerId = backStackEntry.arguments?.getString("customerId").orEmpty()
                CustomerDetailScreen(customerId = customerId)
            }
            composable(Destination.ChatList.route) {
                ChatListScreen(onOpenTicket = { ticketId -> navController.navigate(Destination.ChatThread.build(ticketId)) })
            }
            composable(
                Destination.ChatThread.route,
                arguments = listOf(navArgument("ticketId") { type = NavType.StringType })
            ) { backStackEntry ->
                val ticketId = backStackEntry.arguments?.getString("ticketId").orEmpty()
                ChatThreadScreen(ticketId = ticketId)
            }
            composable(Destination.FeaturedImages.route) { FeaturedImagesScreen() }
            composable(Destination.FeatureCards.route) { FeatureCardsScreen() }
            composable(Destination.DiscountCodes.route) { DiscountCodesScreen() }
            composable(Destination.SellerApplications.route) { SellerApplicationsScreen() }
            composable(Destination.Invoices.route) { InvoicesScreen() }
            composable(Destination.SiteSettings.route) { SiteSettingsScreen() }
            composable(Destination.Profile.route) { ProfileScreen(profile = profile, onSignOut = onSignOut) }
        }
    }
}
