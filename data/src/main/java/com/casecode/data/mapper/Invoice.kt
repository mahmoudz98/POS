package com.casecode.data.mapper

import com.casecode.data.utils.toDateFormatString
import com.casecode.domain.model.users.Invoice
import com.casecode.domain.model.users.InvoiceGroup

fun List<Invoice>.toInvoicesGroup(): List<InvoiceGroup> {
    val invoiceMap = mutableMapOf<String, MutableList<Invoice>>()
    for (invoice in this) {
        val formattedDate = invoice.date.toDateFormatString()
        if (invoiceMap.containsKey(formattedDate).not()) {
            invoiceMap[formattedDate] = mutableListOf()
        }
        invoiceMap[formattedDate]?.add(invoice)
    }
    val invoicesGroup = invoiceMap.map { invoiceGroup ->
        InvoiceGroup(
            invoiceGroup.key,
            invoiceGroup.value,
        )
    }.sortedByDescending { invoiceGroup -> invoiceGroup.date }
    return invoicesGroup
}