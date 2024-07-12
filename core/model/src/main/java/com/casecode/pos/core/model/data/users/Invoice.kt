package com.casecode.pos.core.model.data.users

import java.util.Date

data class Invoice(
    val invoiceId: String = "",
    val date: Date = Date(),
    val createdBy: Int = 0,
    val customer: Customer? = null,
    val items: List<Item> = emptyList(),
) {
    val total: Double
        get() = items.sumOf { it.price * it.quantity }

}
fun Set<Item>.addItemToInvoices(item: Item): Set<Item> {
    val existingItem = find { it.sku == item.sku }
    return if (existingItem != null) {
        toMutableSet().apply {
            remove(existingItem)
            add(existingItem.copy(quantity = existingItem.quantity + 1))
        }
    } else {
        plus(item.copy(quantity = 1.0))
    }
}