package com.casecode.pos.core.domain.repository

import com.casecode.pos.core.domain.utils.Resource
import com.casecode.pos.core.model.data.users.Invoice
import com.casecode.pos.core.model.data.users.InvoiceGroup

interface InvoiceRepository {
    suspend fun addInvoice(invoice: Invoice): Resource<Int>

    suspend fun getInvoices(): Resource<List<InvoiceGroup>>

    suspend fun getTodayInvoices(): Resource<List<Invoice>>
}