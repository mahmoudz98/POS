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
package com.casecode.pos

import android.graphics.Color
import android.os.Bundle
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.metrics.performance.JankStats
import androidx.tracing.trace
import com.casecode.pos.core.analytics.AnalyticsHelper
import com.casecode.pos.core.analytics.LocalAnalyticsHelper
import com.casecode.pos.core.designsystem.theme.POSTheme
import com.casecode.pos.core.domain.utils.NetworkMonitor
import com.casecode.pos.core.ui.utils.moveToSignInActivity
import com.casecode.pos.sync.initializers.SyncSupplierInvoicesOverdue
import com.casecode.pos.ui.MainScreen
import com.casecode.pos.ui.rememberMainAppState
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private val viewModel: MainActivityViewModel by viewModels()

    /**
     * Lazily inject [JankStats], which is used to track jank throughout the app.
     */
    @Inject
    lateinit var lazyStats: dagger.Lazy<JankStats>

    @Inject
    lateinit var networkMonitor: NetworkMonitor

    @Inject
    lateinit var analyticsHelper: AnalyticsHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()
        super.onCreate(savedInstanceState)

        var authUiState: MainAuthUiState by mutableStateOf(MainAuthUiState.Loading)
        lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.mainAuthUiState.onEach { authUiState = it }.collect { state ->
                    trace("posMainAuthState") {
                        when (state) {
                            is MainAuthUiState.LoginByAdmin,
                            is MainAuthUiState.LoginByAdminEmployee,
                            -> {
                                SyncSupplierInvoicesOverdue.initialize(context = this@MainActivity)
                            }

                            is MainAuthUiState.ErrorLogin -> {
                                moveToSignInActivity(this@MainActivity)
                            }

                            else -> {}
                        }
                    }
                }
            }
        }
        splashScreen.setKeepOnScreenCondition {
            authUiState == MainAuthUiState.Loading
        }

        enableEdgeToEdge()
        setContent {
            val darkTheme = isSystemInDarkTheme()
            DisposableEffect(darkTheme) {
                enableEdgeToEdge(
                    statusBarStyle =
                        SystemBarStyle.auto(
                            Color.TRANSPARENT,
                            Color.TRANSPARENT,
                        ) { darkTheme },
                    navigationBarStyle =
                        SystemBarStyle.auto(
                            lightScrim,
                            darkScrim,
                        ) { darkTheme },
                )
                onDispose {}
            }
            val appState =
                rememberMainAppState(
                    networkMonitor = networkMonitor,
                    mainAuthUiState = authUiState,
                )
            CompositionLocalProvider(
                LocalAnalyticsHelper provides analyticsHelper,
            ) {
                POSTheme {
                    MainScreen(appState = appState)
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        lazyStats.get().isTrackingEnabled = true
    }

    override fun onPause() {
        super.onPause()
        lazyStats.get().isTrackingEnabled = false
    }

    /**
     * The default light scrim, as defined by androidx and the platform:
     */
    private val lightScrim = Color.argb(0xe6, 0xFF, 0xFF, 0xFF)

    /**
     * The default dark scrim, as defined by androidx and the platform:
     */
    private val darkScrim = Color.argb(0x80, 0x1b, 0x1b, 0x1b)
}