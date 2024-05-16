package com.casecode.pos.viewmodel

import com.casecode.pos.data.R
import com.casecode.testing.base.BaseTest
import com.casecode.testing.util.MainDispatcherRule
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert
import org.junit.Assert.*
import org.junit.Rule
import org.junit.Test

class InvoicesViewModelTest:BaseTest(){
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    // Subject under test
    private lateinit var viewModel: InvoicesViewModel
    override fun init() {
         viewModel = InvoicesViewModel(getInvoicesUseCase)
    }

    @Test
    fun fetchInvoices_whenHasInvoice_returnListOfInvoices() {
        // when
        val invoices = viewModel.invoices.value
        // Then
        assertSame(invoices, testInvoiceRepository.fakeInvoiceGroup)
    }

    @Test
    fun fetchItems_whenInvoiceHasError_returnMessageError() {
        // When
        testInvoiceRepository setReturnError true
        viewModel.fetchInvoices()
        // Then
        MatcherAssert.assertThat(viewModel.userMessage.value?.peekContent(), `is`(R.string.get_invoice_failure))
    }

}