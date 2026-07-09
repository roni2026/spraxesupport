package com.spraxe.support.ui.screens.auth

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.spraxe.support.data.model.Profile
import com.spraxe.support.data.repository.AuthRepository
import com.spraxe.support.data.repository.StaffAccessDeniedException
import io.github.jan.supabase.auth.SessionStatus
import kotlinx.coroutines.launch

sealed class AuthUiState {
    data object CheckingSession : AuthUiState()
    data object SignedOut : AuthUiState()
    data class SignedIn(val profile: Profile) : AuthUiState()
}

class AuthViewModel(private val repository: AuthRepository = AuthRepository()) : ViewModel() {
    var uiState by mutableStateOf<AuthUiState>(AuthUiState.CheckingSession)
    var isSubmitting by mutableStateOf(false)
    var errorMessage by mutableStateOf<String?>(null)

    fun restoreSession() {
        viewModelScope.launch {
            try {
                val status = repository.currentUser
                if (status == null) {
                    uiState = AuthUiState.SignedOut
                    return@launch
                }
                val profile = repository.requireStaffProfile()
                uiState = AuthUiState.SignedIn(profile)
            } catch (e: Exception) {
                uiState = AuthUiState.SignedOut
            }
        }
    }

    fun signIn(email: String, password: String) {
        if (email.isBlank() || password.isBlank()) {
            errorMessage = "Enter your email and password."
            return
        }
        viewModelScope.launch {
            isSubmitting = true
            errorMessage = null
            try {
                val profile = repository.signInWithEmail(email.trim(), password)
                uiState = AuthUiState.SignedIn(profile)
            } catch (e: StaffAccessDeniedException) {
                errorMessage = e.message
            } catch (e: Exception) {
                errorMessage = e.message ?: "Sign in failed. Check your credentials."
            } finally {
                isSubmitting = false
            }
        }
    }

    fun signOut() {
        viewModelScope.launch {
            repository.signOut()
            uiState = AuthUiState.SignedOut
        }
    }
}
