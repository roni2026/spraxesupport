package com.spraxe.support.ui.screens.content

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.spraxe.support.data.model.FeatureCard
import com.spraxe.support.data.model.FeaturedImage
import com.spraxe.support.data.repository.ContentRepository
import kotlinx.coroutines.launch

class FeaturedImagesViewModel(private val repository: ContentRepository = ContentRepository()) : ViewModel() {
    var images by mutableStateOf<List<FeaturedImage>>(emptyList())
    var isLoading by mutableStateOf(true)

    var showForm by mutableStateOf(false)
    var editing by mutableStateOf<FeaturedImage?>(null)
    var title by mutableStateOf("")
    var description by mutableStateOf("")
    var imageUrl by mutableStateOf("")
    var sortOrder by mutableStateOf("0")
    var isActive by mutableStateOf(true)

    init { refresh() }

    fun refresh() {
        viewModelScope.launch {
            isLoading = true
            try { images = repository.getFeaturedImages() } finally { isLoading = false }
        }
    }

    fun startCreate() {
        editing = null; title = ""; description = ""; imageUrl = ""; sortOrder = "0"; isActive = true
        showForm = true
    }

    fun startEdit(image: FeaturedImage) {
        editing = image
        title = image.title ?: ""
        description = image.description ?: ""
        imageUrl = image.imageUrl
        sortOrder = image.sortOrder.toString()
        isActive = image.isActive
        showForm = true
    }

    fun save() {
        if (imageUrl.isBlank()) return
        viewModelScope.launch {
            val order = sortOrder.toIntOrNull() ?: 0
            val existing = editing
            if (existing?.id == null) {
                repository.createFeaturedImage(title, description, imageUrl, order, isActive)
            } else {
                repository.updateFeaturedImage(existing.id, title, description, imageUrl, order, isActive)
            }
            showForm = false
            refresh()
        }
    }

    fun delete(image: FeaturedImage) {
        val id = image.id ?: return
        viewModelScope.launch { repository.deleteFeaturedImage(id); refresh() }
    }
}

class FeatureCardsViewModel(private val repository: ContentRepository = ContentRepository()) : ViewModel() {
    var cards by mutableStateOf<List<FeatureCard>>(emptyList())
    var isLoading by mutableStateOf(true)

    var showForm by mutableStateOf(false)
    var editing by mutableStateOf<FeatureCard?>(null)
    var title by mutableStateOf("")
    var description by mutableStateOf("")
    var icon by mutableStateOf("Sparkles")
    var imageUrl by mutableStateOf("")
    var sortOrder by mutableStateOf("0")
    var isActive by mutableStateOf(true)

    init { refresh() }

    fun refresh() {
        viewModelScope.launch {
            isLoading = true
            try { cards = repository.getFeatureCards() } finally { isLoading = false }
        }
    }

    fun startCreate() {
        editing = null; title = ""; description = ""; icon = "Sparkles"; imageUrl = ""; sortOrder = "0"; isActive = true
        showForm = true
    }

    fun startEdit(card: FeatureCard) {
        editing = card
        title = card.title
        description = card.description
        icon = card.icon
        imageUrl = card.imageUrl ?: ""
        sortOrder = card.sortOrder.toString()
        isActive = card.isActive
        showForm = true
    }

    fun save() {
        if (title.isBlank()) return
        viewModelScope.launch {
            val order = sortOrder.toIntOrNull() ?: 0
            val existing = editing
            val url = imageUrl.ifBlank { null }
            if (existing?.id == null) {
                repository.createFeatureCard(title, description, icon, url, order, isActive)
            } else {
                repository.updateFeatureCard(existing.id, title, description, icon, url, order, isActive)
            }
            showForm = false
            refresh()
        }
    }

    fun delete(card: FeatureCard) {
        val id = card.id ?: return
        viewModelScope.launch { repository.deleteFeatureCard(id); refresh() }
    }
}
