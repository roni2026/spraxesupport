package com.spraxe.support.ui.screens.orders

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.spraxe.support.data.model.OrderItemRow
import com.spraxe.support.data.model.OrderRow
import com.spraxe.support.data.repository.OrderRepository
import kotlinx.coroutines.launch

class OrdersViewModel(private val repository: OrderRepository = OrderRepository()) : ViewModel() {
    var orders by mutableStateOf<List<OrderRow>>(emptyList())
    var isLoading by mutableStateOf(true)
    var statusFilter by mutableStateOf("all")
    var query by mutableStateOf("")

    init {
        refresh()
    }

    fun refresh() {
        viewModelScope.launch {
            isLoading = true
            try {
                orders = repository.getOrders(statusFilter, query)
            } finally {
                isLoading = false
            }
        }
    }

    fun setStatusFilter(value: String) {
        statusFilter = value
        refresh()
    }

    fun setQuery(value: String) {
        query = value
        refresh()
    }
}

class OrderDetailViewModel(private val repository: OrderRepository = OrderRepository()) : ViewModel() {
    var order by mutableStateOf<OrderRow?>(null)
    var items by mutableStateOf<List<OrderItemRow>>(emptyList())
    var isLoading by mutableStateOf(true)
    var isUpdating by mutableStateOf(false)

    fun load(orderId: String) {
        viewModelScope.launch {
            isLoading = true
            try {
                order = repository.getOrder(orderId)
                items = repository.getOrderItems(orderId)
            } finally {
                isLoading = false
            }
        }
    }

    fun updateStatus(orderId: String, status: String) {
        viewModelScope.launch {
            isUpdating = true
            try {
                repository.updateStatus(orderId, status)
                order = order?.copy(status = status)
            } finally {
                isUpdating = false
            }
        }
    }

    fun updatePaymentStatus(orderId: String, paymentStatus: String) {
        viewModelScope.launch {
            isUpdating = true
            try {
                repository.updatePaymentStatus(orderId, paymentStatus)
                order = order?.copy(paymentStatus = paymentStatus)
            } finally {
                isUpdating = false
            }
        }
    }
}
