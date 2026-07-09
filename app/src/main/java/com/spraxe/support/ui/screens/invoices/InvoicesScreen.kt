package com.spraxe.support.ui.screens.invoices

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.spraxe.support.ui.components.EmptyState
import com.spraxe.support.ui.components.LoadingIndicator

@Composable
fun InvoicesScreen(viewModel: InvoicesViewModel = viewModel()) {
    if (viewModel.isLoading) {
        LoadingIndicator()
        return
    }
    if (viewModel.invoices.isEmpty()) {
        EmptyState("No invoices generated yet.")
        return
    }
    LazyColumn(contentPadding = PaddingValues(16.dp)) {
        items(viewModel.invoices) { invoice ->
            Card(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(invoice.invoiceNumber ?: invoice.id.take(8), style = MaterialTheme.typography.titleMedium)
                    Text("Total: ৳%.2f • Status: %s".format(invoice.total ?: 0.0, invoice.status ?: "N/A"))
                }
            }
        }
    }
}
