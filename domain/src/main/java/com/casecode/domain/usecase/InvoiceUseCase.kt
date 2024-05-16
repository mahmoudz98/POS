package com.casecode.domain.usecase

import com.casecode.domain.model.users.Invoice
import com.casecode.domain.model.users.InvoiceGroup
import com.casecode.domain.model.users.Item
import com.casecode.domain.repository.InvoiceRepository
import com.casecode.domain.utils.Resource
import com.casecode.pos.domain.R
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class AddInvoiceUseCase @Inject constructor(private val invoiceRepository: InvoiceRepository) {
     operator fun invoke(items: List<Item>): Flow<Resource<Int>> {
        return flow {
            emit(Resource.Loading)
            if (items.isEmpty()) {
              return@flow  emit(Resource.empty(message = R.string.invoice_items_empty))
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