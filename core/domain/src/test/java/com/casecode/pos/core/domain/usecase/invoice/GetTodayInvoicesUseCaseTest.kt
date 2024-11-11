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
package com.casecode.pos.core.domain.usecase.invoice

import com.casecode.pos.core.domain.usecase.GetTodayInvoicesUseCase
import com.casecode.pos.core.domain.utils.Resource
import com.casecode.pos.core.model.data.users.Invoice
import com.casecode.pos.core.testing.data.invoicesTestData
import com.casecode.pos.core.testing.repository.TestInvoiceRepository
import com.casecode.pos.core.testing.util.MainDispatcherRule
import kotlinx.coroutines.flow.last
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import kotlin.test.Test
import kotlin.test.assertEquals

class GetTodayInvoicesUseCaseTest {
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    // Subject under test
    private val testInvoiceRepository = TestInvoiceRepository()
    private val getInvoiceTodayUseCase = GetTodayInvoicesUseCase(testInvoiceRepository)

    @Test
    fun whenHasInvoices_returnListOfInvoices() =
        runTest {
            // Given
            val expected = Resource.success<List<Invoice>>(invoicesTestData)

            // When
            val result = getInvoiceTodayUseCase().last()

            // Then
            assertEquals(result, expected)
        }

    @Test
    fun whenHasError_returnError() =
        runTest {
            // Given
            testInvoiceRepository.setReturnError(true)

            // When
            val result = getInvoiceTodayUseCase().last()

            // Then
            assert(result is Resource.Error)
        }

    @Test
    fun whenEmptyInvoices_returnEmpty() =
        runTest {
            // Given
            testInvoiceRepository.setReturnEmpty(true)

            // When
            val result = getInvoiceTodayUseCase().last()

            // Then
            assert(result is Resource.Empty)
        }
}