package com.casecode.pos.viewmodel

import androidx.annotation.VisibleForTesting
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.map
import androidx.lifecycle.viewModelScope
import com.casecode.domain.model.users.Invoice
import com.casecode.domain.model.users.InvoiceGroup
import com.casecode.domain.usecase.GetInvoicesUseCase
import com.casecode.domain.utils.Resource
import com.casecode.pos.R
import com.casecode.pos.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class InvoicesViewModel @Inject constructor(
    private val getInvoicesUseCase: GetInvoicesUseCase,
) : BaseViewModel() {

    private val _invoices = MutableLiveData<List<InvoiceGroup>>()
    val invoices get() = _invoices
    val isInvoiceEmpty: LiveData<Boolean> = _invoices.map { it.isEmpty() }
    private val _dateInvoiceSelected = MutableLiveData<Long?>(null)
    val dateInvoiceSelected get() = _dateInvoiceSelected
    val hasDateInvoiceFilter: LiveData<Boolean> = _dateInvoiceSelected.map { it != null }
    private val _invoiceSelected = MutableLiveData<Invoice>()

    val invoiceSelected: LiveData<Invoice>
        get() {
            hideProgress()
            return _invoiceSelected
        }

    init {
        fetchInvoices()
    }
    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    internal fun fetchInvoices() {
        viewModelScope.launch {

            getInvoicesUseCase().collect {
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
                        _invoices.value = result.data

                        hideProgress()
                    }

                    is Resource.Loading -> {
                        showProgress()
                    }
                }
            }
        }
    }

    fun setDateInvoiceSelected(date: Long?) {
        _dateInvoiceSelected.value = date
    }

    fun setSelectedInvoice(invoice: Invoice) {
        showProgress()
        _invoiceSelected.value = invoice
    }
}