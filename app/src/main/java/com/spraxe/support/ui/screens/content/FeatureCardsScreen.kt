package com.spraxe.support.ui.screens.content

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
import androidx.compose.material.icons.filled.Edit
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
fun FeatureCardsScreen(viewModel: FeatureCardsViewModel = viewModel()) {
    Box(modifier = Modifier.fillMaxSize()) {
        if (viewModel.isLoading) {
            LoadingIndicator()
        } else if (viewModel.cards.isEmpty()) {
            EmptyState("No feature cards yet. Tap + to add one.")
        } else {
            LazyColumn(contentPadding = PaddingValues(16.dp)) {
                items(viewModel.cards) { card ->
                    Card(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
                        Row(modifier = Modifier.padding(16.dp).fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(card.title, style = MaterialTheme.typography.titleMedium)
                                Text(card.description, style = MaterialTheme.typography.bodyMedium)
                            }
                            IconButton(onClick = { viewModel.startEdit(card) }) { Icon(Icons.Filled.Edit, contentDescription = "Edit") }
                            IconButton(onClick = { viewModel.delete(card) }) { Icon(Icons.Filled.Delete, contentDescription = "Delete") }
                        }
                    }
                }
            }
        }

        FloatingActionButton(onClick = { viewModel.startCreate() }, modifier = Modifier.align(Alignment.BottomEnd).padding(20.dp)) {
            Icon(Icons.Filled.Add, contentDescription = "Add card")
        }
    }

    if (viewModel.showForm) {
        AlertDialog(
            onDismissRequest = { viewModel.showForm = false },
            title = { Text(if (viewModel.editing == null) "New feature card" else "Edit feature card") },
            text = {
                Column {
                    OutlinedTextField(value = viewModel.title, onValueChange = { viewModel.title = it }, label = { Text("Title") }, modifier = Modifier.fillMaxWidth())
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(value = viewModel.description, onValueChange = { viewModel.description = it }, label = { Text("Description") }, modifier = Modifier.fillMaxWidth())
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(value = viewModel.icon, onValueChange = { viewModel.icon = it }, label = { Text("Icon name (lucide-react)") }, modifier = Modifier.fillMaxWidth())
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(value = viewModel.sortOrder, onValueChange = { viewModel.sortOrder = it }, label = { Text("Sort order") }, modifier = Modifier.fillMaxWidth())
                    Row(modifier = Modifier.fillMaxWidth().padding(top = 8.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Active")
                        Switch(checked = viewModel.isActive, onCheckedChange = { viewModel.isActive = it })
                    }
                }
            },
            confirmButton = { Button(onClick = { viewModel.save() }) { Text("Save") } },
            dismissButton = { TextButton(onClick = { viewModel.showForm = false }) { Text("Cancel") } }
        )
    }
}
