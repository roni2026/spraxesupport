package com.spraxe.support.ui.screens.discounts

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
fun DiscountCodesScreen(viewModel: DiscountsViewModel = viewModel()) {
    Box(modifier = Modifier.fillMaxSize()) {
        if (viewModel.isLoading) {
            LoadingIndicator()
        } else if (viewModel.codes.isEmpty()) {
            EmptyState("No discount codes yet. Tap + to add one.")
        } else {
            LazyColumn(contentPadding = PaddingValues(16.dp)) {
                items(viewModel.codes) { discountCode ->
                    Card(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
                        Row(modifier = Modifier.padding(16.dp).fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(discountCode.code, style = MaterialTheme.typography.titleMedium)
                                val suffix = if (discountCode.discountType == "percentage") "%" else "৳"
                                Text("${discountCode.discountValue}$suffix off • used ${discountCode.currentUses ?: 0}${discountCode.maxUses?.let { "/$it" } ?: ""}")
                            }
                            Switch(checked = discountCode.isActive, onCheckedChange = { viewModel.toggleActive(discountCode) })
                            IconButton(onClick = { viewModel.delete(discountCode) }) { Icon(Icons.Filled.Delete, contentDescription = "Delete") }
                        }
                    }
                }
            }
        }

        FloatingActionButton(onClick = { viewModel.startCreate() }, modifier = Modifier.align(Alignment.BottomEnd).padding(20.dp)) {
            Icon(Icons.Filled.Add, contentDescription = "Add code")
        }
    }

    if (viewModel.showForm) {
        AlertDialog(
            onDismissRequest = { viewModel.showForm = false },
            title = { Text("New discount code") },
            text = {
                Column {
                    OutlinedTextField(value = viewModel.code, onValueChange = { viewModel.code = it }, label = { Text("Code") }, modifier = Modifier.fillMaxWidth())
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        listOf("percentage", "fixed").forEach { type ->
                            Button(
                                onClick = { viewModel.discountType = type },
                                enabled = viewModel.discountType != type
                            ) { Text(type.replaceFirstChar { it.uppercase() }) }
                        }
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(value = viewModel.discountValue, onValueChange = { viewModel.discountValue = it }, label = { Text("Discount value") }, modifier = Modifier.fillMaxWidth())
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(value = viewModel.minPurchase, onValueChange = { viewModel.minPurchase = it }, label = { Text("Minimum purchase") }, modifier = Modifier.fillMaxWidth())
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(value = viewModel.maxUses, onValueChange = { viewModel.maxUses = it }, label = { Text("Max uses (optional)") }, modifier = Modifier.fillMaxWidth())
                }
            },
            confirmButton = { Button(onClick = { viewModel.save() }) { Text("Create") } },
            dismissButton = { TextButton(onClick = { viewModel.showForm = false }) { Text("Cancel") } }
        )
    }
}
