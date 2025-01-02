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
import com.casecode.pos.core.designsystem.component.SearchWidgetState
import com.casecode.pos.core.domain.usecase.GetSupplierInvoicesUseCase
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
import kotlin.test.assertTrue

class BillsViewModelTest {
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var viewModel: BillsViewModel
    private val testRepo = TestSupplierInvoicesRepository()
    private val networkMonitor = TestNetworkMonitor()
    private val getSupplierInvoicesUseCase = GetSupplierInvoicesUseCase(testRepo)

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
        val expected = testRepo.supplierInvoicesTest.associateBy { it.invoiceId }
        val actual = viewModel.billsUiState.value

        assertEquals(BillsUiState.Success(expected), actual)
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

    @Test
    fun searchWidgetState_defaultValueIsClosed() = runTest {
        assertEquals(SearchWidgetState.CLOSED, viewModel.searchWidgetState.value)
    }

    @Test
    fun searchWidgetState_togglesBetweenStates() = runTest {
        // Initial state is CLOSED
        assertEquals(SearchWidgetState.CLOSED, viewModel.searchWidgetState.value)

        // Open search
        viewModel.onBillsUiEvent(BillsUiEvent.SearchClicked)
        assertEquals(SearchWidgetState.OPENED, viewModel.searchWidgetState.value)

        // Close search
        viewModel.onBillsUiEvent(BillsUiEvent.ClearRecentSearches)
        assertEquals(SearchWidgetState.CLOSED, viewModel.searchWidgetState.value)
    }

    @Test
    fun searchQuery_filtersSuppliersByNameAndBillNumber() = runTest {
        backgroundScope.launch(UnconfinedTestDispatcher()) { viewModel.billsUiState.collect() }

        // Send initial data
        testRepo.sendSupplierInvoices()

        // Search by supplier name
        viewModel.onBillsUiEvent(BillsUiEvent.SearchQueryChanged("Supplier1"))
        var state = viewModel.billsUiState.value as BillsUiState.Success
        assertTrue(
            state.supplierInvoices.values.all {
                it.supplierName.contains("Supplier1", ignoreCase = true)
            },
        )

        // Search by bill number
        viewModel.onBillsUiEvent(BillsUiEvent.SearchQueryChanged("BILL001"))
        state = viewModel.billsUiState.value as BillsUiState.Success
        assertTrue(
            state.supplierInvoices.values.all {
                it.billNumber.contains("BILL001", ignoreCase = true)
            },
        )

        // Empty search returns all bills
        viewModel.onBillsUiEvent(BillsUiEvent.SearchQueryChanged(""))
        state = viewModel.billsUiState.value as BillsUiState.Success
        assertEquals(testRepo.supplierInvoicesTest.size, state.supplierInvoices.size)
    }
}