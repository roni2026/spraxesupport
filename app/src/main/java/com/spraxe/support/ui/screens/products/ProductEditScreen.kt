package com.spraxe.support.ui.screens.products

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun ProductEditScreen(
    productId: String?,
    onSaved: () -> Unit,
    viewModel: ProductEditViewModel = viewModel()
) {
    LaunchedEffect(productId) {
        if (productId != null) viewModel.load(productId)
    }
    LaunchedEffect(viewModel.saved) {
        if (viewModel.saved) onSaved()
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        Text(
            text = if (productId == null) "New product" else "Edit product",
            style = MaterialTheme.typography.titleLarge
        )
        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(value = viewModel.name, onValueChange = { viewModel.name = it }, label = { Text("Name") }, modifier = Modifier.fillMaxWidth())
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(value = viewModel.slug, onValueChange = { viewModel.slug = it }, label = { Text("Slug") }, modifier = Modifier.fillMaxWidth())
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
            value = viewModel.description,
            onValueChange = { viewModel.description = it },
            label = { Text("Description") },
            minLines = 3,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(value = viewModel.price, onValueChange = { viewModel.price = it }, label = { Text("Price") }, modifier = Modifier.fillMaxWidth())
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(value = viewModel.stockQuantity, onValueChange = { viewModel.stockQuantity = it }, label = { Text("Stock quantity") }, modifier = Modifier.fillMaxWidth())
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
            value = viewModel.imagesCsv,
            onValueChange = { viewModel.imagesCsv = it },
            label = { Text("Image URLs (comma separated)") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))

        var categoryExpanded by remember { mutableStateOf(false) }
        val selectedCategoryName = viewModel.categories.firstOrNull { it.id == viewModel.categoryId }?.name ?: "No category"
        OutlinedButton(onClick = { categoryExpanded = true }, modifier = Modifier.fillMaxWidth()) {
            Text(selectedCategoryName)
        }
        DropdownMenu(expanded = categoryExpanded, onDismissRequest = { categoryExpanded = false }) {
            DropdownMenuItem(text = { Text("No category") }, onClick = { viewModel.categoryId = null; categoryExpanded = false })
            viewModel.categories.forEach { category ->
                DropdownMenuItem(
                    text = { Text(category.name) },
                    onClick = { viewModel.categoryId = category.id; categoryExpanded = false }
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Text("Active (visible in store)")
            Switch(checked = viewModel.isActive, onCheckedChange = { viewModel.isActive = it })
        }
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Text("Featured on homepage")
            Switch(checked = viewModel.isFeatured, onCheckedChange = { viewModel.isFeatured = it })
        }

        viewModel.errorMessage?.let {
            Spacer(modifier = Modifier.height(8.dp))
            Text(it, color = MaterialTheme.colorScheme.error)
        }

        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = { viewModel.save(productId) },
            enabled = !viewModel.isSaving,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(if (viewModel.isSaving) "Saving..." else "Save product")
        }
        Spacer(modifier = Modifier.height(24.dp))
    }
}
