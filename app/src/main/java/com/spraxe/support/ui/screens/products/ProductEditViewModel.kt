package com.spraxe.support.ui.screens.products

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.spraxe.support.data.model.Category
import com.spraxe.support.data.repository.CategoryRepository
import com.spraxe.support.data.repository.ProductRepository
import kotlinx.coroutines.launch

class ProductEditViewModel(
    private val repository: ProductRepository = ProductRepository(),
    private val categoryRepository: CategoryRepository = CategoryRepository()
) : ViewModel() {
    var categories by mutableStateOf<List<Category>>(emptyList())

    var name by mutableStateOf("")
    var slug by mutableStateOf("")
    var description by mutableStateOf("")
    var price by mutableStateOf("")
    var stockQuantity by mutableStateOf("")
    var imagesCsv by mutableStateOf("")
    var categoryId by mutableStateOf<String?>(null)
    var isActive by mutableStateOf(true)
    var isFeatured by mutableStateOf(false)

    var isLoading by mutableStateOf(false)
    var isSaving by mutableStateOf(false)
    var errorMessage by mutableStateOf<String?>(null)
    var saved by mutableStateOf(false)

    init {
        viewModelScope.launch { categories = categoryRepository.getCategories() }
    }

    fun load(productId: String) {
        viewModelScope.launch {
            isLoading = true
            try {
                val product = repository.getProduct(productId) ?: return@launch
                name = product.name
                slug = product.slug ?: ""
                description = product.description ?: ""
                price = product.displayPrice.toString()
                stockQuantity = (product.stockQuantity ?: 0).toString()
                imagesCsv = product.images?.joinToString(", ") ?: ""
                categoryId = product.categoryId
                isActive = product.isActive
                isFeatured = product.isFeatured
            } finally {
                isLoading = false
            }
        }
    }

    fun save(productId: String?) {
        if (name.isBlank() || slug.isBlank() || price.toDoubleOrNull() == null) {
            errorMessage = "Name, slug, and a valid price are required."
            return
        }
        viewModelScope.launch {
            isSaving = true
            errorMessage = null
            try {
                val images = imagesCsv.split(",").map { it.trim() }.filter { it.isNotBlank() }
                val priceValue = price.toDouble()
                val stockValue = stockQuantity.toIntOrNull() ?: 0
                if (productId == null) {
                    repository.createProduct(name, slug, description, priceValue, categoryId, stockValue, images, isActive, isFeatured)
                } else {
                    repository.updateProduct(productId, name, slug, description, priceValue, categoryId, stockValue, images, isActive, isFeatured)
                }
                saved = true
            } catch (e: Exception) {
                errorMessage = e.message ?: "Failed to save product."
            } finally {
                isSaving = false
            }
        }
    }
}
