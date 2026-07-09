package com.spraxe.support.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Data classes matching the shared Supabase schema used by spraxe-web and spraxeapp.
 * This staff app (admin + moderator) reads and writes across nearly all of these tables,
 * so fields are kept close to the database column names via @SerialName.
 */

@Serializable
data class Profile(
    val id: String,
    @SerialName("full_name") val fullName: String? = null,
    val email: String? = null,
    val phone: String? = null,
    val address: String? = null,
    @SerialName("company_name") val companyName: String? = null,
    val role: String? = "customer",
    @SerialName("created_at") val createdAt: String? = null
) {
    val isStaff: Boolean get() = role == "admin" || role == "moderator"
    val displayName: String get() = fullName?.takeIf { it.isNotBlank() } ?: email ?: phone ?: id
}

@Serializable
data class Category(
    val id: String,
    val name: String,
    val description: String? = null,
    @SerialName("image_url") val imageUrl: String? = null,
    @SerialName("parent_id") val parentId: String? = null,
    @SerialName("sort_order") val sortOrder: Int? = 0,
    @SerialName("is_active") val isActive: Boolean = true
)

@Serializable
data class Product(
    val id: String,
    val name: String,
    val slug: String? = null,
    val description: String? = null,
    val price: Double? = null,
    @SerialName("base_price") val basePrice: Double? = null,
    val images: List<String>? = null,
    @SerialName("category_id") val categoryId: String? = null,
    @SerialName("stock_quantity") val stockQuantity: Int? = null,
    @SerialName("is_active") val isActive: Boolean = true,
    @SerialName("is_featured") val isFeatured: Boolean = false,
    @SerialName("total_sales") val totalSales: Int? = null,
    @SerialName("created_at") val createdAt: String? = null
) {
    val displayPrice: Double get() = price ?: basePrice ?: 0.0
    val thumbnailUrl: String? get() = images?.firstOrNull()
}

@Serializable
data class OrderRow(
    val id: String,
    @SerialName("order_number") val orderNumber: String? = null,
    @SerialName("user_id") val userId: String? = null,
    val total: Double = 0.0,
    val subtotal: Double? = null,
    val status: String = "pending",
    @SerialName("payment_status") val paymentStatus: String? = null,
    @SerialName("payment_method") val paymentMethod: String? = null,
    @SerialName("payment_transaction_id") val paymentTransactionId: String? = null,
    @SerialName("shipping_address") val shippingAddress: String? = null,
    @SerialName("delivery_location") val deliveryLocation: String? = null,
    @SerialName("shipping_cost") val shippingCost: Double? = null,
    @SerialName("contact_number") val contactNumber: String? = null,
    @SerialName("created_at") val createdAt: String? = null,
    val profiles: Profile? = null
)

@Serializable
data class OrderItemRow(
    val id: String? = null,
    @SerialName("order_id") val orderId: String,
    @SerialName("product_id") val productId: String? = null,
    @SerialName("product_name") val productName: String,
    @SerialName("product_sku") val productSku: String? = null,
    val quantity: Int,
    @SerialName("unit_price") val unitPrice: Double,
    @SerialName("total_price") val totalPrice: Double
)

@Serializable
data class FeaturedImage(
    val id: Int? = null,
    val title: String? = null,
    val description: String? = null,
    @SerialName("image_url") val imageUrl: String,
    @SerialName("sort_order") val sortOrder: Int = 0,
    @SerialName("is_active") val isActive: Boolean = true
)

@Serializable
data class FeatureCard(
    val id: Int? = null,
    val title: String,
    val description: String,
    val icon: String = "Sparkles",
    @SerialName("image_url") val imageUrl: String? = null,
    @SerialName("sort_order") val sortOrder: Int = 0,
    @SerialName("is_active") val isActive: Boolean = true
)

/** A support ticket -- the parent "conversation" a live chat thread hangs off of. */
@Serializable
data class SupportTicket(
    val id: String,
    @SerialName("ticket_number") val ticketNumber: String? = null,
    @SerialName("user_id") val userId: String? = null,
    val type: String = "inquiry",
    val subject: String,
    val message: String,
    val status: String = "open",
    val priority: String = "medium",
    @SerialName("order_id") val orderId: String? = null,
    @SerialName("assigned_to") val assignedTo: String? = null,
    @SerialName("created_at") val createdAt: String? = null,
    @SerialName("updated_at") val updatedAt: String? = null,
    val profiles: Profile? = null
)

/** A single live-chat message inside a support ticket's conversation thread. */
@Serializable
data class SupportMessage(
    val id: String? = null,
    @SerialName("ticket_id") val ticketId: String,
    @SerialName("sender_id") val senderId: String? = null,
    @SerialName("sender_role") val senderRole: String = "customer",
    val message: String,
    @SerialName("is_read") val isRead: Boolean = false,
    @SerialName("created_at") val createdAt: String? = null
)

@Serializable
data class DiscountCode(
    val id: String? = null,
    val code: String,
    @SerialName("discount_type") val discountType: String = "percentage",
    @SerialName("discount_value") val discountValue: Double,
    @SerialName("min_purchase") val minPurchase: Double? = 0.0,
    @SerialName("max_uses") val maxUses: Int? = null,
    @SerialName("current_uses") val currentUses: Int? = 0,
    @SerialName("valid_from") val validFrom: String? = null,
    @SerialName("valid_until") val validUntil: String? = null,
    @SerialName("is_active") val isActive: Boolean = true
)

@Serializable
data class SellerApplication(
    val id: String,
    @SerialName("user_id") val userId: String? = null,
    @SerialName("shop_name") val shopName: String,
    @SerialName("shop_description") val shopDescription: String? = null,
    @SerialName("business_address") val businessAddress: String,
    val phone: String,
    val email: String,
    val status: String = "pending",
    @SerialName("rejection_reason") val rejectionReason: String? = null,
    @SerialName("created_at") val createdAt: String? = null
)

@Serializable
data class SiteSetting(
    val id: String? = null,
    val key: String,
    val value: kotlinx.serialization.json.JsonElement,
    @SerialName("updated_at") val updatedAt: String? = null
)

@Serializable
data class Invoice(
    val id: String,
    @SerialName("order_id") val orderId: String? = null,
    @SerialName("invoice_number") val invoiceNumber: String? = null,
    val total: Double? = null,
    val status: String? = null,
    @SerialName("created_at") val createdAt: String? = null
)

data class DashboardStats(
    val products: Int = 0,
    val orders: Int = 0,
    val customers: Int = 0,
    val pendingOrders: Int = 0,
    val openTickets: Int = 0,
    val inProgressTickets: Int = 0,
    val pendingSellerApps: Int = 0
)
