package com.casecode.pos.feature.invoice

import com.casecode.pos.core.model.data.users.Invoice

sealed interface UIInvoiceDetails {
    data object Loading : UIInvoiceDetails
    data object Empty : UIInvoiceDetails
    data class Success(val invoice: Invoice) : UIInvoiceDetails
}