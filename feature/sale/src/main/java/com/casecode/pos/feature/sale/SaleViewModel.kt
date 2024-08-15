package com.casecode.pos.feature.sale

import androidx.annotation.VisibleForTesting
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.casecode.pos.core.data.model.isInStock
import com.casecode.pos.core.data.utils.NetworkMonitor
import com.casecode.pos.core.domain.usecase.AddInvoiceUseCase
import com.casecode.pos.core.domain.usecase.GetItemsUseCase
import com.casecode.pos.core.domain.usecase.UpdateStockInItemsUseCase
import com.casecode.pos.core.domain.utils.Resource
import com.casecode.pos.core.model.data.users.Item
import com.casecode.pos.core.model.data.users.addItemToInvoices
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SaleViewModel @Inject constructor(
    private val networkMonitor: NetworkMonitor,
    private val getItemsUseCase: GetItemsUseCase,
    private val addInvoiceUseCase: AddInvoiceUseCase,
    private val updateStockInItemsUseCase: UpdateStockInItemsUseCase,
) : ViewModel() {

    private val _uiState = MutableStateFlow(SaleUiState())
    val uiState: StateFlow<SaleUiState> = _uiState.asStateFlow()

    private val isOnline: MutableStateFlow<Boolean> = MutableStateFlow(false)

    init {
        fetchItems()
        setupNetworkMonitor()
    }

    private fun setupNetworkMonitor() = viewModelScope.launch {
        networkMonitor.isOnline.collect {
            isOnline.value = it
        }
    }

    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    internal fun fetchItems() {
        viewModelScope.launch {

            getItemsUseCase().collect {
                when (it) {
                    is Resource.Empty -> {
                        _uiState.update { uiState ->
                            uiState.copy(
                                invoiceState = InvoiceState.EmptyItems,
                            )
                        }
                    }

                    is Resource.Error -> {
                        _uiState.update { uiState ->
                            uiState.copy(
                                invoiceState = InvoiceState.EmptyItems,
                            )
                        }
                    }

                    Resource.Loading -> {
                        _uiState.update { uiState ->
                            uiState.copy(
                                invoiceState = InvoiceState.Loading,
                            )
                        }
                    }

                    is Resource.Success -> {
                        checkTouUpdateInvoiceState()
                        _uiState.update { uiState ->
                            uiState.copy(
                                items = it.data,
                            )
                        }
                    }
                }

            }
        }
    }

    fun scanItem(sku: String) {
        val item = uiState.value.items.find { it.sku == sku }
        if (item != null) {
            this.addItemInvoice(item)
        } else {
            showSnackbarMessage(R.string.feature_sale_error_sale_item_not_found)
        }
    }

    fun addItemInvoice(item: Item) {
        if (!item.isInStock()) {
            return showSnackbarMessage(R.string.feature_sale_item_out_of_stock_message)
        }
        _uiState.update {
            it.copy(
                itemsInvoice = it.itemsInvoice.addItemToInvoices(item),
            )
        }
        updateStockInItem(item, item.quantity.dec())
        checkTouUpdateInvoiceState()

    }

    private fun checkTouUpdateInvoiceState() {
        _uiState.update {
            it.copy(invoiceState = if (it.itemsInvoice.isEmpty()) InvoiceState.EmptyItemInvoice else InvoiceState.HasItems)
        }
    }

    fun updateAmount(amount: String) {
        _uiState.update {
            it.copy(amountInput = amount)
        }
    }

    private fun updateStockInItem(item: Item, quantity: Double) {
        _uiState.update { currentState ->
            currentState.copy(
                items = currentState.items.map {
                    if (it.sku == item.sku) {
                        it.quantity = quantity
                    }
                    it
                },
            )
        }

    }

    fun deleteItemInvoice(item: Item) {

        val itemInStock =
            getItem(item.sku)
                ?: return showSnackbarMessage(com.casecode.pos.core.ui.R.string.core_ui_error_unknown)

        updateStockInItem(
            itemInStock,
            item.quantity.plus(itemInStock.quantity),
        )

        val newItemsInvoice = uiState.value.itemsInvoice.minus(item)
        _uiState.update {
            it.copy(itemsInvoice = newItemsInvoice.toMutableSet())
        }
        checkTouUpdateInvoiceState()
    }


    fun itemInvoiceSelected(item: Item) {
        _uiState.update {
            it.copy(itemInvoiceSelected = item)
        }
    }

    fun updateQuantityItemSelected(item: Item) {
        _uiState.update {
            it.copy(itemSelected = getItem(item.sku), itemInvoiceSelected = item)
        }
    }

    private fun getItem(itemSku: String): Item? = uiState.value.items.find { it.sku == itemSku }

    fun updateQuantityItemInvoice(newQuantity: Double) {
        val updateItemInvoice = _uiState.value.itemInvoiceSelected
            ?: return showSnackbarMessage(R.string.feature_sale_error_update_invoice_item_quantity)
        val updateItemSelectedInStock = getItem(updateItemInvoice.sku)
            ?: return showSnackbarMessage(R.string.feature_sale_error_update_invoice_item_quantity)

        if (newQuantity == updateItemInvoice.quantity) {
            return showSnackbarMessage(R.string.feature_sale_error_update_invoice_item_quantity)
        }

        val newQuantityInStock =
            newQuantity.minus(updateItemInvoice.quantity).minus(updateItemSelectedInStock.quantity)
        updateStockInItem(updateItemSelectedInStock, newQuantityInStock)
        showSnackbarMessage(R.string.feature_sale_success_update_invoice_item_quantity)
        _uiState.update {
            it.copy(
                itemsInvoice = it.itemsInvoice.minus(updateItemInvoice)
                    .plus(updateItemInvoice.copy(quantity = newQuantity)),
            )
        }
    }


    fun updateStockAndAddItemInvoice() {

        val saleItems = uiState.value.itemsInvoice.toList()

        if (!isOnline.value) {
            return showSnackbarMessage(com.casecode.pos.core.ui.R.string.core_ui_error_network)
        }
        viewModelScope.launch {
            when (val updateItems = updateStockInItemsUseCase(saleItems)) {
                is Resource.Empty -> {
                    showSnackbarMessage(updateItems.message as Int)
                }

                is Resource.Error -> {
                    showSnackbarMessage(updateItems.message as Int)
                }

                Resource.Loading -> {}
                is Resource.Success -> {
                    addInvoice(updateItems.data)
                }
            }
        }

    }

    private suspend fun addInvoice(saleItems: List<Item>) {
        addInvoiceUseCase(saleItems).collect { resourceAddInvoice ->
            when (resourceAddInvoice) {
                is Resource.Empty -> {
                    showSnackbarMessage(resourceAddInvoice.message as Int)
                }

                is Resource.Error -> {
                    showSnackbarMessage(resourceAddInvoice.message as Int)
                }

                Resource.Loading -> {}

                is Resource.Success -> {
                    _uiState.update {
                        it.copy(
                            itemsInvoice = mutableSetOf(),
                            invoiceState = InvoiceState.EmptyItemInvoice,
                        )
                    }
                }
            }
        }
    }

    fun snackbarMessageShown() {
        _uiState.update {
            it.copy(userMessage = null)
        }
    }

    fun showSnackbarMessage(message: Int) {
        _uiState.update {
            it.copy(userMessage = message)
        }
    }
}