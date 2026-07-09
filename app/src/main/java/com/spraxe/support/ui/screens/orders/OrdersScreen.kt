package com.spraxe.support.ui.screens.orders

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.spraxe.support.ui.components.EmptyState
import com.spraxe.support.ui.components.LoadingIndicator
import com.spraxe.support.ui.components.StatusBadge

private val statusOptions = listOf("all", "pending", "confirmed", "processing", "delivered", "cancelled")

@Composable
fun OrdersScreen(onOpenOrder: (String) -> Unit, viewModel: OrdersViewModel = viewModel()) {
    Column(modifier = Modifier.fillMaxWidth()) {
        OutlinedTextField(
            value = viewModel.query,
            onValueChange = { viewModel.setQuery(it) },
            label = { Text("Search order number") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth().padding(16.dp, 16.dp, 16.dp, 8.dp)
        )
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            androidx.compose.foundation.lazy.LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(statusOptions) { status ->
                    FilterChip(
                        selected = viewModel.statusFilter == status,
                        onClick = { viewModel.setStatusFilter(status) },
                        label = { Text(status.replaceFirstChar { it.uppercase() }) }
                    )
                }
            }
        }

        if (viewModel.isLoading) {
            LoadingIndicator()
        } else if (viewModel.orders.isEmpty()) {
            EmptyState("No orders found.")
        } else {
            LazyColumn(contentPadding = PaddingValues(16.dp)) {
                items(viewModel.orders) { order ->
                    Card(
                        onClick = { onOpenOrder(order.id) },
                        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp).fillMaxWidth()) {
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text(text = order.orderNumber ?: order.id.take(8), style = MaterialTheme.typography.titleMedium)
                                StatusBadge(status = order.status)
                            }
                            Text(
                                text = "৳%.2f • %s".format(order.total, order.paymentMethod ?: "N/A"),
                                style = MaterialTheme.typography.bodyMedium,
                                modifier = Modifier.padding(top = 4.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}
