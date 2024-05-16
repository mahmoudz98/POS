package com.casecode.domain.repository

import com.casecode.domain.model.users.Invoice
import com.casecode.domain.model.users.InvoiceGroup
import com.casecode.domain.utils.Resource

interface InvoiceRepository {
    suspend  fun addInvoice(invoice: Invoice): Resource<Int>
    suspend fun getInvoices(): Resource<List<InvoiceGroup>>
}