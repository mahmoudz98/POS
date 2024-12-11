package com.casecode.pos.feature.bill.detials

import com.casecode.pos.core.model.data.users.SupplierInvoice

sealed interface BillDetailUiState {
    data object Loading : BillDetailUiState
    data object Error : BillDetailUiState
    data object EmptySelection : BillDetailUiState
    data class Success(val supplierInvoice: SupplierInvoice) : BillDetailUiState
}