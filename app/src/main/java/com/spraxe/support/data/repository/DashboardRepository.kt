package com.spraxe.support.data.repository

import com.spraxe.support.data.model.DashboardStats
import com.spraxe.support.data.model.OrderRow
import com.spraxe.support.data.remote.SupabaseClientProvider
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.query.Count
import io.github.jan.supabase.postgrest.query.Order

/** Aggregate counts + recent activity for the Dashboard home screen. */
class DashboardRepository {
    private val postgrest get() = SupabaseClientProvider.postgrest

    private suspend fun count(table: String, build: io.github.jan.supabase.postgrest.query.PostgrestFilterBuilder.() -> Unit = {}): Int {
        return postgrest.from(table).select {
            count(Count.EXACT)
            filter(build)
            limit(0)
        }.countOrNull()?.toInt() ?: 0
    }

    suspend fun getStats(): DashboardStats {
        val products = count("products")
        val orders = count("orders")
        val customers = count("profiles") { eq("role", "customer") }
        val pendingOrders = count("orders") { eq("status", "pending") }
        val openTickets = count("support_tickets") { eq("status", "open") }
        val inProgressTickets = count("support_tickets") { eq("status", "in_progress") }
        val pendingSellerApps = count("seller_applications") { eq("status", "pending") }

        return DashboardStats(
            products = products,
            orders = orders,
            customers = customers,
            pendingOrders = pendingOrders,
            openTickets = openTickets,
            inProgressTickets = inProgressTickets,
            pendingSellerApps = pendingSellerApps
        )
    }

    suspend fun getRecentOrders(limitCount: Long = 10): List<OrderRow> =
        postgrest.from("orders").select {
            order("created_at", Order.DESCENDING)
            limit(limitCount)
        }.decodeList()
}
