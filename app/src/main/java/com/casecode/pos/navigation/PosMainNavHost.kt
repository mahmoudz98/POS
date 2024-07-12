package com.casecode.pos.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.navOptions
import com.casecode.pos.feature.employee.employeesScreen
import com.casecode.pos.feature.invoice.invoicesGraph
import com.casecode.pos.feature.item.itemsGraph
import com.casecode.pos.feature.item.navigateToItemsGraph
import com.casecode.pos.ui.main.MainAppState
import com.casecode.pos.feature.sale.posScreen
import com.casecode.pos.feature.profile.profileScreen
import com.casecode.pos.feature.setting.settingScreen
import com.casecode.pos.feature.statistics.reportsScreen


@Composable
fun PosMainNavHost(
    appState: MainAppState,
    modifier: Modifier = Modifier,
    startDestination: String = com.casecode.pos.feature.sale.POS_ROUTE,
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
            }
            )
        }
        reportsScreen()
        invoicesGraph(appState.navController, appState::openOrClosed)
        itemsGraph(appState.navController, appState::openOrClosed)
        employeesScreen()
        settingScreen()
        profileScreen { appState.navController.popBackStack() }
        //signOutDialog ()
    }

}