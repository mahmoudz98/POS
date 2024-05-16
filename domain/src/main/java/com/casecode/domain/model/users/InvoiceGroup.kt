package com.casecode.domain.model.users

data class InvoiceGroup(val date:String, val invoices: List<Invoice>){
    val totalInvoiceGroup: Double
        get() = invoices.sumOf { it.total}
}