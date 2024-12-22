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
import com.casecode.pos.core.data.utils.NetworkMonitor
import com.casecode.pos.core.domain.usecase.GetSupplierInvoicesUseCase
import com.casecode.pos.core.domain.utils.Resource
import com.casecode.pos.core.ui.stateInWhileSubscribed
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class BillsViewModel @Inject constructor(
    networkMonitor: NetworkMonitor,
    getSupplierInvoicesUseCase: GetSupplierInvoicesUseCase,
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

    fun showSnackbarMessage(message: Int) {
        _userMessage.update { message }
    }

    fun snackbarMessageShown() {
        _userMessage.value = null
    }

    companion object {
        private const val SELECTED_BILL_ID_KEY = "selectedBillId"
    }
}