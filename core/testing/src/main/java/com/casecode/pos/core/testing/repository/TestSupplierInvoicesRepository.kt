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
package com.casecode.pos.core.testing.repository

import com.casecode.pos.core.domain.repository.SupplierInvoiceRepository
import com.casecode.pos.core.domain.utils.OperationResult
import com.casecode.pos.core.domain.utils.Resource
import com.casecode.pos.core.model.data.users.PaymentDetails
import com.casecode.pos.core.model.data.users.PaymentStatus
import com.casecode.pos.core.model.data.users.SupplierInvoice
import com.casecode.pos.core.testing.base.BaseTestRepository
import com.casecode.pos.core.testing.data.supplierInvoicesTestData
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import com.casecode.pos.core.data.R.string as stringData

class TestSupplierInvoicesRepository @Inject constructor() :
    BaseTestRepository(),
    SupplierInvoiceRepository {
    private val resourcesSupplierInvoicesFlow: MutableSharedFlow<Resource<List<SupplierInvoice>>> =
        MutableSharedFlow(replay = 2, onBufferOverflow = BufferOverflow.DROP_OLDEST)
    val supplierInvoicesTest = ArrayList(supplierInvoicesTestData)

    override fun init() = Unit

    fun sendSupplierInvoices() {
        resourcesSupplierInvoicesFlow.tryEmit(Resource.success(supplierInvoicesTest))
    }

    override fun setReturnEmpty(value: Boolean) {
        super.setReturnEmpty(value)
        resourcesSupplierInvoicesFlow.tryEmit(Resource.empty())
    }

    override fun setReturnError(value: Boolean) {
        super.setReturnError(value)
        resourcesSupplierInvoicesFlow.tryEmit(
            Resource.error(stringData.core_data_error_fetching_supplier_invoices),
        )
    }

    override fun getInvoices(): Flow<Resource<List<SupplierInvoice>>> = resourcesSupplierInvoicesFlow

    override suspend fun getOverdueInvoices(): List<SupplierInvoice> = supplierInvoicesTestData.filter {
        it.paymentStatus != PaymentStatus.PENDING
    }

    override fun getInvoiceDetails(invoiceId: String): Flow<Resource<SupplierInvoice>> = flow {
        emit(Resource.loading())
        if (shouldReturnError) {
            emit(Resource.error(stringData.core_data_error_fetching_supplier_invoices))
        }
        val invoice = supplierInvoicesTest.find { it.invoiceId == invoiceId }
        if (invoice != null) {
            emit(Resource.success(invoice))
        } else {
            emit(Resource.empty())
        }
    }

    override suspend fun addInvoice(invoice: SupplierInvoice): OperationResult {
        if (shouldReturnError) {
            return OperationResult.Failure(
                stringData.core_data_add_supplier_invoice_failure_generic,
            )
        }
        supplierInvoicesTest.add(invoice)
        resourcesSupplierInvoicesFlow.tryEmit(Resource.success(supplierInvoicesTest))
        return OperationResult.Success
    }

    override suspend fun updateInvoice(invoice: SupplierInvoice): OperationResult {
        if (shouldReturnError) {
            return OperationResult.Failure(
                stringData.core_data_update_supplier_invoice_failure_generic,
            )
        }
        supplierInvoicesTest.remove(invoice)
        supplierInvoicesTest.add(invoice)
        resourcesSupplierInvoicesFlow.tryEmit(Resource.success(supplierInvoicesTest))
        return OperationResult.Success
    }

    override suspend fun addPaymentDetails(
        invoiceId: String,
        paymentDetails: PaymentDetails,
        paymentStatus: PaymentStatus,
    ): OperationResult {
        if (shouldReturnError) {
            return OperationResult.Failure(
                stringData.core_data_add_supplier_invoice_failure_generic,
            )
        }

        val invoiceIndex = supplierInvoicesTest.indexOfFirst { it.invoiceId == invoiceId }

        if (invoiceIndex != -1) {
            val existingInvoice = supplierInvoicesTest[invoiceIndex]
            val updatedInvoice = existingInvoice.copy(
                paymentDetails = existingInvoice.paymentDetails + paymentDetails,
                paymentStatus = paymentStatus,
            )
            supplierInvoicesTest[invoiceIndex] = updatedInvoice
            resourcesSupplierInvoicesFlow.tryEmit(Resource.success(supplierInvoicesTest))
            return OperationResult.Success
        }

        return OperationResult.Failure(
            stringData.core_data_add_supplier_invoice_failure_generic,
        )
    }
}