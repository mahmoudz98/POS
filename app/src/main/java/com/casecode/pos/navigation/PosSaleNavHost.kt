package com.casecode.pos.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.navOptions
import com.casecode.pos.feature.item.navigateToItemsGraph
import com.casecode.pos.feature.sale.POS_ROUTE
import com.casecode.pos.feature.sale.posScreen
import com.casecode.pos.feature.sales_report.navigateToSalesReportDetails
import com.casecode.pos.feature.sales_report.salesReportGraph
import com.casecode.pos.feature.setting.settingsGraph
import com.casecode.pos.feature.signout.navigateToSignOut
import com.casecode.pos.feature.signout.signOutDialog
import com.casecode.pos.feature.statistics.reportsScreen
import com.casecode.pos.ui.main.MainAppState


@Composable
fun PosSaleNavHost(
    appState: MainAppState,
    modifier: Modifier = Modifier,
    startDestination: String = POS_ROUTE,
    onSignOutClick: () -> Unit,

    ) {
    NavHost(
        navController = appState.navController,
        startDestination = startDestination,
        modifier = modifier,
    ) {
        posScreen {
            appState.navController.navigateToItemsGraph(
                navOptions {
                    // Pop up to the start destination of the graph to
                    // avoid building up a large stack of destinations
                    // on the back stack as users select items
                    popUpTo(appState.navController.graph.findStartDestination().id) {
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
        //
        reportsScreen(onSalesReportClick = {}, onInventoryReportClick = {})
        salesReportGraph(
            navController = appState.navController,
            onBackClick = { appState.navController.popBackStack() },
            onSalesReportDetailsClick = {
                appState.navController.navigateToSalesReportDetails()
            },
        )
        settingsGraph(
            appState.navController,
            onSignOutClick = {
                appState.navController.navigateToSignOut()
            },
        )
        signOutDialog(
            onSignOut = onSignOutClick,
            onDismiss = appState.navController::popBackStack,
        )

    }

}