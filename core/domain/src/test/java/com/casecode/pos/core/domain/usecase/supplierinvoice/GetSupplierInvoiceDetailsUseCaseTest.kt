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

import com.casecode.pos.core.domain.usecase.GetSupplierInvoiceDetailsUseCase
import com.casecode.pos.core.domain.utils.Resource
import com.casecode.pos.core.testing.repository.TestSupplierInvoicesRepository
import com.casecode.pos.core.testing.util.MainDispatcherRule
import kotlinx.coroutines.flow.last
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import kotlin.test.Test
import kotlin.test.assertEquals

class GetSupplierInvoiceDetailsUseCaseTest {
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    // Subject under test
    private val testRepo = TestSupplierInvoicesRepository()
    private val getSupplierInvoiceDetailsUseCase = GetSupplierInvoiceDetailsUseCase(testRepo)

    @Test
    fun `whenHasSupplierId_returnsSupplierInvoice`() = runTest {
        val supplierInvoiceResult = getSupplierInvoiceDetailsUseCase("INV001")
        backgroundScope.launch(UnconfinedTestDispatcher()) { supplierInvoiceResult.collect {} }
        assertEquals(
            Resource.Success(testRepo.supplierInvoicesTest[0]),
            supplierInvoiceResult.last(),
        )
    }
}