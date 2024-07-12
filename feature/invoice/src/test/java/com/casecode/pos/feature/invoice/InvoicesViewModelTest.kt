package com.casecode.pos.feature.invoice

import com.casecode.pos.core.testing.base.BaseTest
import com.casecode.pos.core.testing.util.MainDispatcherRule
import org.junit.Rule

class InvoicesViewModelTest : BaseTest() {
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    // Subject under test
    private lateinit var viewModel: InvoicesViewModel
    override fun init() {
        viewModel = InvoicesViewModel(getInvoicesUseCase)
    }


}