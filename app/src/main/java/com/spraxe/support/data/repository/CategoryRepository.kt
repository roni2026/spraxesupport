package com.spraxe.support.data.repository

import com.spraxe.support.data.model.Category
import com.spraxe.support.data.remote.SupabaseClientProvider
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.query.Order
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.buildJsonObject

class CategoryRepository {
    private val postgrest get() = SupabaseClientProvider.postgrest

    suspend fun getCategories(): List<Category> =
        postgrest.from("categories").select {
            order("sort_order", Order.ASCENDING)
        }.decodeList()

    suspend fun createCategory(name: String, description: String, imageUrl: String, sortOrder: Int, isActive: Boolean): Category =
        postgrest.from("categories").insert(
            buildJsonObject {
                put("name", JsonPrimitive(name))
                put("description", JsonPrimitive(description))
                put("image_url", JsonPrimitive(imageUrl))
                put("sort_order", JsonPrimitive(sortOrder))
                put("is_active", JsonPrimitive(isActive))
            }
        ) { select() }.decodeSingle()

    suspend fun updateCategory(id: String, name: String, description: String, imageUrl: String, sortOrder: Int, isActive: Boolean) {
        postgrest.from("categories").update(
            buildJsonObject {
                put("name", JsonPrimitive(name))
                put("description", JsonPrimitive(description))
                put("image_url", JsonPrimitive(imageUrl))
                put("sort_order", JsonPrimitive(sortOrder))
                put("is_active", JsonPrimitive(isActive))
            }
        ) { filter { eq("id", id) } }
    }

    suspend fun deleteCategory(id: String) {
        postgrest.from("categories").delete { filter { eq("id", id) } }
    }
}
