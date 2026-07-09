package com.spraxe.support.ui.screens.dashboard

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.spraxe.support.data.model.DashboardStats
import com.spraxe.support.data.model.OrderRow
import com.spraxe.support.data.repository.DashboardRepository
import kotlinx.coroutines.launch

class DashboardViewModel(private val repository: DashboardRepository = DashboardRepository()) : ViewModel() {
    var stats by mutableStateOf(DashboardStats())
    var recentOrders by mutableStateOf<List<OrderRow>>(emptyList())
    var isLoading by mutableStateOf(true)

    init {
        refresh()
    }

    fun refresh() {
        viewModelScope.launch {
            isLoading = true
            try {
                stats = repository.getStats()
                recentOrders = repository.getRecentOrders()
            } finally {
                isLoading = false
            }
        }
    }
}
