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
package com.casecode.pos.feature.statistics

import com.casecode.pos.core.data.R
import com.casecode.pos.core.testing.base.BaseTest
import com.casecode.pos.core.testing.data.invoicesTestData
import com.casecode.pos.core.testing.util.MainDispatcherRule
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertSame
import org.junit.Assert.assertTrue
import org.junit.Rule
import kotlin.test.Test

class ReportsViewModelTest : BaseTest() {
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()
    private lateinit var viewModel: ReportsViewModel

    override fun init() {
        viewModel = ReportsViewModel(networkMonitor, getTodayInvoices)
    }

    @Test
    fun fetchInvoices_whenHasInvoices_shouldReturnInvoices() = runTest {
        val collectJob = launch(UnconfinedTestDispatcher()) { viewModel.uiState.collect() }
        val expected = invoicesTestData
        viewModel.fetchInvoices()

        val actual = viewModel.uiState.value.invoices

        assertSame(actual, expected)
        collectJob.cancel()
    }

    @Test
    fun fetchInvoices_whenError_returnErrorMessage() = runTest {
        val collectJob = launch { viewModel.uiState.collect() }
        invoiceRepository.setReturnError(true)
        viewModel.fetchInvoices()

        assertEquals(
            viewModel.uiState.value.userMessage,
            R.string.core_data_get_invoice_failure,
        )
        collectJob.cancel()
    }

    @Test
    fun fetchInvoices_whenEmpty_returnEmpty() = runTest {
        val collectJob = launch { viewModel.uiState.collect() }
        invoiceRepository.setReturnEmpty(true)
        viewModel.fetchInvoices()

        assertTrue(viewModel.uiState.value.isEmpty)
        collectJob.cancel()
    }
}