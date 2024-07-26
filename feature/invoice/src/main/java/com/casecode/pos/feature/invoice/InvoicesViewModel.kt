package com.casecode.pos.feature.invoice

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
class InvoicesViewModel @Inject constructor(
    private val getInvoicesUseCase: GetInvoicesUseCase,
) : ViewModel() {

    private val _uiInvoiceState = MutableStateFlow<UiInvoiceState>(UiInvoiceState())
    val uiInvoiceState get() = _uiInvoiceState.asStateFlow()

    private val _invoiceSelected = MutableStateFlow<UIInvoiceDetails>(UIInvoiceDetails.Loading)

    @OptIn(FlowPreview::class)
    val invoiceSelected get() = _invoiceSelected.asStateFlow().debounce(300)

    init {
        fetchInvoices()
    }

    private fun fetchInvoices() {
        viewModelScope.launch {
            delay(500)
            getInvoicesUseCase().collect {
                _uiInvoiceState.value = _uiInvoiceState.value.copy(resourceInvoiceGroups = it)

            }
        }
    }

    fun setDateInvoiceSelected(date: Long?) {
        _uiInvoiceState.value = _uiInvoiceState.value.copy(dateInvoiceSelected = date)
    }

    fun setSelectedInvoice(invoice: Invoice) {
        _invoiceSelected.value = UIInvoiceDetails.Success(invoice)
    }


}