package com.casecode.pos.viewmodel

import com.casecode.pos.core.testing.base.BaseTest
import com.casecode.pos.core.testing.util.MainDispatcherRule
import com.casecode.pos.ui.signIn.SignInActivityViewModel
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import com.casecode.pos.core.data.R.string as stringData

class SignInActivityViewModelTest : BaseTest() {
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()
    private lateinit var viewModel: SignInActivityViewModel

    override fun init() {
        viewModel = SignInActivityViewModel(testNetworkMonitor, testAccountService, testAuthService)
    }

    @Test
    fun testSetNetworkMonitor() = runTest {
        testNetworkMonitor.setConnected(true)

        assertTrue(viewModel.signInUiState.value.isOnline)
    }

    @Test
    fun testSignIn_whenOffline_showsNetworkError() = runTest {
        testNetworkMonitor.setConnected(false)

        viewModel.signIn()

        assertEquals(
            com.casecode.pos.core.ui.R.string.core_ui_error_network,
            viewModel.signInUiState.value.userMessage,
        )
    }

    @Test
    fun testSignIn_whenOnline_showsSuccessMessage() = runTest {
        testNetworkMonitor.setConnected(true)

        viewModel.signIn()

        assertEquals(stringData.core_data_sign_in_success, viewModel.signInUiState.value.userMessage)
    }


}