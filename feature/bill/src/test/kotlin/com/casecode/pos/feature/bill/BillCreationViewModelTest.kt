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
import com.casecode.pos.core.domain.usecase.AddSupplierInvoiceUseCase
import com.casecode.pos.core.domain.usecase.GetItemsUseCase
import com.casecode.pos.core.domain.usecase.GetSuppliersUseCase
import com.casecode.pos.core.domain.usecase.UpdateStockInItemsUseCase
import com.casecode.pos.core.testing.repository.TestItemRepository
import com.casecode.pos.core.testing.repository.TestSupplierInvoicesRepository
import com.casecode.pos.core.testing.repository.TestSupplierRepository
import com.casecode.pos.core.testing.util.MainDispatcherRule
import com.casecode.pos.core.testing.util.TestNetworkMonitor
import com.casecode.pos.feature.bill.creation.BillCreationViewModel
import com.casecode.pos.feature.bill.creation.SearchItemUiState
import com.casecode.pos.feature.bill.creation.SearchSupplierUiState
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import kotlin.test.assertEquals
import com.casecode.pos.core.ui.R.string as uiString

class BillCreationViewModelTest {
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()
    private val itemRepository = TestItemRepository()
    private val supplierRepository = TestSupplierRepository()
    private val invoiceSuppliersRepo = TestSupplierInvoicesRepository()

    private val getItemsUseCase: GetItemsUseCase = GetItemsUseCase(itemRepository)
    private val getSuppliers = GetSuppliersUseCase(supplierRepository)
    private val addSupplierInvoice = AddSupplierInvoiceUseCase(invoiceSuppliersRepo)
    private val updateStockInItemsUseCase: UpdateStockInItemsUseCase =
        UpdateStockInItemsUseCase(itemRepository)
    private val networkMonitor = TestNetworkMonitor()
    private lateinit var viewModel: BillCreationViewModel

    @Before
    fun setup() {
        viewModel = BillCreationViewModel(
            networkMonitor,
            getItemsUseCase,
            getSuppliers,
            addSupplierInvoice,
            updateStockInItemsUseCase,
            SavedStateHandle(),
        )
    }

    @Test
    fun filterSupplierState_isEmptySearch() = runTest {
        backgroundScope.launch(
            UnconfinedTestDispatcher(),
        ) { viewModel.filterSupplierState.collect() }
        assertEquals(SearchSupplierUiState.EmptyQuery, viewModel.filterSupplierState.value)
        supplierRepository.sendSuppliers()
        assertEquals(
            SearchSupplierUiState.EmptyQuery,
            viewModel.filterSupplierState.value,
        )
    }

    @Test
    fun filterSupplierState_isSuccessAfterEmptySearchState() = runTest {
        backgroundScope.launch(
            UnconfinedTestDispatcher(),
        ) { viewModel.filterSupplierState.collect() }
        assertEquals(SearchSupplierUiState.EmptyQuery, viewModel.filterSupplierState.value)
        val searchSupplier = "John"
        viewModel.onSearchQuerySupplierChanged(searchSupplier)
        val actualSuppliers = supplierRepository.suppliersTest
            .map { it.contactName }
            .filter { it.contains(searchSupplier, ignoreCase = true) }
        supplierRepository.sendSuppliers()
        assertEquals(
            SearchSupplierUiState.Success(actualSuppliers),
            viewModel.filterSupplierState.value,
        )
    }

    @Test
    fun filterSupplierState_isLoadFailedAfterEmptySearchState() = runTest {
        backgroundScope.launch(
            UnconfinedTestDispatcher(),
        ) { viewModel.filterSupplierState.collect() }
        assertEquals(SearchSupplierUiState.EmptyQuery, viewModel.filterSupplierState.value)
        val searchSupplier = "John"
        viewModel.onSearchQuerySupplierChanged(searchSupplier)
        supplierRepository.setReturnError(true)
        assertEquals(
            SearchSupplierUiState.LoadFailed,
            viewModel.filterSupplierState.value,
        )
    }

    @Test
    fun filterSupplierState_isEmptyResultAfterEmptySearchState() = runTest {
        backgroundScope.launch(
            UnconfinedTestDispatcher(),
        ) { viewModel.filterSupplierState.collect() }
        assertEquals(SearchSupplierUiState.EmptyQuery, viewModel.filterSupplierState.value)
        val searchSupplier = "fake supplier name "
        viewModel.onSearchQuerySupplierChanged(searchSupplier)
        supplierRepository.sendSuppliers()
        assertEquals(
            SearchSupplierUiState.EmptyResult,
            viewModel.filterSupplierState.value,
        )
    }

    @Test
    fun filterItemState_isEmptySearch() = runTest {
        backgroundScope.launch(
            UnconfinedTestDispatcher(),
        ) { viewModel.filterItemsUiState.collect() }
        assertEquals(SearchItemUiState.EmptyQuery, viewModel.filterItemsUiState.value)
        itemRepository.sendItems()
        assertEquals(SearchItemUiState.EmptyQuery, viewModel.filterItemsUiState.value)
    }

    @Test
    fun filterItemState_isEmptyResultAfterEmptySearchState() = runTest {
        backgroundScope.launch(
            UnconfinedTestDispatcher(),
        ) { viewModel.filterItemsUiState.collect() }
        assertEquals(SearchItemUiState.EmptyQuery, viewModel.filterItemsUiState.value)
        val searchItem = "item"
        viewModel.onSearchQueryItemChanged(searchItem)
        itemRepository.sendItems()
        assertEquals(SearchItemUiState.EmptyResult, viewModel.filterItemsUiState.value)
    }

    @Test
    fun filterItemState_isSuccessAfterEmptySearchState() = runTest {
        backgroundScope.launch(
            UnconfinedTestDispatcher(),
        ) { viewModel.filterItemsUiState.collect() }
        assertEquals(SearchItemUiState.EmptyQuery, viewModel.filterItemsUiState.value)
        val searchItem = "Iphone1"
        viewModel.onSearchQueryItemChanged(searchItem)
        val actualSuppliers = itemRepository.itemsTest
            .filter { it.name.contains(searchItem, ignoreCase = true) }
        itemRepository.sendItems()
        assertEquals(SearchItemUiState.Success(actualSuppliers), viewModel.filterItemsUiState.value)
    }

    @Test
    fun filterItemState_isLoadFailedAfterEmptySearchState() = runTest {
        backgroundScope.launch(
            UnconfinedTestDispatcher(),
        ) { viewModel.filterItemsUiState.collect() }
        assertEquals(SearchItemUiState.EmptyQuery, viewModel.filterItemsUiState.value)
        val searchItem = "Iphone1"
        viewModel.onSearchQueryItemChanged(searchItem)

        itemRepository.setReturnError(true)
        assertEquals(SearchItemUiState.LoadFailed, viewModel.filterItemsUiState.value)
    }

    @Test
    fun updateStockThenAddBill_whenHasInvoice_returnSuccess() = runTest {
        backgroundScope.launch(UnconfinedTestDispatcher()) { viewModel.billInputState.collect() }

        viewModel.updateStockThenAddBill()
    }

    @Test
    fun updateStockThenAddBill_whenNetworkUnavailable_returnErrorMessage() = runTest {
        backgroundScope.launch(UnconfinedTestDispatcher()) { viewModel.userMessage.collect() }
        networkMonitor.setConnected(false)
        viewModel.updateStockThenAddBill()
        assertEquals(uiString.core_ui_error_network, viewModel.userMessage.value)
    }
}