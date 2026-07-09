package com.spraxe.support.ui.screens.categories

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.spraxe.support.ui.components.EmptyState
import com.spraxe.support.ui.components.LoadingIndicator

@Composable
fun CategoriesScreen(viewModel: CategoriesViewModel = viewModel()) {
    Box(modifier = Modifier.fillMaxSize()) {
        if (viewModel.isLoading) {
            LoadingIndicator()
        } else if (viewModel.categories.isEmpty()) {
            EmptyState("No categories yet. Tap + to add one.")
        } else {
            LazyColumn(contentPadding = PaddingValues(16.dp)) {
                items(viewModel.categories) { category ->
                    Card(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
                        Row(
                            modifier = Modifier.padding(16.dp).fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(category.name, style = MaterialTheme.typography.titleMedium)
                                if (!category.description.isNullOrBlank()) {
                                    Text(category.description ?: "", style = MaterialTheme.typography.bodyMedium)
                                }
                            }
                            IconButton(onClick = { viewModel.startEdit(category) }) {
                                Icon(Icons.Filled.Add, contentDescription = "Edit")
                            }
                            IconButton(onClick = { viewModel.delete(category) }) {
                                Icon(Icons.Filled.Delete, contentDescription = "Delete")
                            }
                        }
                    }
                }
            }
        }

        FloatingActionButton(
            onClick = { viewModel.startCreate() },
            modifier = Modifier.align(Alignment.BottomEnd).padding(20.dp)
        ) {
            Icon(Icons.Filled.Add, contentDescription = "Add category")
        }
    }

    if (viewModel.showForm) {
        AlertDialog(
            onDismissRequest = { viewModel.showForm = false },
            title = { Text(if (viewModel.editingCategory == null) "New category" else "Edit category") },
            text = {
                Column {
                    OutlinedTextField(value = viewModel.name, onValueChange = { viewModel.name = it }, label = { Text("Name") }, modifier = Modifier.fillMaxWidth())
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(value = viewModel.description, onValueChange = { viewModel.description = it }, label = { Text("Description") }, modifier = Modifier.fillMaxWidth())
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(value = viewModel.imageUrl, onValueChange = { viewModel.imageUrl = it }, label = { Text("Image URL") }, modifier = Modifier.fillMaxWidth())
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(value = viewModel.sortOrder, onValueChange = { viewModel.sortOrder = it }, label = { Text("Sort order") }, modifier = Modifier.fillMaxWidth())
                    Row(modifier = Modifier.fillMaxWidth().padding(top = 8.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Active")
                        Switch(checked = viewModel.isActive, onCheckedChange = { viewModel.isActive = it })
                    }
                }
            },
            confirmButton = {
                Button(onClick = { viewModel.save() }, enabled = !viewModel.isSaving) { Text("Save") }
            },
            dismissButton = {
                TextButton(onClick = { viewModel.showForm = false }) { Text("Cancel") }
            }
        )
    }
}
