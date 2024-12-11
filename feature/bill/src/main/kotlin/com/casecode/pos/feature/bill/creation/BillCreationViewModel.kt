package com.casecode.pos.feature.bill.creation

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.casecode.pos.core.data.utils.NetworkMonitor
import com.casecode.pos.core.domain.usecase.AddSupplierInvoiceUseCase
import com.casecode.pos.core.domain.usecase.GetItemsUseCase
import com.casecode.pos.core.domain.usecase.GetSupplierInvoiceDetailsUseCase
import com.casecode.pos.core.domain.usecase.GetSuppliersUseCase
import com.casecode.pos.core.domain.usecase.UpdateStockInItemsUseCase
import com.casecode.pos.core.domain.usecase.UpdateSupplierInvoiceUseCase
import com.casecode.pos.core.domain.utils.OperationResult
import com.casecode.pos.core.domain.utils.Resource
import com.casecode.pos.core.model.data.users.Item
import com.casecode.pos.core.model.data.users.SupplierInvoice
import com.casecode.pos.core.ui.stateInWhileSubscribed
import com.casecode.pos.feature.bill.R
import com.casecode.pos.feature.bill.SearchItemUiState
import com.casecode.pos.feature.bill.SearchSupplierUiState
import com.casecode.pos.feature.bill.detials.BillDetailUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject
import com.casecode.pos.core.ui.R.string as uiString

