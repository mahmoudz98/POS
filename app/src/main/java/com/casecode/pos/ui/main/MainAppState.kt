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
package com.casecode.pos.ui.main

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavDestination
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navOptions
import com.casecode.pos.core.data.service.AuthService
import com.casecode.pos.core.data.utils.NetworkMonitor
import com.casecode.pos.core.model.data.LoginStateResult
import com.casecode.pos.core.model.data.permissions.Permission
import com.casecode.pos.feature.employee.EMPLOYEES_ROUTE
import com.casecode.pos.feature.employee.navigateToEmployees
import com.casecode.pos.feature.item.ITEMS_ROUTE
import com.casecode.pos.feature.item.ITEM_DIALOG_ROUTE
import com.casecode.pos.feature.item.ITEM_UPDATE_DIALOG_ROUTE
import com.casecode.pos.feature.item.QR_PRINT_ITEM_DIALOG_ROUTE
import com.casecode.pos.feature.item.navigateToItemsGraph
import com.casecode.pos.feature.profile.navigateToProfile
import com.casecode.pos.feature.sale.POS_ROUTE
import com.casecode.pos.feature.sale.navigateToPos
import com.casecode.pos.feature.setting.SETTING_ROUTE
import com.casecode.pos.feature.setting.navigateToSettings
import com.casecode.pos.feature.signout.SIGN_OUT_ROUTE
import com.casecode.pos.feature.statistics.REPORTS_ROUTE
import com.casecode.pos.feature.statistics.navigateToReports
import com.casecode.pos.navigation.SaleTopLevelDestination
import com.casecode.pos.navigation.TopLevelDestination
import com.casecode.pos.navigation.TopLevelDestination.EMPLOYEES
import com.casecode.pos.navigation.TopLevelDestination.ITEMS
import com.casecode.pos.navigation.TopLevelDestination.POS
import com.casecode.pos.navigation.TopLevelDestination.REPORTS
import com.casecode.pos.navigation.TopLevelDestination.SETTINGS
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import com.casecode.pos.navigation.SaleTopLevelDestination.POS as POSSale
import com.casecode.pos.navigation.SaleTopLevelDestination.REPORTS as ReportsSale
import com.casecode.pos.navigation.SaleTopLevelDestination.SETTING as SettingSale

@Composable
fun rememberMainAppState(
    networkMonitor: NetworkMonitor? = null,
    authService: AuthService,
    coroutineScope: CoroutineScope = rememberCoroutineScope(),
    navController: NavHostController = rememberNavController(),
): MainAppState =
    remember(
        navController,
        coroutineScope,
        networkMonitor,
    ) {
        MainAppState(
            navController = navController,
            coroutineScope = coroutineScope,
            networkMonitor = networkMonitor!!,
            authService = authService,
        )
    }

@Stable
class TopAppBarAction(
    val icon: ImageVector = Icons.Default.Person,
    val actionIconContent: Int = com.casecode.pos.feature.profile.R.string.feature_profile_title,
    val onClick: () -> Unit = { },
)

sealed interface MainAuthUiState {
    data object Loading : MainAuthUiState

    data object LoginByAdmin : MainAuthUiState

    data object LoginByAdminEmployee : MainAuthUiState

    data object LoginBySaleEmployee : MainAuthUiState

    data object LoginByNoneEmployee : MainAuthUiState

    data object ErrorLogin : MainAuthUiState
}

