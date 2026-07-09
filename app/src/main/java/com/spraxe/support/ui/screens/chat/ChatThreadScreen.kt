package com.spraxe.support.ui.screens.chat

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.Card
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.spraxe.support.ui.components.LoadingIndicator
import com.spraxe.support.ui.components.StatusBadge

private val ticketStatuses = listOf("open", "in_progress", "resolved", "closed")
private val priorities = listOf("low", "medium", "high")

@Composable
fun ChatThreadScreen(ticketId: String, viewModel: ChatThreadViewModel = viewModel()) {
    LaunchedEffect(ticketId) { viewModel.load(ticketId) }
    val listState = rememberLazyListState()

    LaunchedEffect(viewModel.messages.size) {
        if (viewModel.messages.isNotEmpty()) listState.animateScrollToItem(viewModel.messages.size - 1)
    }

    val ticket = viewModel.ticket
    if (viewModel.isLoading && ticket == null) {
        LoadingIndicator()
        return
    }

    Column(modifier = Modifier.fillMaxSize()) {
        ticket?.let {
            TicketHeader(ticket = it, viewModel = viewModel)
        }

        LazyColumn(
            state = listState,
            modifier = Modifier.weight(1f).fillMaxWidth(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(viewModel.messages) { message ->
                val fromStaff = message.senderRole == "staff" || message.senderRole == "admin" || message.senderRole == "moderator"
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = if (fromStaff) Arrangement.End else Arrangement.Start
                ) {
                    Surface(
                        color = if (fromStaff) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant,
                        contentColor = if (fromStaff) Color.White else MaterialTheme.colorScheme.onSurface,
                        shape = RoundedCornerShape(14.dp)
                    ) {
                        Text(
                            text = message.message,
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth().padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = viewModel.draft,
                onValueChange = { viewModel.draft = it },
                placeholder = { Text("Type a reply...") },
                modifier = Modifier.weight(1f)
            )
            IconButton(
                onClick = { viewModel.send(ticketId) },
                enabled = !viewModel.isSending && viewModel.draft.isNotBlank()
            ) {
                Icon(Icons.Filled.Send, contentDescription = "Send")
            }
        }
    }
}

@Composable
private fun TicketHeader(ticket: com.spraxe.support.data.model.SupportTicket, viewModel: ChatThreadViewModel) {
    Card(modifier = Modifier.fillMaxWidth().padding(12.dp)) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(ticket.subject, style = MaterialTheme.typography.titleMedium)
            Text(
                text = ticket.profiles?.displayName ?: (ticket.ticketNumber ?: ""),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
            )
            Row(modifier = Modifier.padding(top = 8.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                var statusExpanded by remember { mutableStateOf(false) }
                Box {
                    OutlinedButton(onClick = { statusExpanded = true }) {
                        StatusBadge(status = ticket.status)
                    }
                    DropdownMenu(expanded = statusExpanded, onDismissRequest = { statusExpanded = false }) {
                        ticketStatuses.forEach { status ->
                            DropdownMenuItem(
                                text = { Text(status.replace('_', ' ').replaceFirstChar { it.uppercase() }) },
                                onClick = {
                                    statusExpanded = false
                                    viewModel.updateStatus(ticket.id, status)
                                }
                            )
                        }
                    }
                }

                var priorityExpanded by remember { mutableStateOf(false) }
                Box {
                    OutlinedButton(onClick = { priorityExpanded = true }) {
                        Text("Priority: ${ticket.priority.replaceFirstChar { it.uppercase() }}")
                    }
                    DropdownMenu(expanded = priorityExpanded, onDismissRequest = { priorityExpanded = false }) {
                        priorities.forEach { priority ->
                            DropdownMenuItem(
                                text = { Text(priority.replaceFirstChar { it.uppercase() }) },
                                onClick = {
                                    priorityExpanded = false
                                    viewModel.updatePriority(ticket.id, priority)
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}