@HiltViewModel
class BillCreationViewModel @Inject constructor(
    networkMonitor: NetworkMonitor,
    getItemsUseCase: GetItemsUseCase,
    getSuppliersUseCase: GetSuppliersUseCase,
    private val getBillDetailsUseCase: GetSupplierInvoiceDetailsUseCase,
    private val addSupplierInvoiceUseCase: AddSupplierInvoiceUseCase,
    private val updateSupplierInvoiceUseCase: UpdateSupplierInvoiceUseCase,
    private val updateStockInItemsUseCase: UpdateStockInItemsUseCase,
    private val savedStateHandle: SavedStateHandle,
) : ViewModel() {
    private val isOnline: StateFlow<Boolean> =
        networkMonitor.isOnline.stateIn(viewModelScope, SharingStarted.Companion.Eagerly, false)

    val searchQuerySupplier = savedStateHandle.getStateFlow(key = SEARCH_SUPPLIER_QUERY, "")
    val searchQueryItem = savedStateHandle.getStateFlow(key = SEARCH_ITEM_QUERY, "")
    val billInputState = MutableStateFlow<BillInputState>(BillInputState())
    val filterSupplierState: StateFlow<SearchSupplierUiState> =
        combine(getSuppliersUseCase(), searchQuerySupplier) { result, supplierQuery ->
            if (supplierQuery.isEmpty()) {
                SearchSupplierUiState.EmptyQuery
            } else {
                when (result) {
                    is Resource.Loading -> SearchSupplierUiState.Loading
                    is Resource.Error -> SearchSupplierUiState.LoadFailed
                    is Resource.Empty -> SearchSupplierUiState.EmptyResult
                    is Resource.Success -> {
                        val suppliers = result.data.map { it.contactName }
                        val filteredSuppliers = suppliers.filter {
                            it.contains(supplierQuery, ignoreCase = true)
                        }
                        if (filteredSuppliers.isEmpty()) {
                            SearchSupplierUiState.EmptyResult
                        } else {
                            SearchSupplierUiState.Success(filteredSuppliers)
                        }
                    }
                }
            }
        }
            .stateInWhileSubscribed(SearchSupplierUiState.EmptyQuery)
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
    val billDetailsUiState: StateFlow<BillDetailUiState> = selectedBillId.flatMapLatest { billId ->
        Timber.e("billId: $billId")
        if (billId.isEmpty()) {
            flowOf(BillDetailUiState.EmptySelection)
        } else {
            getBillDetailsUseCase(billId).flatMapLatest {
                when (it) {
                    is Resource.Loading -> flowOf(BillDetailUiState.Loading)
                    is Resource.Error -> flowOf(BillDetailUiState.Error)
                    is Resource.Success -> {
                        billInputState.update { current -> BillInputState(it.data) }
                        flowOf(BillDetailUiState.Success(it.data))
                    }

                    is Resource.Empty -> flowOf(BillDetailUiState.EmptySelection)
                }
            }
        }
    }.stateInWhileSubscribed(BillDetailUiState.Loading)
    private val _itemSelected = MutableStateFlow<Item?>(null)
    val itemSelected = _itemSelected.asStateFlow()
    private val _userMessage = MutableStateFlow<Int?>(null)
    val userMessage = _userMessage.asStateFlow()

    fun onSearchQuerySupplierChanged(query: String) {
        savedStateHandle[SEARCH_SUPPLIER_QUERY] = query
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

    fun updateStockThenAddBill() {
        viewModelScope.launch {
            if (!isOnline.value) {
                showSnackbarMessage(uiString.core_ui_error_network)
                return@launch
            }
            val inputInvoice = billInputState.value
            val supplierInvoice = SupplierInvoice(
                billNumber = inputInvoice.billNumber,
                supplierName = inputInvoice.supplierName,
                issueDate = inputInvoice.issueDate,
                dueDate = inputInvoice.dueDate,
                totalAmount = inputInvoice.total,
                discountType = inputInvoice.discountTypeCurrency,
                amountDiscounted = inputInvoice.discount.toDoubleOrNull() ?: 0.0,
                invoiceItems = inputInvoice.invoiceItems,
            )
            when (val updateResult = updateStockInItemsUseCase(inputInvoice.invoiceItems, true)) {
                is Resource.Success -> {
                    addBill(supplierInvoice)
                }

                is Resource.Error -> showSnackbarMessage(updateResult.message as Int)
                is Resource.Empty -> showSnackbarMessage(updateResult.message as Int)
                Resource.Loading -> Unit
            }
        }
    }

    private suspend fun addBill(supplierInvoice: SupplierInvoice) {
        val billResult = addSupplierInvoiceUseCase(supplierInvoice)
        when (billResult) {
            is OperationResult.Success -> {
                billInputState.update { BillInputState() }
            }

            is OperationResult.Failure -> {
                showSnackbarMessage(billResult.message)
            }
        }
    }

    fun updateStockThenUpdateBill() {
        viewModelScope.launch {
            if (!isOnline.value) {
                showSnackbarMessage(uiString.core_ui_error_network)
                return@launch
            }
            val oldInvoice =
                (billDetailsUiState.value as? BillDetailUiState.Success)?.supplierInvoice
            if (oldInvoice == null) {
               // showSnackbarMessage(R.string.feature_bill_item_error_price_empty)
                return@launch
            }
            val inputInvoice = billInputState.value
            if (oldInvoice.invoiceItems == inputInvoice.invoiceItems &&
                oldInvoice.dueDate == inputInvoice.dueDate &&
                oldInvoice.issueDate == inputInvoice.issueDate
            ) {

                return@launch
            }

            val supplierInvoice = SupplierInvoice(
                billNumber = inputInvoice.billNumber,
                supplierName = inputInvoice.supplierName,
                issueDate = inputInvoice.issueDate,
                dueDate = inputInvoice.dueDate,
                totalAmount = inputInvoice.total,
                discountType = inputInvoice.discountTypeCurrency,
                amountDiscounted = inputInvoice.discount.toDoubleOrNull() ?: 0.0,
                invoiceItems = inputInvoice.invoiceItems,
            )
            when (val updateResult = updateStockInItemsUseCase(inputInvoice.invoiceItems, true)) {
                is Resource.Success -> {
                    updateBill(supplierInvoice)
                }

                is Resource.Error -> showSnackbarMessage(updateResult.message as Int)
                is Resource.Empty -> showSnackbarMessage(updateResult.message as Int)
                Resource.Loading -> Unit
            }
        }
    }

    private suspend fun updateBill(supplierInvoice: SupplierInvoice) {
        val billResult = updateSupplierInvoiceUseCase(supplierInvoice)
        when (billResult) {
            is OperationResult.Success -> {
                billInputState.update { BillInputState() }
            }

            is OperationResult.Failure -> {
                showSnackbarMessage(billResult.message)
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
        private const val SEARCH_SUPPLIER_QUERY = "searchQuerySupplier"
        private const val SEARCH_ITEM_QUERY = "searchQueryItem"
        private const val SELECTED_BILL_ID_KEY = "selectedBillId"
    }
}