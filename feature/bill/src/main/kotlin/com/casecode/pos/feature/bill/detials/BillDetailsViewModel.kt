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
package com.casecode.pos.feature.bill.detials

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.casecode.pos.core.data.utils.NetworkMonitor
import com.casecode.pos.core.domain.usecase.AddPaymentDetailsUseCase
import com.casecode.pos.core.domain.usecase.GetItemsUseCase
import com.casecode.pos.core.domain.usecase.GetSupplierInvoiceDetailsUseCase
import com.casecode.pos.core.domain.usecase.UpdateStockInItemsUseCase
import com.casecode.pos.core.domain.usecase.UpdateSupplierInvoiceUseCase
import com.casecode.pos.core.domain.utils.OperationResult
import com.casecode.pos.core.domain.utils.Resource
import com.casecode.pos.core.model.data.users.Item
import com.casecode.pos.core.model.data.users.PaymentDetails
import com.casecode.pos.core.model.data.users.SupplierInvoice
import com.casecode.pos.core.ui.shareInWhileSubscribed
import com.casecode.pos.core.ui.stateInWhileSubscribed
import com.casecode.pos.feature.bill.R
import com.casecode.pos.feature.bill.creation.BillInputState
import com.casecode.pos.feature.bill.creation.SearchItemUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.math.abs
import com.casecode.pos.core.ui.R.string as uiString

