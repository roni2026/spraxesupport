package com.spraxe.support.data.repository

import com.spraxe.support.data.model.Product
import com.spraxe.support.data.remote.SupabaseClientProvider
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.query.Order
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.buildJsonObject

/** Full product catalog management: create, edit, delete, toggle active/featured. */
class ProductRepository {
    private val postgrest get() = SupabaseClientProvider.postgrest

    suspend fun getProducts(query: String? = null, categoryId: String? = null): List<Product> =
        postgrest.from("products").select {
            order("created_at", Order.DESCENDING)
            filter {
                if (!query.isNullOrBlank()) ilike("name", "%$query%")
                if (!categoryId.isNullOrBlank()) eq("category_id", categoryId)
            }
        }.decodeList()

    suspend fun getProduct(id: String): Product? =
        postgrest.from("products").select {
            filter { eq("id", id) }
        }.decodeSingleOrNull()

    suspend fun createProduct(
        name: String,
        slug: String,
        description: String,
        price: Double,
        categoryId: String?,
        stockQuantity: Int,
        images: List<String>,
        isActive: Boolean,
        isFeatured: Boolean
    ): Product = postgrest.from("products").insert(
        buildJsonObject {
            put("name", JsonPrimitive(name))
            put("slug", JsonPrimitive(slug))
            put("description", JsonPrimitive(description))
            put("price", JsonPrimitive(price))
            put("base_price", JsonPrimitive(price))
            if (categoryId != null) put("category_id", JsonPrimitive(categoryId))
            put("stock_quantity", JsonPrimitive(stockQuantity))
            put("images", JsonArray(images.map { JsonPrimitive(it) }))
            put("is_active", JsonPrimitive(isActive))
            put("is_featured", JsonPrimitive(isFeatured))
        }
    ) { select() }.decodeSingle()

    suspend fun updateProduct(
        id: String,
        name: String,
        slug: String,
        description: String,
        price: Double,
        categoryId: String?,
        stockQuantity: Int,
        images: List<String>,
        isActive: Boolean,
        isFeatured: Boolean
    ) {
        postgrest.from("products").update(
            buildJsonObject {
                put("name", JsonPrimitive(name))
                put("slug", JsonPrimitive(slug))
                put("description", JsonPrimitive(description))
                put("price", JsonPrimitive(price))
                put("base_price", JsonPrimitive(price))
                if (categoryId != null) put("category_id", JsonPrimitive(categoryId))
                put("stock_quantity", JsonPrimitive(stockQuantity))
                put("images", JsonArray(images.map { JsonPrimitive(it) }))
                put("is_active", JsonPrimitive(isActive))
                put("is_featured", JsonPrimitive(isFeatured))
            }
        ) {
            filter { eq("id", id) }
        }
    }

    suspend fun setActive(id: String, isActive: Boolean) {
        postgrest.from("products").update(
            buildJsonObject { put("is_active", JsonPrimitive(isActive)) }
        ) { filter { eq("id", id) } }
    }

    suspend fun setFeatured(id: String, isFeatured: Boolean) {
        postgrest.from("products").update(
            buildJsonObject { put("is_featured", JsonPrimitive(isFeatured)) }
        ) { filter { eq("id", id) } }
    }

    suspend fun deleteProduct(id: String) {
        postgrest.from("products").delete { filter { eq("id", id) } }
    }
}
