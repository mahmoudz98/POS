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
package com.casecode.pos.feature.bill

import androidx.lifecycle.SavedStateHandle
import com.casecode.pos.core.domain.usecase.AddPaymentDetailsUseCase
import com.casecode.pos.core.domain.usecase.GetSupplierInvoicesUseCase
import com.casecode.pos.core.domain.usecase.UpdateSupplierInvoiceUseCase
import com.casecode.pos.core.testing.repository.TestSupplierInvoicesRepository
import com.casecode.pos.core.testing.util.MainDispatcherRule
import com.casecode.pos.core.testing.util.TestNetworkMonitor
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import kotlin.test.Test
import kotlin.test.assertEquals

class BillViewModelTest {
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var viewModel: BillsViewModel
    private val testRepo = TestSupplierInvoicesRepository()
    private val networkMonitor = TestNetworkMonitor()
    private val getSupplierInvoicesUseCase = GetSupplierInvoicesUseCase(testRepo)
    private val updateSupplierInvoiceUseCase = UpdateSupplierInvoiceUseCase(testRepo)
    private val addPaymentDetailsUseCase = AddPaymentDetailsUseCase(testRepo)

    @Before
    fun setup() {
        viewModel = BillsViewModel(
            networkMonitor,
            getSupplierInvoicesUseCase,
            SavedStateHandle(),
        )
    }

    @Test
    fun billUiStateIsSuccessAfterLoadingBills() = runTest {
        backgroundScope.launch(UnconfinedTestDispatcher()) { viewModel.billsUiState.collect() }
        assertEquals(BillsUiState.Loading, viewModel.billsUiState.value)
        testRepo.sendSupplierInvoices()
        val actual = viewModel.billsUiState.value
        val expected = testRepo.supplierInvoicesTest.associateBy { it.invoiceId }

        assertEquals(BillsUiState.Success(expected), viewModel.billsUiState.value)
    }

    @Test
    fun billUiStateIsEmptyAfterLoadingState() = runTest {
        backgroundScope.launch(UnconfinedTestDispatcher()) { viewModel.billsUiState.collect() }
        assertEquals(BillsUiState.Loading, viewModel.billsUiState.value)
        testRepo.setReturnEmpty(true)
        val actual = viewModel.billsUiState.value
        assertEquals(BillsUiState.Empty, actual)
    }

    @Test
    fun billUiStateIsErrorAfterLoadingState() = runTest {
        backgroundScope.launch(UnconfinedTestDispatcher()) { viewModel.billsUiState.collect() }
        assertEquals(BillsUiState.Loading, viewModel.billsUiState.value)
        testRepo.setReturnError(true)
        val actual = viewModel.billsUiState.value
        assertEquals(BillsUiState.Error, actual)
    }
}