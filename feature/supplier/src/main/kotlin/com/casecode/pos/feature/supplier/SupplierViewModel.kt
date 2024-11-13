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
package com.casecode.pos.feature.supplier

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.casecode.pos.core.data.utils.NetworkMonitor
import com.casecode.pos.core.designsystem.component.SearchWidgetState
import com.casecode.pos.core.domain.usecase.AddSupplierUseCase
import com.casecode.pos.core.domain.usecase.DeleteSupplierUseCase
import com.casecode.pos.core.domain.usecase.GetSuppliersUseCase
import com.casecode.pos.core.domain.usecase.UpdateSupplierUseCase
import com.casecode.pos.core.domain.utils.OperationResult
import com.casecode.pos.core.domain.utils.Resource
import com.casecode.pos.core.model.data.users.Supplier
import com.casecode.pos.core.ui.R.string as uiString
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * ViewModel for the Supplier screen.
 *
 * This ViewModel is responsible for managing the UI state and data related to Suppliers. It
 * interacts with use cases to perform operations like fetching, adding, updating, and deleting
 * suppliers.
 *
 * @param networkMonitor Monitors the network connection status.
 * @param getSuppliersUseCase Use case for retrieving suppliers.
 * @param addSupplierUseCase Use case for adding a new supplier.
 * @param updateSupplierUseCase Use case for updating an existing supplier.
 * @param deleteSupplierUseCase Use case for deleting a supplier.
 */
@HiltViewModel
class SupplierViewModel
@Inject
constructor(
    networkMonitor: NetworkMonitor,
    getSuppliersUseCase: GetSuppliersUseCase,
    private val addSupplierUseCase: AddSupplierUseCase,
    private val updateSupplierUseCase: UpdateSupplierUseCase,
    private val deleteSupplierUseCase: DeleteSupplierUseCase,
    private val savedStateHandle: SavedStateHandle,
) : ViewModel() {
    private val isOnline: StateFlow<Boolean> =
        networkMonitor.isOnline.stateIn(viewModelScope, SharingStarted.Eagerly, false)
    val searchQuery = savedStateHandle.getStateFlow(key = SEARCH_QUERY, initialValue = "")
    val searchWidgetState =
        savedStateHandle.getStateFlow(
            key = SEARCH_WIDGET_STATE,
            initialValue = SearchWidgetState.CLOSED,
        )
    private val _userMessage = MutableStateFlow<Int?>(null)
    val userMessage = _userMessage.asStateFlow()
    val suppliersUiState: StateFlow<SuppliersUiState> =
        getSuppliersUseCase()
            .map { result ->
                when (result) {
                    is Resource.Loading -> SuppliersUiState.Loading
                    is Resource.Error -> {
                        showSnackbarMessage(result.message as Int)
                        SuppliersUiState.Error
                    }

                    is Resource.Empty -> SuppliersUiState.Empty
                    is Resource.Success -> SuppliersUiState.Success(result.data)
                }
            }
            .stateIn(
                viewModelScope,
                SharingStarted.WhileSubscribed(5000L),
                SuppliersUiState.Loading,
            )
    val filteredSuppliers: StateFlow<List<Supplier>> = searchQuery
        .combine(suppliersUiState) { query, uiState ->
            if (uiState is SuppliersUiState.Success) {
                uiState.suppliers.filter { supplier ->
                    supplier.contactName.contains(query, ignoreCase = true) ||
                        supplier.companyName.contains(query, ignoreCase = true)
                }
            } else {
                emptyList()
            }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000L), emptyList())

    private val _supplierSelected = MutableStateFlow<Supplier?>(null)
    val supplierSelected = _supplierSelected.asStateFlow()

    fun closeSearchWidgetState() {
        savedStateHandle[SEARCH_WIDGET_STATE] = SearchWidgetState.CLOSED
    }

    fun openSearchWidgetState() {
        savedStateHandle[SEARCH_WIDGET_STATE] = SearchWidgetState.OPENED
    }

    fun onSearchQueryChanged(searchText: String) {
        savedStateHandle[SEARCH_QUERY] = searchText
    }
    fun addSupplier(supplier: Supplier) {
        viewModelScope.launch {
            if (!isOnline.value) {
                showSnackbarMessage(uiString.core_ui_error_network)
                return@launch
            }
            when (val result = addSupplierUseCase(supplier)) {
                is OperationResult.Success -> {
                    showSnackbarMessage(R.string.feature_supplier_add_success_message)
                }

                is OperationResult.Failure -> {
                    showSnackbarMessage(result.message)
                }
            }
        }
    }

    fun onSelectSupplier(supplier: Supplier) {
        _supplierSelected.update { supplier }
    }

    fun updateSupplier(newSupplier: Supplier) {
        viewModelScope.launch {
            if (!isOnline.value) {
                showSnackbarMessage(uiString.core_ui_error_network)
                return@launch
            }
            val oldSupplier = _supplierSelected.value
            if (oldSupplier == newSupplier) {
                showSnackbarMessage(R.string.feature_supplier_update_error_message)
                return@launch
            }
            when (val result = updateSupplierUseCase(oldSupplier!!, newSupplier)) {
                is OperationResult.Success -> {
                    showSnackbarMessage(R.string.feature_supplier_update_success_message)
                }

                is OperationResult.Failure -> {
                    showSnackbarMessage(result.message)
                }
            }
        }
    }

    fun deleteSupplier() {
        viewModelScope.launch {
            if (!isOnline.value) {
                showSnackbarMessage(uiString.core_ui_error_network)
                return@launch
            }
            val supplier = _supplierSelected.value ?: return@launch
            when (val result = deleteSupplierUseCase(supplier)) {
                is OperationResult.Success -> {
                    showSnackbarMessage(R.string.feature_supplier_delete_success_message)
                }

                is OperationResult.Failure -> {
                    showSnackbarMessage(result.message)
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
}

private const val SEARCH_QUERY = "searchQuery"
private const val SEARCH_WIDGET_STATE = "searchWidgetState"