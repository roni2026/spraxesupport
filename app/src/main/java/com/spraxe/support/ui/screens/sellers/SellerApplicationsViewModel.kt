package com.spraxe.support.ui.screens.sellers

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.spraxe.support.data.model.SellerApplication
import com.spraxe.support.data.repository.SellerApplicationRepository
import kotlinx.coroutines.launch

class SellerApplicationsViewModel(private val repository: SellerApplicationRepository = SellerApplicationRepository()) : ViewModel() {
    var applications by mutableStateOf<List<SellerApplication>>(emptyList())
    var isLoading by mutableStateOf(true)
    var statusFilter by mutableStateOf("pending")

    init { refresh() }

    fun refresh() {
        viewModelScope.launch {
            isLoading = true
            try { applications = repository.getApplications(statusFilter) } finally { isLoading = false }
        }
    }

    fun setStatusFilter(value: String) {
        statusFilter = value
        refresh()
    }

    fun approve(application: SellerApplication) {
        viewModelScope.launch { repository.approve(application.id); refresh() }
    }

    fun reject(application: SellerApplication, reason: String) {
        viewModelScope.launch { repository.reject(application.id, reason); refresh() }
    }
}
