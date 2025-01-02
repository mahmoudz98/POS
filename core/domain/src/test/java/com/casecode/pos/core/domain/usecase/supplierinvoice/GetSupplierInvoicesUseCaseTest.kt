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
package com.casecode.pos.core.domain.usecase.supplierinvoice

import com.casecode.pos.core.domain.usecase.GetSupplierInvoicesUseCase
import com.casecode.pos.core.domain.utils.Resource
import com.casecode.pos.core.testing.repository.TestSupplierInvoicesRepository
import com.casecode.pos.core.testing.util.MainDispatcherRule
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import kotlin.test.Test
import kotlin.test.assertEquals
import com.casecode.pos.core.data.R.string as stringData

class GetSupplierInvoicesUseCaseTest {
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    // Subject under test
    private val supplierInvoicesRepository = TestSupplierInvoicesRepository()
    private val getSupplierInvoicesUseCase = GetSupplierInvoicesUseCase(supplierInvoicesRepository)

    @Test
    fun whenInvoicesExist_returnsInvoices() = runTest {
        // Given
        supplierInvoicesRepository.sendSupplierInvoices()
        // When
        val invoices = getSupplierInvoicesUseCase()
        // Then
        assertEquals(
            Resource.success(supplierInvoicesRepository.supplierInvoicesTest),
            invoices.first(),
        )
    }

    @Test
    fun whenHasError_returnsError() = runTest {
        // Given
        supplierInvoicesRepository.setReturnError(true)
        // When
        val invoices = getSupplierInvoicesUseCase()
        // Then
        assertEquals(
            Resource.error(stringData.core_data_error_fetching_supplier_invoices),
            invoices.first(),
        )
    }

    @Test
    fun whenHasNoInvoices_returnsEmpty() = runTest {
        // Given
        supplierInvoicesRepository.setReturnEmpty(true)
        // When
        val invoices = getSupplierInvoicesUseCase()
        // Then
        assertEquals(Resource.empty(), invoices.first())
    }
}