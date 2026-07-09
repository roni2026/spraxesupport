package com.spraxe.support.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.ChatBubble
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.Discount
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Storefront
import androidx.compose.ui.graphics.vector.ImageVector

sealed class Destination(val route: String) {
    data object Splash : Destination("splash")
    data object Login : Destination("login")

    data object Dashboard : Destination("dashboard")
    data object Orders : Destination("orders")
    data object OrderDetail : Destination("orders/{orderId}") {
        fun build(orderId: String) = "orders/$orderId"
    }
    data object Products : Destination("products")
    data object ProductEdit : Destination("products/edit?productId={productId}") {
        fun build(productId: String? = null) = if (productId == null) "products/edit" else "products/edit?productId=$productId"
    }
    data object Categories : Destination("categories")
    data object Customers : Destination("customers")
    data object CustomerDetail : Destination("customers/{customerId}") {
        fun build(customerId: String) = "customers/$customerId"
    }
    data object ChatList : Destination("chat")
    data object ChatThread : Destination("chat/{ticketId}") {
        fun build(ticketId: String) = "chat/$ticketId"
    }
    data object FeaturedImages : Destination("content/featured")
    data object FeatureCards : Destination("content/cards")
    data object DiscountCodes : Destination("discounts")
    data object SellerApplications : Destination("sellers")
    data object SiteSettings : Destination("settings")
    data object Invoices : Destination("invoices")
    data object Profile : Destination("profile")
}

/** Bottom bar: the 4 highest-frequency staff tasks. Everything else lives in the drawer. */
enum class BottomTab(val route: String, val label: String, val icon: ImageVector) {
    DASHBOARD(Destination.Dashboard.route, "Dashboard", Icons.Filled.Dashboard),
    ORDERS(Destination.Orders.route, "Orders", Icons.Filled.ShoppingBag),
    CHAT(Destination.ChatList.route, "Live Chat", Icons.Filled.ChatBubble),
    PROFILE(Destination.Profile.route, "Profile", Icons.Filled.Person)
}

data class DrawerItem(val route: String, val label: String, val icon: ImageVector)

/** Full admin menu, shown in the navigation drawer -- this is how "manage everything" is reached. */
val drawerItems = listOf(
    DrawerItem(Destination.Dashboard.route, "Dashboard", Icons.Filled.Dashboard),
    DrawerItem(Destination.Orders.route, "Orders", Icons.Filled.ShoppingBag),
    DrawerItem(Destination.ChatList.route, "Live Chat & Support", Icons.Filled.ChatBubble),
    DrawerItem(Destination.Products.route, "Products", Icons.Filled.Inventory2),
    DrawerItem(Destination.Categories.route, "Categories", Icons.Filled.Category),
    DrawerItem(Destination.Customers.route, "Customers", Icons.Filled.People),
    DrawerItem(Destination.FeaturedImages.route, "Hero Banners", Icons.Filled.Image),
    DrawerItem(Destination.FeatureCards.route, "Feature Cards", Icons.Filled.Star),
    DrawerItem(Destination.DiscountCodes.route, "Discount Codes", Icons.Filled.Discount),
    DrawerItem(Destination.SellerApplications.route, "Seller Applications", Icons.Filled.Storefront),
    DrawerItem(Destination.Invoices.route, "Invoices", Icons.Filled.Receipt),
    DrawerItem(Destination.SiteSettings.route, "Site Settings", Icons.Filled.Settings)
)
