package com.spraxe.support.ui.screens.customers

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.spraxe.support.ui.components.LoadingIndicator
import com.spraxe.support.ui.components.StatusBadge

@Composable
fun CustomerDetailScreen(customerId: String, viewModel: CustomerDetailViewModel = viewModel()) {
    LaunchedEffect(customerId) { viewModel.load(customerId) }

    val customer = viewModel.customer
    if (viewModel.isLoading || customer == null) {
        LoadingIndicator()
        return
    }

    LazyColumn(contentPadding = PaddingValues(16.dp)) {
        item {
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(customer.displayName, style = MaterialTheme.typography.titleLarge)
                    customer.email?.let { Text("Email: $it", modifier = Modifier.padding(top = 4.dp)) }
                    customer.phone?.let { Text("Phone: $it", modifier = Modifier.padding(top = 4.dp)) }
                    customer.address?.let { Text("Address: $it", modifier = Modifier.padding(top = 4.dp)) }
                }
            }
        }
        item {
            Text("Order history", style = MaterialTheme.typography.titleLarge, modifier = Modifier.padding(top = 16.dp, bottom = 8.dp))
        }
        items(viewModel.orders) { order ->
            Card(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
                Row(
                    modifier = Modifier.padding(16.dp).fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text(order.orderNumber ?: order.id.take(8), style = MaterialTheme.typography.titleMedium)
                        Text("৳%.2f".format(order.total))
                    }
                    StatusBadge(status = order.status)
                }
            }
        }
    }
}
