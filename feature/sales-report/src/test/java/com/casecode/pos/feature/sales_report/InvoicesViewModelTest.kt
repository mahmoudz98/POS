package com.casecode.pos.feature.sales_report

import com.casecode.pos.core.testing.base.BaseTest
import com.casecode.pos.core.testing.util.MainDispatcherRule
import org.junit.Rule

class InvoicesViewModelTest : BaseTest() {
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    // Subject under test
    private lateinit var viewModel: SalesReportViewModel

    override fun init() {
        viewModel = SalesReportViewModel(getInvoices)
    }
}