@HiltViewModel
class BillDetailsViewModel @Inject constructor(
    networkMonitor: NetworkMonitor,
    getItemsUseCase: GetItemsUseCase,
    private val getBillDetailsUseCase: GetSupplierInvoiceDetailsUseCase,
    private val addPaymentDetailsUseCase: AddPaymentDetailsUseCase,
    private val updateSupplierInvoiceUseCase: UpdateSupplierInvoiceUseCase,
    private val updateStockInItemsUseCase: UpdateStockInItemsUseCase,
    private val savedStateHandle: SavedStateHandle,
) : ViewModel() {

    private val isOnline: StateFlow<Boolean> =
        networkMonitor.isOnline.stateIn(viewModelScope, SharingStarted.Companion.Eagerly, false)

    val searchQueryItem = savedStateHandle.getStateFlow(key = SEARCH_ITEM_QUERY, "")
    val billInputState = MutableStateFlow<BillInputState>(BillInputState())

    val filterItemsUiState: StateFlow<SearchItemUiState> =
        combine(getItemsUseCase(), searchQueryItem) { result, itemQuery ->
            if (itemQuery.isEmpty()) {
                SearchItemUiState.EmptyQuery
            } else {
                when (result) {
                    is Resource.Loading -> SearchItemUiState.Loading
                    is Resource.Error -> SearchItemUiState.LoadFailed
                    is Resource.Empty -> SearchItemUiState.EmptyResult
                    is Resource.Success -> {
                        val filterItems = result.data.filter {
                            it.name.contains(itemQuery, ignoreCase = true) ||
                                it.sku.contains(itemQuery, ignoreCase = true) ||
                                it.category.contains(itemQuery, ignoreCase = true)
                        }
                        if (filterItems.isEmpty()) {
                            SearchItemUiState.EmptyResult
                        } else {
                            SearchItemUiState.Success(filterItems)
                        }
                    }
                }
            }
        }.stateInWhileSubscribed(SearchItemUiState.EmptyQuery)
    private val selectedBillId = savedStateHandle.getStateFlow(
        key = SELECTED_BILL_ID_KEY,
        initialValue = "",
    )
    val billDetailsUiState: SharedFlow<BillDetailUiState> = selectedBillId.flatMapLatest { billId ->
        if (billId.isEmpty()) {
            flowOf(BillDetailUiState.EmptySelection)
        } else {
            getBillDetailsUseCase(billId).map { resource ->
                mapResourceToUiState(resource)
            }
        }
    }.shareInWhileSubscribed(2)
    private val _itemSelected = MutableStateFlow<Item?>(null)
    val itemSelected = _itemSelected.asStateFlow()
    private val _userMessage = MutableStateFlow<Int?>(null)
    val userMessage = _userMessage.asStateFlow()

    private fun mapResourceToUiState(resource: Resource<SupplierInvoice>): BillDetailUiState {
        return when (resource) {
            is Resource.Loading -> BillDetailUiState.Loading
            is Resource.Error -> BillDetailUiState.Error
            is Resource.Success -> {
                billInputState.update { BillInputState(resource.data) }
                BillDetailUiState.Success(resource.data)
            }
            is Resource.Empty -> BillDetailUiState.EmptySelection
        }
    }
    fun onSearchQueryItemChanged(query: String) {
        savedStateHandle[SEARCH_ITEM_QUERY] = query
    }

    fun addBillItem(item: Item) {
        billInputState.update { it.apply { addItem(item) } }
    }

    fun onSelectedItem(item: Item) {
        _itemSelected.value = item
    }

    fun updateBillItem(item: Item) {
        billInputState.update { it.apply { updateItem(item) } }
    }

    fun removeItem(item: Item) {
        billInputState.update { it.apply { removeItem(item) } }
    }

    fun onBillIdChange(billId: String?) {
        savedStateHandle[SELECTED_BILL_ID_KEY] = billId
    }

    fun updateStockThenUpdateBill() {
        viewModelScope.launch {
            if (!isOnline.value) {
                showSnackbarMessage(uiString.core_ui_error_network)
                return@launch
            }
            val oldInvoice =
                (billDetailsUiState.replayCache.last() as? BillDetailUiState.Success)?.supplierInvoice
            if (oldInvoice == null) {
                showSnackbarMessage(R.string.feature_bill_updated_failure_duplicate)
                return@launch
            }

            val inputInvoice = billInputState.value

            // Compare items and quantities
            val (itemsToRemove, itemsToAdd, itemsToUpdate) =
                compareInvoiceItems(oldInvoice.invoiceItems, inputInvoice.invoiceItems)
            val isNoChange = itemsToRemove.isEmpty() &&
                itemsToAdd.isEmpty() &&
                itemsToUpdate.isEmpty() &&
                oldInvoice.dueDate == inputInvoice.dueDate &&
                oldInvoice.issueDate == inputInvoice.issueDate

            // Check if no changes were made
            if (isNoChange) {
                showSnackbarMessage(R.string.feature_bill_updated_failure_duplicate)
                return@launch
            }
            println(
                """First item to update: $itemsToUpdate
                || Items to add: $itemsToAdd
                || Items to remove: $itemsToRemove
                """.trimMargin(),
            )
            val supplierInvoice = SupplierInvoice(
                invoiceId = oldInvoice.invoiceId,
                billNumber = oldInvoice.billNumber,
                supplierName = inputInvoice.supplierName,
                issueDate = inputInvoice.issueDate,
                dueDate = inputInvoice.dueDate,
                totalAmount = inputInvoice.total,
                discountType = inputInvoice.discountTypeCurrency,
                amountDiscounted = inputInvoice.discount.toDoubleOrNull() ?: 0.0,
                invoiceItems = inputInvoice.invoiceItems,
            )

            // Update stock for removed, added, and updated items
            when (
                val updateResult =
                    updateStockForItemChanges(itemsToRemove, itemsToAdd, itemsToUpdate)
            ) {
                is OperationResult.Success -> {
                    updateBill(supplierInvoice)
                }

                is OperationResult.Failure -> showSnackbarMessage(updateResult.message)
            }
        }
    }

    fun compareInvoiceItems(
        oldItems: List<Item>,
        newItems: List<Item>,
    ): Triple<List<Item>, List<Item>, List<Pair<Item, Item>>> {
        // Create maps for quick lookup by SKU
        val oldItemMap = oldItems.associateBy { it.hashCode() }
        val newItemMap = newItems.associateBy { it.hashCode() }

        // Items to remove: in old list but not in the new list
        val itemsToRemove = oldItems.filter { it.hashCode() !in newItemMap }

        // Items to add: in new list but not in the old list
        val itemsToAdd = newItems.filter { it.hashCode() !in oldItemMap }

        // Items to update: exist in both lists but with different quantities
        val itemsToUpdate = newItems.mapNotNull { newItem ->
            val oldItem = oldItemMap[newItem.hashCode()]
            if (oldItem != null && (
                    oldItem.quantity != newItem.quantity &&
                        oldItem.costPrice != newItem.costPrice
                    )
            ) {
                oldItem to newItem
            } else {
                null
            }
        }

        return Triple(itemsToRemove, itemsToAdd, itemsToUpdate)
    }

    private suspend fun updateStockForItemChanges(
        itemsToRemove: List<Item>,
        itemsToAdd: List<Item>,
        itemsToUpdate: List<Pair<Item, Item>>,
    ): OperationResult {
        // Collect all stock update operations
        val stockUpdateOperations = mutableListOf<OperationResult>()

        // Handle removed items (subtract from stock)
        if (itemsToRemove.isNotEmpty()) {
            val removeOperation = updateStockInItemsUseCase(
                items = itemsToRemove.map { it.copy(quantity = it.quantity) },
                isIncrement = false,
            )
            stockUpdateOperations.add(removeOperation)
        }

        // Handle added items (add to stock)
        if (itemsToAdd.isNotEmpty()) {
            val addOperation = updateStockInItemsUseCase(
                items = itemsToAdd.map { it.copy(quantity = it.quantity) },
                isIncrement = true,
            )
            stockUpdateOperations.add(addOperation)
        }

        // Handle updated items with more precise quantity management
        val updatedItemOperations = itemsToUpdate.mapNotNull { (oldItem, newItem) ->
            val quantityDifference = newItem.quantity - oldItem.quantity

            // Only process if there's an actual quantity change
            if (quantityDifference != 0) {
                // Determine if we're increasing or decreasing stock
                val isIncreasing = quantityDifference > 0

                // Use absolute value of difference for stock update
                val absoluteQuantityChange = abs(quantityDifference)

                // Create an item with the absolute quantity change
                val itemToUpdate = newItem.copy(quantity = absoluteQuantityChange)

                // Perform stock update
                updateStockInItemsUseCase(
                    items = listOf(itemToUpdate),
                    isIncrement = isIncreasing,
                )
            } else {
                null
            }
        }

        stockUpdateOperations.addAll(updatedItemOperations)

        // Comprehensive error handling
        return when {
            // Check if any operation failed
            stockUpdateOperations.any { it !is OperationResult.Success } -> {
                // Find and return the first error
                stockUpdateOperations.firstOrNull { it !is OperationResult.Success }
                    ?: OperationResult.Failure(R.string.feature_bill_item_error_price_empty)
            }

            // All operations successful
            else -> OperationResult.Success
        }
    }

    private suspend fun updateBill(supplierInvoice: SupplierInvoice) {
        val billResult = updateSupplierInvoiceUseCase(supplierInvoice)
        when (billResult) {
            is OperationResult.Success -> {
                billInputState.update { BillInputState() }
                showSnackbarMessage(R.string.feature_bill_updated_successfully)
            }

            is OperationResult.Failure -> {
                showSnackbarMessage(billResult.message)
            }
        }
    }

    fun addPaymentDetails(paymentDetails: PaymentDetails) {
        viewModelScope.launch {
            if (billDetailsUiState.replayCache.last() is BillDetailUiState.Success) {
                val result = addPaymentDetailsUseCase(
                    (
                        billDetailsUiState
                            .replayCache.last() as BillDetailUiState.Success
                        ).supplierInvoice,
                    paymentDetails,
                )
                when (result) {
                    is OperationResult.Success -> {
                    }

                    is OperationResult.Failure -> {
                        showSnackbarMessage(result.message)
                    }
                }
            }
        }
    }

    fun snackbarMessageShown() {
        _userMessage.value = null
    }

    fun showSnackbarMessage(message: Int) {
        _userMessage.update { message }
    }

    companion object {
        private const val SEARCH_ITEM_QUERY = "searchQueryItem"
        private const val SELECTED_BILL_ID_KEY = "selectedBillId"
    }
}