/*
 * Designed and developed 2024 by Mahmood Abdalhafeez
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://opensource.org/licenses/MIT
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.casecode.pos.core.domain.usecase

import com.casecode.pos.core.domain.repository.SupplierInvoiceRepository
import com.casecode.pos.core.domain.utils.OperationResult
import com.casecode.pos.core.domain.utils.Resource
import com.casecode.pos.core.model.data.users.PaymentDetails
import com.casecode.pos.core.model.data.users.PaymentStatus
import com.casecode.pos.core.model.data.users.SupplierInvoice
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.onEach
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import javax.inject.Inject

/**
 * Use case for retrieving all supplier invoices.
 *
 * This use case interacts with the `SupplierInvoiceRepository` to fetch all
 * available supplier invoices.
 *
 * @property supplierInvoiceRepository The repository responsible for accessing supplier invoice data.
 */
class GetSupplierInvoicesUseCase @Inject constructor(
    private val supplierInvoiceRepository: SupplierInvoiceRepository,
) {
    operator fun invoke() = supplierInvoiceRepository.getInvoices()
}

class GetSupplierInvoiceDetailsUseCase @Inject constructor(
    private val supplierInvoiceRepo: SupplierInvoiceRepository,
) {
    operator fun invoke(id: String?): Flow<Resource<SupplierInvoice>> = flow {
        if (id.isNullOrEmpty()) {
            emit(Resource.empty())
        } else {
            emitAll(supplierInvoiceRepo.getInvoiceDetails(id))
        }
    }.onEach {
        Resource.Loading
        it
    }
}

/**
 * Use case for adding a new supplier invoice.
 *
 * This use case interacts with the [SupplierInvoiceRepository] to persist the invoice data.
 *
 * @property supplierInvoiceRepository The repository responsible for managing supplier invoices.
 */
class AddSupplierInvoiceUseCase @Inject constructor(
    private val supplierInvoiceRepository: SupplierInvoiceRepository,
) {
    suspend operator fun invoke(invoice: SupplierInvoice) =
        supplierInvoiceRepository.addInvoice(invoice)
}

/**
 * Use case for updating a supplier invoice.
 *
 * This use case is responsible for updating an existing supplier invoice in the repository.
 *
 * @property supplierInvoiceRepository The repository for managing supplier invoices.
 */
class UpdateSupplierInvoiceUseCase @Inject constructor(
    private val supplierInvoiceRepository: SupplierInvoiceRepository,
) {
    suspend operator fun invoke(invoice: SupplierInvoice) =
        supplierInvoiceRepository.updateInvoice(invoice)
}

/**
 * Use case for adding payment details to a supplier invoice.
 *
 * This use case is responsible for:
 * - Adding the payment details to the invoice.
 * - Updating the payment status of the invoice based on the total amount paid and due date.
 *
 * @param supplierInvoiceRepository The repository for accessing and modifying supplier invoices.
 */
class AddPaymentDetailsUseCase @Inject constructor(
    private val supplierInvoiceRepository: SupplierInvoiceRepository,
) {
    suspend operator fun invoke(
        supplierInvoice: SupplierInvoice,
        paymentDetails: PaymentDetails,
    ): OperationResult {
        val totalPaid =
            supplierInvoice.paymentDetails.sumOf { it.amountPaid } + paymentDetails.amountPaid
        val paymentStatus = determinePaymentStatus(
            supplierInvoice.totalAmount,
            totalPaid,
            supplierInvoice.dueDate,
        )
        val id = supplierInvoice.paymentDetails.size.inc().toString()
        return supplierInvoiceRepository.addPaymentDetails(
            supplierInvoice.invoiceId,
            paymentDetails.copy(paymentId = id),
            paymentStatus,
        )
    }

    private fun determinePaymentStatus(
        totalAmount: Double,
        totalPaid: Double,
        dueDate: Instant,
    ): PaymentStatus = when {
        totalPaid >= totalAmount -> PaymentStatus.PAID
        Clock.System.now() > dueDate && totalPaid > 0 -> PaymentStatus.OVERDUE
        totalPaid > 0 -> PaymentStatus.PARTIALLY_PAID
        else -> PaymentStatus.PENDING
    }
}