package com.spraxe.support.ui.screens.sellers

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.spraxe.support.data.model.SellerApplication
import com.spraxe.support.ui.components.EmptyState
import com.spraxe.support.ui.components.LoadingIndicator
import com.spraxe.support.ui.components.StatusBadge

private val statuses = listOf("pending", "approved", "rejected", "all")

@Composable
fun SellerApplicationsScreen(viewModel: SellerApplicationsViewModel = viewModel()) {
    var rejecting by remember { mutableStateOf<SellerApplication?>(null) }
    var rejectionReason by remember { mutableStateOf("") }

    Column(modifier = Modifier.fillMaxWidth()) {
        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp), contentPadding = PaddingValues(16.dp)) {
            items(statuses) { status ->
                FilterChip(
                    selected = viewModel.statusFilter == status,
                    onClick = { viewModel.setStatusFilter(status) },
                    label = { Text(status.replaceFirstChar { it.uppercase() }) }
                )
            }
        }

        if (viewModel.isLoading) {
            LoadingIndicator()
        } else if (viewModel.applications.isEmpty()) {
            EmptyState("No seller applications here.")
        } else {
            LazyColumn(contentPadding = PaddingValues(horizontal = 16.dp)) {
                items(viewModel.applications) { application ->
                    Card(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text(application.shopName, style = MaterialTheme.typography.titleMedium)
                                StatusBadge(status = application.status)
                            }
                            Text(application.businessAddress, style = MaterialTheme.typography.bodyMedium)
                            Text("${application.phone} • ${application.email}", style = MaterialTheme.typography.bodyMedium)
                            if (application.status == "pending") {
                                Row(modifier = Modifier.padding(top = 8.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    Button(onClick = { viewModel.approve(application) }) { Text("Approve") }
                                    OutlinedButton(onClick = { rejecting = application; rejectionReason = "" }) { Text("Reject") }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    rejecting?.let { application ->
        AlertDialog(
            onDismissRequest = { rejecting = null },
            title = { Text("Reject application") },
            text = {
                OutlinedTextField(
                    value = rejectionReason,
                    onValueChange = { rejectionReason = it },
                    label = { Text("Reason") },
                    modifier = Modifier.fillMaxWidth()
                )
            },
            confirmButton = {
                Button(onClick = { viewModel.reject(application, rejectionReason); rejecting = null }) { Text("Reject") }
            },
            dismissButton = { TextButton(onClick = { rejecting = null }) { Text("Cancel") } }
        )
    }
}
