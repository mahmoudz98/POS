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
import androidx.lifecycle.ViewModel
import com.casecode.pos.core.domain.utils.NetworkMonitor
import com.casecode.pos.core.designsystem.component.SearchWidgetState
import com.casecode.pos.core.domain.usecase.GetSupplierInvoicesUseCase
import com.casecode.pos.core.domain.utils.Resource
import com.casecode.pos.core.model.data.users.PaymentStatus
import com.casecode.pos.core.model.data.users.SupplierInvoice
import com.casecode.pos.core.ui.stateInWhileSubscribed
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.datetime.Clock
import javax.inject.Inject

@HiltViewModel
class BillsViewModel @Inject constructor(
    networkMonitor: NetworkMonitor,
    getSupplierInvoicesUseCase: GetSupplierInvoicesUseCase,
    private val savedStateHandle: SavedStateHandle,
) : ViewModel() {

    private val _userMessage = MutableStateFlow<Int?>(null)
    val userMessage = _userMessage.asStateFlow()
    val searchQuery = savedStateHandle.getStateFlow(key = SEARCH_QUERY, initialValue = "")
    val searchWidgetState =
        savedStateHandle.getStateFlow(
            key = SEARCH_WIDGET_STATE,
            initialValue = SearchWidgetState.CLOSED,
        )
    private val _filterUiState = MutableStateFlow(BillsFilterUiState())
    val filterUiState = _filterUiState.asStateFlow()
    val billsUiState: StateFlow<BillsUiState> =
        combine(
            getSupplierInvoicesUseCase(),
            searchQuery,
            _filterUiState,
        ) { billsResource, searchText, filterState ->
            when (billsResource) {
                is Resource.Loading -> BillsUiState.Loading
                is Resource.Error -> {
                    showSnackbarMessage(billsResource.message as Int)
                    BillsUiState.Error
                }

                is Resource.Empty -> {
                    BillsUiState.Empty
                }

                is Resource.Success -> {
                    val suppliers = billsResource.data.mapNotNullTo(mutableSetOf()) {
                        it.supplierName.takeIf { name -> name.isNotBlank() }
                    }
                    val filteredBills = billsResource.data
                        .asSequence()
                        .filter { bill -> applySearchFilter(bill, searchText) }
                        .filter { bill ->
                            applyPaymentStatusFilter(
                                bill,
                                filterState.paymentStatusFilter,
                            )
                        }
                        .filter { bill -> applyDateRangeFilter(bill, filterState.dateRange) }
                        .filter { bill ->
                            filterState.selectedSuppliers.isEmpty() ||
                                bill.supplierName in filterState.selectedSuppliers
                        }
                        .sortedWith(getSortComparator(filterState.sortType))
                        .associateBy { it.invoiceId }
                    BillsUiState.Success(filteredBills, suppliers)
                }
            }
        }.stateInWhileSubscribed(BillsUiState.Loading)

    fun onBillsUiEvent(billsUiEvent: BillsUiEvent) {
        when (billsUiEvent) {
            is BillsUiEvent.SearchClicked -> {
                openSearchWidgetState()
            }

            is BillsUiEvent.SearchQueryChanged -> {
                onSearchQueryChanged(billsUiEvent.query)
            }

            is BillsUiEvent.ClearRecentSearches -> {
                closeSearchWidgetState()
            }

            is BillsUiEvent.SupplierSelected -> {
                onSupplierSelected(billsUiEvent.name)
            }

            is BillsUiEvent.SupplierUnselected -> {
                onSupplierUnSelected(billsUiEvent.name)
            }

            is BillsUiEvent.SortPaymentStatusChanged -> {
                onSortPaymentStatusChanged(billsUiEvent.status)
            }

            is BillsUiEvent.SortTypeChanged -> {
                onSortChanged(billsUiEvent.sortType)
            }

            is BillsUiEvent.RestDefaultFilter -> {
                onClearFilter()
            }

            is BillsUiEvent.MessageShown -> {
                snackbarMessageShown()
            }
        }
    }

    private fun closeSearchWidgetState() {
        savedStateHandle[SEARCH_WIDGET_STATE] = SearchWidgetState.CLOSED
    }

    private fun openSearchWidgetState() {
        savedStateHandle[SEARCH_WIDGET_STATE] = SearchWidgetState.OPENED
    }

    private fun onSearchQueryChanged(searchText: String) {
        savedStateHandle[SEARCH_QUERY] = searchText
    }

    private fun onSupplierSelected(name: String) {
        val selectedSuppliers = _filterUiState.value.selectedSuppliers.toMutableSet()
        selectedSuppliers.add(name)
        _filterUiState.update {
            it.copy(selectedSuppliers = selectedSuppliers)
        }
    }

    private fun onSupplierUnSelected(name: String) {
        val selectedSuppliers = _filterUiState.value.selectedSuppliers.toMutableSet()
        selectedSuppliers.remove(name)
        _filterUiState.update {
            it.copy(selectedSuppliers = selectedSuppliers)
        }
    }

    private fun onSortPaymentStatusChanged(paymentStatusFilter: PaymentStatusFilter) {
        _filterUiState.update {
            it.copy(paymentStatusFilter = paymentStatusFilter)
        }
    }

    private fun onSortChanged(sortType: InvoiceSortType) {
        _filterUiState.update {
            it.copy(sortType = sortType)
        }
    }

    private fun onClearFilter() {
        _filterUiState.update { BillsFilterUiState() }
    }

    private fun showSnackbarMessage(message: Int) {
        _userMessage.update { message }
    }

    private fun snackbarMessageShown() {
        _userMessage.value = null
    }

    private fun applySearchFilter(bill: SupplierInvoice, query: String): Boolean {
        if (query.isBlank()) return true
        return bill.supplierName.contains(query, ignoreCase = true) ||
            bill.billNumber.contains(query, ignoreCase = true)
    }

    private fun applyPaymentStatusFilter(
        invoice: SupplierInvoice,
        filter: PaymentStatusFilter,
    ): Boolean = when (filter) {
        PaymentStatusFilter.All -> true
        PaymentStatusFilter.Paid -> invoice.paymentStatus == PaymentStatus.PAID
        PaymentStatusFilter.Pending -> invoice.paymentStatus == PaymentStatus.PENDING
        PaymentStatusFilter.PartiallyPaid -> {
            invoice.paymentStatus == PaymentStatus.PARTIALLY_PAID
        }

        PaymentStatusFilter.Overdue -> isOverdue(invoice)
    }

    private fun isOverdue(invoice: SupplierInvoice): Boolean = invoice.paymentStatus == PaymentStatus.OVERDUE ||
        (invoice.dueDate < Clock.System.now() && invoice.dueAmount > 0)

    private fun applyDateRangeFilter(bill: SupplierInvoice, dateRange: DateRange?): Boolean {
        if (dateRange == null) return true
        return bill.issueDate >= dateRange.startDate && bill.issueDate <= dateRange.endDate
    }

    private fun getSortComparator(sortType: InvoiceSortType): Comparator<SupplierInvoice> = when (sortType) {
        InvoiceSortType.DateNewToOld -> compareByDescending { it.issueDate }
        InvoiceSortType.DateOldToNew -> compareBy { it.issueDate }
        InvoiceSortType.AmountHighToLow -> compareByDescending { it.totalAmount }
        InvoiceSortType.AmountLowToHigh -> compareBy { it.totalAmount }
    }

    companion object {
        private const val SEARCH_QUERY = "searchQuery"
        private const val SEARCH_WIDGET_STATE = "searchWidgetState"
    }
}