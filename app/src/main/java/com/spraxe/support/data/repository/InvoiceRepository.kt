package com.spraxe.support.data.repository

import com.spraxe.support.data.model.Invoice
import com.spraxe.support.data.remote.SupabaseClientProvider
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.query.Order

/** Read-only view of generated invoices for staff reference/lookup. */
class InvoiceRepository {
    private val postgrest get() = SupabaseClientProvider.postgrest

    suspend fun getInvoices(): List<Invoice> =
        postgrest.from("invoices").select {
            order("created_at", Order.DESCENDING)
        }.decodeList()
}
