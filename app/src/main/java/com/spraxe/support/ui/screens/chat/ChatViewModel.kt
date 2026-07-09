package com.spraxe.support.ui.screens.chat

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.spraxe.support.data.model.SupportMessage
import com.spraxe.support.data.model.SupportTicket
import com.spraxe.support.data.repository.AuthRepository
import com.spraxe.support.data.repository.SupportRepository
import kotlinx.coroutines.launch

/** List of all support tickets = list of live chat conversations, newest activity first. */
class ChatListViewModel(private val repository: SupportRepository = SupportRepository()) : ViewModel() {
    var tickets by mutableStateOf<List<SupportTicket>>(emptyList())
    var isLoading by mutableStateOf(true)
    var statusFilter by mutableStateOf("all")

    init {
        refresh()
        observeLive()
    }

    fun refresh() {
        viewModelScope.launch {
            isLoading = true
            try {
                tickets = repository.getTickets(statusFilter)
            } finally {
                isLoading = false
            }
        }
    }

    fun setStatusFilter(value: String) {
        statusFilter = value
        refresh()
    }

    private fun observeLive() {
        viewModelScope.launch {
            try {
                repository.subscribeToTicketsChannel()
                repository.observeTicketChanges().collect { updated ->
                    val current = tickets.toMutableList()
                    val index = current.indexOfFirst { it.id == updated.id }
                    if (index >= 0) current[index] = updated else current.add(0, updated)
                    tickets = current.sortedByDescending { it.updatedAt ?: it.createdAt ?: "" }
                }
            } catch (_: Exception) {
                // Realtime is a nice-to-have here; pull-to-refresh still works if it's unavailable.
            }
        }
    }
}

/** A single ticket's live chat conversation. */
class ChatThreadViewModel(
    private val repository: SupportRepository = SupportRepository(),
    private val authRepository: AuthRepository = AuthRepository()
) : ViewModel() {
    var ticket by mutableStateOf<SupportTicket?>(null)
    var messages by mutableStateOf<List<SupportMessage>>(emptyList())
    var draft by mutableStateOf("")
    var isLoading by mutableStateOf(true)
    var isSending by mutableStateOf(false)

    fun load(ticketId: String) {
        viewModelScope.launch {
            isLoading = true
            try {
                ticket = repository.getTicket(ticketId)
                messages = repository.getMessages(ticketId)
                repository.markMessagesRead(ticketId)
                observeLive(ticketId)
            } finally {
                isLoading = false
            }
        }
    }

    private fun observeLive(ticketId: String) {
        viewModelScope.launch {
            try {
                repository.subscribeToTicketChannel(ticketId)
                repository.observeNewMessages(ticketId).collect { incoming ->
                    if (messages.none { it.id == incoming.id }) {
                        messages = messages + incoming
                        if (incoming.senderRole == "customer") repository.markMessagesRead(ticketId)
                    }
                }
            } catch (_: Exception) {
                // Falls back to the initial snapshot if Realtime can't connect.
            }
        }
    }

    fun send(ticketId: String) {
        val text = draft.trim()
        if (text.isBlank()) return
        viewModelScope.launch {
            isSending = true
            try {
                val staffId = authRepository.currentUser?.id
                val sent = repository.sendMessage(ticketId, staffId, text)
                if (messages.none { it.id == sent.id }) messages = messages + sent
                draft = ""
                ticket = ticket?.copy(status = "in_progress")
            } finally {
                isSending = false
            }
        }
    }

    fun updateStatus(ticketId: String, status: String) {
        viewModelScope.launch {
            repository.updateTicketStatus(ticketId, status)
            ticket = ticket?.copy(status = status)
        }
    }

    fun updatePriority(ticketId: String, priority: String) {
        viewModelScope.launch {
            repository.updateTicketPriority(ticketId, priority)
            ticket = ticket?.copy(priority = priority)
        }
    }
}
