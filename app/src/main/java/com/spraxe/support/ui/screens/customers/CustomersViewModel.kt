package com.spraxe.support.ui.screens.customers

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.spraxe.support.data.model.OrderRow
import com.spraxe.support.data.model.Profile
import com.spraxe.support.data.repository.CustomerRepository
import kotlinx.coroutines.launch

class CustomersViewModel(private val repository: CustomerRepository = CustomerRepository()) : ViewModel() {
    var customers by mutableStateOf<List<Profile>>(emptyList())
    var isLoading by mutableStateOf(true)
    var query by mutableStateOf("")

    init {
        refresh()
    }

    fun refresh() {
        viewModelScope.launch {
            isLoading = true
            try {
                customers = repository.getCustomers(query)
            } finally {
                isLoading = false
            }
        }
    }

    fun setQuery(value: String) {
        query = value
        refresh()
    }
}

class CustomerDetailViewModel(private val repository: CustomerRepository = CustomerRepository()) : ViewModel() {
    var customer by mutableStateOf<Profile?>(null)
    var orders by mutableStateOf<List<OrderRow>>(emptyList())
    var isLoading by mutableStateOf(true)

    fun load(customerId: String) {
        viewModelScope.launch {
            isLoading = true
            try {
                customer = repository.getCustomer(customerId)
                orders = repository.getCustomerOrders(customerId)
            } finally {
                isLoading = false
            }
        }
    }
}
