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
package com.casecode.pos.feature.login

import com.casecode.pos.core.testing.base.BaseTest
import com.casecode.pos.core.testing.util.MainDispatcherRule
import com.casecode.pos.feature.login.employee.LoginEmployeeViewModel
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
        viewModel = LoginEmployeeViewModel(networkMonitor, accountService)
    }

    @Test
    fun testLoginByEmployee_whenOffline_showsNetworkError() =
        runTest {
            networkMonitor.setConnected(false)

            viewModel.loginByEmployee("uid", "name", "password")

            assertEquals(
                com.casecode.pos.core.ui.R.string.core_ui_error_network,
                viewModel.loginEmployeeUiState.value.userMessage,
            )
        }

    @Test
    fun testLoginByEmployee_whenSuccess_returnProgressFalse() =
        runTest {
            networkMonitor.setConnected(true)

            viewModel.loginByEmployee("uid", "name", "password")
            assertFalse(viewModel.loginEmployeeUiState.value.inProgressLoginEmployee)
        }
}