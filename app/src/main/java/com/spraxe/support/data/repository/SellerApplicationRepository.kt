package com.spraxe.support.data.repository

import com.spraxe.support.data.model.SellerApplication
import com.spraxe.support.data.remote.SupabaseClientProvider
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.query.Order
import kotlinx.serialization.json.JsonNull
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.buildJsonObject

class SellerApplicationRepository {
    private val postgrest get() = SupabaseClientProvider.postgrest

    suspend fun getApplications(statusFilter: String? = null): List<SellerApplication> =
        postgrest.from("seller_applications").select {
            order("created_at", Order.DESCENDING)
            filter {
                if (!statusFilter.isNullOrBlank() && statusFilter != "all") eq("status", statusFilter)
            }
        }.decodeList()

    suspend fun approve(id: String) {
        postgrest.from("seller_applications").update(
            buildJsonObject {
                put("status", JsonPrimitive("approved"))
                put("rejection_reason", JsonNull)
            }
        ) { filter { eq("id", id) } }
    }

    suspend fun reject(id: String, reason: String) {
        postgrest.from("seller_applications").update(
            buildJsonObject {
                put("status", JsonPrimitive("rejected"))
                put("rejection_reason", JsonPrimitive(reason))
            }
        ) { filter { eq("id", id) } }
    }
}
