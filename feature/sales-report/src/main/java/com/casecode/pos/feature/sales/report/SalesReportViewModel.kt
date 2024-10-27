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
package com.casecode.pos.feature.sales.report

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.casecode.pos.core.domain.usecase.GetInvoicesUseCase
import com.casecode.pos.core.model.data.users.Invoice
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SalesReportViewModel
@Inject
constructor(
    private val getInvoicesUseCase: GetInvoicesUseCase,
) : ViewModel() {
    private val _uiSalesReportState = MutableStateFlow(UiSalesReportState())
    val uiSalesReportState get() = _uiSalesReportState.asStateFlow()

    private val _invoiceSelected =
        MutableStateFlow<UISalesReportDetails>(UISalesReportDetails.Loading)

    @OptIn(FlowPreview::class)
    val invoiceSelected get() = _invoiceSelected.asStateFlow().debounce(300)

    init {
        fetchInvoices()
    }

    private fun fetchInvoices() {
        viewModelScope.launch {
            delay(500)
            getInvoicesUseCase().collect {
                _uiSalesReportState.value =
                    _uiSalesReportState.value.copy(resourceInvoiceGroups = it)
            }
        }
    }

    fun setDateInvoiceSelected(date: Long?) {
        _uiSalesReportState.value = _uiSalesReportState.value.copy(dateInvoiceSelected = date)
    }

    fun setSelectedInvoice(invoice: Invoice) {
        _invoiceSelected.value = UISalesReportDetails.Success(invoice)
    }
}