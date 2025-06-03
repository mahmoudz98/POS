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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.metrics.performance.JankStats
import androidx.tracing.trace
import com.casecode.pos.core.analytics.AnalyticsHelper
import com.casecode.pos.core.analytics.LocalAnalyticsHelper
import com.casecode.pos.core.designsystem.theme.POSTheme
import com.casecode.pos.core.domain.utils.NetworkMonitor
import com.casecode.pos.sync.initializers.SyncSupplierInvoicesOverdue
import com.casecode.pos.ui.MainApp
import com.casecode.pos.ui.rememberMainAppState
import dagger.hilt.android.AndroidEntryPoint
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

        splashScreen.setKeepOnScreenCondition {
            viewModel.initialDestinationState.value.shouldKeepSplashScreen()
        }
        setContent {
            val darkTheme = isSystemInDarkTheme()
            LaunchedEffect(darkTheme) {
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
            }
            val currentAuthUiState by viewModel.initialDestinationState.collectAsStateWithLifecycle()
            LaunchedEffect(currentAuthUiState) {
                trace("posMainAuthStateSideEffects") {
                    if (currentAuthUiState is InitialDestinationState.LoginByAdmin ||
                        currentAuthUiState is InitialDestinationState.LoginByAdminEmployee ||
                        currentAuthUiState is InitialDestinationState.LoginBySaleEmployee
                    ) {
                        SyncSupplierInvoicesOverdue.initialize(context = this@MainActivity)
                    }
                }
            }
            val appState =
                rememberMainAppState(
                    networkMonitor = networkMonitor,
                    initialDestinationState = currentAuthUiState,
                )
            CompositionLocalProvider(
                LocalAnalyticsHelper provides analyticsHelper,
            ) {
                if (currentAuthUiState != InitialDestinationState.Loading) {
                    POSTheme {
                        MainApp(appState = appState)
                    }
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