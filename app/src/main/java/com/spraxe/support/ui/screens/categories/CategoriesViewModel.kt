package com.spraxe.support.ui.screens.categories

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.spraxe.support.data.model.Category
import com.spraxe.support.data.repository.CategoryRepository
import kotlinx.coroutines.launch

class CategoriesViewModel(private val repository: CategoryRepository = CategoryRepository()) : ViewModel() {
    var categories by mutableStateOf<List<Category>>(emptyList())
    var isLoading by mutableStateOf(true)

    // Inline add/edit form state
    var editingCategory by mutableStateOf<Category?>(null)
    var showForm by mutableStateOf(false)
    var name by mutableStateOf("")
    var description by mutableStateOf("")
    var imageUrl by mutableStateOf("")
    var sortOrder by mutableStateOf("0")
    var isActive by mutableStateOf(true)
    var isSaving by mutableStateOf(false)

    init {
        refresh()
    }

    fun refresh() {
        viewModelScope.launch {
            isLoading = true
            try {
                categories = repository.getCategories()
            } finally {
                isLoading = false
            }
        }
    }

    fun startCreate() {
        editingCategory = null
        name = ""; description = ""; imageUrl = ""; sortOrder = "0"; isActive = true
        showForm = true
    }

    fun startEdit(category: Category) {
        editingCategory = category
        name = category.name
        description = category.description ?: ""
        imageUrl = category.imageUrl ?: ""
        sortOrder = (category.sortOrder ?: 0).toString()
        isActive = category.isActive
        showForm = true
    }

    fun save() {
        if (name.isBlank()) return
        viewModelScope.launch {
            isSaving = true
            try {
                val order = sortOrder.toIntOrNull() ?: 0
                val existing = editingCategory
                if (existing == null) {
                    repository.createCategory(name, description, imageUrl, order, isActive)
                } else {
                    repository.updateCategory(existing.id, name, description, imageUrl, order, isActive)
                }
                showForm = false
                refresh()
            } finally {
                isSaving = false
            }
        }
    }

    fun delete(category: Category) {
        viewModelScope.launch {
            repository.deleteCategory(category.id)
            refresh()
        }
    }
}