@Stable
class MainAppState(
    val navController: NavHostController,
    val coroutineScope: CoroutineScope,
    networkMonitor: NetworkMonitor,
    authService: AuthService,
) {
    val currentDestination: NavDestination?
        @Composable get() = navController.currentBackStackEntryAsState().value?.destination

    val currentTopAppBarAction: TopAppBarAction?
        @Composable get() =
            when (currentDestination?.route) {
                POS_ROUTE, REPORTS_ROUTE, EMPLOYEES_ROUTE, SETTING_ROUTE, SIGN_OUT_ROUTE ->
                    TopAppBarAction(
                        icon = Icons.Default.Person,
                        com.casecode.pos.feature.profile.R.string.feature_profile_title,
                        onClick = {
                            navController.navigateToProfile(
                                navOptions {
                                    launchSingleTop = true
                                    restoreState = true
                                },
                            )
                        },
                    )

                else -> null
            }

    val currentSaleTopLevelDestination: SaleTopLevelDestination?
        @Composable get() =
            when (currentDestination?.route) {
                POS_ROUTE -> POSSale
                REPORTS_ROUTE -> ReportsSale
                SETTING_ROUTE -> SettingSale
                else -> null
            }

    val currentAdminTopLevelDestination: TopLevelDestination?
        @Composable get() =
            when (currentDestination?.route) {
                POS_ROUTE -> POS
                REPORTS_ROUTE -> REPORTS
                ITEMS_ROUTE, ITEM_DIALOG_ROUTE, ITEM_UPDATE_DIALOG_ROUTE, QR_PRINT_ITEM_DIALOG_ROUTE -> ITEMS
                EMPLOYEES_ROUTE -> EMPLOYEES
                SETTING_ROUTE -> SETTINGS
                else -> null
            }

    val isOffline =
        networkMonitor.isOnline.map(Boolean::not).stateIn(
            scope = coroutineScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = false,
        )
    val mainAuthUiState =
        authService.loginData
            .map {
                when (it) {
                    LoginStateResult.Loading -> MainAuthUiState.Loading
                    is LoginStateResult.EmployeeLogin -> {
                        when (it.employee.permission) {
                            Permission.ADMIN -> MainAuthUiState.LoginByAdminEmployee
                            Permission.SALE -> MainAuthUiState.LoginBySaleEmployee
                            Permission.NONE -> MainAuthUiState.LoginByNoneEmployee
                        }
                    }

                    LoginStateResult.Error, LoginStateResult.NotSignIn -> MainAuthUiState.ErrorLogin
                    is LoginStateResult.NotCompleteBusiness -> MainAuthUiState.ErrorLogin
                    is LoginStateResult.SuccessLoginAdmin -> MainAuthUiState.LoginByAdmin
                }
            }.stateIn(
                scope = coroutineScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = MainAuthUiState.Loading,
            )

    /**
     * Map of top level destinations to be used in the TopBar. The key is the
     * route.
     */
    val topLevelDestinations: List<TopLevelDestination> = TopLevelDestination.entries

    /**
     * Map of top level destinations to be used in the TopBar. The key is the
     * route.
     */
    val saleTopLevelDestinations: List<SaleTopLevelDestination> = SaleTopLevelDestination.entries

    /**
     * UI logic for navigating to a top level destination in the app. Top level destinations have
     * only one copy of the destination of the back stack, and save and restore state whenever you
     * navigate to and from it.
     *
     * @param topLevelDestination: The destination the app needs to navigate to.
     */

    fun navigateToTopLevelDestination(topLevelDestination: TopLevelDestination) {
        val topLevelNavOptions =
            navOptions {
                // Pop up to the start destination of the graph to
                // avoid building up a large stack of destinations
                // on the back stack as users select items
                popUpTo(navController.graph.findStartDestination().id) {
                    saveState = true
                }
                // Avoid multiple copies of the same destination when
                // reselecting the same item
                launchSingleTop = true
                // Restore state when reselecting a previously selected item
                restoreState = true
            }

        when (topLevelDestination) {
            POS -> navController.navigateToPos(topLevelNavOptions)
            REPORTS -> navController.navigateToReports(topLevelNavOptions)
            ITEMS -> navController.navigateToItemsGraph(topLevelNavOptions)
            EMPLOYEES -> navController.navigateToEmployees(topLevelNavOptions)
            SETTINGS -> navController.navigateToSettings(topLevelNavOptions)
        }
    }

    /**
     * UI logic for navigating to a top level destination in the app. Top level destinations have
     * only one copy of the destination of the back stack, and save and restore state whenever you
     * navigate to and from it.
     *
     * @param saleTopLevelDestination: The destination the app needs to navigate to.
     */

    fun navigateToSaleTopLevelDestination(saleTopLevelDestination: SaleTopLevelDestination) {
        val topLevelNavOptions =
            navOptions {
                // Pop up to the start destination of the graph to
                // avoid building up a large stack of destinations
                // on the back stack as users select items
                popUpTo(navController.graph.findStartDestination().id) {
                    saveState = true
                }
                // Avoid multiple copies of the same destination when
                // reselecting the same item
                launchSingleTop = true
                // Restore state when reselecting a previously selected item
                restoreState = true
            }

        when (saleTopLevelDestination) {
            POSSale -> navController.navigateToPos(topLevelNavOptions)
            ReportsSale -> navController.navigateToReports(topLevelNavOptions)
            SettingSale -> navController.navigateToSettings(topLevelNavOptions)
        }
    }
}