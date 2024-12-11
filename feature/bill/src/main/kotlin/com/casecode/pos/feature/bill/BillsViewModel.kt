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
import androidx.lifecycle.viewModelScope
import com.casecode.pos.core.data.utils.NetworkMonitor
import com.casecode.pos.core.domain.usecase.AddPaymentDetailsUseCase
import com.casecode.pos.core.domain.usecase.GetSupplierInvoicesUseCase
import com.casecode.pos.core.domain.usecase.UpdateSupplierInvoiceUseCase
import com.casecode.pos.core.domain.utils.OperationResult
import com.casecode.pos.core.domain.utils.Resource
import com.casecode.pos.core.model.data.users.PaymentDetails
import com.casecode.pos.core.ui.stateInWhileSubscribed
import com.casecode.pos.feature.bill.detials.BillDetailUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class BillsViewModel @Inject constructor(
    networkMonitor: NetworkMonitor,
    getSupplierInvoicesUseCase: GetSupplierInvoicesUseCase,
    private val updateSupplierInvoiceUseCase: UpdateSupplierInvoiceUseCase,
    private val addPaymentDetailsUseCase: AddPaymentDetailsUseCase,
    private val savedStateHandle: SavedStateHandle,
) : ViewModel() {

    private val _userMessage = MutableStateFlow<Int?>(null)
    val userMessage = _userMessage.asStateFlow()
    val billsUiState: StateFlow<BillsUiState> =
        getSupplierInvoicesUseCase().map { result ->
            when (result) {
                is Resource.Loading -> BillsUiState.Loading
                is Resource.Error -> {
                    showSnackbarMessage(result.message as Int)
                    BillsUiState.Error
                }

                is Resource.Empty -> {
                    BillsUiState.Empty
                }

                is Resource.Success -> {
                    BillsUiState.Success(result.data.associateBy { it.invoiceId })
                }
            }
        }.stateInWhileSubscribed(BillsUiState.Loading)
    private val selectedBillId = savedStateHandle.getStateFlow(
        key = SELECTED_BILL_ID_KEY,
        initialValue = "",
    )
    val supplierInvoiceSelected: StateFlow<BillDetailUiState> =
        selectedBillId
            .flatMapLatest { invoiceId ->
                Timber.e("InvoiceId: $invoiceId")
                if (invoiceId.isEmpty()) {
                    flowOf(BillDetailUiState.EmptySelection)
                } else {
                    billsUiState.flatMapLatest { uiState ->
                        when (uiState) {
                            is BillsUiState.Loading -> flowOf(BillDetailUiState.Loading)
                            is BillsUiState.Error -> flowOf(BillDetailUiState.Error)
                            is BillsUiState.Empty -> flowOf(BillDetailUiState.EmptySelection)
                            is BillsUiState.Success -> {
                                val selectedInvoice = uiState.supplierInvoices[invoiceId]
                                if (selectedInvoice != null) {
                                    flow {
                                        emit(BillDetailUiState.Loading)
                                        delay(400)
                                        emit(BillDetailUiState.Success(selectedInvoice))
                                    }
                                } else {
                                    flowOf(BillDetailUiState.Error)
                                }
                            }
                            else -> flowOf(BillDetailUiState.Error)
                        }
                    }
                }
            }.stateInWhileSubscribed(BillDetailUiState.Loading)

    fun onSupplierInvoiceIdSelected(invoiceId: String) {
        savedStateHandle[SELECTED_BILL_ID_KEY] = invoiceId
    }


    fun addPaymentDetails(paymentDetails: PaymentDetails) {
        viewModelScope.launch {
            if (supplierInvoiceSelected.value is BillDetailUiState.Success) {
                val result = addPaymentDetailsUseCase(
                    (
                        supplierInvoiceSelected
                            .value as BillDetailUiState.Success
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

    fun showSnackbarMessage(message: Int) {
        _userMessage.update { message }
    }

    fun snackbarMessageShown() {
        _userMessage.value = null
    }
}
private const val SELECTED_BILL_ID_KEY = "selectedBillId"