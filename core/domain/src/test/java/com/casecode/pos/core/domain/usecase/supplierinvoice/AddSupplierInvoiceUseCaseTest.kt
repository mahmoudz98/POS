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

import com.casecode.pos.core.domain.usecase.AddSupplierInvoiceUseCase
import com.casecode.pos.core.domain.utils.OperationResult
import com.casecode.pos.core.model.data.users.SupplierInvoice
import com.casecode.pos.core.testing.repository.TestSupplierInvoicesRepository
import com.casecode.pos.core.testing.util.MainDispatcherRule
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import kotlin.test.Test
import kotlin.test.assertEquals
import com.casecode.pos.core.data.R.string as stringData

class AddSupplierInvoiceUseCaseTest {
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val testSupplierRepository = TestSupplierInvoicesRepository()
    private val addSupplierInvoiceUseCase = AddSupplierInvoiceUseCase(testSupplierRepository)
    private val testSupplier = SupplierInvoice()

    @Test
    fun whenSupplierInvoiceAdded_returnSuccess() = runTest {
        val result = addSupplierInvoiceUseCase(testSupplier)
        assertEquals(OperationResult.Success, result)
    }

    @Test
    fun whenSupplierInvoiceAddFails_returnFailure() = runTest {
        testSupplierRepository.setReturnError(true)
        val result = addSupplierInvoiceUseCase(testSupplier)
        assertEquals(
            OperationResult.Failure(stringData.core_data_add_supplier_invoice_failure_generic),
            result,
        )
    }
}