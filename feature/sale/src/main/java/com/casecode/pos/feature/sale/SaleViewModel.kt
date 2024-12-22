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
package com.casecode.pos.feature.sale

import androidx.annotation.VisibleForTesting
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.casecode.pos.core.data.utils.NetworkMonitor
import com.casecode.pos.core.domain.usecase.AddInvoiceUseCase
import com.casecode.pos.core.domain.usecase.GetItemsUseCase
import com.casecode.pos.core.domain.usecase.UpdateStockInItemsUseCase
import com.casecode.pos.core.domain.utils.OperationResult
import com.casecode.pos.core.domain.utils.Resource
import com.casecode.pos.core.model.data.users.Item
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel for the Sale screen.
 *
 * This ViewModel handles the logic for adding items to a sale, calculating totals,
 * and managing the UI state of the sale screen.
 *
 * @param networkMonitor Monitors the network connection status.
 * @param getItemsUseCase Use case for retrieving items data.
 * @param addInvoiceUseCase Use case for adding a new invoice.
 * @param updateStockInItemsUseCase Use case for updating the stock of items.
 * @param savedStateHandle Used to restore UI state after process death.
 */
@HiltViewModel
class SaleViewModel
@Inject
constructor(
    networkMonitor: NetworkMonitor,
    getItemsUseCase: GetItemsUseCase,
    private val addInvoiceUseCase: AddInvoiceUseCase,
    private val updateStockInItemsUseCase: UpdateStockInItemsUseCase,
    private val savedStateHandle: SavedStateHandle,
) : ViewModel() {
    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    val items = MutableStateFlow<MutableMap<String, Item>>(mutableMapOf())
    val searchQuery = savedStateHandle.getStateFlow(SEARCH_QUERY, initialValue = "")
    val itemsUiState: StateFlow<ItemsUiState> = getItemsUseCase().map { result ->
        when (result) {
            is Resource.Empty -> ItemsUiState.Empty
            is Resource.Error -> {
                showSnackbarMessage(result.message as Int)
                ItemsUiState.Empty
            }

            Resource.Loading -> ItemsUiState.Loading
            is Resource.Success -> {
                items.update { result.data.associateBy { it.sku }.toMutableMap() }
                ItemsUiState.Success
            }
        }
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000L),
        ItemsUiState.Loading,
    )
    private val saleItemsUiState = SaleItemsUiState()
    val saleItemsState get() = saleItemsUiState.items
    val totalSaleItems get() = saleItemsUiState.totalSaleItems

    private val _itemSelected = MutableStateFlow<Item?>(null)
    val itemSelected = _itemSelected.asStateFlow()
    private val _itemInvoiceSelected = MutableStateFlow<Item?>(null)
    val itemInvoiceSelected = _itemInvoiceSelected.asStateFlow()
    private val _amountInput = MutableStateFlow("")
    val amountInput = _amountInput.asStateFlow()
    val restOfAmount: StateFlow<Double> = _amountInput.map { amount ->
        amount.toDoubleOrNull()?.let {
            if (it == saleItemsUiState.totalSaleItems) {
                0.0
            } else {
                it.minus(saleItemsUiState.totalSaleItems)
            }
        } ?: 0.0
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000L),
        0.0,
    )
    val searchResultItemsUiState: StateFlow<SearchItemsUiState> = combine(
        items,
        searchQuery,
    ) { items, query ->
        if (query.isBlank()) {
            SearchItemsUiState.EmptySearch
        } else {
            val filteredItems = items.values
                .asSequence()
                .filter { matchesSearchCriteria(it, query) }
                .toList()
            if (filteredItems.isEmpty()) {
                SearchItemsUiState.EmptyResult
            } else {
                SearchItemsUiState.Success(filteredItems)
            }
        }
    }.catch { emit(SearchItemsUiState.LoadFailed) }
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000L),
            SearchItemsUiState.EmptyResult,
        )
    private val isOnline = networkMonitor.isOnline
        .stateIn(viewModelScope, SharingStarted.Eagerly, false)
    private val _userMessage = MutableStateFlow<Int?>(null)
    val userMessage = _userMessage.asStateFlow()

    private fun matchesSearchCriteria(item: Item, searchText: String): Boolean =
        with(searchText.lowercase()) {
            item.name.lowercase().contains(this, ignoreCase = true) ||
                item.sku.contains(this) ||
                item.category.contains(this, ignoreCase = true) ||
                item.sku.contains(normalizeNumber(this), ignoreCase = true)
        }

    private fun normalizeNumber(input: String): String {
        val arabicNumerals = listOf('٠', '١', '٢', '٣', '٤', '٥', '٦', '٧', '٨', '٩')
        val englishNumerals = listOf('0', '1', '2', '3', '4', '5', '6', '7', '8', '9')
        val sb = StringBuilder()
        for (char in input) {
            when (char) {
                in englishNumerals -> sb.append(char)
                in arabicNumerals -> {
                    val index = arabicNumerals.indexOf(char)
                    sb.append(englishNumerals[index])
                }

                else -> sb.append(char)
            }
        }
        return sb.toString()
    }

    fun onSearchQueryChanged(query: String) {
        savedStateHandle[SEARCH_QUERY] = query
    }

    fun scanItem(sku: String) {
        items.value[sku]?.let {
            addItemInvoice(it)
        } ?: showSnackbarMessage(R.string.feature_sale_error_sale_item_not_found)
    }

    fun addItemInvoice(item: Item) {
        if (!item.isInStockAndTracked()) {
            showSnackbarMessage(R.string.feature_sale_item_out_of_stock_message)
            return
        }
        saleItemsUiState.addItem(item)
        updateStockInItem(item, item.quantity - 1)
    }

    private fun updateStockInItem(item: Item, quantity: Int) {
        if (!item.isTrackStock()) return

        items.update { current ->
            current.apply {
                put(item.sku, item.copy(quantity = quantity))
            }
        }
    }

    fun deleteItemInvoice(item: Item) {
        val itemInStock = getItem(item.sku) ?: run {
            showSnackbarMessage(com.casecode.pos.core.ui.R.string.core_ui_error_unknown)
            return
        }

        updateStockInItem(
            item = itemInStock,
            quantity = item.quantity + itemInStock.quantity,
        )
        saleItemsUiState.removeItem(item)
    }

    fun updateQuantityItemInvoice(newQuantity: Int) {
        val updateItemInvoice = _itemInvoiceSelected.value ?: run {
            showSnackbarMessage(R.string.feature_sale_error_update_invoice_item_quantity)
            return
        }
        val updateItemSelectedInStock = getItem(updateItemInvoice.sku) ?: run {
            showSnackbarMessage(R.string.feature_sale_error_update_invoice_item_quantity)
            return
        }

        if (newQuantity == updateItemInvoice.quantity) {
            showSnackbarMessage(R.string.feature_sale_error_update_invoice_item_quantity)
            return
        }
        val wholeStock = updateItemSelectedInStock.quantity + updateItemInvoice.quantity
        val newQuantityInStock = wholeStock.minus(newQuantity)

        updateStockInItem(updateItemSelectedInStock, newQuantityInStock)
        saleItemsUiState.updateItemQuantity(updateItemInvoice.sku, newQuantity)
    }

    fun updateStockAndAddItemInvoice() {
        viewModelScope.launch {
            if (!isOnline.value) {
                showSnackbarMessage(com.casecode.pos.core.ui.R.string.core_ui_error_network)
                return@launch
            }
            val saleItems = saleItemsState.toList()
            when (val updateResult = updateStockInItemsUseCase(saleItems)) {
                is OperationResult.Success -> addSaleInvoice(saleItems)
                is OperationResult.Failure -> showSnackbarMessage(updateResult.message)
            }
        }
    }

    private suspend fun addSaleInvoice(saleItems: List<Item>) {
        addInvoiceUseCase(saleItems).collect { result ->
            when (result) {
                is Resource.Success -> saleItemsUiState.clear()
                is Resource.Error -> showSnackbarMessage(result.message as Int)
                is Resource.Empty -> showSnackbarMessage(result.message as Int)
                Resource.Loading -> Unit
            }
        }
    }

    fun updateAmount(amount: String) {
        _amountInput.value = amount
    }

    fun itemInvoiceSelected(item: Item) {
        _itemInvoiceSelected.value = item
    }

    fun updateQuantityItemSelected(item: Item) {
        _itemInvoiceSelected.value = item
        _itemSelected.value = getItem(item.sku)
    }

    private fun getItem(sku: String): Item? = items.value[sku]

    fun snackbarMessageShown() {
        _userMessage.value = null
    }

    fun showSnackbarMessage(message: Int) {
        _userMessage.update { message }
    }
}
private const val SEARCH_QUERY = "searchQuery"