package com.casecode.pos.feature.bill

import com.casecode.pos.core.model.data.users.SupplierInvoice

sealed interface BillsUiState {
    object Loading : BillsUiState
    object Error : BillsUiState
    object Empty : BillsUiState
    data class Success(val supplierInvoices: Map<String, SupplierInvoice>) : BillsUiState
}