package com.spraxe.support.data.repository

import com.spraxe.support.data.model.DiscountCode
import com.spraxe.support.data.remote.SupabaseClientProvider
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.query.Order
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.buildJsonObject

class DiscountRepository {
    private val postgrest get() = SupabaseClientProvider.postgrest

    suspend fun getCodes(): List<DiscountCode> =
        postgrest.from("discount_codes").select {
            order("created_at", Order.DESCENDING)
        }.decodeList()

    suspend fun createCode(
        code: String,
        discountType: String,
        discountValue: Double,
        minPurchase: Double,
        maxUses: Int?,
        validUntil: String?,
        isActive: Boolean
    ): DiscountCode = postgrest.from("discount_codes").insert(
        buildJsonObject {
            put("code", JsonPrimitive(code.uppercase()))
            put("discount_type", JsonPrimitive(discountType))
            put("discount_value", JsonPrimitive(discountValue))
            put("min_purchase", JsonPrimitive(minPurchase))
            if (maxUses != null) put("max_uses", JsonPrimitive(maxUses))
            if (validUntil != null) put("valid_until", JsonPrimitive(validUntil))
            put("is_active", JsonPrimitive(isActive))
        }
    ) { select() }.decodeSingle()

    suspend fun setActive(id: String, isActive: Boolean) {
        postgrest.from("discount_codes").update(
            buildJsonObject { put("is_active", JsonPrimitive(isActive)) }
        ) { filter { eq("id", id) } }
    }

    suspend fun deleteCode(id: String) {
        postgrest.from("discount_codes").delete { filter { eq("id", id) } }
    }
}
