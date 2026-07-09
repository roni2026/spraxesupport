package com.spraxe.support.ui.screens.chat

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.spraxe.support.ui.components.EmptyState
import com.spraxe.support.ui.components.LoadingIndicator
import com.spraxe.support.ui.components.StatusBadge

private val ticketStatuses = listOf("all", "open", "in_progress", "resolved", "closed")

@Composable
fun ChatListScreen(onOpenTicket: (String) -> Unit, viewModel: ChatListViewModel = viewModel()) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = "Live Chat & Support",
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.padding(16.dp, 16.dp, 16.dp, 8.dp)
        )
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = PaddingValues(horizontal = 16.dp)
        ) {
            items(ticketStatuses) { status ->
                FilterChip(
                    selected = viewModel.statusFilter == status,
                    onClick = { viewModel.setStatusFilter(status) },
                    label = { Text(status.replace('_', ' ').replaceFirstChar { it.uppercase() }) }
                )
            }
        }

        if (viewModel.isLoading) {
            LoadingIndicator()
        } else if (viewModel.tickets.isEmpty()) {
            EmptyState("No conversations yet.")
        } else {
            LazyColumn(contentPadding = PaddingValues(16.dp)) {
                items(viewModel.tickets) { ticket ->
                    Card(
                        onClick = { onOpenTicket(ticket.id) },
                        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp).fillMaxWidth()) {
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text(
                                    text = ticket.subject,
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.SemiBold,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                    modifier = Modifier.weight(1f)
                                )
                                StatusBadge(status = ticket.status)
                            }
                            Text(
                                text = ticket.profiles?.displayName ?: (ticket.ticketNumber ?: ticket.id.take(8)),
                                style = MaterialTheme.typography.bodyMedium,
                                modifier = Modifier.padding(top = 2.dp)
                            )
                            Text(
                                text = ticket.message,
                                style = MaterialTheme.typography.bodyMedium,
                                maxLines = 2,
                                overflow = TextOverflow.Ellipsis,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                                modifier = Modifier.padding(top = 4.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}
