package com.spraxe.support.ui.screens.settings

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.spraxe.support.ui.components.EmptyState
import com.spraxe.support.ui.components.LoadingIndicator

@Composable
fun SiteSettingsScreen(viewModel: SettingsViewModel = viewModel()) {
    if (viewModel.isLoading) {
        LoadingIndicator()
        return
    }
    if (viewModel.settings.isEmpty()) {
        EmptyState("No site settings configured yet.")
        return
    }

    LazyColumn(contentPadding = PaddingValues(16.dp)) {
        items(viewModel.settings) { setting ->
            Card(
                onClick = { viewModel.startEdit(setting) },
                modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(setting.key, style = MaterialTheme.typography.titleMedium)
                    Text(setting.value.toString(), style = MaterialTheme.typography.bodyMedium)
                }
            }
        }
    }

    viewModel.editing?.let { setting ->
        AlertDialog(
            onDismissRequest = { viewModel.editing = null },
            title = { Text("Edit ${setting.key}") },
            text = {
                Column {
                    OutlinedTextField(
                        value = viewModel.editValue,
                        onValueChange = { viewModel.editValue = it },
                        label = { Text("Value (raw JSON)") },
                        minLines = 2,
                        modifier = Modifier.fillMaxWidth()
                    )
                    viewModel.errorMessage?.let {
                        Text(it, color = MaterialTheme.colorScheme.error)
                    }
                }
            },
            confirmButton = { Button(onClick = { viewModel.save() }) { Text("Save") } },
            dismissButton = { TextButton(onClick = { viewModel.editing = null }) { Text("Cancel") } }
        )
    }
}
