package com.casecode.domain.model.users

import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.ServerTimestamp
import java.util.Date

data class Invoice(
    @DocumentId val invoiceId: String = "",
    @ServerTimestamp val date: Date = Date(),
    val createdBy: Int = 0,
    val customer: Customer? = null,
    val items: List<Item> = emptyList(),
) {
    val total: Double
        get() = items.sumOf { it.price * it.quantity }
}