package com.casecode.pos.core.domain.usecase

import com.casecode.pos.core.domain.R
import com.casecode.pos.core.domain.repository.InvoiceRepository
import com.casecode.pos.core.domain.utils.Resource
import com.casecode.pos.core.model.data.users.Invoice
import com.casecode.pos.core.model.data.users.InvoiceGroup
import com.casecode.pos.core.model.data.users.Item
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class AddInvoiceUseCase @Inject constructor(private val invoiceRepository: InvoiceRepository) {
    operator fun invoke(items: List<Item>): Flow<Resource<Int>> {
        return flow {
            emit(Resource.Loading)
            if (items.isEmpty()) {
                return@flow emit(Resource.empty(message = R.string.invoice_items_empty))
            }
            val invoice = Invoice(items = items)
            emit(invoiceRepository.addInvoice(invoice))
        }
    }
}

class GetInvoicesUseCase @Inject constructor(private val invoiceRepository: InvoiceRepository) {
    operator fun invoke(): Flow<Resource<List<InvoiceGroup>>> {
        return flow {
            emit(Resource.Loading)
            emit(invoiceRepository.getInvoices())
        }
    }
}
class GetTodayInvoicesUseCase @Inject constructor(private val invoiceRepository: InvoiceRepository) {
    operator fun invoke(): Flow<Resource<List<Invoice>>> {
        return flow {
            emit(Resource.Loading)
            emit(invoiceRepository.getTodayInvoices())
        }
    }
}