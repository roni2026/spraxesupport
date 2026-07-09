package com.spraxe.support.data.repository

import com.spraxe.support.data.model.OrderItemRow
import com.spraxe.support.data.model.OrderRow
import com.spraxe.support.data.remote.SupabaseClientProvider
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.query.Order
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.buildJsonObject

/** Admin/moderator order management: browse, filter, and update status/payment on any order. */
class OrderRepository {
    private val postgrest get() = SupabaseClientProvider.postgrest

    suspend fun getOrders(statusFilter: String? = null, query: String? = null): List<OrderRow> =
        postgrest.from("orders").select {
            order("created_at", Order.DESCENDING)
            filter {
                if (!statusFilter.isNullOrBlank() && statusFilter != "all") eq("status", statusFilter)
                if (!query.isNullOrBlank()) ilike("order_number", "%$query%")
            }
        }.decodeList()

    suspend fun getOrder(orderId: String): OrderRow? =
        postgrest.from("orders").select {
            filter { eq("id", orderId) }
        }.decodeSingleOrNull()

    suspend fun getOrderItems(orderId: String): List<OrderItemRow> =
        postgrest.from("order_items").select {
            filter { eq("order_id", orderId) }
        }.decodeList()

    suspend fun updateStatus(orderId: String, status: String) {
        postgrest.from("orders").update(
            buildJsonObject { put("status", JsonPrimitive(status)) }
        ) {
            filter { eq("id", orderId) }
        }
    }

    suspend fun updatePaymentStatus(orderId: String, paymentStatus: String) {
        postgrest.from("orders").update(
            buildJsonObject { put("payment_status", JsonPrimitive(paymentStatus)) }
        ) {
            filter { eq("id", orderId) }
        }
    }
}
