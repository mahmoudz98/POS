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
package com.casecode.pos.core.data.repository.business

import com.casecode.pos.core.firebase.model.SupportedCurrency
import com.casecode.pos.core.testing.datasource.TestConfigDataSource
import com.casecode.pos.core.testing.util.CoroutinesTestRule
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class CurrencyRepositoryImplTest {

    @get:Rule
    val coroutinesRule = CoroutinesTestRule()

    private lateinit var configDataSource: TestConfigDataSource
    private lateinit var repository: CurrencyRepositoryImpl

    @Before
    fun setup() {
        configDataSource = TestConfigDataSource()
        repository = CurrencyRepositoryImpl(
            configDataSource = configDataSource,
            ioDispatcher = UnconfinedTestDispatcher(),
        )
    }

    @Test
    fun getSupportedCurrencies_currenciesAvailable_returnsCurrencies() = runTest {
        // Given
        val currencies = listOf(
            SupportedCurrency("USD", "US Dollar", "دولار", "$", 2),
            SupportedCurrency("EUR", "Euro", "يورو", "€", 2),
        )
        configDataSource.setSupportedCurrencies(currencies)

        // When
        val result = repository.getSupportedCurrencies()

        // Then
        assertTrue(result.isSuccess)
        assertEquals(2, result.getOrNull()?.size)
        assertEquals("USD", result.getOrNull()?.first()?.code)
    }

    @Test
    fun getSupportedCurrencies_empty_returnsEmpty() = runTest {
        // Given
        configDataSource.setSupportedCurrencies(emptyList())

        // When
        val result = repository.getSupportedCurrencies()

        // Then
        assertTrue(result.isSuccess)
        assertEquals(result.getOrNull()?.isEmpty(), true)
    }

    @Test
    fun getSupportedCurrencies_dataSourceError_returnsFailure() = runTest {
        // Given
        configDataSource.setShouldReturnError(true)

        // When
        val result = repository.getSupportedCurrencies()

        // Then
        assertTrue(result.isFailure)
    }
}
