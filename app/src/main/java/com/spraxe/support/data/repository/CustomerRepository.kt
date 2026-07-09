package com.spraxe.support.data.repository

import com.spraxe.support.data.model.OrderRow
import com.spraxe.support.data.model.Profile
import com.spraxe.support.data.remote.SupabaseClientProvider
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.query.Order

/** Read-mostly view of customers for staff: search, view profile + order history. */
class CustomerRepository {
    private val postgrest get() = SupabaseClientProvider.postgrest

    suspend fun getCustomers(query: String? = null): List<Profile> =
        postgrest.from("profiles").select {
            order("created_at", Order.DESCENDING)
            filter {
                eq("role", "customer")
                if (!query.isNullOrBlank()) {
                    or {
                        ilike("full_name", "%$query%")
                        ilike("email", "%$query%")
                        ilike("phone", "%$query%")
                    }
                }
            }
        }.decodeList()

    suspend fun getCustomer(id: String): Profile? =
        postgrest.from("profiles").select {
            filter { eq("id", id) }
        }.decodeSingleOrNull()

    suspend fun getCustomerOrders(userId: String): List<OrderRow> =
        postgrest.from("orders").select {
            filter { eq("user_id", userId) }
            order("created_at", Order.DESCENDING)
        }.decodeList()

    /** Staff view of the whole team: admins + moderators (for a future "manage staff" screen). */
    suspend fun getStaff(): List<Profile> =
        postgrest.from("profiles").select {
            filter { or { eq("role", "admin"); eq("role", "moderator") } }
        }.decodeList()
}
