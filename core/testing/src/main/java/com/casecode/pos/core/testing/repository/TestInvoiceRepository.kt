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

import com.casecode.pos.core.data.R
import com.casecode.pos.core.domain.repository.InvoiceRepository
import com.casecode.pos.core.domain.utils.Resource
import com.casecode.pos.core.model.data.users.Invoice
import com.casecode.pos.core.model.data.users.InvoiceGroup
import com.casecode.pos.core.testing.base.BaseTestRepository
import com.casecode.pos.core.testing.data.invoicesGroupTestData
import com.casecode.pos.core.testing.data.invoicesTestData
import javax.inject.Inject

class TestInvoiceRepository
@Inject
constructor() :
    BaseTestRepository(),
    InvoiceRepository {
    override suspend fun addInvoice(invoice: Invoice): Resource<Int> {
        if (shouldReturnError) {
            return Resource.error(R.string.core_data_add_invoice_failure)
        }
        return Resource.success(R.string.core_data_add_invoice_successfully)
    }

    override suspend fun getInvoices(): Resource<List<InvoiceGroup>> {
        if (shouldReturnError) {
            return Resource.error(R.string.core_data_get_invoice_failure)
        }
        if (shouldReturnEmpty) {
            return Resource.empty()
        }
        return Resource.success(invoicesGroupTestData)
    }

    override suspend fun getTodayInvoices(): Resource<List<Invoice>> {
        if (shouldReturnError) {
            return Resource.error(R.string.core_data_get_invoice_failure)
        }
        if (shouldReturnEmpty) {
            return Resource.empty()
        }
        return Resource.success(invoicesTestData)
    }

    override fun init() = Unit
}