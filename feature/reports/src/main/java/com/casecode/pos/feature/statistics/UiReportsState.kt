package com.casecode.pos.feature.statistics

import androidx.annotation.StringRes
import com.casecode.pos.core.model.data.users.Invoice

data class UiReportsState(
    val invoices: List<Invoice> = emptyList(),
    val isLoading: Boolean = true,
    val isEmpty: Boolean = false,
    @StringRes val userMessage: Int? = null,
) {
    val countOfInvoice: Int
        get() {
            return invoices.size
        }
    val totalInvoiceSalesToday: Double
        get() {
            return invoices.sumOf { it.total }
        }

}