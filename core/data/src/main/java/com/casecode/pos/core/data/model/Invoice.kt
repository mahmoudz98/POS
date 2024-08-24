package com.casecode.pos.core.data.model

import com.casecode.pos.core.data.utils.Invoice_CREATEDBY_FIELD
import com.casecode.pos.core.data.utils.Invoice_CUSTOMER_FIELD
import com.casecode.pos.core.data.utils.Invoice_DATE_FIELD
import com.casecode.pos.core.data.utils.Invoice_ITEMS_FIELD
import com.casecode.pos.core.data.utils.Invoice_NAME_FIELD
import com.casecode.pos.core.data.utils.toDateFormatString
import com.casecode.pos.core.model.data.users.Customer
import com.casecode.pos.core.model.data.users.Invoice
import com.casecode.pos.core.model.data.users.InvoiceGroup
import com.casecode.pos.core.model.data.users.Item
import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.ServerTimestamp
import java.util.Date

data class InvoiceDataModel(
    @DocumentId val invoiceId: String = "",
    @ServerTimestamp val date: Date = Date(),
    val createdBy: String = "",
    val customer: Customer? = null,
    val items: List<Item> = emptyList(),
)

fun Invoice.asExternalMapper(
    documentRef: DocumentReference,
    createdBy: String,
): Map<String, Any?> {
    val networkInvoice = InvoiceDataModel(documentRef.id, this.date, createdBy, this.customer, this.items)
    val invoiceMap =
        mapOf(
            Invoice_NAME_FIELD to documentRef.id,
            Invoice_DATE_FIELD to networkInvoice.date,
            Invoice_CREATEDBY_FIELD to networkInvoice.createdBy,
            Invoice_CUSTOMER_FIELD to networkInvoice.customer,
            Invoice_ITEMS_FIELD to networkInvoice.items,
        )
    return invoiceMap
}

fun List<Invoice>.toInvoicesGroup(): List<InvoiceGroup> {
    val invoiceMap = mutableMapOf<String, MutableList<Invoice>>()
    for (invoice in this) {
        val formattedDate = invoice.date.toDateFormatString()
        if (invoiceMap.containsKey(formattedDate).not()) {
            invoiceMap[formattedDate] = mutableListOf()
        }
        invoiceMap[formattedDate]?.add(invoice)
    }
    val invoicesGroup =
        invoiceMap
            .map { invoiceGroup ->
                InvoiceGroup(
                    invoiceGroup.key,
                    invoiceGroup.value,
                )
            }.sortedByDescending { invoiceGroup -> invoiceGroup.date }
    return invoicesGroup
}