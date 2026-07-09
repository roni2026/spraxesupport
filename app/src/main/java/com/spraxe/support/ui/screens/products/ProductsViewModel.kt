package com.spraxe.support.ui.screens.products

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.spraxe.support.data.model.Category
import com.spraxe.support.data.model.Product
import com.spraxe.support.data.repository.CategoryRepository
import com.spraxe.support.data.repository.ProductRepository
import kotlinx.coroutines.launch

class ProductsViewModel(
    private val repository: ProductRepository = ProductRepository(),
    private val categoryRepository: CategoryRepository = CategoryRepository()
) : ViewModel() {
    var products by mutableStateOf<List<Product>>(emptyList())
    var categories by mutableStateOf<List<Category>>(emptyList())
    var isLoading by mutableStateOf(true)
    var query by mutableStateOf("")

    init {
        refresh()
    }

    fun refresh() {
        viewModelScope.launch {
            isLoading = true
            try {
                categories = categoryRepository.getCategories()
                products = repository.getProducts(query)
            } finally {
                isLoading = false
            }
        }
    }

    fun setQuery(value: String) {
        query = value
        refresh()
    }

    fun toggleActive(product: Product) {
        viewModelScope.launch {
            repository.setActive(product.id, !product.isActive)
            refresh()
        }
    }

    fun toggleFeatured(product: Product) {
        viewModelScope.launch {
            repository.setFeatured(product.id, !product.isFeatured)
            refresh()
        }
    }

    fun deleteProduct(product: Product) {
        viewModelScope.launch {
            repository.deleteProduct(product.id)
            refresh()
        }
    }
}
