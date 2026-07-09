package com.spraxe.support.data.repository

import com.spraxe.support.data.model.SupportMessage
import com.spraxe.support.data.model.SupportTicket
import com.spraxe.support.data.remote.SupabaseClientProvider
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.query.Order
import io.github.jan.supabase.realtime.PostgresAction
import io.github.jan.supabase.realtime.channel
import io.github.jan.supabase.realtime.postgresChangeFlow
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.map
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.buildJsonObject

/**
 * Support tickets + the live chat thread hanging off each one.
 *
 * `support_messages` is a companion table to `support_tickets` (see
 * supabase/migrations/20260709120000_support_live_chat.sql in this repo) that turns a ticket
 * into a real two-way conversation: the customer's original message seeds the thread, and every
 * reply -- from either the customer (via a future website/app chat widget) or staff (from this
 * app) -- is a row in `support_messages`. Realtime is used so staff see new customer messages
 * the instant they arrive, without polling or refreshing.
 */
class SupportRepository {
    private val postgrest get() = SupabaseClientProvider.postgrest
    private val realtime get() = SupabaseClientProvider.realtime

    suspend fun getTickets(statusFilter: String? = null): List<SupportTicket> =
        postgrest.from("support_tickets").select {
            order("updated_at", Order.DESCENDING)
            filter {
                if (!statusFilter.isNullOrBlank() && statusFilter != "all") eq("status", statusFilter)
            }
        }.decodeList()

    suspend fun getTicket(id: String): SupportTicket? =
        postgrest.from("support_tickets").select {
            filter { eq("id", id) }
        }.decodeSingleOrNull()

    suspend fun updateTicketStatus(id: String, status: String) {
        postgrest.from("support_tickets").update(
            buildJsonObject { put("status", JsonPrimitive(status)) }
        ) { filter { eq("id", id) } }
    }

    suspend fun updateTicketPriority(id: String, priority: String) {
        postgrest.from("support_tickets").update(
            buildJsonObject { put("priority", JsonPrimitive(priority)) }
        ) { filter { eq("id", id) } }
    }

    suspend fun assignTicket(id: String, staffId: String) {
        postgrest.from("support_tickets").update(
            buildJsonObject { put("assigned_to", JsonPrimitive(staffId)) }
        ) { filter { eq("id", id) } }
    }

    // ---- Live chat messages ----

    suspend fun getMessages(ticketId: String): List<SupportMessage> =
        postgrest.from("support_messages").select {
            order("created_at", Order.ASCENDING)
            filter { eq("ticket_id", ticketId) }
        }.decodeList()

    suspend fun sendMessage(ticketId: String, senderId: String?, message: String): SupportMessage {
        val row = postgrest.from("support_messages").insert(
            buildJsonObject {
                put("ticket_id", JsonPrimitive(ticketId))
                if (senderId != null) put("sender_id", JsonPrimitive(senderId))
                put("sender_role", JsonPrimitive("staff"))
                put("message", JsonPrimitive(message))
                put("is_read", JsonPrimitive(true))
            }
        ) { select() }.decodeSingle<SupportMessage>()

        // Replying moves a ticket out of "open" and into "in_progress" automatically,
        // mirroring how a human moderator would triage it.
        postgrest.from("support_tickets").update(
            buildJsonObject { put("status", JsonPrimitive("in_progress")) }
        ) { filter { eq("id", ticketId); neq("status", "resolved"); neq("status", "closed") } }

        return row
    }

    suspend fun markMessagesRead(ticketId: String) {
        postgrest.from("support_messages").update(
            buildJsonObject { put("is_read", JsonPrimitive(true)) }
        ) {
            filter {
                eq("ticket_id", ticketId)
                eq("sender_role", "customer")
            }
        }
    }

    /** Realtime stream of newly-inserted messages for a single ticket's conversation. */
    fun observeNewMessages(ticketId: String): Flow<SupportMessage> {
        val channel = realtime.channel("support-messages-$ticketId")
        return channel.postgresChangeFlow<PostgresAction.Insert>(schema = "public") {
            table = "support_messages"
            filter("ticket_id", io.github.jan.supabase.postgrest.query.FilterOperator.EQ, ticketId)
        }.map { it.decodeRecord<SupportMessage>() }
    }

    /** Realtime stream of newly-inserted/updated tickets, used to live-refresh the chat list. */
    fun observeTicketChanges(): Flow<SupportTicket> {
        val channel = realtime.channel("support-tickets-all")
        return channel.postgresChangeFlow<PostgresAction>(schema = "public") {
            table = "support_tickets"
        }.map {
            when (it) {
                is PostgresAction.Insert -> it.decodeRecord<SupportTicket>()
                is PostgresAction.Update -> it.decodeRecord<SupportTicket>()
                else -> null
            }
        }.filterNotNull()
    }

    suspend fun subscribeToTicketChannel(ticketId: String) {
        realtime.channel("support-messages-$ticketId").let {
            if (it.status.value.name != "JOINED") it.subscribe()
        }
    }

    suspend fun subscribeToTicketsChannel() {
        realtime.channel("support-tickets-all").let {
            if (it.status.value.name != "JOINED") it.subscribe()
        }
    }
}
