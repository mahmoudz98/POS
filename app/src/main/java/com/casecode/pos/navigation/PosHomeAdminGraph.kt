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

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.navigation
import androidx.navigation.navOptions
import com.casecode.pos.feature.bill.navigation.billsGraph
import com.casecode.pos.feature.bill.navigation.navigateToBillsGraph
import com.casecode.pos.feature.employee.employeesScreen
import com.casecode.pos.feature.employee.navigateToEmployees
import com.casecode.pos.feature.inventory.navigation.inventoryScreen
import com.casecode.pos.feature.item.navigation.itemsGraph
import com.casecode.pos.feature.item.navigation.navigateToItemsGraph
import com.casecode.pos.feature.profile.profileScreen
import com.casecode.pos.feature.purchase.navigation.purchaseScreen
import com.casecode.pos.feature.sale.navigation.SaleRoute
import com.casecode.pos.feature.sale.navigation.saleScreen
import com.casecode.pos.feature.sales.report.navigateToSalesReport
import com.casecode.pos.feature.sales.report.navigateToSalesReportDetails
import com.casecode.pos.feature.sales.report.salesReportGraph
import com.casecode.pos.feature.setting.settingsGraph
import com.casecode.pos.feature.signout.navigateToSignOut
import com.casecode.pos.feature.signout.signOutDialog
import com.casecode.pos.feature.statistics.reportsScreen
import com.casecode.pos.feature.supplier.navigation.navigateToSupplier
import com.casecode.pos.feature.supplier.navigation.supplierScreen
import com.casecode.pos.ui.MainAppState
import kotlinx.serialization.Serializable

@Serializable
object AdminHomeGraphRoute

fun NavGraphBuilder.homeAdminGraph(
    appState: MainAppState,
    enterTransition: (AnimatedContentTransitionScope<NavBackStackEntry>.() -> EnterTransition?)? = null,
    exitTransition: (AnimatedContentTransitionScope<NavBackStackEntry>.() -> ExitTransition?)? = null,
) {
    navigation<AdminHomeGraphRoute>(
        startDestination = SaleRoute,
        enterTransition = enterTransition,
        exitTransition = exitTransition,

    ) {
        saleScreen {
            appState.navController.navigateToItemsGraph(
                defaultNavOptions(),
            )
        }

        // TODO: invoke navigate to inventory report screen
        reportsScreen(
            onSalesReportClick = { appState.navController.navigateToSalesReport() },
            onInventoryReportClick = {},
        )
        salesReportGraph(
            navController = appState.navController,
            onSalesReportDetailsClick = {
                appState.navController.navigateToSalesReportDetails()
            },
        )
        inventoryScreen(
            onItemsScreenClick = {
                appState.navController.navigateToItemsGraph()
            },
        )
        itemsGraph(appState.navController)
        supplierScreen { appState.navController.popBackStack() }
        purchaseScreen(
            onSupplierScreenClick = {
                appState.navController.navigateToSupplier()
            },
            onBillsScreenClick = {
                appState.navController.navigateToBillsGraph()
            },
        )
        billsGraph(appState.navController)
        settingsGraph(
            appState.navController,
            onEmployeesScreenClick = {
                appState.navController.navigateToEmployees()
            },
            onSignOutClick = {
                appState.navController.navigateToSignOut()
            },
        )
        employeesScreen()
        signOutDialog(
            onSignOut = {
                appState.signOut()
            },
            onDismiss = appState.navController::popBackStack,
        )
        profileScreen { appState.navController.popBackStack() }
    }
}

fun NavController.navigateToMainGraph(navOptions: NavOptions? = null) =
    navigate(AdminHomeGraphRoute, navOptions)

fun defaultNavOptions(): NavOptions = navOptions {
    launchSingleTop = true
    restoreState = true
}