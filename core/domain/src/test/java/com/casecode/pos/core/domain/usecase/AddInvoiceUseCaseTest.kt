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

import com.casecode.pos.core.domain.utils.Resource
import com.casecode.pos.core.model.data.users.Item
import com.casecode.pos.core.testing.data.itemsTestData
import com.casecode.pos.core.testing.repository.TestInvoiceRepository
import com.casecode.pos.core.testing.util.MainDispatcherRule
import kotlinx.coroutines.flow.last
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import kotlin.test.Test
import kotlin.test.assertEquals
import com.casecode.pos.core.data.R.string as stringData
import com.casecode.pos.core.domain.R.string as stringDomain

class AddInvoiceUseCaseTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    // Subject under test
    private val testInvoiceRepository = TestInvoiceRepository()
    private val addInvoiceUseCase = AddInvoiceUseCase(testInvoiceRepository)

    @Test
    fun addInvoiceUseCase_whenHasInvoices_returnMessageAddedInvoice() =
        runTest {
            // Given
            // When
            val result = addInvoiceUseCase(itemsTestData).last()
            // Then
            assertEquals(
                result,
                (Resource.success(stringData.core_data_add_invoice_successfully)),
            )
        }

    @Test
    fun addInvoiceUseCase_InputEmptyItems_returnMessageEmptyItems() =
        runTest {
            // Given
            val items = listOf<Item>()
            // When
            val result = addInvoiceUseCase(items).last()
            // Then
            assertEquals(
                result,
                (Resource.empty(message = stringDomain.core_domain_invoice_items_empty)),
            )
        }

    @Test
    fun addInvoiceUseCase_hasError_returnMessageError() =
        runTest {
            // Given
            val items = itemsTestData
            // When
            testInvoiceRepository setReturnError true
            val result = addInvoiceUseCase(items).last()
            // Then
            assertEquals(
                result,
                (Resource.error(stringData.core_data_add_invoice_failure)),
            )
        }
}