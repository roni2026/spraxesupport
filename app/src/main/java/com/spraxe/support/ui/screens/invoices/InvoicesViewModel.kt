package com.spraxe.support.ui.screens.invoices

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.spraxe.support.data.model.Invoice
import com.spraxe.support.data.repository.InvoiceRepository
import kotlinx.coroutines.launch

class InvoicesViewModel(private val repository: InvoiceRepository = InvoiceRepository()) : ViewModel() {
    var invoices by mutableStateOf<List<Invoice>>(emptyList())
    var isLoading by mutableStateOf(true)

    init {
        viewModelScope.launch {
            isLoading = true
            try { invoices = repository.getInvoices() } finally { isLoading = false }
        }
    }
}
