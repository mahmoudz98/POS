package com.casecode.pos.feature.sale

import com.casecode.pos.core.model.data.users.Item

data class SaleUiState(
    val searchQuery: String = "",
    val items: List<Item> = emptyList(),
    val itemsInvoice: Set<Item> = emptySet(),
    val itemInvoiceSelected: Item? = null,
    val itemSelected: Item? = null,
    val amountInput: String = "",
    val userMessage: Int? = null,
) {
    val totalItemsInvoice: Double
        get() = itemsInvoice.sumOf { it.price.times(it.quantity) }

    val restOfAmount: Double
        get() = amountInput.toDoubleOrNull().run {
            if (this == null || this == totalItemsInvoice) {
                return 0.0
            }
            return this.minus(totalItemsInvoice)
        }

    // Add a new property to represent the state
    val invoiceState: InvoiceState
        get() =
            if (items.isEmpty()) {
                InvoiceState.EmptyItems
            } else if (itemsInvoice.isEmpty()) {
                InvoiceState.EmptyItemInvoice
            } else {
                InvoiceState.HasItems
            }
}

// Sealed class to represent the invoice states
sealed interface InvoiceState {
    data object EmptyItems : InvoiceState
    data object EmptyItemInvoice : InvoiceState
    data object HasItems : InvoiceState
}

sealed interface SaleItemsInvoiceUiState {
    data object Empty : SaleItemsInvoiceUiState
    data class Success(val itemsInvoice: Set<Item>) : SaleItemsInvoiceUiState
}