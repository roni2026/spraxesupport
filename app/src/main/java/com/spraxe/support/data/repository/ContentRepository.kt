package com.spraxe.support.data.repository

import com.spraxe.support.data.model.FeatureCard
import com.spraxe.support.data.model.FeaturedImage
import com.spraxe.support.data.remote.SupabaseClientProvider
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.query.Order
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.buildJsonObject

/** Manages the storefront's hero banners ("featured_images") and "Why Shop With Spraxe" cards. */
class ContentRepository {
    private val postgrest get() = SupabaseClientProvider.postgrest

    suspend fun getFeaturedImages(): List<FeaturedImage> =
        postgrest.from("featured_images").select {
            order("sort_order", Order.ASCENDING)
        }.decodeList()

    suspend fun createFeaturedImage(title: String, description: String, imageUrl: String, sortOrder: Int, isActive: Boolean): FeaturedImage =
        postgrest.from("featured_images").insert(
            buildJsonObject {
                put("title", JsonPrimitive(title))
                put("description", JsonPrimitive(description))
                put("image_url", JsonPrimitive(imageUrl))
                put("sort_order", JsonPrimitive(sortOrder))
                put("is_active", JsonPrimitive(isActive))
            }
        ) { select() }.decodeSingle()

    suspend fun updateFeaturedImage(id: Int, title: String, description: String, imageUrl: String, sortOrder: Int, isActive: Boolean) {
        postgrest.from("featured_images").update(
            buildJsonObject {
                put("title", JsonPrimitive(title))
                put("description", JsonPrimitive(description))
                put("image_url", JsonPrimitive(imageUrl))
                put("sort_order", JsonPrimitive(sortOrder))
                put("is_active", JsonPrimitive(isActive))
            }
        ) { filter { eq("id", id) } }
    }

    suspend fun deleteFeaturedImage(id: Int) {
        postgrest.from("featured_images").delete { filter { eq("id", id) } }
    }

    suspend fun getFeatureCards(): List<FeatureCard> =
        postgrest.from("feature_cards").select {
            order("sort_order", Order.ASCENDING)
        }.decodeList()

    suspend fun createFeatureCard(title: String, description: String, icon: String, imageUrl: String?, sortOrder: Int, isActive: Boolean): FeatureCard =
        postgrest.from("feature_cards").insert(
            buildJsonObject {
                put("title", JsonPrimitive(title))
                put("description", JsonPrimitive(description))
                put("icon", JsonPrimitive(icon))
                if (imageUrl != null) put("image_url", JsonPrimitive(imageUrl))
                put("sort_order", JsonPrimitive(sortOrder))
                put("is_active", JsonPrimitive(isActive))
            }
        ) { select() }.decodeSingle()

    suspend fun updateFeatureCard(id: Int, title: String, description: String, icon: String, imageUrl: String?, sortOrder: Int, isActive: Boolean) {
        postgrest.from("feature_cards").update(
            buildJsonObject {
                put("title", JsonPrimitive(title))
                put("description", JsonPrimitive(description))
                put("icon", JsonPrimitive(icon))
                if (imageUrl != null) put("image_url", JsonPrimitive(imageUrl))
                put("sort_order", JsonPrimitive(sortOrder))
                put("is_active", JsonPrimitive(isActive))
            }
        ) { filter { eq("id", id) } }
    }

    suspend fun deleteFeatureCard(id: Int) {
        postgrest.from("feature_cards").delete { filter { eq("id", id) } }
    }
}
