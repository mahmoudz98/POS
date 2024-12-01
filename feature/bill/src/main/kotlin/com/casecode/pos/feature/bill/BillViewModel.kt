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
import com.casecode.pos.core.model.data.users.SupplierInvoice
import com.casecode.pos.core.ui.stateInWhileSubscribed
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.transform
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed interface InvoiceSelectionUiState {
    data object Loading : InvoiceSelectionUiState
    data object Error : InvoiceSelectionUiState
    data object EmptySelection : InvoiceSelectionUiState
    data class Success(val supplierInvoice: SupplierInvoice) : InvoiceSelectionUiState
}

@HiltViewModel
class BillViewModel @Inject constructor(
    networkMonitor: NetworkMonitor,
    getSupplierInvoicesUseCase: GetSupplierInvoicesUseCase,
    private val updateSupplierInvoiceUseCase: UpdateSupplierInvoiceUseCase,
    private val addPaymentDetailsUseCase: AddPaymentDetailsUseCase,
    private val savedStateHandle: SavedStateHandle,
) : ViewModel() {

    private val _userMessage = MutableStateFlow<Int?>(null)
    val userMessage = _userMessage.asStateFlow()
    val billsUiState: StateFlow<BillsUiState> =
        getSupplierInvoicesUseCase().flatMapLatest { result ->
            when (result) {
                is Resource.Loading -> flowOf(BillsUiState.Loading)
                is Resource.Error -> {
                    showSnackbarMessage(result.message as Int)
                    flowOf(BillsUiState.Error)
                }

                is Resource.Empty -> {
                    flowOf(BillsUiState.Empty)
                }

                is Resource.Success -> {
                    flowOf(BillsUiState.Success(result.data.associateBy { it.invoiceId }))
                }
            }
        }
            .stateInWhileSubscribed(BillsUiState.Loading)
    private val selectedInvoiceId = MutableStateFlow<String?>(null)

    val supplierInvoiceSelected: StateFlow<InvoiceSelectionUiState> =
        selectedInvoiceId.flatMapLatest { invoiceId ->
            if (invoiceId.isNullOrEmpty()) {
                flowOf(InvoiceSelectionUiState.EmptySelection)
            } else {
                billsUiState.map { uiState ->
                    when {
                        uiState is BillsUiState.Loading -> InvoiceSelectionUiState.Loading
                        uiState is BillsUiState.Error -> InvoiceSelectionUiState.Error
                        uiState is BillsUiState.Empty -> InvoiceSelectionUiState.EmptySelection
                        uiState is BillsUiState.Success && invoiceId in uiState.supplierInvoices -> {
                            InvoiceSelectionUiState.Loading
                            delay(400)
                            InvoiceSelectionUiState.Success(uiState.supplierInvoices[invoiceId]!!)
                        }
                        else -> InvoiceSelectionUiState.Error
                    }
                }
            }
        }
        .stateInWhileSubscribed(InvoiceSelectionUiState.Loading)

    fun onSupplierInvoiceIdSelected(invoiceId: String) {
        selectedInvoiceId.value = invoiceId
    }

    fun addPaymentDetails(paymentDetails: PaymentDetails) {
        viewModelScope.launch {
            if (supplierInvoiceSelected.value is InvoiceSelectionUiState.Success) {
                val result = addPaymentDetailsUseCase(
                    (
                        supplierInvoiceSelected
                            .value as InvoiceSelectionUiState.Success
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