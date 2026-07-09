package com.spraxe.support.ui.screens.settings

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.spraxe.support.data.model.SiteSetting
import com.spraxe.support.data.repository.SettingsRepository
import kotlinx.coroutines.launch

class SettingsViewModel(private val repository: SettingsRepository = SettingsRepository()) : ViewModel() {
    var settings by mutableStateOf<List<SiteSetting>>(emptyList())
    var isLoading by mutableStateOf(true)
    var errorMessage by mutableStateOf<String?>(null)

    var editing by mutableStateOf<SiteSetting?>(null)
    var editValue by mutableStateOf("")

    init { refresh() }

    fun refresh() {
        viewModelScope.launch {
            isLoading = true
            try { settings = repository.getSettings() } finally { isLoading = false }
        }
    }

    fun startEdit(setting: SiteSetting) {
        editing = setting
        editValue = setting.value.toString()
    }

    fun save() {
        val setting = editing ?: return
        viewModelScope.launch {
            try {
                repository.updateSetting(setting.key, editValue)
                editing = null
                refresh()
            } catch (e: Exception) {
                errorMessage = "Invalid JSON value: ${e.message}"
            }
        }
    }
}
