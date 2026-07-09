package com.spraxe.support.ui.screens.discounts

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.spraxe.support.data.model.DiscountCode
import com.spraxe.support.data.repository.DiscountRepository
import kotlinx.coroutines.launch

class DiscountsViewModel(private val repository: DiscountRepository = DiscountRepository()) : ViewModel() {
    var codes by mutableStateOf<List<DiscountCode>>(emptyList())
    var isLoading by mutableStateOf(true)

    var showForm by mutableStateOf(false)
    var code by mutableStateOf("")
    var discountType by mutableStateOf("percentage")
    var discountValue by mutableStateOf("")
    var minPurchase by mutableStateOf("0")
    var maxUses by mutableStateOf("")

    init { refresh() }

    fun refresh() {
        viewModelScope.launch {
            isLoading = true
            try { codes = repository.getCodes() } finally { isLoading = false }
        }
    }

    fun startCreate() {
        code = ""; discountType = "percentage"; discountValue = ""; minPurchase = "0"; maxUses = ""
        showForm = true
    }

    fun save() {
        val value = discountValue.toDoubleOrNull() ?: return
        if (code.isBlank()) return
        viewModelScope.launch {
            repository.createCode(
                code = code,
                discountType = discountType,
                discountValue = value,
                minPurchase = minPurchase.toDoubleOrNull() ?: 0.0,
                maxUses = maxUses.toIntOrNull(),
                validUntil = null,
                isActive = true
            )
            showForm = false
            refresh()
        }
    }

    fun toggleActive(discountCode: DiscountCode) {
        val id = discountCode.id ?: return
        viewModelScope.launch { repository.setActive(id, !discountCode.isActive); refresh() }
    }

    fun delete(discountCode: DiscountCode) {
        val id = discountCode.id ?: return
        viewModelScope.launch { repository.deleteCode(id); refresh() }
    }
}
