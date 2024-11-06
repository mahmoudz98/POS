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
package com.casecode.pos.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.navOptions
import com.casecode.pos.feature.employee.employeesScreen
import com.casecode.pos.feature.item.navigation.itemsGraph
import com.casecode.pos.feature.item.navigation.navigateToItemsGraph
import com.casecode.pos.feature.profile.profileScreen
import com.casecode.pos.feature.sale.navigation.SaleRoute
import com.casecode.pos.feature.sale.navigation.saleScreen
import com.casecode.pos.feature.sales.report.navigateToSalesReport
import com.casecode.pos.feature.sales.report.navigateToSalesReportDetails
import com.casecode.pos.feature.sales.report.salesReportGraph
import com.casecode.pos.feature.setting.settingsGraph
import com.casecode.pos.feature.signout.navigateToSignOut
import com.casecode.pos.feature.signout.signOutDialog
import com.casecode.pos.feature.statistics.reportsScreen
import com.casecode.pos.ui.MainAppState

@Composable
fun PosMainNavHost(
    appState: MainAppState,
    modifier: Modifier = Modifier,
    onSignOutClick: () -> Unit,
) {
    NavHost(
        navController = appState.navController,
        startDestination = SaleRoute,
        modifier = modifier,
    ) {
        saleScreen {
            appState.navController.navigateToItemsGraph(
                navOptions {
                    // Pop up to the start destination of the graph to
                    // avoid building up a large stack of destinations
                    // on the back stack as users select items
                    popUpTo(
                        appState.navController.graph
                            .findStartDestination()
                            .id,
                    ) {
                        saveState = true
                    }
                    // Avoid multiple copies of the same destination when
                    // reselecting the same item
                    launchSingleTop = true
                    // Restore state when reselecting a previously selected item
                    restoreState = true
                },
            )
        }
        // TODO: invoke navigate to inventory report screen
        reportsScreen(
            onSalesReportClick = { appState.navController.navigateToSalesReport() },
            onInventoryReportClick = {},
        )
        salesReportGraph(
            navController = appState.navController,
            onBackClick = { appState.navController.popBackStack() },
            onSalesReportDetailsClick = {
                appState.navController.navigateToSalesReportDetails()
            },
        )
        itemsGraph(appState.navController)
        employeesScreen()
        settingsGraph(
            appState.navController,
            onSignOutClick = {
                appState.navController.navigateToSignOut()
            },
        )
        signOutDialog(
            onSignOut = {
                onSignOutClick()
            },
            onDismiss = appState.navController::popBackStack,
        )
        profileScreen { appState.navController.popBackStack() }
    }
}