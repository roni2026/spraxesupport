package com.spraxe.support.ui.screens.customers

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.spraxe.support.ui.components.EmptyState
import com.spraxe.support.ui.components.LoadingIndicator

@Composable
fun CustomersScreen(onOpenCustomer: (String) -> Unit, viewModel: CustomersViewModel = viewModel()) {
    Column(modifier = Modifier.fillMaxWidth()) {
        OutlinedTextField(
            value = viewModel.query,
            onValueChange = { viewModel.setQuery(it) },
            label = { Text("Search name, email, or phone") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth().padding(16.dp)
        )

        if (viewModel.isLoading) {
            LoadingIndicator()
        } else if (viewModel.customers.isEmpty()) {
            EmptyState("No customers found.")
        } else {
            LazyColumn(contentPadding = PaddingValues(horizontal = 16.dp)) {
                items(viewModel.customers) { customer ->
                    Card(
                        onClick = { onOpenCustomer(customer.id) },
                        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(customer.displayName, style = MaterialTheme.typography.titleMedium)
                            customer.email?.let { Text(it, style = MaterialTheme.typography.bodyMedium) }
                            customer.phone?.let { Text(it, style = MaterialTheme.typography.bodyMedium) }
                        }
                    }
                }
            }
        }
    }
}
