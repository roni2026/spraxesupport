package com.spraxe.support.data.repository

import com.spraxe.support.data.model.Profile
import com.spraxe.support.data.remote.SupabaseClientProvider
import io.github.jan.supabase.auth.SessionStatus
import io.github.jan.supabase.auth.providers.builtin.Email
import io.github.jan.supabase.auth.user.UserInfo
import io.github.jan.supabase.postgrest.from
import kotlinx.coroutines.flow.Flow

/**
 * Staff-only auth. Sign-in uses the same Supabase Auth project as the website/customer app,
 * but this repository additionally enforces that the signed-in profile's `role` is
 * "admin" or "moderator" -- customers who mistakenly install this app are signed back out
 * with a clear error instead of reaching any admin data.
 */
class StaffAccessDeniedException(message: String) : Exception(message)

class AuthRepository {
    private val auth get() = SupabaseClientProvider.auth
    private val postgrest get() = SupabaseClientProvider.postgrest

    val sessionStatus: Flow<SessionStatus> get() = auth.sessionStatus
    val currentUser: UserInfo? get() = auth.currentUserOrNull()

    suspend fun signInWithEmail(email: String, password: String): Profile {
        auth.signInWith(Email) {
            this.email = email
            this.password = password
        }
        return requireStaffProfile()
    }

    suspend fun signOut() {
        auth.signOut()
    }

    /** Fetches the current user's profile and verifies it is staff, signing out otherwise. */
    suspend fun requireStaffProfile(): Profile {
        val userId = auth.currentUserOrNull()?.id
            ?: throw StaffAccessDeniedException("Not signed in.")

        val profile = postgrest.from("profiles").select {
            filter { eq("id", userId) }
        }.decodeSingleOrNull<Profile>()
            ?: throw StaffAccessDeniedException("No profile found for this account.")

        if (!profile.isStaff) {
            auth.signOut()
            throw StaffAccessDeniedException(
                "This account is a customer account. Spraxe Support is for admins and moderators only."
            )
        }
        return profile
    }

    suspend fun getCurrentProfile(): Profile? {
        val userId = auth.currentUserOrNull()?.id ?: return null
        return postgrest.from("profiles").select {
            filter { eq("id", userId) }
        }.decodeSingleOrNull<Profile>()
    }

    suspend fun updateProfile(profile: Profile) {
        postgrest.from("profiles").update(profile) {
            filter { eq("id", profile.id) }
        }
    }
}
