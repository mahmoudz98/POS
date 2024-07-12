package com.casecode.pos.feature.login_employee

import com.casecode.pos.core.testing.base.BaseTest
import com.casecode.pos.core.testing.util.MainDispatcherRule
import kotlinx.coroutines.test.runTest
import org.junit.Rule

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse

class LoginEmployeeViewModelTest : BaseTest() {
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var viewModel: LoginEmployeeViewModel

    override fun init() {
        viewModel = LoginEmployeeViewModel(testNetworkMonitor, testAccountService)
    }

    @Test
    fun testLoginByEmployee_whenOffline_showsNetworkError() = runTest {
        testNetworkMonitor.setConnected(false)

        viewModel.loginByEmployee("uid", "name", "password")

        assertEquals(
            com.casecode.pos.core.ui.R.string.core_ui_error_network,
            viewModel.loginEmployeeUiState.value.userMessage,
        )
    }

    @Test
    fun testLoginByEmployee_whenSuccess_returnProgressFalse() = runTest {
        testNetworkMonitor.setConnected(true)

        viewModel.loginByEmployee("uid", "name", "password")
        assertFalse(viewModel.loginEmployeeUiState.value.inProgressLoginEmployee)
    }
}