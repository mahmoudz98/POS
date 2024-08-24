package com.casecode.pos.core.model.data.users

data class InvoiceGroup(
    val date: String,
    val invoices: List<Invoice>,
) {
    val totalInvoiceGroup: Double
        get() = invoices.sumOf { it.total }
}