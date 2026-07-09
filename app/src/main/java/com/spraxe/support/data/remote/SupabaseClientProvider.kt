package com.spraxe.support.data.remote

import com.spraxe.support.BuildConfig
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.Auth
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.realtime.Realtime
import io.github.jan.supabase.storage.Storage

/**
 * Single shared Supabase client for the whole app, wired to the same project the
 * Spraxe website and customer app talk to. The URL/anon key are injected at build time
 * via BuildConfig (see app/build.gradle.kts + gradle.properties / local.properties) so no
 * secret ever needs to be hardcoded here.
 *
 * Realtime is installed in addition to Postgrest/Auth/Storage so the Live Chat screens can
 * subscribe to postgres_changes on `support_messages` and see new customer messages the
 * instant they're inserted, without polling.
 */
object SupabaseClientProvider {
    val client: SupabaseClient by lazy {
        createSupabaseClient(
            supabaseUrl = BuildConfig.SUPABASE_URL,
            supabaseKey = BuildConfig.SUPABASE_ANON_KEY
        ) {
            install(Postgrest)
            install(Auth) {
                // Lets a Supabase email/OTP or OAuth redirect (spraxesupport://login-callback)
                // resume a session inside the app.
                scheme = "spraxesupport"
                host = "login-callback"
            }
            install(Storage)
            install(Realtime)
        }
    }

    val auth get() = client.auth
    val postgrest get() = client.postgrest
    val realtime get() = client.realtime
}
