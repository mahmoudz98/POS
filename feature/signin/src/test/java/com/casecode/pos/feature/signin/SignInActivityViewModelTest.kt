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
package com.casecode.pos.feature.signin

import com.casecode.pos.core.testing.repository.TestAccountRepository
import com.casecode.pos.core.testing.service.TestAuthService
import com.casecode.pos.core.testing.util.MainDispatcherRule
import com.casecode.pos.core.testing.util.TestNetworkMonitor
import com.casecode.pos.core.ui.R
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import com.casecode.pos.core.data.R.string as stringData

class SignInActivityViewModelTest {
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    // Subject under test
    private lateinit var viewModel: SignInActivityViewModel
    private val networkMonitor = TestNetworkMonitor()
    private val accountService = TestAccountRepository()
    private val authService = TestAuthService()

    @Before
    fun init() {
        viewModel = SignInActivityViewModel(
            networkMonitor,
            accountService,
            authService,
        )
    }

    @Test
    fun testSetNetworkMonitor() =
        runTest {
            networkMonitor.setConnected(true)

            assertTrue(viewModel.signInUiState.value.isOnline)
        }

    @Test
    fun testSignIn_whenOffline_showsNetworkError() =
        runTest {
            networkMonitor.setConnected(false)

            viewModel.signIn({ "" })

            assertEquals(
                R.string.core_ui_error_network,
                viewModel.signInUiState.value.userMessage,
            )
        }

    @Test
    fun testSignIn_whenOnline_showsSuccessMessage() =
        runTest {
            networkMonitor.setConnected(true)

            viewModel.signIn({ "" })

            assertEquals(
                stringData.core_data_sign_in_success,
                viewModel.signInUiState.value.userMessage,
            )
        }
}