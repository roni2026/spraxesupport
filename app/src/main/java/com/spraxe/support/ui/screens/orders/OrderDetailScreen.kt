package com.spraxe.support.ui.screens.orders

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.Divider
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.spraxe.support.ui.components.LoadingIndicator
import com.spraxe.support.ui.components.StatusBadge

private val orderStatuses = listOf("pending", "confirmed", "processing", "delivered", "cancelled")
private val paymentStatuses = listOf("pending", "paid", "failed", "refunded")

@Composable
fun OrderDetailScreen(orderId: String, viewModel: OrderDetailViewModel = viewModel()) {
    LaunchedEffect(orderId) { viewModel.load(orderId) }

    val order = viewModel.order
    if (viewModel.isLoading || order == null) {
        LoadingIndicator()
        return
    }

    LazyColumn(contentPadding = androidx.compose.foundation.layout.PaddingValues(16.dp)) {
        item {
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text(order.orderNumber ?: order.id, style = MaterialTheme.typography.titleLarge)
                        StatusBadge(status = order.status)
                    }
                    Text("Total: ৳%.2f".format(order.total), modifier = Modifier.padding(top = 8.dp))
                    order.contactNumber?.let { Text("Phone: $it", modifier = Modifier.padding(top = 4.dp)) }
                    order.shippingAddress?.let { Text("Address: $it", modifier = Modifier.padding(top = 4.dp)) }
                    Text("Payment: ${order.paymentMethod ?: "N/A"} (${order.paymentStatus ?: "pending"})", modifier = Modifier.padding(top = 4.dp))
                }
            }
        }

        item {
            Card(modifier = Modifier.fillMaxWidth().padding(top = 12.dp)) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Update order status", style = MaterialTheme.typography.titleMedium)
                    var expanded by remember { mutableStateOf(false) }
                    OutlinedButton(onClick = { expanded = true }, modifier = Modifier.padding(top = 8.dp)) {
                        Text(order.status.replaceFirstChar { it.uppercase() })
                    }
                    DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                        orderStatuses.forEach { status ->
                            DropdownMenuItem(
                                text = { Text(status.replaceFirstChar { it.uppercase() }) },
                                onClick = {
                                    expanded = false
                                    viewModel.updateStatus(orderId, status)
                                }
                            )
                        }
                    }

                    Divider(modifier = Modifier.padding(vertical = 12.dp))

                    Text("Update payment status", style = MaterialTheme.typography.titleMedium)
                    var paymentExpanded by remember { mutableStateOf(false) }
                    OutlinedButton(onClick = { paymentExpanded = true }, modifier = Modifier.padding(top = 8.dp)) {
                        Text((order.paymentStatus ?: "pending").replaceFirstChar { it.uppercase() })
                    }
                    DropdownMenu(expanded = paymentExpanded, onDismissRequest = { paymentExpanded = false }) {
                        paymentStatuses.forEach { status ->
                            DropdownMenuItem(
                                text = { Text(status.replaceFirstChar { it.uppercase() }) },
                                onClick = {
                                    paymentExpanded = false
                                    viewModel.updatePaymentStatus(orderId, status)
                                }
                            )
                        }
                    }
                }
            }
        }

        item {
            Text("Items", style = MaterialTheme.typography.titleLarge, modifier = Modifier.padding(top = 16.dp, bottom = 8.dp))
        }
        items(viewModel.items) { orderItem ->
            Card(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(orderItem.productName, style = MaterialTheme.typography.titleMedium)
                    Text("Qty ${orderItem.quantity} × ৳%.2f = ৳%.2f".format(orderItem.unitPrice, orderItem.totalPrice))
                }
            }
        }
    }
}
