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
package com.casecode.pos.ui

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navOptions
import com.casecode.pos.MainAuthUiState
import com.casecode.pos.core.data.utils.NetworkMonitor
import com.casecode.pos.feature.profile.R
import com.casecode.pos.feature.profile.navigateToProfile
import com.casecode.pos.feature.sale.SaleRoute
import com.casecode.pos.feature.setting.SettingNavigation
import com.casecode.pos.feature.signout.SignOutRoute
import com.casecode.pos.feature.statistics.ReportsRoute
import com.casecode.pos.navigation.AdminTopLevelDestination
import com.casecode.pos.navigation.SaleTopLevelDestination
import com.casecode.pos.navigation.TopLevelDestination
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

@Composable
fun rememberMainAppState(
    networkMonitor: NetworkMonitor? = null,
    mainAuthUiState: MainAuthUiState,
    coroutineScope: CoroutineScope = rememberCoroutineScope(),
    navController: NavHostController = rememberNavController(),
): MainAppState =
    remember(
        navController,
        coroutineScope,
        networkMonitor,
        mainAuthUiState,
    ) {
        MainAppState(
            navController = navController,
            coroutineScope = coroutineScope,
            networkMonitor = networkMonitor!!,
            mainAuthUiState = mainAuthUiState,
        )
    }

@Stable
class TopAppBarAction(
    val icon: ImageVector = Icons.Default.Person,
    val actionIconContent: Int = R.string.feature_profile_title,
    val onClick: () -> Unit = { },
)

@Stable
class MainAppState(
    val navController: NavHostController,
    val coroutineScope: CoroutineScope,
    networkMonitor: NetworkMonitor,
    val mainAuthUiState: MainAuthUiState,
) {
    val currentDestination: NavDestination?
        @Composable get() = navController.currentBackStackEntryAsState().value?.destination

    /**
     * Map of top level destinations to be used in the TopBar. The key is the
     * route.
     */
    val topLevelDestinations: List<AdminTopLevelDestination> by lazy { AdminTopLevelDestination.entries }

    val saleTopLevelDestinations: List<SaleTopLevelDestination> by lazy { SaleTopLevelDestination.entries }

    private fun isProfileRoute(currentDestination: NavDestination?): Boolean {
        val profileRoutes = setOf(
            SaleRoute::class,
            ReportsRoute::class,
            SettingNavigation.SettingRoute::class,
            SignOutRoute::class,
        )
        return profileRoutes.any { currentDestination?.hasRoute(it) == true }
    }

    val currentTopAppBarAction: TopAppBarAction?
        @Composable
        get() {
            return if (isProfileRoute(currentDestination)) {
                TopAppBarAction(
                    icon = Icons.Default.Person,
                    actionIconContent = R.string.feature_profile_title,
                    onClick = {
                        navController.navigateToProfile(
                            navOptions {
                                launchSingleTop = true
                                restoreState = true
                            },
                        )
                    },
                )
            } else {
                null
            }
        }

    @Composable
    private fun getCurrentTopLevelDestination(destinations: List<TopLevelDestination>): TopLevelDestination? =
        destinations.firstOrNull { currentDestination?.hasRoute(it.route) == true }

    val currentAdminTopLevelDestination: TopLevelDestination?
        @Composable get() = getCurrentTopLevelDestination(topLevelDestinations)

    val currentSaleTopLevelDestination: TopLevelDestination?
        @Composable get() = getCurrentTopLevelDestination(saleTopLevelDestinations)

    val isOffline =
        networkMonitor.isOnline.map(Boolean::not).stateIn(
            scope = coroutineScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = false,
        )

    /**
     * UI logic for navigating to a top level destination in the app. Top level destinations have
     * only one copy of the destination of the back stack, and save and restore state whenever you
     * navigate to and from it.
     *
     * @param topLevelDestination: The destination the app needs to navigate to.
     */

    fun navigateToTopLevelDestination(topLevelDestination: TopLevelDestination) {
        val navOptions = navOptions {
            popUpTo(navController.graph.findStartDestination().id) {
                saveState = true
            }
            launchSingleTop = true
            restoreState = true
        }
        topLevelDestination.navigate(navController, navOptions)
    }
}