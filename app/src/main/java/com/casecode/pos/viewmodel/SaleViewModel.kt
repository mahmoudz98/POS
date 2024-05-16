package com.casecode.pos.viewmodel

import androidx.annotation.VisibleForTesting
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.map
import androidx.lifecycle.viewModelScope
import com.casecode.data.mapper.addItemToInvoices
import com.casecode.data.mapper.hasItemOutOfStock
import com.casecode.data.mapper.updateQuantityItem
import com.casecode.data.utils.NetworkMonitor
import com.casecode.domain.model.users.Item
import com.casecode.domain.usecase.AddInvoiceUseCase
import com.casecode.domain.usecase.GetItemsUseCase
import com.casecode.domain.usecase.UpdateStockInItemsUseCase
import com.casecode.domain.utils.Resource
import com.casecode.pos.R
import com.casecode.pos.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SaleViewModel @Inject constructor(
    private val networkMonitor: NetworkMonitor,
    private val getItemsUseCase: GetItemsUseCase,
    private val addInvoiceUseCase: AddInvoiceUseCase,
    private val updateStockInItemsUseCase: UpdateStockInItemsUseCase,
) : BaseViewModel() {
    private val isOnline: MutableLiveData<Boolean> = MutableLiveData(false)

    private val _items = MutableLiveData<List<Item>>()
    val items get() = _items
    private val _itemsInvoice: MutableLiveData<MutableSet<Item>> =
        MutableLiveData<MutableSet<Item>>(mutableSetOf())
    val itemsInvoice get() = _itemsInvoice

    val isEmptyItems: LiveData<Boolean> = _items.map { it.isEmpty() }
    val isInvoiceEmpty: LiveData<Boolean> = _itemsInvoice.map { it.isEmpty() }
    private val _itemInvoiceSelected = MutableLiveData<Item>()
    val itemInvoiceSelected get() = _itemInvoiceSelected
    private val itemSelected = MutableLiveData<Item>()
    val totalItemsInvoice: LiveData<Double> = itemsInvoice.map { newItems ->
        newItems.sumOf { it.price * it.quantity }
    }



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
                when (val result = it) {
                    is Resource.Error -> {
                        showSnackbarMessage(
                            result.message as? Int ?: R.string.all_error_unknown,
                        )
                    }
                    is Resource.Empty -> {
                        hideProgress()
                    }
                    is Resource.Success -> {
                        _items.value = result.data
                        hideProgress()
                    }
                    is Resource.Loading -> {
                        showProgress()
                    }
                }
            }
        }
    }

    fun scanItem(sku: String) {
        val item = _items.value?.find { it.sku == sku }
        if (item != null) {
            this.addItemInvoice(item)
        } else {
            showSnackbarMessage(R.string.search_qr_cant_find_item)
        }
    }

    fun addItemInvoice(item: Item) {
        if (item.hasItemOutOfStock()) {
            return showSnackbarMessage(R.string.sale_item_out_of_stock)
        }
        val (isAdded, newItemsInvoice) = _itemsInvoice.value?.addItemToInvoices(item)
            ?: return showSnackbarMessage(R.string.add_item_invoice_fail)
        println("$isAdded")
        if (!isAdded) {
            showSnackbarMessage(R.string.add_item_invoice_fail)
            return
        }
        _itemsInvoice.value = newItemsInvoice
        updateStockInItem(item, item.quantity.dec())

    }

    private fun updateStockInItem(item: Item, quantity: Double) {
        _items.value?.find { it.sku == item.sku }?.let {
            it.quantity = quantity
        } ?: run {
            showSnackbarMessage(R.string.all_error_unknown)
        }
    }

    fun deleteItemInvoice(item: Item) {
        val newItemsInvoice = _itemsInvoice.value?.minus(item)
        _itemsInvoice.value = newItemsInvoice?.toMutableSet()
        setItemSelected(item.sku)
        updateStockInItem(
            itemSelected.value!!,
            item.quantity.plus(itemSelected.value?.quantity ?: Double.NaN),
        )
    }

    fun itemInvoiceSelected(item: Item) {
        _itemInvoiceSelected.value = item
        setItemSelected(item.sku)
    }

    private fun setItemSelected(itemSku: String) {
        itemSelected.value = _items.value?.find { it.sku == itemSku }
    }

    fun isItemSelectedQuantityInStock(textQuantity: Double): Pair<Boolean, Double> {
        val quantityItem = itemSelected.value?.quantity ?: Double.NaN
        val quantityItemInvoiceOld = itemInvoiceSelected.value?.quantity ?: Double.NaN
        val quantityItemInvoiceInStock = quantityItem.plus(quantityItemInvoiceOld)
        return Pair(textQuantity <= quantityItemInvoiceInStock, quantityItemInvoiceOld)
    }

    fun updateQuantityItemInvoice(newQuantity: Double) {
        val updateItemInvoice = itemInvoiceSelected.value
        val updateItemSelectedInStock = itemSelected.value
        val oldItemsInvoice = _itemsInvoice.value
        if (updateItemInvoice == null || updateItemSelectedInStock == null || oldItemsInvoice == null) {
            return showSnackbarMessage(R.string.quantity_item_invoice_update_fail)

        }
        if (newQuantity == updateItemInvoice.quantity) {
            return showSnackbarMessage(R.string.quantity_item_invoice_update_fail)
        }

        val (isUpdated, updateInvoiceItems) = oldItemsInvoice.updateQuantityItem(
            updateItemInvoice,
            newQuantity,
        )

        if (!isUpdated) {
            return showSnackbarMessage(R.string.quantity_item_invoice_update_fail)
        }
        _itemsInvoice.value = updateInvoiceItems
        val newQuantityInStock =
            newQuantity.minus(updateItemInvoice.quantity).minus(updateItemSelectedInStock.quantity)
        updateStockInItem(updateItemSelectedInStock, newQuantityInStock)
        showSnackbarMessage(R.string.quantity_item_invoice_update_success)

    }
    fun setRestOfAmount(amount: Double): Double {
        val total = totalItemsInvoice.value ?: return 0.0
        if (amount == total) {
            return 0.0
        }
        return amount.minus(total)

    }
    fun updateStockAndAddItemInvoice() {

        val saleItems = _itemsInvoice.value?.toList()

        if (isOnline.value == false) {
            return showSnackbarMessage(R.string.network_error)
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
        addInvoiceUseCase(saleItems).collect {
            when (it) {
                is Resource.Empty -> {
                    showSnackbarMessage(it.message as Int)
                    hideProgress()

                }

                is Resource.Error -> {
                    showSnackbarMessage(it.message as Int)
                    hideProgress()
                }

                Resource.Loading -> {
                    showProgress()
                }

                is Resource.Success -> {
                    hideProgress()
                    _itemsInvoice.value = mutableSetOf()
                }
            }
        }
    }
}