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

import com.casecode.pos.core.domain.R
import com.casecode.pos.core.domain.repository.InvoiceRepository
import com.casecode.pos.core.domain.utils.Resource
import com.casecode.pos.core.model.data.users.Invoice
import com.casecode.pos.core.model.data.users.InvoiceGroup
import com.casecode.pos.core.model.data.users.Item
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class AddInvoiceUseCase
@Inject
constructor(
    private val invoiceRepository: InvoiceRepository,
) {
    operator fun invoke(items: List<Item>): Flow<Resource<Int>> {
        return flow {
            emit(Resource.Loading)
            if (items.isEmpty()) {
                return@flow emit(
                    Resource.Companion.empty(message = R.string.core_domain_invoice_items_empty),
                )
            }
            val invoice = Invoice(items = items)
            emit(invoiceRepository.addInvoice(invoice))
        }
    }
}

class GetInvoicesUseCase
@Inject
constructor(
    private val invoiceRepository: InvoiceRepository,
) {
    operator fun invoke(): Flow<Resource<List<InvoiceGroup>>> = flow {
        emit(Resource.Loading)
        emit(invoiceRepository.getInvoices())
    }
}

class GetTodayInvoicesUseCase
@Inject
constructor(
    private val invoiceRepository: InvoiceRepository,
) {
    operator fun invoke(): Flow<Resource<List<Invoice>>> = flow {
        emit(Resource.Loading)
        emit(invoiceRepository.getTodayInvoices())
    }
}