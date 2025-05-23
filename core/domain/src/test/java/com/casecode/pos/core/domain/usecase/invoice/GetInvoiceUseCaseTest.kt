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

import com.casecode.pos.core.domain.usecase.GetInvoicesUseCase
import com.casecode.pos.core.domain.utils.Resource
import com.casecode.pos.core.testing.data.invoicesGroupTestData
import com.casecode.pos.core.testing.repository.TestInvoiceRepository
import com.casecode.pos.core.testing.util.MainDispatcherRule
import kotlinx.coroutines.flow.last
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import kotlin.test.Test
import kotlin.test.assertEquals

class GetInvoiceUseCaseTest {
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    // Subject under test
    private val testInvoiceRepository = TestInvoiceRepository()
    private val getInvoicesUseCase = GetInvoicesUseCase(testInvoiceRepository)

    @Test
    fun whenHasInvoices_returnListOfInvoices() = runTest {
        // Given
        val expected = Resource.success(invoicesGroupTestData)

        // When
        val result = getInvoicesUseCase().last()

        // Then
        assertEquals(expected, result)
    }

    @Test
    fun whenHasError_returnError() = runTest {
        // Given
        testInvoiceRepository.setReturnError(true)

        // When
        val result = getInvoicesUseCase().last()

        // Then
        assert(result is Resource.Error)
    }

    @Test
    fun whenEmptyInvoices_returnEmpty() = runTest {
        // Given
        testInvoiceRepository.setReturnEmpty(true)

        // When
        val result = getInvoicesUseCase().last()

        // Then
        assert(result is Resource.Empty)
    }
}