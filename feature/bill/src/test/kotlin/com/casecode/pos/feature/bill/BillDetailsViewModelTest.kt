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
import com.casecode.pos.core.domain.usecase.GetItemsUseCase
import com.casecode.pos.core.domain.usecase.GetSupplierInvoiceDetailsUseCase
import com.casecode.pos.core.domain.usecase.UpdateStockInItemsUseCase
import com.casecode.pos.core.domain.usecase.UpdateSupplierInvoiceUseCase
import com.casecode.pos.core.model.data.users.Item
import com.casecode.pos.core.model.data.users.SupplierInvoice
import com.casecode.pos.core.testing.repository.TestItemRepository
import com.casecode.pos.core.testing.repository.TestSupplierInvoicesRepository
import com.casecode.pos.core.testing.util.MainDispatcherRule
import com.casecode.pos.core.testing.util.TestNetworkMonitor
import com.casecode.pos.feature.bill.creation.BillInputState
import com.casecode.pos.feature.bill.detials.BillDetailUiState
import com.casecode.pos.feature.bill.detials.BillDetailsViewModel
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.time.Duration
import com.casecode.pos.core.ui.R.string as uiString

@Suppress("CONTEXT_RECEIVERS_DEPRECATED")
class BillDetailsViewModelTest {
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()
    private lateinit var viewModel: BillDetailsViewModel
    private val itemRepository = TestItemRepository()
    private val invoiceSuppliersRepo = TestSupplierInvoicesRepository()

    private val getItemsUseCase: GetItemsUseCase = GetItemsUseCase(itemRepository)
    private val addPaymentDetailsUseCase = AddPaymentDetailsUseCase(invoiceSuppliersRepo)
    private val getSupplierInvoiceDetailsUseCase =
        GetSupplierInvoiceDetailsUseCase(invoiceSuppliersRepo)
    private val updateSupplierInvoice = UpdateSupplierInvoiceUseCase(invoiceSuppliersRepo)
    private val updateStockInItemsUseCase: UpdateStockInItemsUseCase =
        UpdateStockInItemsUseCase(itemRepository)
    private val networkMonitor = TestNetworkMonitor()

    @Before
    fun setup() {
        viewModel = BillDetailsViewModel(
            networkMonitor = networkMonitor,
            getItemsUseCase = getItemsUseCase,
            getBillDetailsUseCase = getSupplierInvoiceDetailsUseCase,
            addPaymentDetailsUseCase = addPaymentDetailsUseCase,
            updateSupplierInvoiceUseCase = updateSupplierInvoice,
            updateStockInItemsUseCase = updateStockInItemsUseCase,
            savedStateHandle = SavedStateHandle(),
        )
    }

    @Test
    fun billDetailUiStateIsSuccessAfterLoading() = runTest {
        collectState(viewModel.billDetailsUiState)
        val invoice = invoiceSuppliersRepo.supplierInvoicesTest[0]
        viewModel.onBillIdChange(invoice.invoiceId)
        testScheduler.advanceUntilIdle()
        assertEquals(BillDetailUiState.Loading, viewModel.billDetailsUiState.replayCache.first())
        assertEquals(
            BillDetailUiState.Success(invoice),
            viewModel.billDetailsUiState.replayCache.last(),
        )
    }

    @Test
    fun billDetailUiStateIsEmptyAfterLoading() = runTest {
        backgroundScope.launch(
            UnconfinedTestDispatcher(),
        ) { viewModel.billDetailsUiState.collect() }

        viewModel.onBillIdChange("")
        assertEquals(
            BillDetailUiState.EmptySelection,
            viewModel.billDetailsUiState.replayCache.last(),
        )
    }

    @Test
    fun updateStockThenUpdateBill_whenHasInvoice_returnSuccess() = runTest {
        collectStates()
        // Setup: Prepare an existing invoice in the billDetailsUiState
        val existingInvoice = invoiceSuppliersRepo.supplierInvoicesTest[0]
        viewModel.onBillIdChange(existingInvoice.invoiceId)

        // Modify invoice details
        val modifiedInvoice = createModifiedInvoice(existingInvoice)
        viewModel.billInputState.value = BillInputState(modifiedInvoice)
        networkMonitor.setConnected(true)

        // Execute the method
        viewModel.updateStockThenUpdateBill()

        // Verify success message
        assertEquals(R.string.feature_bill_updated_successfully, viewModel.userMessage.value)
    }

    @Test
    fun updateStockThenUpdateBill_whenNetworkUnavailable_returnErrorMessage() = runTest {
        // Simulate offline state
        networkMonitor.setConnected(false)

        // Execute the method
        viewModel.updateStockThenUpdateBill()

        // Verify network error message
        assertEquals(uiString.core_ui_error_network, viewModel.userMessage.value)
    }

    @Test
    fun updateStockThenUpdateBill_whenNoChanges_returnDuplicateMessage() = runTest {
        collectStates()

        // Set billInputState to the same invoice
        val existingInvoice = invoiceSuppliersRepo.supplierInvoicesTest[0]
        viewModel.onBillIdChange(existingInvoice.invoiceId)
        networkMonitor.setConnected(true)
        // Execute the method
        viewModel.updateStockThenUpdateBill()

        // Verify duplicate update message
        assertEquals(R.string.feature_bill_updated_failure_duplicate, viewModel.userMessage.value)
    }

    @Test
    fun updateStockThenUpdateBill_whenItemsAddedAndRemoved_returnSuccess() = runTest {
        collectStates()
        // Setup: Existing invoice
        val existingInvoice = invoiceSuppliersRepo.supplierInvoicesTest[0]
        viewModel.onBillIdChange(existingInvoice.invoiceId)

        // Create modified invoice with some items added and some removed
        viewModel.billInputState.value = BillInputState(createModifiedItemsInvoice(existingInvoice))
        networkMonitor.setConnected(true)
        // Execute the method
        viewModel.updateStockThenUpdateBill()

        assertEquals(R.string.feature_bill_updated_successfully, viewModel.userMessage.value)
    }

    private fun createModifiedInvoice(existingInvoice: SupplierInvoice): SupplierInvoice {
        // Create a modified version of the existing invoice
        return existingInvoice.copy(
            dueDate = existingInvoice.dueDate.plus(Duration.parse("5d")),
            totalAmount = existingInvoice.totalAmount + 100.0,
        )
    }

    private fun createModifiedItemsInvoice(existingInvoice: SupplierInvoice): SupplierInvoice {
        // Create a modified version of the existing invoice
        val item = Item(
            name = "CaseCode",
            costPrice = 20.0,
            quantity = 1,
            sku = "3421423423",
        )
        val newItems = existingInvoice.invoiceItems.toMutableList()
        newItems.removeLastOrNull()
        newItems.add(item)
        return existingInvoice.copy(
            invoiceItems = newItems,
            dueDate = existingInvoice.dueDate.plus(Duration.parse("5d")),
            totalAmount = existingInvoice.totalAmount + 100.0,
        )
    }

    context(TestScope)
    private fun collectStates() {
        collectState(viewModel.billDetailsUiState)
        collectState(viewModel.billInputState)
        collectState(viewModel.userMessage)
    }

    context(TestScope)
    private fun collectState(stateFlow: SharedFlow<*>) {
        backgroundScope.launch(UnconfinedTestDispatcher()) { stateFlow.collect() }
    }
}