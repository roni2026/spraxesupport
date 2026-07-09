package com.spraxe.support.ui.screens.dashboard

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.spraxe.support.ui.components.LoadingIndicator
import com.spraxe.support.ui.components.StatCard
import com.spraxe.support.ui.components.StatusBadge

@Composable
fun DashboardScreen(
    onOpenOrder: (String) -> Unit,
    viewModel: DashboardViewModel = viewModel()
) {
    if (viewModel.isLoading && viewModel.recentOrders.isEmpty()) {
        LoadingIndicator()
        return
    }

    LazyColumn(
        modifier = Modifier.fillMaxWidth(),
        contentPadding = androidx.compose.foundation.layout.PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Text(text = "Overview", style = MaterialTheme.typography.titleLarge)
        }
        item {
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                userScrollEnabled = false
            ) {
                item { StatCard(label = "Products", value = viewModel.stats.products.toString(), modifier = Modifier.fillMaxWidth()) }
                item { StatCard(label = "Total orders", value = viewModel.stats.orders.toString(), modifier = Modifier.fillMaxWidth()) }
                item { StatCard(label = "Customers", value = viewModel.stats.customers.toString(), modifier = Modifier.fillMaxWidth()) }
                item { StatCard(label = "Pending orders", value = viewModel.stats.pendingOrders.toString(), modifier = Modifier.fillMaxWidth()) }
                item { StatCard(label = "Open tickets", value = viewModel.stats.openTickets.toString(), modifier = Modifier.fillMaxWidth()) }
                item { StatCard(label = "In-progress chats", value = viewModel.stats.inProgressTickets.toString(), modifier = Modifier.fillMaxWidth()) }
            }
        }
        item {
            Text(text = "Recent orders", style = MaterialTheme.typography.titleLarge, modifier = Modifier.padding(top = 12.dp))
        }
        items(viewModel.recentOrders) { order ->
            Card(
                onClick = { onOpenOrder(order.id) },
                modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp).fillMaxWidth()) {
                    androidx.compose.foundation.layout.Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(text = order.orderNumber ?: order.id.take(8), style = MaterialTheme.typography.titleMedium)
                        StatusBadge(status = order.status)
                    }
                    Text(
                        text = "৳%.2f".format(order.total),
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
            }
        }
    }
}
