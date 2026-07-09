package com.spraxe.support.ui.screens.products

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarBorder
import androidx.compose.material3.Card
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.spraxe.support.data.model.Product
import com.spraxe.support.ui.components.ConfirmDialog
import com.spraxe.support.ui.components.EmptyState
import com.spraxe.support.ui.components.FallbackAsyncImage
import com.spraxe.support.ui.components.LoadingIndicator

@Composable
fun ProductsScreen(onEditProduct: (String?) -> Unit, viewModel: ProductsViewModel = viewModel()) {
    var pendingDelete by remember { mutableStateOf<Product?>(null) }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxWidth()) {
            OutlinedTextField(
                value = viewModel.query,
                onValueChange = { viewModel.setQuery(it) },
                label = { Text("Search products") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth().padding(16.dp)
            )

            if (viewModel.isLoading) {
                LoadingIndicator()
            } else if (viewModel.products.isEmpty()) {
                EmptyState("No products yet. Tap + to add one.")
            } else {
                LazyColumn(contentPadding = PaddingValues(horizontal = 16.dp, vertical = 4.dp)) {
                    items(viewModel.products) { product ->
                        Card(
                            onClick = { onEditProduct(product.id) },
                            modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
                        ) {
                            Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                                FallbackAsyncImage(
                                    url = product.thumbnailUrl,
                                    modifier = Modifier.size(56.dp).padding(end = 12.dp)
                                )
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(product.name, style = MaterialTheme.typography.titleMedium)
                                    Text("৳%.2f • Stock %d".format(product.displayPrice, product.stockQuantity ?: 0))
                                }
                                IconButton(onClick = { viewModel.toggleFeatured(product) }) {
                                    Icon(
                                        imageVector = if (product.isFeatured) Icons.Filled.Star else Icons.Filled.StarBorder,
                                        contentDescription = "Featured"
                                    )
                                }
                                Switch(checked = product.isActive, onCheckedChange = { viewModel.toggleActive(product) })
                                IconButton(onClick = { pendingDelete = product }) {
                                    Icon(Icons.Filled.Delete, contentDescription = "Delete")
                                }
                            }
                        }
                    }
                }
            }
        }

        FloatingActionButton(
            onClick = { onEditProduct(null) },
            modifier = Modifier.align(Alignment.BottomEnd).padding(20.dp)
        ) {
            Icon(Icons.Filled.Add, contentDescription = "Add product")
        }
    }

    pendingDelete?.let { product ->
        ConfirmDialog(
            title = "Delete product?",
            message = "\"${product.name}\" will be permanently removed from the catalog.",
            confirmLabel = "Delete",
            onConfirm = {
                viewModel.deleteProduct(product)
                pendingDelete = null
            },
            onDismiss = { pendingDelete = null }
        )
    }
}
