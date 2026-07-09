package com.spraxe.support.data.repository

import com.spraxe.support.data.model.SiteSetting
import com.spraxe.support.data.remote.SupabaseClientProvider
import io.github.jan.supabase.postgrest.from
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.buildJsonObject

class SettingsRepository {
    private val postgrest get() = SupabaseClientProvider.postgrest

    suspend fun getSettings(): List<SiteSetting> =
        postgrest.from("site_settings").select().decodeList()

    /** Value is stored as raw JSON text (e.g. `"60"`, `"true"`, `{"a":1}`) and parsed here. */
    suspend fun updateSetting(key: String, rawJsonValue: String) {
        val parsed = Json.parseToJsonElement(rawJsonValue)
        postgrest.from("site_settings").update(
            buildJsonObject { put("value", parsed) }
        ) { filter { eq("key", key) } }
    }

    suspend fun createSetting(key: String, rawJsonValue: String) {
        val parsed = Json.parseToJsonElement(rawJsonValue)
        postgrest.from("site_settings").insert(
            buildJsonObject {
                put("key", JsonPrimitive(key))
                put("value", parsed)
            }
        )
    }
}
