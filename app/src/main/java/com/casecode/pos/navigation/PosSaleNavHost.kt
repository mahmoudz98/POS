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
import com.casecode.pos.feature.inventory.navigation.inventoryScreen
import com.casecode.pos.feature.item.navigation.itemsSaleGraph
import com.casecode.pos.feature.item.navigation.navigateToItemsGraph
import com.casecode.pos.feature.item.navigation.navigateToItemsSaleGraph
import com.casecode.pos.feature.profile.profileScreen
import com.casecode.pos.feature.sale.navigation.SaleRoute
import com.casecode.pos.feature.sale.navigation.saleScreen
import com.casecode.pos.feature.sales.report.navigateToSalesReportDetails
import com.casecode.pos.feature.sales.report.salesReportGraph
import com.casecode.pos.feature.setting.settingsGraph
import com.casecode.pos.feature.signout.navigateToSignOut
import com.casecode.pos.feature.signout.signOutDialog
import com.casecode.pos.feature.statistics.reportsScreen
import com.casecode.pos.feature.supplier.navigation.supplierScreen
import com.casecode.pos.ui.MainAppState

@Composable
fun PosSaleNavHost(
    appState: MainAppState,
    modifier: Modifier = Modifier,
) {
    NavHost(
        navController = appState.navController,
        startDestination = SaleRoute,
        modifier = modifier,
    ) {
        saleScreen {
            appState.navController.navigateToItemsGraph(
                defaultNavOptions(appState.navController.graph.findStartDestination().id),
            )
        }
        reportsScreen(onSalesReportClick = {}, onInventoryReportClick = {})
        salesReportGraph(
            navController = appState.navController,
            onSalesReportDetailsClick = {
                appState.navController.navigateToSalesReportDetails()
            },
        )
        inventoryScreen(
            onItemsScreenClick = {
                appState.navController.navigateToItemsSaleGraph()
            },
        )

        itemsSaleGraph(appState.navController)
        supplierScreen(onBackClick = { appState.navController.popBackStack() })
        // TODO: remove employees button from setting with sales user
        settingsGraph(
            appState.navController,
            onEmployeesScreenClick = {},
            onSignOutClick = {
                appState.navController.navigateToSignOut()
            },
        )
        signOutDialog(
            onSignOut = {
                // activity?.moveToSignInActivity()
            },
            onDismiss = appState.navController::popBackStack,
        )
        profileScreen { appState.navController.popBackStack() }
    }
}